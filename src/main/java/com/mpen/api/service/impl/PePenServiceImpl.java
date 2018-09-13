/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mpen.api.bean.PenInfo;
import com.mpen.api.bean.PenMac;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.domain.DdbPenCmd;
import com.mpen.api.domain.DdbPenReadingLimit;
import com.mpen.api.domain.DdbPenSerialNumberRelationship;
import com.mpen.api.domain.EnumConst;
import com.mpen.api.exception.CacheException;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.EnumConstMapper;
import com.mpen.api.mapper.PePenMapper;
import com.mpen.api.mapper.PenCmdMapper;
import com.mpen.api.mapper.PenReadingLimitMapper;
import com.mpen.api.mapper.PenSerialNumberRelationshipMapper;
import com.mpen.api.service.MemCacheService;
import com.mpen.api.service.PePenService;
import com.mpen.api.util.CommUtil;

@Component
public class PePenServiceImpl implements PePenService {
    private static final Logger logger = LoggerFactory.getLogger(PePenServiceImpl.class);
    @Autowired
    private PePenMapper pePenMapper;
    @Autowired
    private EnumConstMapper enumConstMapper;
    @Autowired
    private PenSerialNumberRelationshipMapper penSerialNumberRelationshipMapper;
    @Autowired
    private MemCacheService memCacheService;
    @Autowired
    private PenReadingLimitMapper penReadingLimitMapper;
    @Autowired
    private PenCmdMapper penCmdMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean savePen(String identifiaction, PenInfo penInfo) throws SdkException {
        checkCode(identifiaction, penInfo.getCode());
        DdbPePen pen = null;
        pen = this.getPenByIdentifiaction(identifiaction);
        final EnumConst active = enumConstMapper.getByNamespaceCode(Constants.PEN_FLAG_ACTIVE, Constants.ONE);
        if (pen == null) {
            pen = new DdbPePen();
            pen.setId(CommUtil.genRecordKey());
            pen.setIdentifiaction(identifiaction);
            pen.setCode(identifiaction);
            pen.setName(identifiaction);
            pen.setMacAddress(penInfo.getMacAddress());
            pen.setFlagActivie(active.getId());
            pen.setCreateDatetime(new Date());
            pen.setActiveAdd(penInfo.getActiveAdd());
            pen.setPublicKey(penInfo.getPublicKey());
            
            pePenMapper.create(pen);
        } else {
            pen.setFlagActivie(active.getId());
            pen.setCreateDatetime(new Date());
            pePenMapper.update(pen);
        }
        return true;
    }

