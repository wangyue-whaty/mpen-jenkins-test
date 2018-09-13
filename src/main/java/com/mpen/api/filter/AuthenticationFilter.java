/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.filter;

import com.mp.shared.common.NetworkResult;
import com.mpen.api.bean.UserSession;
import com.mpen.api.common.Constants;
import com.mpen.api.common.Uris;
import com.mpen.api.exception.SdkException;
import com.mpen.api.service.PePenService;
import com.mpen.api.service.SsoUserService;
import com.mpen.api.util.CommUtil;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String[] URLS = { "http://www.mpen.com.cn", "https://www.mpen.com.cn", "http://ddb.webtrn.cn",
        "https://ddb.webtrn.cn", "http://code.mpen.com.cn", "https://code.mpen.com.cn", "http://api.mpen.com.cn",
        "https://api.mpen.com.cn" };

    @Value("${web.allow-urls}")
    private String allowUrls;
    @Value("${web.disallow-urls}")
    private String disallowUrls;
    @Autowired
    private SsoUserService ssoUserService;
    @Autowired
    private PePenService pePenService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        // 设置禁止缓存
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setCharacterEncoding("utf8");
        NetworkResult<Object> result = null;
        String msg = checkRequestAccess(httpRequest);
        if (!Constants.SUCCESS.equals(msg)) {
            result = new NetworkResult<>(NetworkResult.BAD_REQUEST_ERROR_CODE, msg);
            logger.error(result.getErrorMsg(), httpRequest.getRequestURL());
            ((HttpServletResponse) response).setStatus(200);
            response.getWriter().write(Constants.GSON.toJson(result));
            return; // 直接返回
        }
        if (!checkUriAccess(httpRequest)) {
            logger.error(Constants.NO_MACHING_ERROR_MSG, httpRequest.getRequestURL());
            ((HttpServletResponse) response).setStatus(404);
            result = new NetworkResult<>(NetworkResult.NO_MACHING_ERROR_CODE, NetworkResult.NO_MACHING_ERROR_MSG);
            response.getWriter().write(Constants.GSON.toJson(result));
            return; // 直接返回
        }
        if (checkLoginAccess(httpRequest)) {
            chain.doFilter(httpRequest, response);
        } else {
            logger.error(Constants.INVALID_LOGINID_ERROR, httpRequest.getRequestURL());
            ((HttpServletResponse) response).setStatus(403);
            result = new NetworkResult<>(NetworkResult.ACCESS_FORBIDDEN_ERROR_CODE, Constants.NO_MATCHING_USER);
            response.getWriter().write(Constants.GSON.toJson(result));
            return; // 直接返回
        }
    }

    private boolean checkLoginAccess(HttpServletRequest httpRequest) {
        final String loginId = CommUtil.getLoginId(httpRequest);
        if (isInNoNeedLoginList(httpRequest)) {
            return true;
        }
        UserSession userSession = null;
        try {
            userSession = ssoUserService.getUserSessionByLoginId(loginId);
        } catch (SdkException sdkException) {
            logger.error("", sdkException);
        }
        if (userSession != null && StringUtils.isNotBlank(userSession.getLoginId())) {
            httpRequest.setAttribute(Constants.LOGINIDKEY, loginId);
            return true;
        }
        return checkReadingAllow(httpRequest);
    }

    /**
     * 未绑定时，默认允许云点读100次.
     * 
     */
    private boolean checkReadingAllow(HttpServletRequest httpRequest) {
        if (isInMustLoginList(httpRequest)) {
            return false;
        }
        return pePenService.checkReadingAllow(httpRequest);
    }

    /**
     * TODO 校验是否在开放接口白名单中
     * 
     */
    private boolean isInNoNeedLoginList(HttpServletRequest httpRequest) {
        // TODO 临时写死，之后改为配置文件
        final String uri = httpRequest.getRequestURI();
        return uri.startsWith("/v1/publishing") || uri.startsWith("/v1/qa") || uri.startsWith("/v1/files")
            || uri.startsWith("/v1/user/login") || uri.startsWith("/v1/mobiles/app") || uri.startsWith("/v1/questions")
            || (uri.startsWith("/v1/pens/") && (Constants.ADB_ADMIT.equals(httpRequest.getParameter("action"))
                || Constants.UPGRADE_APP.equals(httpRequest.getParameter("action"))
                || Constants.UPGRADE_ROM.equals(httpRequest.getParameter("action"))))
            || (uri.startsWith("/v1/logs/dataAnalysis") && "https".equals(CommUtil.getScheme(httpRequest)))
                && Constants.ACCESS_CONTROL_KEY.equals(httpRequest.getParameter("key"))
            || uri.startsWith(Uris.V1_MEMECHACHE) || uri.startsWith(Uris.V1_QUESTION) || uri.startsWith(Uris.V1_SHOPS)
            || uri.equals(Uris.V1_LOGS + "/");
    }

    /**
     * TODO 校验是否在必须绑定接口黑名单中
     * 
     */
    private boolean isInMustLoginList(HttpServletRequest httpRequest) {
        final String uri = httpRequest.getRequestURI();
        return uri.equals("/v1/messages/")
            || (uri.startsWith("/v1/pens/") && (Constants.CHECK_BIND.equals(httpRequest.getParameter("action"))))
            || uri.equals("/v1/pens/") || (uri.startsWith(Uris.V1_USER) && !uri.contains(Uris.LOGIN));

    }

    /**
     * TODO 校验请求是否在允许的配置文件中
     * 
     */
    private boolean checkUriAccess(HttpServletRequest httpRequest) {
        final String uri = httpRequest.getRequestURI();
        if (StringUtils.isNotBlank(disallowUrls) && matchPattern(uri, disallowUrls)) {
            return false;
        }
        if (StringUtils.isNotBlank(allowUrls) && !matchPattern(uri, allowUrls)) {
            return false;
        }
        return true;
    }

    private boolean matchPattern(String uri, String urlPatterns) {
        final String[] urlArray = urlPatterns.split(",");
        for (String url : urlArray) {
            if (uri.startsWith(url)) {
                return true;
            }
        }
        return false;
    }

    private String checkRequestAccess(HttpServletRequest request) {
        // 验证来源
        final String referer = request.getHeader("Referer");
        // TODO 为了兼容之前的，允许referer为空情况，以后去除
        if (StringUtils.isBlank(referer)) {
            return Constants.SUCCESS;
        }
        // TODO 验证来源是否为网页端ajax请求，以后增加user-agent
        for (String url : URLS) {
            if (referer.startsWith(url)) {
                return Constants.SUCCESS;
            }
        }
        if (!"mpenAndroid".equalsIgnoreCase(referer) && !"mpenIOS".equalsIgnoreCase(referer)
            && !"mpenPen".equalsIgnoreCase(referer) && !"mpenPublish".equalsIgnoreCase(referer)) {
            return Constants.WRONG_REFERER;
        }
        final String userAgent = request.getHeader("User-Agent");
        return CommUtil.checkUserAgent(userAgent);
    }

    @Override
    public void destroy() {
    }

}
