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
	public static String PAGE_ANSWER_URL = COMMON_URL+"/answer.html";
	/**
	 * 设置页面类型
	 */
	public static String PAGE_SET_URL = COMMON_URL+"/set.html";
	/**
	 * 记录页面类型
	 */
	public static String PAGE_RECORD_URL = COMMON_URL+"/record.html";
	
	/********图片路径常量*******/
	public static String imagePack = "/com/zkxltech/image";
	/**
	 * 头部左上角图片
	 */
	public static String titleImg = imagePack + "/title.png";
	
	/**
	 * 悬浮框默认图片
	 */
	public static String image01 = imagePack + "/答题-悬浮控件_23.png";
	/**
	 * 悬浮后图片
	 */
	public static String image02 = imagePack + "/答题-悬浮控件_13-23.png";
	/**
	 * 答题图片
	 */
	public static String answer_img = imagePack + "/答题-悬浮控件_13-16.png";
	
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
