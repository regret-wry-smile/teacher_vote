package com.ejet.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.core.util.StringUtils;
import com.ejet.core.util.constant.Constant;
import com.zkxltech.domain.Answer;
import com.zkxltech.domain.StudentInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 多选
 * @author zkxl
 *
 */
public class RedisMapSingleAnswer {
	private static final Logger logger = LoggerFactory.getLogger(RedisMapSingleAnswer.class);
	/**字母对应的人数*/
    private static Map<String,Integer> singleAnswerNumMap = Collections.synchronizedMap(new HashMap<>());
    /**字母对应的学生名称*/
    private static Map<String,List<String>> singleAnswerStudentNameMap = Collections.synchronizedMap(new HashMap<>());
    /**本班卡号对应学生信息*/
    private static Map<String,StudentInfo> studentInfoMap = new HashMap<>();
    /**记录提交的卡id*/
    private static Set<String> iclickerIdsSet = new HashSet<>();
    
    private static Answer answer;
    public static final String  CHAR_A = "A",CHAR_B = "B",CHAR_C = "C",CHAR_D = "D",
                                NUMBER_1 = "1", NUMBER_2 = "2",NUMBER_3 = "3",NUMBER_4 = "4",NUMBER_5 = "5",
                                NUMBER_6 = "6",NUMBER_7 = "7",NUMBER_8 = "8",NUMBER_9 = "9",
                                JUDGE_TRUE = "true",JUDGE_FALSE = "false";
    
    public static void addAnswer(String jsonData){
        JSONArray jsonArray= JSONArray.fromObject(jsonData);
        for (Object object : jsonArray) {
            JSONObject jsonObject = JSONObject.fromObject(object);
            String card_id = jsonObject.getString("card_id");
            if (iclickerIdsSet.contains(card_id)) { //如果已经提交过忽略
                continue ;
            }
            StudentInfo studentInfo = studentInfoMap.get(card_id);
            if (studentInfo == null) { //如果根据卡号未找到学生,表示不是本班的
                continue;
            }
            JSONArray answers =  JSONArray.fromObject(jsonObject.get("answers"));
            for (Object answerOb : answers) {
                JSONObject answerJO = JSONObject.fromObject(answerOb);
                String result = answerJO.getString("answer");
                if (StringUtils.isEmpty(result)) {
                    continue;
                }
                iclickerIdsSet.add(card_id);
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
               List<String> list = singleAnswerStudentNameMap.get(result);
               if (list == null) {
                   list = new ArrayList<>();
               }
               list.add(studentInfo.getStudentName());
            }
        }
        BrowserManager.refresAnswerNum();
    }
    
    private static void setJudgeCount(String result) {
        switch (result) {
            case JUDGE_TRUE:
                singleAnswerNumMap.put(JUDGE_TRUE, singleAnswerNumMap.get(JUDGE_TRUE)+1);
                break;
            case JUDGE_FALSE:
                singleAnswerNumMap.put(JUDGE_FALSE, singleAnswerNumMap.get(CHAR_B)+1);
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
                singleAnswerNumMap.put(NUMBER_4, singleAnswerNumMap.get(NUMBER_4));
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
        jo.put("current", iclickerIdsSet.size());
        return jo.toString();
    }
    //获取答案对应的学生名称
    public static Object getSingleAnswerStudentName(Object params) {
        JSONObject jo = JSONObject.fromObject(params);
        if (!jo.containsKey("answer")) {
            logger.error("缺少参数答案 :\"answer\"");
            return new ArrayList<>().toString();   
        }
        String answer = jo.getString("answer");
        List<String> list = singleAnswerStudentNameMap.get(answer);
        return list.toString();
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
    public static void cleariclickerIdsSet(){
        iclickerIdsSet.clear();
    }
}
