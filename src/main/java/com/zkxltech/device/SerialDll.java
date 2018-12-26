package com.zkxltech.device;

import com.sun.jna.Library;
import com.sun.jna.Native;
/**
 * dll文件对应的函数
 * 返回int类型的 0 成功，1失败
 * @author zkxl
 *
 */
public interface SerialDll extends Library  {
	SerialDll INSTANTCE = (SerialDll) Native.loadLibrary("SerialDll", SerialDll.class);
	/**
	 * 获取设备信息
	 * @return
	 */
	String get_device_info();
	/**
	 * 清除白名单
	 * @return
	 */
	int clear_wl();
	/**
	 * 开始无线绑定指令
	 * @param mode  1 无线手动绑定指令， 2无线自动绑定指令
	 * @param uid_str 答题器的 UID 列表
	 * @return
	 */
	int wireless_bind_start( int mode, String uid_str );
	/**
	 * 获取无线绑定上报结果
	 * @return
	 */
	String get_wireless_bind_info();
	/**
	 * 停止无线绑定
	 * @return
	 */
	int wireless_bind_stop( );
	
	/**
	 * 开始答题
	 * @return
	 */
	int answer_start( int is_quick_response, String answer_str );
	/**
	 * 获取作答结果
	 * @return
	 */
	String get_answer_list();
	/**
	 * 停止作答
	 * @return
	 */
	int answer_stop( );
	/**
	 * 设置信道
	 * @param tx_ch 发送信道
	 * @param rx_ch 接收信道
	 * @return
	 */
	int set_rf_ch(int rf_ch);
	/**
	 * 设置发送功率
	 * @param tx_power
	 * @return
	 */
	int set_tx_power( int tx_power );
}
