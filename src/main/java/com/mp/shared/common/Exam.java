package com.mp.shared.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.mp.shared.utils.Json;

public final class Exam implements Serializable {
    private static final long serialVersionUID = 1184171126499764147L;
    public int num;
    // 试卷总分
    public int totalPoints;
    // 试卷名称
    public String name;
    // 试题
    public ArrayList<Topic> topic;

    public static final class Topic implements Serializable {
        private static final long serialVersionUID = 6725410462561433010L;
        // 大题标题
        public String title;
        public ArrayList<SubTopic> subTopic;
    }

    public static final class SubTopic implements Serializable {
        private static final long serialVersionUID = 2850990710903722563L;
        public int examNum;
        public int num;
        // 分数
        public int points;
        // 问题
        public String question;
        // 答题限制时间
        public int limitTimeSec;
        // 参考答案
        public ArrayList<String> refAnswer;
        // 标准答案
        public StaAnswer staAnswer;
        // 题目类型 0：朗读题；1：对话题
        public int type;

        public String getAnswer() {
            return staAnswer == null ? refAnswer.get(0) : Json.GSON.toJson(staAnswer);
        }

    }

    /**
     * 云之声对话题要求数据格式， 由工具http://101.231.106.182:5000/#/生成
     *
     */
    public static final class StaAnswer implements Serializable {
        private static final long serialVersionUID = 555236596899040330L;
        public String Version;
        public String DisplayText;
        public String GrammarWeight;
        public String Grammar;
    }

}
