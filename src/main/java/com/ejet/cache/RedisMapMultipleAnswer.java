package com.ejet.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.core.util.RedisMapUtil;
import com.ejet.core.util.StringUtils;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.domain.Answer;
import com.zkxltech.domain.StudentInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 多选
 * @author zkxl
 *
 */
public class RedisMapMultipleAnswer {
	private static final Logger logger = LoggerFactory.getLogger(RedisMapMultipleAnswer.class);
	/**
	 * 当前答题编号
	 */
	private static String answerId;
	
	/**
	 * 当前答题
	 */
	private static char[] range;
	
	/**
	 * 每个答案作答信息
	 */
	public static Map<String, Object> everyAnswerMap = Collections.synchronizedMap(new HashMap<String, Object>());
	
	/**
	 * 每个答案作答信息
	 */
	public static Map<String, Object> answerMap = Collections.synchronizedMap(new HashMap<String, Object>());
	
	/**
	 * 作答计数
	 */
	private static Map<String, Object> answerNum = Collections.synchronizedMap(new HashMap<String, Object>());
	/**
	 * 每个人的作答信息
	 */
	public static Map<String, Object> everyBodyMap = Collections.synchronizedMap(new HashMap<String, Object>());
	
	
	private static String[] keyEveryAnswerMap = {"uuid","questionId","everyAnswer"};
	
	private static String[] keyEveryBodyMap = {"uuid","questionId","iclicker"};
	
	private static String[] keyAnswerMap = {"uuid","questionId","answer"};
	
	/**
	 * 开始答题
	 */
	
	public static void startAnswer(String rangStr){
		answerId = StringUtils.getUUID();
		range = splitString(rangStr);
	}
	
	/**
	 * 清空缓存
	 */
	public static void clearMap(){
		everyAnswerMap.clear();
		everyBodyMap.clear();
	}
	
	
	public static String getRange(){
		return JSONArray.fromObject(range).toString();
	}

	
	/**
	 * 添加多选作答详情
	 * @param score
	 */
	public static void addEveryAnswerInfo(String jsonData){
		try {
			keyEveryBodyMap[0] = answerId; //主题编号
			keyEveryAnswerMap[0] = answerId;
			keyAnswerMap[0] = answerId;
			JSONArray jsonArray = JSONArray.fromObject(jsonData); 
	        for (int  i= 0; i < jsonArray.size(); i++) {
	        	JSONObject jsonObject = jsonArray.getJSONObject(i); //，每个学生的作答信息
	        	String carId = jsonObject.getString("card_id"); //答题器编号
	        	StudentInfo studentInfo = verifyCardId(carId);
	        	if (studentInfo != null) {
	        		JSONArray answers =  JSONArray.fromObject(jsonObject.get("answers"));
	        		for (int j = 0; j < answers.size(); j++) {
	        			JSONObject answeJSONObject = answers.getJSONObject(j);
	        			String num = answeJSONObject.getString("id");//节目编号(题目编号)
	        			if (StringUtils.isEmpty( answeJSONObject.getString("answer"))) {
							continue;
						}
	        			keyEveryBodyMap[1] = num;
	        			keyEveryAnswerMap[1] = num;
	        			keyAnswerMap[1] = num;
	        			keyEveryBodyMap[2] = carId;
	        			Answer answer = (Answer) JSONObject.toBean((JSONObject) RedisMapUtil.getRedisMap(everyBodyMap, keyEveryBodyMap, 0), Answer.class);
//	        			if (answer!= null && !StringUtils.isEmpty(answer.getAnswer())) {
//	        				//已经上传了答案就跳过
//	        				continue;
//						}
	        			RedisMapUtil.setRedisMap(everyBodyMap, keyEveryBodyMap, 0, answeJSONObject);
	        			
	        			String answerString = answeJSONObject.getString("answer");
	        			
	        			if (StringUtils.isEmpty(answerString)) {
	        				//过滤答案为空的数据
	        				continue;
						}
	        			keyAnswerMap[2] = answerString;
	        			List<StudentInfo> studentInfos2 = (List<StudentInfo>) RedisMapUtil.getRedisMap(answerMap, keyAnswerMap, 0);
	    				if (com.zkxltech.ui.util.StringUtils.isEmptyList(studentInfos2)) {
	    					studentInfos2 = new ArrayList<StudentInfo>();
						}
	    				studentInfos2.add(studentInfo);
	    				
	    				RedisMapUtil.setRedisMap(answerMap, keyAnswerMap, 0, studentInfos2);
	        			
	        			
	        			char[] everyAnswer = answerString.toCharArray();
	        			for (int k = 0; k < everyAnswer.length; k++) {
	        				keyEveryAnswerMap[2] = String.valueOf(everyAnswer[k]);
	        				List<StudentInfo> studentInfos = (List<StudentInfo>) RedisMapUtil.getRedisMap(everyAnswerMap, keyEveryAnswerMap, 0);
	        				if (com.zkxltech.ui.util.StringUtils.isEmptyList(studentInfos)) {
	        					studentInfos = new ArrayList<StudentInfo>();
							}
	        				studentInfos.add(studentInfo);
	        				RedisMapUtil.setRedisMap(everyAnswerMap, keyEveryAnswerMap, 0, studentInfos);
						}
					}
				}
	        }
	        BrowserManager.refresAnswerNum();
		} catch (Exception e) {
			BrowserManager.showMessage(false, "解析作答数据失败！");
			logger.error(IOUtils.getError(e));
		}
		
    }
	
