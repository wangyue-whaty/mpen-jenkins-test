package com.mpen.api.domain;

import java.io.Serializable;

import com.google.gson.reflect.TypeToken;
import com.mp.shared.common.Page;
import com.mp.shared.common.PageInfo;
import com.mpen.api.common.Constants;

public class DdbPageDetail implements Serializable, CacheInfos<PageInfo> {
    private static final long serialVersionUID = -6256558683925540523L;
    private int id;
    private String pageInfos;
    private String version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPageInfos() {
        return pageInfos;
    }

    public void setPageInfos(String pageInfos) {
        this.pageInfos = pageInfos;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public DdbPageDetail() {

    }

    public DdbPageDetail(Page<PageInfo> page) {
        this.pageInfos = Constants.GSON.toJson(page);
        this.version = page.getVersion().toString();
    }

    public Page<PageInfo> formDetail() {
        return Constants.GSON.fromJson(this.pageInfos, new TypeToken<Page<PageInfo>>() {
        }.getType());
    }
}
