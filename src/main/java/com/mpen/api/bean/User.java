/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.bean;

import java.io.Serializable;

/**
 * TODO 用户参数bean.
 * 
 * @author zyt
 *
 */
public final class User implements Serializable {
    private static final long serialVersionUID = 2599725682350032035L;
    private String action;
    private String userName;
    private String password;
    private String nickName;
    private Integer age;
    private String grade;
    private String school;
    private String sex;
    private String address;

    public final String getAddress() {
        return address;
    }

    public final void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNickName() {
        return nickName;
    }

    public Integer getAge() {
        return age;
    }

    public String getGrade() {
        return grade;
    }

    public String getSchool() {
        return school;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
