package com.ejet.cache;

import com.ejet.core.util.io.IOUtils;
import com.zkxltech.domain.StudentInfo;
import com.zkxltech.service.impl.StudentInfoServiceImpl;
import com.zkxltech.ui.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: ZhouWei
 * @date:2018年6月25日 下午5:18:24
 */
public class RedisMapBind {
    private static final Logger logger = LoggerFactory.getLogger(RedisMapBind.class);
    /** 获取未绑定学生信息 */
    private static List<StudentInfo> noStudentInfos = Collections.synchronizedList(new ArrayList<StudentInfo>());
    /** 获取已绑定学生信息 */
    private static List<StudentInfo> studentInfosl = Collections.synchronizedList(new ArrayList<StudentInfo>());
    /** 一键配对缓存 */
    private static Map<String, Object> bindMap = Collections.synchronizedMap(new HashMap<String, Object>());
    /**答题器id对应的学生*/
    private static Map<String, StudentInfo> studentInfoMap = Collections.synchronizedMap(new HashMap<>());
    /**绑定状态*/
    private static final String STATE_BIND = "1";
    /***/
    private static final StudentInfoServiceImpl SIS= new StudentInfoServiceImpl();
    /**数据库中所有卡对应的状态*/
    private static Map<String,String> cardIdMap = new HashMap<>();
    public static void addBindMap(String jsonData){
        if (StringUtils.isEmptyList(noStudentInfos)){
            BrowserManager.showMessage(false,"All people are bound");
        }
    	try {
    		logger.info("【绑定接收到的数据】"+jsonData);
            JSONArray jsonArray = JSONArray.fromObject(jsonData);
            A : for (Object object : jsonArray) {
                JSONObject jo = JSONObject.fromObject(object);
                String cardId = jo.getString("card_id");
                for (int i=0;i<studentInfosl.size();i++){
                    if (studentInfosl.get(i).getIclickerId().equals(cardId)){
                        BrowserManager.showMessage(false,"This answerer is bound");
                        continue A;
                    }
                }
                int i = 0;
                StudentInfo studentInfo = noStudentInfos.get(i);
                studentInfo.setIclickerId(cardId);
                //如果不包含,表示该卡不是本班的学生,跳过
//                if (!studentInfoMap.containsKey(cardId)) {
//                    continue;
//                }
                //判断该学生是否已经绑定了,如果绑定了就不用再绑了
//                StudentInfo studentInfo = studentInfoMap.get(cardId);
//                if (Constant.BING_YES.equals(studentInfo.getStatus())) {
//                    continue;
//                }
                studentInfo.setStatus(STATE_BIND);
                Integer accomplish = (Integer)bindMap.get("accomplish");
                ++accomplish;
                Integer notAccomplish = (Integer)bindMap.get("notAccomplish");
                --notAccomplish;
                bindMap.put("studentName", studentInfo.getStudentName()); //学生名称
                bindMap.put("studentId",studentInfo.getStudentId());//学生id
                bindMap.put("accomplish", accomplish);
                bindMap.put("notAccomplish",notAccomplish);
                SIS.updateStudentById(studentInfo);
                studentInfosl.add(studentInfo);
                noStudentInfos.remove(i);
                ++i;
            }
            BrowserManager.refreshBindCard();

		} catch (Exception e) {
			logger.error("【绑定】"+IOUtils.getError(e));
		}
    	
    }
    
	public static String getBindMapValue(){
		return JSONObject.fromObject(bindMap).toString();
    }
	
    public static Map<String, Object> getBindMap() {
        return bindMap;
    }

    public static Map<String, StudentInfo> getStudentInfoMap() {
        return studentInfoMap;
    }
    public static void clearStudentInfoMap(){
        studentInfoMap.clear();
    }

    public static void setStudentInfoMap(Map<String, StudentInfo> studentInfoMap) {
        RedisMapBind.studentInfoMap = studentInfoMap;
    }

    public static Map<String,String> getCardIdSet() {
        return cardIdMap;
    }

    public static void setCardIdSet(Map<String,String> cardIdMap) {
        RedisMapBind.cardIdMap = cardIdMap;
    }
    public static void clearCardIdMap() {
        cardIdMap.clear();
    }

    public static void setBindMap(Map<String, Object> bindMap) {
        RedisMapBind.bindMap = bindMap;
    }
    public static void clearBindMap() {
        bindMap.clear();
    }

    public static List<StudentInfo> getNoStudentInfos() {
        return noStudentInfos;
    }

    public static void setNoStudentInfos(List<StudentInfo> noStudentInfos) {
        RedisMapBind.noStudentInfos = noStudentInfos;
    }

    public static List<StudentInfo> getStudentInfosl() {
        return studentInfosl;
    }

    public static void setStudentInfosl(List<StudentInfo> studentInfosl) {
        RedisMapBind.studentInfosl = studentInfosl;
    }
}
