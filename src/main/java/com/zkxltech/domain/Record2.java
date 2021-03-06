package com.zkxltech.domain;

import java.util.List;

public class Record2 {
	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 班级id。这个字段现在改为组
	 */
	private String classId;
	/**
	 * 科目。这个字段现在改为场景
	 */
	private String subject;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 课时表对应的主键。这个字段现在改为备注
	 */
	private String classHourId;
	/**
	 * 试卷id。这个字段用于评分中的主题。又改为存class_hour主键。class_hour_name为存备注
	 */
	private String testId;
	/**
	 * 题目id
	 */
	private String questionId;
	/**
	 * 题目名称
	 */
	private String question;
	/**
	 * 对象名称
	 */
	private String questionName;
	/**
	 * 1-6对应的英文名
	 */
	private String questionShow;
	/**
	 * 题目类型  1-答题字母  2-答题数字 3-答题判断  4-答题多选  5-投票  6-评分
	 */
	private String questionType;
	/**
	 * 答题器编号
	 */
	private String iclickerId;
	/**
	 * 学生id
	 */
	private String studentId;
	/**
	 * 学生名称
	 */
	private String studentName;
	/**
	 * 做题答案
	 */
	private String answer;
	/**
	 * 开始上课时间
	 */
	private String answerStart;
	/**
	 * 答题器点击提交时间
	 */
	private String answerClick;
	/**
	 * 停止答题时间
	 */
	private String answerEnd;
	/**
	 * 多选范围
	 */
	private String range;
	/**
	 * 
	 */
	private List<String> studentIds;
	/**
	 * 主键集合
	 */
	private List<Integer> Ids;
	
	private List<Record2> datalists;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAnswerStart() {
		return answerStart;
	}
	public void setAnswerStart(String answerStart) {
		this.answerStart = answerStart;
	}
	public String getAnswerEnd() {
		return answerEnd;
	}
	public void setAnswerEnd(String answerEnd) {
		this.answerEnd = answerEnd;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getClassHourId() {
		return classHourId;
	}
	public void setClassHourId(String classHourId) {
		this.classHourId = classHourId;
	}
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswerClick() {
		return answerClick;
	}
	public void setAnswerClick(String answerClick) {
		this.answerClick = answerClick;
	}
	public List<String> getStudentIds() {
		return studentIds;
	}
	public void setStudentIds(List<String> studentIds) {
		this.studentIds = studentIds;
	}
	public String getIclickerId() {
		return iclickerId;
	}
	public void setIclickerId(String iclickerId) {
		this.iclickerId = iclickerId;
	}
	public String getQuestionName() {
		return questionName;
	}
	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}
	public String getQuestionShow() {
		return questionShow;
	}
	public void setQuestionShow(String questionShow) {
		this.questionShow = questionShow;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public List<Integer> getIds() {
		return Ids;
	}
	public void setIds(List<Integer> ids) {
		Ids = ids;
	}
	
	public List<Record2> getDatalists() {
		return datalists;
	}
	public void setDatalists(List<Record2> datalists) {
		this.datalists = datalists;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
}
