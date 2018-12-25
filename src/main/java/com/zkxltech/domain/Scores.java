package com.zkxltech.domain;

public class Scores {
    /**
     * 评分主题编号
     */
    private String id;
    /**
     * 评分主题
     */
    private String  title;
    /**
     * 评分描述
     */
    private String  describe;
    /**
     * 节目名称
     */
    private String program;
    /**
     * 总分
     */
    private Integer total;
    /**
     * 评分人数
     */
    private Integer peopleSum;

    /**
     * 平均分
     */
    private String average;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPeopleSum() {
        return peopleSum;
    }

    public void setPeopleSum(Integer peopleSum) {
        this.peopleSum = peopleSum;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }
}

