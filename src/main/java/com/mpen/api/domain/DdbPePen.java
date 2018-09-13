/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.domain;

import java.io.Serializable;
import java.util.Date;

/** 笔信息. */
public final class DdbPePen implements Serializable {
    private static final long serialVersionUID = 5089005053464014060L;
    private String id;
    /** 编号. */
    private String code;
    /** 名字. */
    private String name;
    /** 出厂标识. */
    private String identifiaction;
    /** MAC 地址. */
    private String macAddress;
    /** 是否激活的设备. */
    private String flagActivie;
    /** 创建时间. */
    private Date createDatetime;
    /** rom灰度升级标记. */
    private String item;
    /** app灰度升级标记. */
    private String label;
    /** app版本. */
    private String appVersion;
    /** rom版本. */
    private String romVersion;
    /** 是否绑定. */
    public String isBind;
    /** 是否是测试笔. */
    private String isTest;
    /** 是否允许打开adb. */
    private String penAdmit;
    private String publicKey;
    private String activeAdd;
    private Type type;

    /**
     * 笔类型
     *
     */
    public enum Type {
        ANDROID, LINUX
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getActiveAdd() {
        return activeAdd;
    }

    public void setActiveAdd(String activeAdd) {
        this.activeAdd = activeAdd;
    }

    public String getPenAdmit() {
        return penAdmit;
    }

    public void setPenAdmit(String penAdmit) {
        this.penAdmit = penAdmit;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getRomVersion() {
        return romVersion;
    }

    public void setRomVersion(String romVersion) {
        this.romVersion = romVersion;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getIsTest() {
        return isTest;
    }

    public void setIsTest(String isTest) {
        this.isTest = isTest;
    }

    public String getIsBind() {
        return isBind;
    }

    public void setIsBind(String isBind) {
        this.isBind = isBind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifiaction() {
        return identifiaction;
    }

    public void setIdentifiaction(String identifiaction) {
        this.identifiaction = identifiaction;
    }

    public String getFlagActivie() {
        return flagActivie;
    }

    public void setFlagActivie(String flagActivie) {
        this.flagActivie = flagActivie;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

}