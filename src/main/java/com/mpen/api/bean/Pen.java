/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.bean;

import java.io.Serializable;

/**
 * TODO 笔资源参数bean.
 * 
 * @author zyt
 *
 */
public final class Pen implements Serializable {
    private static final long serialVersionUID = 81315594188457015L;
    private String action;
    private String identifiaction;
    private String macAddress;
    private String serialNumber;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIdentifiaction() {
        return identifiaction;
    }

    public void setIdentifiaction(String identifiaction) {
        this.identifiaction = identifiaction;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

}
