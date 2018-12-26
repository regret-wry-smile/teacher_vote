package com.zkxltech.service;

import com.zkxltech.domain.Result;

public interface EquipmentService {
public Result get_device_info();
	/**
	 * 停止绑定
	 * @return
	 */
	Result bind_stop();
	/**
	 * 清除白名单
	 * @return
	 */
	Result clear_wl() ;
    /**
     * 设置信道
     * @param tx_ch
     * @param rx_ch
     * @return
     */
    Result set_channel( int rf_ch) ;
    /**
     * 设置发送功率
     * @param tx_power
     * @return
     */
    Result set_tx_power(int tx_power) ;
    /**
     * 设备和数据库绑定的状态同步
     * @return
     */
    Result equipmentDatabaseSynchronization();
    /**
     *  开始答题
     */
    Result answerStart2(String answerType,Object param);
    /**
     * 停止答题
     */
    Result answer_stop();
	
}
