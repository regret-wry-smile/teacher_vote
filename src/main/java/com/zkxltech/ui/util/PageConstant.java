package com.zkxltech.ui.util;

import org.eclipse.swt.browser.Browser;

public class PageConstant {
	/**
	 * 浏览器对象
	 */
	public static Browser browser;
	/********页面路径常量*******/
	private final static String COMMON_URL = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/" + "htmls";
	/**
	 * 调试页面路径
	 */
	public final static String TEST_PAGE_URL = COMMON_URL+"/myTest.html";
	/**
	 * 答题页面类型
	 */
	public static String PAGE_ANSWER_URL_START = COMMON_URL+"/page/answermoudle/answerCenter.html";
	
	/**
	 * 答题页面类型
	 */
	public static String PAGE_ANSWER_URL_NO_START = COMMON_URL+"/page/answermoudle/startAnswer.html";
	/**
	 * 投票答题页面类型
	 */
	public static String VOTE_ANSWER_URL_START = COMMON_URL+"/page/answermoudle/votemoudle.html";
	
	/**
	 * 设置页面类型
	 */
	//public static String PAGE_SET_URL = COMMON_URL+"/page/setmodule/setmodule.html";
	public static String PAGE_SET_URL = COMMON_URL+"/page/setmodule/login.html";
	//public static String PAGE_SET_URL = COMMON_URL+"/page/test01.html";
	/**
	 * 记录页面类型
	 */
	public static String PAGE_RECORD_URL = COMMON_URL+"/page/resultmoudle/answerRecord.html";
	
	/********图片路径常量*******/
	public static String imagePack = "/com/zkxltech/image";
	/**
	 * 头部左上角图片
	 */
	public static String titleImg = imagePack + "/logoxk.png";
	
	/**
	 * 悬浮框默认图片
	 */
	public static String image01 = imagePack + "/答题-悬浮控件_23.png";
	/**
	 * 悬浮后图片
	 */
	public static String image02 = imagePack + "/答题-悬浮控件_13-23.png";
	/**
	 * 关闭图片(未选中)
	 */
	public static String select_close = imagePack + "/答题-悬浮控件_13.png";
	/**
	 * 答题图片(未选中)
	 */
	public static String select_answer = imagePack + "/答题-悬浮控件_13-16.png";
	/**
	 * 设置图片(未选中)
	 */
	public static String select_set = imagePack + "/答题-悬浮控件_13-18.png";
	/**
	 * 记录图片(未选中)
	 */
	public static String select_record = imagePack + "/答题-悬浮控件_13-20.png";
	/**
	 * 关闭图片(选中)
	 */
	public static String select_close02 = imagePack + "/答题-悬浮控件_13(2).png";
	/**
	 * 答题图片(选中)
	 */
	public static String select_answer02 = imagePack + "/答题-悬浮控件_13-16(2).png";
	/**
	 * 设置图片(选中)
	 */
	public static String select_set02 = imagePack + "/答题-悬浮控件_13-18(2).png";
	/**
	 * 记录图片(选中)
	 */
	public static String select_record02 = imagePack + "/答题-悬浮控件_13-20(2).png";
	/**
	/**
	 * 白色框关闭图片
	 */
	public static String close_white = imagePack + "/close_white.png";
	
	/**
	 * 黑色关闭图片
	 */
	public static String close_black = imagePack + "/guanbi.png";
	
	/**
	 * 最大化
	 */
	public static String max_white = imagePack + "/max_white.png";
	/**
	 * 最小化——黑色
	 */
	public static String min_black = imagePack + "/min_black.png";
	
}
