package com.zkxltech.device;

import com.sun.jna.Native;

public class Test {
	/**
	 * 获取设备信息
	 * @return
	 */
	@org.junit.Test
	public void get_device_info() {
		System.out.println(SerialDll.INSTANTCE.get_device_info());;
	}
	/**
	 * 开始无线绑定指令
	 * @param mode  1 无线手动绑定指令， 2无线自动绑定指令
	 * @param uid_str 答题器的 UID 列表
	 * @return
	 */
	@org.junit.Test
	public void wireless_bind_start() {
		System.out.println(SerialDll.INSTANTCE.wireless_bind_start(1, ""));
	}
	
	/**
	 * 开始无线绑定指令
	 * @param mode  1 无线手动绑定指令， 2无线自动绑定指令
	 * @param uid_str 答题器的 UID 列表
	 * @return
	 */
	@org.junit.Test
	public void get_wireless_bind_info() {
		System.out.println(SerialDll.INSTANTCE.get_wireless_bind_info());
	}
	/**
	 * 清除白名单
	 * @return
	 */
	@org.junit.Test
	public void clear_wl() {
		System.out.println(SerialDll.INSTANTCE.clear_wl());
	}
	/**
	 * 停止无线绑定
	 * @return
	 */
	@org.junit.Test
	public void wireless_bind_stop() {
		System.out.println(SerialDll.INSTANTCE.wireless_bind_stop());
	}
	/**
	 * 开始答题
	 * @return
	 */
	@org.junit.Test
	public void answer_start() {
		System.out.println(SerialDll.INSTANTCE.answer_start( 1, ""));
	}
	/**
	 * 获取作答结果
	 * @return
	 */
	@org.junit.Test
	public void get_answer_list() {
		System.out.println(SerialDll.INSTANTCE.get_answer_list());
	}
	/**
	 * 停止作答
	 * @return
	 */
	@org.junit.Test
	public void answer_stop() {
		System.out.println(SerialDll.INSTANTCE.answer_stop());
	}
	/**
	 * 设置信道
	 * @param rf_ch
	 * @return
	 */
	@org.junit.Test
	public void set_rf_ch() {
	//	System.out.println(SerialDll.INSTANTCE.set_rf_ch(90,110));
		System.out.println(SerialDll.INSTANTCE.get_device_info());
	}
	/**
	 * 设置发送功率
	 * @param tx_power
	 * @return
	 */
	@org.junit.Test
	public void set_tx_power() {
		System.out.println(SerialDll.INSTANTCE.set_tx_power(4));
	}
}
