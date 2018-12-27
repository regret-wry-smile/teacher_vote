package com.zkxltech.domain;

/**
 * @author: ZhouWei
 * @date:2018年6月22日 上午11:40:21
 */
public class Answer {
    /**题id*/
    private String id;
    /**题类型*/
    private String type;
    /**作答范围*/
    private String range;
    /**答题后返回的答案*/
    private String answer;
    /**答题后返回的答案的时间*/
    private String answerClick;

    public String getAnswerClick() {
        return answerClick;
    }

    public void setAnswerClick(String answerClick) {
        this.answerClick = answerClick;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRange() {
        return range;
    }
    public void setRange(String range) {
        this.range = range;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    @Override
    public String toString() {
        return "Answer [id=" + id + ", type=" + type + ", range=" + range + ", answer=" + answer + "]";
    }
}
