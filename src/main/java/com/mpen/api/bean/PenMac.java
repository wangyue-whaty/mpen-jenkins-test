/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.bean;

import java.io.Serializable;

import com.mpen.api.domain.DdbPePen.Type;

public final class PenMac implements Serializable {
    private static final long serialVersionUID = 2998484489521683047L;
    private String name;
    private String macAndroid;
    private String macIOS;
    private String serialNumber;
    private Type penType;

    public final Type getPenType() {
        return penType;
    }

    public final void setPenType(Type penType) {
        this.penType = penType;
    }

    public final String getSerialNumber() {
        return serialNumber;
    }

    public final void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAndroid() {
        return macAndroid;
    }

    public void setMacAndroid(String macAndroid) {
        this.macAndroid = macAndroid;
    }

    public String getMacIOS() {
        return macIOS;
    }

    public void setMacIOS(String macIOS) {
        this.macIOS = macIOS;
    }
}
