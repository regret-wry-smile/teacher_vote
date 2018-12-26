package com.zkxltech.ui.enums;

import java.util.ArrayList;
import java.util.List;

public enum SettingEnum {
	ONE("1",1,51),
	TWO("2",2,52),
	THREEN("3",3,53),
	FOUR("4",3,53),
	FIVE("5",3,53),
	SIX("6",90,110),
	SEVEN("7",92,112),
	EIGHT("8",94,114),
	NINE("9",96,116),
	TEN("10",98,118);
	private String name;  //组名
	private Integer tx_ch;  //发送信道
	private Integer rx_ch; //接收信道
	
	SettingEnum(String name,Integer tx_ch,Integer rx_ch) { 
		this.name = name;  
        this.tx_ch = tx_ch;  
        this.rx_ch = rx_ch;  
    } 
	/**
	 * 根据名称获取信道
	 * @param name
	 * @return
	 */
	public static Integer getTxchByName(String name) {
		SettingEnum[] s = SettingEnum.values();
		for (SettingEnum settingEnum : s) {
			if (settingEnum.name.equals(name)) {
				return settingEnum.tx_ch;
			}
		}
		return null;
	}
	
	/**
	 * 根据名称获取发送功率
	 * @param name
	 * @return
	 */
	public static Integer getRxchByName(String name) {
		SettingEnum[] s = SettingEnum.values();
		for (SettingEnum settingEnum : s) {
			if (settingEnum.name.equals(name)) {
				return settingEnum.rx_ch;
			}
		}
		return null;
	}
	/**
	 * 获取组名
	 * @param tx_ch 信道
	 * @param tx_power 发送频率
	 * @return
	 */
	public static String getNameByTxchAndRxch(Integer tx_ch, Integer rx_ch) {
		SettingEnum[] s = SettingEnum.values();
		for (SettingEnum settingEnum : s) {
			if (settingEnum.tx_ch == tx_ch && settingEnum.rx_ch == rx_ch) {
				return settingEnum.name;
			}
		}
		return null;
	}
	
	/**
	 * 获取所有组名
	 */
	public static List<String> getAllName() {
		List<String> list = new ArrayList<String>();
		SettingEnum[] s = SettingEnum.values();
		for (SettingEnum settingEnum : s) {
			list.add(settingEnum.name);
		}
		return list;
	}
}