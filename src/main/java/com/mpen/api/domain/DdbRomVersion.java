/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.domain;

import java.io.Serializable;
import java.util.Date;

public final class DdbRomVersion implements Serializable {
    private static final long serialVersionUID = -7553969264084422654L;
    private String id;
    // 版本名称
    private String name;
    // 版本等级
    private Integer level;
    // 创建时间
    private Date createtime;

    private DdbPePen.Type type;

    public DdbPePen.Type getType() {
        return type;
    }

    public void setType(DdbPePen.Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

}
