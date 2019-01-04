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
	 * 当前班级学生信息(已签到学生)
	 */
	public static List<StudentInfo> studentInfos;
	
	/**
	 * 当前班级学生信息(所有学生)
	 */
	public static List<StudentInfo> allStudentInfos;
	
	public static ClassHour classHour;
	/**
	 * 当前设备绑定的所有答题器编号<进入设置页面后同步设备和数据库绑定状态时保存到该处,以便后期修改学生的答题器编号时进行查询更新>
	 */ 
	/**
	 * 设备连接状态(当设备连接状态改变的时候该变量会相应改变)
	 */
	public static boolean isEquipmentStatus = false;
	
	public static String modeMsg = Constant.BUSINESS_NORMAL;
	
	private static List<String> iclickerIds;
	
	public static List<String> getIclickerIds() {
        return iclickerIds;
    }

    public static void setIclickerIds(List<String> iclickerIds) {
        Global.iclickerIds = iclickerIds;
    }

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

	public static String getModeMsg() {
		return modeMsg;
	}

	public static void setModeMsg(String modeMsg) {
		Global.modeMsg = modeMsg;
	}

	public static List<StudentInfo> getAllStudentInfos() {
		return allStudentInfos;
	}

	public static void setAllStudentInfos(List<StudentInfo> allStudentInfos) {
		Global.allStudentInfos = allStudentInfos;
	}


	
	
	
}
