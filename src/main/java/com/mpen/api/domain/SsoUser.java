/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.domain;

import java.io.Serializable;
import java.util.Date;

public final class SsoUser implements Serializable {

    private static final long serialVersionUID = 4400049200793253822L;

    private String id;

    private String loginId;

    private String password;

    /**
     * 户用姓名.
     */
    private String trueName;

    private String fkRoleId;

    /**
     * ENUMCONST是否有效.
     */
    private String flagIsvalid;

    private String flagBak;

    /**
     * 登录次数.
     */
    private Integer loginNum;

    /**
     * 最后登录时间.
     */
    private Date lastLoginDate;

    /**
     * 最后登录IP.
     */
    private String lastLoginIp;

    private String flagUserType;

    private String flagSystem;

    private String nickName;

    private String onlineTime;

    private String photo;

    private String mobileAlias;

    /**
     * 备用唯一标识字段（目前河南用到）.
     */
    private String token;

    private String guid;

    private String mobile;

    private String weiboidentifier;

    private String qqidentifier;

    private String weixinidentifier;

    private Date updateDate;

    private Date createDate;

    private String sitecode;

    private String email;

    private String bindmobile;

    private String bindemail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getFkRoleId() {
        return fkRoleId;
    }

    public void setFkRoleId(String fkRoleId) {
        this.fkRoleId = fkRoleId;
    }

    public String getFlagIsvalid() {
        return flagIsvalid;
    }

    public void setFlagIsvalid(String flagIsvalid) {
        this.flagIsvalid = flagIsvalid;
    }

    public String getFlagBak() {
        return flagBak;
    }

    public void setFlagBak(String flagBak) {
        this.flagBak = flagBak;
    }

    public Integer getLoginNum() {
        return loginNum;
    }

    public void setLoginNum(Integer loginNum) {
        this.loginNum = loginNum;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getFlagUserType() {
        return flagUserType;
    }

    public void setFlagUserType(String flagUserType) {
        this.flagUserType = flagUserType;
    }

    public String getFlagSystem() {
        return flagSystem;
    }

    public void setFlagSystem(String flagSystem) {
        this.flagSystem = flagSystem;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMobileAlias() {
        return mobileAlias;
    }

    public void setMobileAlias(String mobileAlias) {
        this.mobileAlias = mobileAlias;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWeiboidentifier() {
        return weiboidentifier;
    }

    public void setWeiboidentifier(String weiboidentifier) {
        this.weiboidentifier = weiboidentifier;
    }

    public String getQqidentifier() {
        return qqidentifier;
    }

    public void setQqidentifier(String qqidentifier) {
        this.qqidentifier = qqidentifier;
    }

    public String getWeixinidentifier() {
        return weixinidentifier;
    }

    public void setWeixinidentifier(String weixinidentifier) {
        this.weixinidentifier = weixinidentifier;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSitecode() {
        return sitecode;
    }

    public void setSitecode(String sitecode) {
        this.sitecode = sitecode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBindmobile() {
        return bindmobile;
    }

    public void setBindmobile(String bindmobile) {
        this.bindmobile = bindmobile;
    }

    public String getBindemail() {
        return bindemail;
    }

    public void setBindemail(String bindemail) {
        this.bindemail = bindemail;
    }

}