    /**
     * 参数校验 指定规则不匹配 抛出异常.
     * 
     */
    private void checkCode(String identifiaction, String code) throws SdkException {
        final String first = identifiaction.substring(0, 3);
        final String second = identifiaction.substring(3);
        final String string = DigestUtils.md5Hex(DigestUtils.md5Hex(second + first + second));
        if (!code.equals(string)) {
            throw new SdkException(Constants.WRONG_IDENTIFIACTION);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIdentification(String identifiaction) {
        pePenMapper.deleteByIdentifiaction(identifiaction);
    }

    @Override
    public Boolean adbAdmit(String identifiaction) throws SdkException {
        final DdbPePen pen = this.getPenByIdentifiaction(identifiaction);
        if (pen == null) {
            throw new SdkException(Constants.NO_MACHING_PEN);
        }
        if (Constants.ONE.equals(pen.getPenAdmit())) {
            return true;
        }
        return false;
    }

    @Override
    public List<PenMac> getUnBindPen(String macStr) throws SdkException {
        String[] macs = StringUtils.split(macStr, "__");
        if (macs.length == 0) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        final List<PenMac> penMacs = new ArrayList<PenMac>();
        for (String str : macs) {
            final String temp = str.replace(Constants.MPEN, "").replace(":", "");
            if (str == null || temp.length() != 12) {
                continue;
            }
            final String mac = temp.substring(0, 2) + ":" + temp.substring(2, 4) + ":" + temp.substring(4, 6) + ":"
                + temp.substring(6, 8) + ":" + temp.substring(8, 10) + ":" + temp.substring(10, 12);
            final DdbPePen pen = pePenMapper.getByMac(mac);
            if (pen == null) {
                continue;
            }
            // 验证笔是否激活状态
            if (!Constants.ONE.equals(enumConstMapper.getById(pen.getFlagActivie()).getCode())) {
                continue;
            }
            // 验证笔是否已绑定
            if (!Constants.ONE.equals(pen.getIsBind())) {
                PenMac penMac = new PenMac();
                penMac.setName(Constants.MPEN + "-" + mac.replace(":", "").substring(0, 6));
                penMac.setMacAndroid(mac);
                penMac.setMacIOS(str);
                final DdbPenSerialNumberRelationship serialNumber = penSerialNumberRelationshipMapper
                    .getVolidDataByPenId(pen.getId());
                if (serialNumber != null) {
                    penMac.setSerialNumber(serialNumber.getSerialNumber());
                }
                penMac.setPenType(pen.getType());
                penMacs.add(penMac);
            }
        }
        if (penMacs.size() == 0) {
            throw new SdkException(Constants.NO_MACHING_PEN);
        }
        return penMacs;
    }

    @Override
    public PenMac getCompleteMac(String str) throws SdkException {
        if (str == null || str.length() != 6) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        final String mac = str.substring(0, 2) + ":" + str.substring(2, 4) + ":" + str.substring(4, 6);
        final DdbPePen pen = pePenMapper.getByDefaultMac(mac + "%");
        if (pen == null) {
            throw new SdkException(Constants.NO_MACHING_PEN);
        }
        if (Constants.ONE.equals(pen.getIsBind())) {
            throw new SdkException(Constants.PEN_IS_BIND);
        }
        final PenMac penMac = new PenMac();
        penMac.setMacAndroid(pen.getMacAddress());
        return penMac;
    }

    @Override
    public DdbPePen getPenByIdentifiaction(String identifiaction) {
        if (StringUtils.isEmpty(identifiaction)) {
            return null;
        }
        final String key = CommUtil.getCacheKey(Constants.CACHE_PENINFO_KEY_PREFIX + identifiaction);
        DdbPePen pen = null;
        try {
            pen = this.memCacheService.get(key);
        } catch (CacheException e) {
            e.printStackTrace();
        }
        if (pen == null) {
            pen = pePenMapper.getByIdentifiaction(identifiaction);
            if (pen != null) {
                try {
                    this.memCacheService.set(key, pen, Constants.DEFAULT_CACHE_EXPIRATION);
                } catch (CacheException e) {
                    e.printStackTrace();
                }
            }
        }
        return pen;
    }

    /**
     * 虽然有可能会写入数据库，但是数据本身要求不那么精确；所以不需要使用Transactional开启事务
     */
    @Override
    public boolean checkReadingAllow(HttpServletRequest httpRequest) {
        final String penId = (String) httpRequest.getAttribute(Constants.PENKEY);
        DdbPePen pen;
        pen = getPenByIdentifiaction(penId);
        if (pen == null || StringUtils.isBlank(pen.getId())) {
            return false;
        }
        DdbPenReadingLimit penLimit = penReadingLimitMapper.getByPenId(pen.getId());
        if (penLimit != null && penLimit.getTimes() >= Constants.READING_LIMIT) {
            return false;
        }
        if (!httpRequest.getRequestURI().contains("/v1/books/")) {
            if (penLimit == null) {
                penLimit = new DdbPenReadingLimit();
                penLimit.setId(CommUtil.genRecordKey());
                penLimit.setEditDate(Instant.now());
                penLimit.setFkPenId(pen.getId());
                penLimit.setTimes(1);
                penReadingLimitMapper.save(penLimit);
            } else {
                penLimit.setTimes(penLimit.getTimes() + 1);
                penLimit.setEditDate(Instant.now());
                penReadingLimitMapper.updateTimesByPenId(penLimit);
            }
        }
        return true;
    }

    @Override
    public DdbPenCmd getPenCmd(String penId, String cmdId, String result) throws SdkException {
        final DdbPePen pen = getPenByIdentifiaction(penId);
        if (pen == null) {
            throw new SdkException(Constants.NO_MACHING_PEN);
        }
        // 前一次执行命令的id
        if (StringUtils.isNotBlank(cmdId)) {
            penCmdMapper.updateStatusById(cmdId, result);
        }
        return penCmdMapper.getByPenId(pen.getId());
    }

    @Override
    public boolean updateCmdUrl(String comdId, String url, String description) {
        penCmdMapper.updateUrlById(comdId, url, description);
        return true;
    }

    @Override
    public boolean checkAppVersion(HttpServletRequest request) {
        final String penId = (String) request.getAttribute(Constants.PENKEY);
        if (StringUtils.isBlank(penId)) {
            return false;
        }
        final DdbPePen pen = getPenByIdentifiaction(penId);
        final DdbPePen.Type penType = pen.getType() == null ? DdbPePen.Type.ANDROID : pen.getType();
        if (pen == null || (penType == DdbPePen.Type.ANDROID && (StringUtils.isBlank(pen.getAppVersion())
            || pen.getAppVersion().compareTo(Constants.LIMIT_APP_VERSION) < 0))) {
            return false;
        }
        return true;
    }

}
