package com.zkxltech.config;

/**
 * 全局变量信息
 * 
 * @author ShenYijie
 *
 */
public class Global {
	/**
	 * 版本信息
	 */
	public static final String VERSION = "vote-v1.0.0.1";
	/**
	 * 是否教师端
	 */
	private static boolean isTeacher = false;
	/**
	 * 是否同步到教师端
	 */
	private static boolean synToTeacher = true;
	/**
	 * 随机生成客户端唯一ID
	 */
	private static String clientId = null;
	/**
	 * 停止答题
	 */
	private static boolean isStopAnswer = false;
	/**
	 * 当前答题类型
	 */
	private static String answerType;
	
	
	public static boolean isTeacher() {
		if(ConfigConstant.projectConf.getApp_type()!=null &&
				ConfigConstant.projectConf.getApp_type().equals("T")) {
			isTeacher = true;
		}
		return isTeacher;
	}
	
	public static boolean isSynToTeacher() {
		if(ConfigConstant.projectConf.getApp_sync()!=null &&
				ConfigConstant.projectConf.getApp_sync().toLowerCase().equals("false")) {
			synToTeacher = false;
		}
		return synToTeacher;
	}

	public static boolean isStopAnswer() {
		return isStopAnswer;
	}

	public static void setStopAnswer(boolean isStopAnswer) {
		Global.isStopAnswer = isStopAnswer;
	}

	/**
	 * 获取客户端随机UUID
	 */
	public static String getClientUUID() {
		if(clientId==null) {
			clientId = java.util.UUID.randomUUID().toString();
			clientId = clientId.replaceAll("-", "");
		}
		return clientId;
	}

	public static String getAnswerType() {
		return answerType;
	}

	public static void setAnswerType(String answerType) {
		Global.answerType = answerType;
	}
	
	
	

}
