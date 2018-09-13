package com.mp.shared.common;

import java.io.Serializable;

public final class FullBookInfo implements Serializable, IsValid {

    private static final long serialVersionUID = -6118003990310631645L;

    public BookInfo bookInfo;
    public int numPages; // how many pages in the book
    public int sequence; // 序列号
    public String isbn;
    public String photo;
    public String grade; // 适应年级
    public Type type; // 图书类型
    public String author; // 作者
    public String introduction;// 简介
    public String isPreDownload;// 是否为预下载书籍
    public boolean hasMpp; // 是否支持园丁系统

    public enum Type implements Serializable {
        STUDY("课本学习"), READ("课外阅读"), TEST("课后练习"), OTHER("其他");
        private String name;

        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 是否数据合法
     * 
     * @return
     */
    public boolean isValid() {
        return !(bookInfo == null || bookInfo.id == null || bookInfo.id.length() == 0 || bookInfo.version == null);
    }

}
