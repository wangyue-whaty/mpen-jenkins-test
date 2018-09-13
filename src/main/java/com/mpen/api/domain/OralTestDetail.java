package com.mpen.api.domain;

import java.io.Serializable;
import java.time.Instant;

public class OralTestDetail implements Serializable {
    private static final long serialVersionUID = 6894673364057308314L;
    private int id;
    private String loginId;
    private String fkBookId;
    private String penId;
    private int num;
    private Instant uploadTime;
    private String recordingUrl;
    private String recognizeTxt;
    private double score;
    // 流利度
    private double fluency;
    // 完整度
    private double integrity;
    // 标准度
    private double pronunciation;
    private Instant answerPenTime;
    // 是否已处理0：未处理；1：已处理
    private int isDeal;
    // 由哪台服务处理
    private int shardNum;

    public double getFluency() {
        return fluency;
    }

    public void setFluency(double fluency) {
        this.fluency = fluency;
    }

    public double getIntegrity() {
        return integrity;
    }

    public void setIntegrity(double integrity) {
        this.integrity = integrity;
    }

    public double getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(double pronunciation) {
        this.pronunciation = pronunciation;
    }

    public int getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(int isDeal) {
        this.isDeal = isDeal;
    }

    public int getShardNum() {
        return shardNum;
    }

    public void setShardNum(int shardNum) {
        this.shardNum = shardNum;
    }

    public Instant getAnswerPenTime() {
        return answerPenTime;
    }

    public void setAnswerPenTime(Instant answerPenTime) {
        this.answerPenTime = answerPenTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public String getFkBookId() {
        return fkBookId;
    }

    public void setFkBookId(String fkBookId) {
        this.fkBookId = fkBookId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Instant getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Instant uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

    public String getRecognizeTxt() {
        return recognizeTxt;
    }

    public void setRecognizeTxt(String recognizeTxt) {
        this.recognizeTxt = recognizeTxt;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
