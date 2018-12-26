package com.zkxltech.device;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.core.util.constant.EquipmentConstant;
import com.zkxltech.domain.Answer;

/**
 * 硬件指令封装接口
 * @author zkxl
 *
 */
public class DeviceComm {
	private static final Logger log = LoggerFactory.getLogger(DeviceComm.class);
	/**
	 * 获取设备信息
	 * @return
	 */
	public static String getDeviceInfo(){
		String str = SerialDll.INSTANTCE.get_device_info();
		log.info("【调用dll- 设备信息】"+str);
		return str;
	}
	/**
	 * 清除白名单
	 * @return
	 */
	public static int clearWl(){
		int str = SerialDll.INSTANTCE.clear_wl();
		log.info("【调用dll- 清除白名单】"+str);
		return str;
	}
	/**
	 * 开始无线绑定指令
	 * @param mode  1 无线手动绑定指令， 2无线自动绑定指令
	 * @param uid_str 答题器的 UID 列表
	 * @return
	 */
	public static int wirelessBindStart( int mode, String uid_str ){
		int str = SerialDll.INSTANTCE.wireless_bind_start(mode, uid_str);
		log.info("【调用dll- 开始无线绑定】"+str);
		return str;
	}
	/**
	 * 获取无线绑定上报结果
	 * @return
	 */
	public static String getWirelessBindInfo(){
		String str = SerialDll.INSTANTCE.get_wireless_bind_info();
	//	log.info("【调用dll- 获取无线绑定上报结果】"+str);
		return str;
	}
	/**
	 * 停止无线绑定
	 * @return
	 */
	public static int wirelessBindStop(){
		int str = SerialDll.INSTANTCE.wireless_bind_stop();
		log.info("【调用dll- 停止无线绑定】"+str);
		return str;
	}
	
	/**
	 * 开始答题(多题)
	 * @return
	 */
	public static int answerStart(List<Answer> answers){
		int str = SerialDll.INSTANTCE.answer_start(0, EquipmentConstant.ANSWER_START_CODE(answers));
		log.info("【调用dll- 开始答题(多题)】"+str);
		return str;
	}
	
	/**
	 * 开始答题(单题)
	 * @param answers 
	 * 1.constant.SINGLE_ANSWER_CHAR 字母题
	 * 2.constant.SINGLE_ANSWER_NUMBER 数字题
	 * 3.constant.SINGLE_ANSWER_JUDGE 判断题
	 * 4. constant.getMultipleAnswerCHAR()
	 * @return
	 */
	public static int answerStart(String answers){
		int str = SerialDll.INSTANTCE.answer_start(1, EquipmentConstant.ANSWER_START_CODE(answers));
		log.info("【调用dll- 开始答题(单题)】"+EquipmentConstant.ANSWER_START_CODE(answers));
		return str;
	}
	/**
	 * 获取作答结果
	 * @return
	 */
	public static String getAnswerList(){
		String str = SerialDll.INSTANTCE.get_answer_list();
//		log.info("【调用dll- 获取答题结果】"+str);
		return str;
	}
	/**
	 * 停止作答
	 * @return
	 */
	public static int answerStop(){
		int str = SerialDll.INSTANTCE.answer_stop();
		log.info("【调用dll- 停止作答】"+str);
		return str;
	}
	/**
	 * 设置信道
	 * @param rf_ch
	 * @return
	 */
	public static int setRfCh(int rf_ch){
		int str = SerialDll.INSTANTCE.set_rf_ch(rf_ch);
		log.info("【调用dll- 设置信道】"+str);
		return str;
	}
	
	/**
	 * 设置发送功率
	 * @param tx_power
	 * @return
	 */
	public static int setTxPower(int tx_power){
		int str = SerialDll.INSTANTCE.set_tx_power(tx_power);
		log.info("【调用dll- 设置发送功率】"+str);
		return str;
	}
}
