/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.bean;

import java.io.Serializable;

/**
 * TODO 用户信息返回bean.
 * 
 * @author zyt
 *
 */
public final class UserInfo implements Serializable {
    private static final long serialVersionUID = 5352246237843060452L;
    private String loginId;
    private String sex;
    private String trueName;
    private String bindDevice;
    private String grade;
    private String macAddress;
    private String photo;
    private String school;
    private Integer age;
    private Integer learnedDays;
    private Integer learnedBooks;
    private float learnedDurations;
    private Integer integral;

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getLearnedDays() {
        return learnedDays;
    }

    public Integer getLearnedBooks() {
        return learnedBooks;
    }

    public float getLearnedDurations() {
        return learnedDurations;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setLearnedDays(Integer learnedDays) {
        this.learnedDays = learnedDays;
    }

    public void setLearnedBooks(Integer learnedBooks) {
        this.learnedBooks = learnedBooks;
    }

    public void setLearnedDurations(float learnedDurations) {
        this.learnedDurations = learnedDurations;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getBindDevice() {
        return bindDevice;
    }

    public void setBindDevice(String bindDevice) {
        this.bindDevice = bindDevice;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
