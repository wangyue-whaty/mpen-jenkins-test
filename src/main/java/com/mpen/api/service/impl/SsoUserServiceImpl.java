/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpen.api.bean.User;
import com.mpen.api.bean.UserInfo;
import com.mpen.api.bean.UserSession;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPeCustom;
import com.mpen.api.domain.DdbPeLabel;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.domain.DdbPrPenCustom;
import com.mpen.api.domain.SsoUser;
import com.mpen.api.exception.CacheException;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.PeCustomMapper;
import com.mpen.api.mapper.PeLabelMapper;
import com.mpen.api.mapper.PePenMapper;
import com.mpen.api.mapper.PrPenCustomMapper;
import com.mpen.api.mapper.SsoUserMapper;
import com.mpen.api.service.MemCacheService;
import com.mpen.api.service.PeCustomService;
import com.mpen.api.service.SsoUserService;
import com.mpen.api.util.CommUtil;
import com.mpen.api.util.FileUtils;

/**
 * SsoUserService服务.
 *
 * @author kai
 *
 */
@Component
public class SsoUserServiceImpl implements SsoUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SsoUserServiceImpl.class);

    @Autowired
    private SsoUserMapper ssoUserMapper;
    @Autowired
    private PeCustomMapper peCustomMapper;
    @Autowired
    private PrPenCustomMapper prPenCustomMapper;
    @Autowired
    private PePenMapper pePenMapper;
    @Autowired
    private MemCacheService memCacheService;
    @Autowired
    private PeCustomService peCustomService;
    @Autowired
    private PeLabelMapper peLabelMapper;

    @Override
    public SsoUser getByLoginId(String loginId) {
        return this.ssoUserMapper.getByLoginId(loginId);
    }

    /**
     * 获取usersession.
     * 
     * @return UserSession
     * @throws SdkException
     *             SDK异常
     */
    @Override
    public UserSession getUserSessionByLoginId(String loginId) throws SdkException {

        if (StringUtils.isEmpty(loginId)) {
            return null;
        }

        final String key = CommUtil.getCacheKey(Constants.CACHE_USERSESSION_KEY_PREFIX + loginId);

        UserSession us = null;
        try {
            us = (UserSession) this.memCacheService.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (us == null) {
                final String loginIdAfterDecode = URLDecoder.decode(loginId, "UTF-8");
                final DdbPeCustom peCustom = this.peCustomService.getByLoginId(loginIdAfterDecode);
                final SsoUser ssoUser = this.getByLoginId(loginIdAfterDecode);
                us = new UserSession();
                us.setSsoUser(ssoUser);
                us.setPeCustom(peCustom);
                us.setId(ssoUser.getId());
                us.setLoginId(ssoUser.getLoginId());
                try {
                    this.memCacheService.set(key, us, Constants.DEFAULT_CACHE_EXPIRATION);
                } catch (CacheException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取用户信息错误！", e);
            throw new SdkException("用户名不存在！");
        }

        return us;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(SsoUser ssoUser) {
        if (ssoUser == null) {
            return null;
        } else if (StringUtils.isEmpty(ssoUser.getId())) {
            ssoUser.setId(CommUtil.genRecordKey());
        }
        try {
            this.ssoUserMapper.create(ssoUser);
            return ssoUser.getId();
        } catch (Exception ex) {
            LOGGER.error("create ssoUser error!", ex);
            return null;
        }
    }

    @Override
    public SsoUser getById(String id) {
        return this.ssoUserMapper.getById(id);
    }

    @Override
    public void delete(String id) {
        this.ssoUserMapper.delete(id);
    }

    /**
     * 登陆.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfo login(User user, HttpServletRequest request, HttpServletResponse response)
        throws SdkException, JsonParseException, JsonMappingException, IOException {
        final UserSession userSession = getUserSessionByLoginId(user.getUserName());
        // 验证用户名是否存在
        if (userSession == null || userSession.getSsoUser() == null) {
            throw new SdkException(Constants.NO_MATCHING_USER);
        }
        // 验证密码
        if (!user.getPassword().toLowerCase().equals(userSession.getSsoUser().getPassword().toLowerCase())) {
            if (!DigestUtils.md5Hex(user.getPassword().toLowerCase())
                .equals(userSession.getSsoUser().getPassword().toLowerCase())) {
                throw new SdkException(Constants.WRONG_PASSWORD);
            }
        }
        return ucenterLogin(userSession, request, response, true);
    }

    private UserInfo ucenterLogin(UserSession userSession, HttpServletRequest request, HttpServletResponse response,
        boolean checkAdd) throws SdkException, JsonParseException, JsonMappingException, IOException {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.LOGINIDKEY, userSession.getSsoUser().getBindmobile());
        params.put(Constants.PASSWORD, userSession.getSsoUser().getPassword());
        params.put(Constants.SITECODE, Constants.SCHOOL_NO);
        params.put(Constants.JSON, Constants.JSON);
        params.put(Constants.IP_PARAM, CommUtil.getIpAddr(request));
        // 向用户中心发送请求
        final String result = CommUtil.post(Constants.USERCENTER_LOGIN_ADDS, params);
        if (StringUtils.isBlank(result)) {
            throw new SdkException(Constants.UCENTER_ERROR);
        }
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> jsonObject = mapper.readValue(result, HashMap.class);
        if (jsonObject.containsKey(Constants.RESULT)) {
            if (Constants.SUCCESS.equals(jsonObject.get(Constants.RESULT))) {
                final Map<String, Object> tip = (Map<String, Object>) jsonObject.get(Constants.TIP);
                if (tip != null) {
                    response.setCharacterEncoding(Constants.UTF8_ENCODING);
                    response.setContentType(Constants.CONTENT_TYPE);
                    Cookie cookie = new Cookie(Constants.LOGINIDKEY, userSession.getSsoUser().getLoginId());
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    cookie = new Cookie(Constants.UCENTERKEY, (String) tip.get(Constants.TICKET));
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
                // 只有普通登陆的时候才检查一下
                if (checkAdd) {
                    // 登陆的时候 如果DdbPeCustom为空的话就重建一个
                    if (userSession.getPeCustom() == null) {
                        userSession.setPeCustom(createPeCustom(userSession.getSsoUser()));
                    }
                }
            } else {
                throw new SdkException(Constants.UCENTER_ERROR);
            }
        } else {
            throw new SdkException(Constants.UCENTER_ERROR);
        }
        final UserInfo userInfo = new UserInfo();
        userInfo.setLoginId(userSession.getPeCustom().getLoginId());
        userInfo.setSex(userSession.getPeCustom().getFlagGender());
        userInfo.setTrueName(StringUtils.isBlank(userSession.getPeCustom().getNickName())
            ? userSession.getPeCustom().getTrueName() : userSession.getPeCustom().getNickName());
        final DdbPeLabel ddbPeLabel = peLabelMapper.getById(userSession.getPeCustom().getFkLabelId());
        if (ddbPeLabel != null) {
            userInfo.setGrade(ddbPeLabel.getName());
        }
        userInfo.setAge(userSession.getPeCustom().getAge());
        userInfo.setPhoto(FileUtils.getFullRequestPath(userSession.getSsoUser().getPhoto()));
        final DdbPrPenCustom penCustom = prPenCustomMapper.getByUserId(userSession.getPeCustom().getId());
        if (penCustom != null) {
            final DdbPePen pen = pePenMapper.getById(penCustom.getFkPenId());
            if (pen != null) {
                userInfo.setBindDevice(pen.getIdentifiaction());
                userInfo.setMacAddress(pen.getMacAddress());
            }
        }
        return userInfo;
    }

    private DdbPeCustom createPeCustom(SsoUser ssoUser) {
        final String mobile = ssoUser.getBindmobile();
        // 设置为学生角色
        ssoUser.setFkRoleId(Constants.ZERO);
        ssoUser.setTrueName(mobile);
        // 变更用户中心生成的loginId
        ssoUser.setLoginId(mobile);
        ssoUserMapper.update(ssoUser);
        final DdbPeCustom peCustom = new DdbPeCustom();
        peCustom.setId(CommUtil.genRecordKey());
        peCustom.setFkUserId(ssoUser.getId());
        peCustom.setTrueName(mobile);
        peCustom.setMobilephone(mobile);
        peCustom.setLoginId(mobile);
        peCustomMapper.create(peCustom);
        try {
            memCacheService
                .delete(CommUtil.getCacheKey(Constants.CACHE_USERSESSION_KEY_PREFIX + peCustom.getLoginId()));
        } catch (CacheException e) {
            e.printStackTrace();
        }
        return peCustom;
    }
}
