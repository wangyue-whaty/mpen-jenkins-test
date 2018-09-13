/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mpen.api.bean.User;
import com.mpen.api.bean.UserInfo;
import com.mpen.api.bean.UserSession;
import com.mpen.api.domain.SsoUser;
import com.mpen.api.exception.SdkException;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SsoUserService接口.
 *
 * @author kai
 *
 */
public interface SsoUserService {
    /**
     * 获取用户信息.
     * 
     */
    UserSession getUserSessionByLoginId(String loginId) throws SdkException;

    /**
     * 获取用户信息.
     * 
     */
    SsoUser getByLoginId(String loginId);

    /**
     * 获取用户信息.
     * 
     */
    SsoUser getById(String id);

    /**
     * 创建用户.
     * 
     */
    String create(SsoUser ssoUser);

    /**
     * 删除用户.
     * 
     */
    void delete(String id);

    /**
     * 登陆.
     * 
     */
    UserInfo login(User user, HttpServletRequest request, HttpServletResponse response) throws SdkException,
        JsonParseException, JsonMappingException, IOException;
}
