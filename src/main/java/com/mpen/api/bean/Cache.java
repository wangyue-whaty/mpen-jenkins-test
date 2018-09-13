package com.mpen.api.bean;

public final class Cache {
    private String key;
    private String bookId;
    private String loginId;
    private String id;

    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public final String getLoginId() {
        return loginId;
    }

    public final void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
