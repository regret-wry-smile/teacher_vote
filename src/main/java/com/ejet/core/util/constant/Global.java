package com.ejet.core.util.constant;

import java.util.List;

import com.zkxltech.domain.ClassHour;
import com.zkxltech.domain.ClassInfo;
import com.zkxltech.domain.StudentInfo;

public class Global {
	/**
	 * 当前班级id
	 */
	public  static String classId;
	/**
	 * 当前班级信息
	 */
	public static ClassInfo classInfo;
	
	/**
	 * 当前班级学生信息
	 */
	public static List<StudentInfo> studentInfos;
	
	public static ClassHour classHour;

	public static ClassInfo getClassInfo() {
		return classInfo;
	}

	public static void setClassInfo(ClassInfo classInfo) {
		Global.classInfo = classInfo;
	}

	public static List<StudentInfo> getStudentInfos() {
		return studentInfos;
	}

	public static void setStudentInfos(List<StudentInfo> studentInfos) {
		Global.studentInfos = studentInfos;
	}

	public static ClassHour getClassHour() {
		return classHour;
	}

	public static void setClassHour(ClassHour classHour) {
		Global.classHour = classHour;
	}

	public static String getClassId() {
		return classId;
	}

	public static void setClassId(String classId) {
		Global.classId = classId;
	}


	
	
	
}
