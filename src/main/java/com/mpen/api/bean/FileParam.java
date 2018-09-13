package com.mpen.api.bean;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import com.mpen.api.domain.PersistenceFile;

public class FileParam implements Serializable {
    private static final long serialVersionUID = 2923685353662271170L;
    private String action;
    private String uuid;
    private String filePath;
    private MultipartFile file;
    private String description;
    private int num;
    private long time;

    private PersistenceFile.ProjectType progect;
    private String type;
    // 时间戳（ms）
    private long createTime;
    private int priority;
    private String version;
    private String user;
    private String optional;

    public PersistenceFile.ProjectType getProgect() {
        return progect;
    }

    public void setProgect(PersistenceFile.ProjectType progect) {
        this.progect = progect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
