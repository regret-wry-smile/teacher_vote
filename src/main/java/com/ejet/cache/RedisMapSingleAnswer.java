package com.ejet.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ejet.core.util.RedisMapUtil;
import com.ejet.core.util.StringUtils;
import com.ejet.core.util.comm.ListUtils;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.domain.Answer;
import com.zkxltech.domain.ClassHour;
import com.zkxltech.domain.ClassTestVo;
import com.zkxltech.domain.QuestionInfo;
import com.zkxltech.domain.Record;
import com.zkxltech.domain.Record2;
import com.zkxltech.domain.StudentInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 多选
 * @author zkxl
 *
 */
public class RedisMapSingleAnswer {
	/**
	 * 每条作答记录缓存
	 * -答题器编号
	 * 			-题号
	 */
	private static Map<String, Object> studentAnswer = Collections.synchronizedMap(new HashMap<String, Object>());
	private static String[] keyEveryAnswerMap = {"iclickerId","questionId"};
	
	private static final Logger logger = LoggerFactory.getLogger(RedisMapSingleAnswer.class);
	/**字母对应的人数*/
    private static Map<String,Integer> singleAnswerNumMap = Collections.synchronizedMap(new HashMap<>());
    /**字母对应的学生名称*/
    private static Map<String,List<StudentInfo>> singleAnswerStudentNameMap = Collections.synchronizedMap(new HashMap<>());
    /**本班卡号对应学生信息*/
    private static Map<String,StudentInfo> studentInfoMap = new HashMap<>();
    /**记录提交的卡id*/
    //private static Set<String> iclickerIdsSet = new HashSet<>();
    private static Map<String,String> iclickerAnswerMap = new HashMap<>();
    
    private static Answer answer;
    public static final String  CHAR_A = "A",CHAR_B = "B",CHAR_C = "C",CHAR_D = "D",CHAR_E = "E",CHAR_F= "F",
                                NUMBER_1 = "1", NUMBER_2 = "2",NUMBER_3 = "3",NUMBER_4 = "4",NUMBER_5 = "5",
                                NUMBER_6 = "6",NUMBER_7 = "7",NUMBER_8 = "8",NUMBER_9 = "9",
                                JUDGE_TRUE = "true",JUDGE_FALSE = "false";
    
