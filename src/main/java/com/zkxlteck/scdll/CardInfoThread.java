package com.zkxlteck.scdll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.cache.RedisMapBind;
import com.ejet.core.util.comm.StringUtils;

public class CardInfoThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(CardInfoThread.class);
    private boolean FLAG = true;
    public boolean isFLAG() {
        return FLAG;
    }
    public void setFLAG(boolean fLAG) {
        FLAG = fLAG;
    }
    @Override
    public void run() {
        while(FLAG){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error(" Thread sleep failure ");
            }
            String jsonData = ScDll.intance.get_wireless_bind_info() ;
            if (!StringUtils.isBlank(jsonData)) {
                logger.info("获取到答题数据:===>>"+jsonData);
                RedisMapBind.addBindMap(jsonData);
            }
        }
    }
}