	/**
	 * 获取柱状图需要的数据
	 * @param score
	 */
	public static String getEveryAnswerInfoBar(){
		try {
			String[] keString = new String[2];
			keString[0] = answerId;
			keString[1] = "1";
			RedisMapUtil.getRedisMap(everyAnswerMap, keString, 0);
			logger.info("每个答案的选择信息："+JSONArray.fromObject(RedisMapUtil.getRedisMap(everyAnswerMap, keString, 0)).toString());
			return JSONObject.fromObject(RedisMapUtil.getRedisMap(everyAnswerMap, keString, 0)).toString();
		} catch (Exception e) {
			logger.error(IOUtils.getError(e));
			BrowserManager.showMessage(false, "获取作答信息失败！");
			return null;
		}
    }
	
	/**
	 * 获取选择答案对应的人数
	 * @param score
	 */
	public static String getAnswerInfoSum(){
		try {
			String[] keString = new String[2];
			keString[0] = answerId;
			keString[1] = "1";
			RedisMapUtil.getRedisMap(answerMap, keString, 0);
			logger.info("每个答案的选择信息："+JSONArray.fromObject(RedisMapUtil.getRedisMap(answerMap, keString, 0)).toString());
			return JSONObject.fromObject(RedisMapUtil.getRedisMap(answerMap, keString, 0)).toString();
		} catch (Exception e) {
			logger.error(IOUtils.getError(e));
			BrowserManager.showMessage(false, "获取作答信息失败！");
			return null;
		}
		
    }
	
	/**
	 * 获取答题人数
	 * @param score
	 */
	public static String getAnswerNum(){
		try {
			String[] keString = new String[2];
			keString[0] = answerId;
			keString[1] = "1";
			Map<String, Object> map = (Map<String, Object>) RedisMapUtil.getRedisMap(everyBodyMap, keString, 0);
			int answerN = 0;
			if (map != null) {
				answerN = map.size();
			}
			logger.info("作答人数："+ answerN+"总人数:"+Global.getStudentInfos().size());
			answerNum.put("answerNum", answerN);
			answerNum.put("total", Global.getStudentInfos().size());
			return JSONObject.fromObject(answerNum).toString();
		} catch (Exception e) {
			logger.error(IOUtils.getError(e));
			BrowserManager.showMessage(false, "获取作答信息失败！");
			return null;
		}
		
    }
	
//	public static void main(String[] args) {	
//		
//		startAnswer("A-F");
//		
//		List<StudentInfo> studentInfos = new ArrayList<StudentInfo>();
//		StudentInfo studentInfo = new StudentInfo();
//		studentInfo.setIclickerId("0000001");
//		studentInfos.add(studentInfo);
//		StudentInfo studentInfo2 = new StudentInfo();
//		studentInfo2.setIclickerId("0000002");
//		studentInfos.add(studentInfo2);
//		Global.setStudentInfos(studentInfos);
//		
//		JSONArray jsonData = new JSONArray();
//		
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("fun", "update_answer_list");
//		jsonObject.put("card_id", "0000001");
//		JSONArray jsonArray = new JSONArray();
//		JSONObject jsonObject2 = new JSONObject();
//		jsonObject2.put("type", "s");
//		jsonObject2.put("id", "1");
//		jsonObject2.put("answer", "ABC");
//		jsonArray.add(jsonObject2);
//		jsonObject.put("answers", jsonArray);
//	
//		JSONObject jsonObject_1 = new JSONObject();
//		jsonObject_1.put("fun", "update_answer_list");
//		jsonObject_1.put("card_id", "0000002");
//		JSONArray jsonArray_1 = new JSONArray();
//		JSONObject jsonObject2_1 = new JSONObject();
//		jsonObject2_1.put("type", "s");
//		jsonObject2_1.put("id", "1");
//		jsonObject2_1.put("answer", "ABCF");
//		jsonArray_1.add(jsonObject2_1);
//		jsonObject_1.put("answers", jsonArray_1);
//		
//		jsonData.add(jsonObject);
//		jsonData.add(jsonObject_1);
//		
//		
//		
//		addEveryAnswerInfo(jsonData.toString());
//		
//		System.out.println("每个人的作答详情"+JSONObject.fromObject(everyBodyMap));
//		System.out.println("每个答案的作答详情"+JSONObject.fromObject(everyAnswerMap));
//		System.out.println("作答统计"+JSONObject.fromObject(getAnswerNum()));
//		
//		System.out.println("答案："+JSONObject.fromObject(getAnswerInfoSum()));
//	}
	
	/**
	 * 判断该答题器编号是否属于当前班级
	 */
	public static StudentInfo verifyCardId(String cardId){
		for (int i = 0; i < Global.studentInfos.size(); i++) {
    		if (cardId.equals(Global.studentInfos.get(i).getIclickerId())) { //是否属于当前班级
				return Global.studentInfos.get(i);
			}
		}
		return null;
		
	}
	
	/**
	 * "A-D" 转换为 ["A","B","C","D"]
	 * @param string
	 * @return
	 */
	private static char[] splitString(String string){
		if (string.length() != 3) {
			return null;
		}
		if (string.substring(1, 2).hashCode() != 45) {
			return null;
		}
		int startCode = string.substring(0, 1).hashCode();
		int endCode = string.substring(2, 3).hashCode();
		if (startCode >= endCode) {
			return null;
		}

		char[] chars = new char[endCode-startCode+1];
		int index = 0;
		for (int i = startCode; i <= endCode; i++) {
			chars[index++] = (char)i;
		}
		return chars;
	}
	
}
