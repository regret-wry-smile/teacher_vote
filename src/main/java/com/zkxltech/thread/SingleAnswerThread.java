package com.zkxltech.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.cache.RedisMapSingleAnswer;
import com.ejet.core.util.comm.StringUtils;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.device.DeviceComm;

import net.sf.json.JSONArray;

public class SingleAnswerThread extends BaseThread {
    private static final Logger logger = LoggerFactory.getLogger(SingleAnswerThread.class);
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
                    Thread.sleep(100);
                    String data = DeviceComm.getAnswerList();
                    if (!StringUtils.isBlank(data) && !"[]".equals(data)) {
                        logger.info("获取到答题数据:===>>"+data);
                    	String jsonData = JSONArray.fromObject(data).toString();
//                        StringBuilder stringBuilder = new StringBuilder(jsonData);
//                        if (jsonData.startsWith("{")) {
//                            stringBuilder.insert(0, "[").append("]");
//                        }
//                        jsonData = stringBuilder.toString();
                    RedisMapSingleAnswer.addAnswer(jsonData);
                }
            }
        } catch (InterruptedException e) {
            logger.error(IOUtils.getError(e));
        } catch (Throwable e){
            logger.error("线程获取硬件数据异常:"+IOUtils.getError(e));
        }
    }
}
