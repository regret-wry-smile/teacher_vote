package com.zkxltech.domain;

import java.util.List;

/***
 * 评分
 * @author zkxl
 *
 */
public class Score {
	/**
	 * 评分主题编号
	 */
	private Integer id;
	/**
	 * 评分主题编号
	 */
	private String testId;
	/**
	 * 评分主题
	 */
	private String  title;
	/**
	 * 评分描述
	 */
	private String  describe;
	/**
	 * 评分对象
	 * @return
	 */
	private List<String> programs;
	/**
	 * 评分对象
	 * @return
	 */
	private StringBuffer program;
	/**
	 * 评分对象个数
	 * @return
	 */
	private int programNum;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public List<String> getPrograms() {
		return programs;
	}
	public void setPrograms(List<String> programs) {
		this.programs = programs;
	}
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
	public StringBuffer getProgram() {
		return program;
	}
	public void setProgram(StringBuffer program) {
		this.program = program;
	}
	public int getProgramNum() {
		return programNum;
	}
	public void setProgramNum(int programNum) {
		this.programNum = programNum;
	}
}
