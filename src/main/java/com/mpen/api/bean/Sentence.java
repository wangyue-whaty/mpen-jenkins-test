/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.bean;

import java.io.Serializable;

public final class Sentence implements Serializable {
    private static final long serialVersionUID = 5485620821548159196L;
    private String title;
    private int number;
    private float score;

    public String getTitle() {
        return title;
    }

    public int getNumber() {
        return number;
    }

    public float getScore() {
        return score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