    public static void addAnswer(String jsonData){
    	Record2 record2 = new Record2();
    	logger.info("【单选接收到的数据】"+jsonData);
        JSONArray jsonArray= JSONArray.fromObject(jsonData);
        for (Object object : jsonArray) {
            JSONObject jsonObject = JSONObject.fromObject(object);
            if (!jsonObject.containsKey("result")) {
            	  String card_id = jsonObject.getString("card_id");
                  StudentInfo studentInfo = studentInfoMap.get(card_id);
                  
                  record2.setStudentId(studentInfo.getStudentId());
                  record2.setStudentName(studentInfo.getStudentName());
                  record2.setClassId(studentInfo.getClassId());
                  
                  if (studentInfo == null) { //如果根据卡号未找到学生,表示不是本班的
                      continue;
                  }
                  String sb = jsonObject.getString("update_time");
                  String str = sb.substring(0,19);
                  record2.setAnswerStart(str);
                  
                  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//结束时间
                  record2.setAnswerClick(df.format(new Date()));
                  
                  JSONArray answers =  JSONArray.fromObject(jsonObject.get("answers"));
                  for (Object answerOb : answers) {
                      JSONObject answerJO = JSONObject.fromObject(answerOb);
                      String result = answerJO.getString("answer");
                      
                      record2.setAnswer(result);
                      record2.setQuestionType(answerJO.getString("type"));//s位字母，d位数字，j位判断
                      switch(record2.getQuestionType()){
                      	case "s":
                      		record2.setQuestion("Letter");
                      		break;
                      	case "d":
                      		record2.setQuestion("Digit");
                      		break;
                      	case "j":
                      		record2.setQuestion("Y/N");
                      		break;
                      }
                      
                      if (StringUtils.isEmpty(result)) {
                          continue;
                      }
                      if (iclickerAnswerMap.containsKey(card_id)) { //已经提交过,将以前提交的答题总数减一,并将以前该答题对象的学生名称去掉,将新值重新添加
                          String lastAnswer = iclickerAnswerMap.get(card_id);
                          Integer countNum = singleAnswerNumMap.get(lastAnswer);
                          singleAnswerNumMap.put(lastAnswer, --countNum);
                          List<StudentInfo> list = singleAnswerStudentNameMap.get(lastAnswer);
                          list.remove(studentInfo.getStudentName());
                      }
                      iclickerAnswerMap.put(card_id, result);
                      switch (answer.getType()) {
                          case Constant.ANSWER_CHAR_TYPE:
                              setCharCount(result);
                              break;
                          case Constant.ANSWER_NUMBER_TYPE:
                              setNumberCount(result);
                              break;
                          case Constant.ANSWER_JUDGE_TYPE:
                              setJudgeCount(result);
                              break;
                      }
                     List<StudentInfo> list = singleAnswerStudentNameMap.get(result);
                     if (list == null) {
                         list = new ArrayList<>();
                         singleAnswerStudentNameMap.put(result, list);
                     }
                     list.add(studentInfo);
                  }
                  keyEveryAnswerMap[0]=card_id;
            }
            
        }
        keyEveryAnswerMap[1]="1";
        RedisMapUtil.setRedisMap(studentAnswer, keyEveryAnswerMap, 0, record2);
        BrowserManager.refresAnswerNum();
    }
    public static List<Record2> getSingleRecordList(){
    	try {
			List<Record2> records = new ArrayList<Record2>();
			List<ClassTestVo> classTestVos = new ArrayList<ClassTestVo>();
			for (String id : iclickerAnswerMap.keySet()) { //遍历学生
				StudentInfo studentInfo= studentInfoMap.get(id);
				keyEveryAnswerMap[0] = studentInfo.getIclickerId();
					Record2 record2 = (Record2) RedisMapUtil.getRedisMap(studentAnswer, keyEveryAnswerMap, 0);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//结束时间
					record2.setAnswerEnd(df.format(new Date()));
					System.out.println("====="+JSONObject.fromObject(record2));
					ClassHour str = Global.getClassHour();
					record2.setSubject(str.getSubjectName());
					record2.setClassHourId(str.getClassHourName());
					records.add(record2);
			}
			logger.info("要保存的单选题作答记录："+JSONArray.fromObject(records));
			return records;
		} catch (Exception e) {
			logger.error(IOUtils.getError(e));
			BrowserManager.showMessage(false, "获取作答据失败！");
			return null;
		}
    }
    private static void setJudgeCount(String result) {
        switch (result) {
            case JUDGE_TRUE:
                singleAnswerNumMap.put(JUDGE_TRUE, singleAnswerNumMap.get(JUDGE_TRUE)+1);
                break;
            case JUDGE_FALSE:
                singleAnswerNumMap.put(JUDGE_FALSE, singleAnswerNumMap.get(JUDGE_FALSE)+1);
                break;
        }
    }
    private static void setNumberCount(String result) {
        switch (result) {
            case NUMBER_1:
                singleAnswerNumMap.put(NUMBER_1, singleAnswerNumMap.get(NUMBER_1)+1);
                break;
            case NUMBER_2:
                singleAnswerNumMap.put(NUMBER_2, singleAnswerNumMap.get(NUMBER_2)+1);
                break;
            case NUMBER_3:
                singleAnswerNumMap.put(NUMBER_3, singleAnswerNumMap.get(NUMBER_3)+1);
                break;
            case NUMBER_4:
                singleAnswerNumMap.put(NUMBER_4, singleAnswerNumMap.get(NUMBER_4)+1);
                break;
            case NUMBER_5:
                singleAnswerNumMap.put(NUMBER_5, singleAnswerNumMap.get(NUMBER_5)+1);
                break;
            case NUMBER_6:
                singleAnswerNumMap.put(NUMBER_6, singleAnswerNumMap.get(NUMBER_6)+1);
                break;
            case NUMBER_7:
                singleAnswerNumMap.put(NUMBER_7, singleAnswerNumMap.get(NUMBER_7)+1);
                break;
            case NUMBER_8:
                singleAnswerNumMap.put(NUMBER_8, singleAnswerNumMap.get(NUMBER_8)+1);
                break;
            case NUMBER_9:
                singleAnswerNumMap.put(NUMBER_9, singleAnswerNumMap.get(NUMBER_9)+1);
                break;
        }
    }
    
    
    private static void setCharCount(String result) {
        switch (result) {
            case CHAR_A:
                singleAnswerNumMap.put(CHAR_A, singleAnswerNumMap.get(CHAR_A)+1);
                break;
            case CHAR_B:
                singleAnswerNumMap.put(CHAR_B, singleAnswerNumMap.get(CHAR_B)+1);
                break;
            case CHAR_C:
                singleAnswerNumMap.put(CHAR_C, singleAnswerNumMap.get(CHAR_C)+1);
                break;
            case CHAR_D:
                singleAnswerNumMap.put(CHAR_D, singleAnswerNumMap.get(CHAR_D)+1);
                break;
            case CHAR_E:
            	singleAnswerNumMap.put(CHAR_E, singleAnswerNumMap.get(CHAR_E)+1);
            	break;
            case CHAR_F:
            	singleAnswerNumMap.put(CHAR_F, singleAnswerNumMap.get(CHAR_F)+1);
            	break;
        }
    }
    //获取每个答案对应的人数
    public static String getSingleAnswer(){
        return JSONObject.fromObject(singleAnswerNumMap).toString();
    }
    //获取当前提交答案的人数
    public static Object getSingleAnswerNum() {
        JSONObject jo = new JSONObject();
        jo.put("totalStudent", studentInfoMap.size());
        jo.put("current", iclickerAnswerMap.size());
        return jo.toString();
    }
    //获取答案对应的学生名称
    public static Object getSingleAnswerStudentName(Object params) {
        JSONObject jo = JSONObject.fromObject(params);
        if (!jo.containsKey("answer")) {
            logger.error("缺少参数答案 :\"answer\"");
            return JSONArray.fromObject(new ArrayList<>()).toString();   
        }
        List<StudentInfo> list = singleAnswerStudentNameMap.get(jo.getString("answer"));
    
        if (ListUtils.isEmpty(list)) {
            return JSONArray.fromObject(new ArrayList<>()).toString();
        }
        return JSONArray.fromObject(list).toString();
    }
    public static Map<String, StudentInfo> getStudentInfoMap() {
        return studentInfoMap;
    }
    public static void setStudentInfoMap(Map<String, StudentInfo> studentInfoMap) {
        RedisMapSingleAnswer.studentInfoMap = studentInfoMap;
    }
    public static void clearStudentInfoMap() {
        studentInfoMap.clear();
    }
    public static Map<String, Integer> getSingleAnswerNumMap() {
        return singleAnswerNumMap;
    }
    public static void setSingleAnswerNumMap(Map<String, Integer> singleAnswerNumMap) {
        RedisMapSingleAnswer.singleAnswerNumMap = singleAnswerNumMap;
    }
    public static void clearSingleAnswerNumMap() {
        singleAnswerNumMap.clear();
    }
    public static Answer getAnswer() {
        return answer;
    }
    public static void setAnswer(Answer answer) {
        RedisMapSingleAnswer.answer = answer;
    }
    public static void clearSingleAnswerStudentNameMap(){
        singleAnswerStudentNameMap.clear();
    }
    public static void clearIclickerAnswerMap(){
        iclickerAnswerMap.clear();
    }
}
