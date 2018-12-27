package com.zkxltech.service;

import com.zkxltech.domain.Result;

public interface ClassHourService {
	/**
	 * 开始上课
	 */
	Result startClass (Object classHour);
	/**
	 * 查询课程列表
	 * @param classId 班级id
	 * @param subjectName 科目名称
	 * @return
	 */
	Result selectClassInfo(Object classId, Object subjectName);
	/**
	 * 新增课程
	 * @param object classhour对象
	 * @return
	 */
	Result insertClassInfo(Object object);
	/**
	 * 删除课程
	 * @param object classhour对象
	 * @return
	 */
	Result deleteClassInfo(Object object);
	
	/**
	 * 更新课程时间
	 * @param object classhour对象
	 * @return
	 */
	Result updateClassInfoTime(Object object);
	
	
	/**
	 * 下课
	 */
	Result endClass();
	
	/**
	 * 获取当前班级信息
	 * @return
	 */
	Result getClassInfo();

	Result getSubject(Object classHourObj);
}
