package com.zkxltech.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.cache.BrowserManager;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.domain.Result;
import com.zkxltech.service.impl.EquipmentServiceImpl;

/**
 * @author: ZhouWei
 * @date:2018年7月2日 下午12:32:18
 */
public class EquipmentStatusThread extends BaseThread {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentStatusThread.class);
    private boolean FLAG = true;
    public boolean isFLAG() {
        return FLAG;
    }
    public void setFLAG(boolean fLAG) {
        FLAG = fLAG;
    }
    
    @Override
    public void stopThread() {
        FLAG = false;
    }
    
    @Override
    public void run() {
        try {
            while(FLAG){
                boolean isAnswerStart = Global.isEquipmentStatus;
                Result r = EquipmentServiceImpl.getInstance().get_device_info();
                boolean converStatusToBoolean = converStatusToBoolean(r.getRet());
                if (isAnswerStart != converStatusToBoolean) {
                    BrowserManager.refreEquipmentState(converStatusToBoolean);
                    Global.isEquipmentStatus = converStatusToBoolean;
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            logger.error(IOUtils.getError(e));
        } catch (Throwable e){
            logger.error("线程获取硬件数据异常",IOUtils.getError(e));
        }
    }
    public static boolean converStatusToBoolean(String status){
        if (status.equals(Constant.ERROR)) {
            return false;
        }else{
            return true;
        }
    }
}
