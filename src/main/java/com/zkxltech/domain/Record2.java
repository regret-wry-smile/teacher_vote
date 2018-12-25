package com.zkxltech.domain;

public class Record2 {
	private Integer id;
	private String classId;
	private String subject;
	private String classHourId;
	private String testId;
	private String questionId;
	private String question;
	private String questionType;
	private String studentId;
	private String studentName;
	private String answer;
	private String answerStart;
	private String answerClick;
	private String answerEnd;
	
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
	
}
