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
import com.zkxltech.domain.Answer;
import com.zkxltech.domain.Score;
import com.zkxltech.domain.ScoreVO;
import com.zkxltech.domain.StudentInfo;
import com.zkxltech.domain.Vote;
import com.zkxltech.domain.VoteVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 *评分相关缓存
 * @author zkxl
 *
 */
public class RedisMapVote {
	private static final Logger logger = LoggerFactory.getLogger(RedisMapVote.class);
	/**
	 * 当前投票主题id
	 */
	private static String voteInfoId;
	/**
	 * 投票主题信息缓存
	 */
	public static Map<String, Object> voteInfoMap = Collections.synchronizedMap(new HashMap<String, Object>());
	/**
	 * 投票详情信息缓存
	 */
	public static Map<String, Object> voteDetailInfoMap = Collections.synchronizedMap(new HashMap<String, Object>());
	/**
	 * 投票柱状图数据缓存
	 */
	public static Map<String, Object> barMap = Collections.synchronizedMap(new HashMap<String, Object>());
	
	public static List<VoteVO> voteVos =new ArrayList<VoteVO>();
	
	private static String[] keyVoteInfoMap = {"uuid"};
	
	private static String[] keyVoteDetailInfoMap = {"uuid","questionId","iclicker"};
	
	private static String[] keyBarMap = {"questionId"};
	
	/**
	 * 清空缓存
	 */
	public static void clearMap(){
		voteInfoMap.clear();
		voteDetailInfoMap.clear();
		barMap.clear();
		voteVos.clear();
	}
	
	/**
	 * 添加主题
	 * @param score
	 */
	public static void addVoteInfo(Vote vote){
		voteInfoId =  StringUtils.getUUID();
		keyVoteInfoMap[0] = voteInfoId;
		RedisMapUtil.setRedisMap(voteInfoMap, keyVoteInfoMap, 0, vote);
    }
	/**
	 * 获取主题
	 */
	public static Vote getVoteInfo(){
		keyVoteInfoMap[0] = voteInfoId;
		Vote vote =  (Vote) RedisMapUtil.getRedisMap(voteInfoMap, keyVoteInfoMap, 0);
		return vote;
	}
	
	/**
	 * 初始化柱状图数据缓存
	 */
//	public static void initBarMap(){
//		for (String uuid : scoreDetailInfoMap.keySet()) {
//			Map<String, Object> map1 =  (Map<String, Object>) scoreDetailInfoMap.get(uuid);
//			for (int i = 0; i < getScoreInfo().getPrograms().size(); i++) {
//				if (map1.containsKey(String.valueOf(i+1))) {
//					Map<String, Object> map2 = (Map<String, Object>) map1.get(String.valueOf(i));
//					keyBarMap[0] = String.valueOf(i+1); //题号
//					int total = 0;//总分
//					int peopleSum = 0; //人数
//					for (String iclickerId : map2.keySet()) {
//						Answer answer = (Answer) JSONObject.toBean((JSONObject) map2.get(iclickerId), Answer.class);
//						total = total + Integer.parseInt(answer.getAnswer());
//						peopleSum ++;
//					}
//					JSONObject jsonObject = new JSONObject();
//					jsonObject.put("total", total);
//					jsonObject.put("peopleSum", peopleSum);
//					RedisMapUtil.setRedisMap(barMap, keyBarMap, 0, jsonObject);
//				}else {
//					keyBarMap[0] = String.valueOf(i+1); //题号
//					int total = 0;//总分
//					int peopleSum = 0; //人数
//					JSONObject jsonObject = new JSONObject();
//					jsonObject.put("total", total);
//					jsonObject.put("peopleSum", peopleSum);
//					RedisMapUtil.setRedisMap(barMap, keyBarMap, 0, jsonObject);
//				}
//			}
//        }
//	}
	/**
	 * 添加投票详情
	 */
	public static void addVoteDetailInfo(String jsonData){
		logger.info("【投票接收到的数据】"+jsonData);
		keyVoteDetailInfoMap[0] = voteInfoId; //主题编号
		JSONArray jsonArray = JSONArray.fromObject(jsonData); 
        for (int  i= 0; i < jsonArray.size(); i++) {
        	JSONObject jsonObject = jsonArray.getJSONObject(i); //，每个学生的作答信息
        	String carId = jsonObject.getString("card_id"); //答题器编号
        	if (verifyCardId(carId)) {
        		JSONArray answers =  JSONArray.fromObject(jsonObject.get("answers"));
        		for (int j = 0; j < answers.size(); j++) {
        			JSONObject answeJSONObject = answers.getJSONObject(j);
        			String num = answeJSONObject.getString("id");//节目编号(题目编号)
//        			String answer = answeJSONObject.getString("answer");//答案
        			keyVoteDetailInfoMap[1] = num;
        			keyVoteDetailInfoMap[2] = carId;
        			Answer answer = (Answer) JSONObject.toBean((JSONObject) RedisMapUtil.getRedisMap(voteDetailInfoMap, keyVoteDetailInfoMap, 0), Answer.class);
        			if (answer != null && !StringUtils.isEmpty(answer.getAnswer())) {
        				continue;
					}
        			RedisMapUtil.setRedisMap(voteDetailInfoMap, keyVoteDetailInfoMap, 0, answeJSONObject);
				}
			}
        }
        BrowserManager.refresVote();
    }
	
	
	/**
	 * 转换成柱状图相关数据
	 * @param score
	 */
	public static void dealBarInfo(){
		List<String> programs =  getVoteInfo().getPrograms();
		voteVos.clear();
		if (!(voteDetailInfoMap.size() > 0)) {
			for (int i = 0; i < programs.size(); i++) {
				String questionId = String.valueOf(i+1);
				keyBarMap[0] = questionId; //题号
				int agree = 0;//赞成
				int disagree = 0; //反对
				int waiver = 0; //弃权
				VoteVO voteVO = new VoteVO();
				voteVO.setNum(questionId);
				voteVO.setProgram(programs.get(i));
				voteVO.setAgree(agree);
				voteVO.setDisagree(disagree);
				voteVO.setWaiver(waiver);
				voteVos.add(voteVO);
			}
		}else {
			for (String uuid : voteDetailInfoMap.keySet()) {
				Map<String, Object> map1 =  (Map<String, Object>) voteDetailInfoMap.get(uuid);
				for (int i = 0; i < programs.size(); i++) {
					String questionId = String.valueOf(i+1);
					if (map1.containsKey(questionId)) {
						Map<String, Object> map2 = (Map<String, Object>) map1.get(questionId);
						keyBarMap[0] = questionId; //题号
						int agree = 0;//赞成
						int disagree = 0; //反对
						int waiver = 0; //弃权
						for (String iclickerId : map2.keySet()) {
							Answer answer = (Answer) JSONObject.toBean((JSONObject) map2.get(iclickerId), Answer.class);
							if ("A".equalsIgnoreCase(answer.getAnswer())) {
								agree++;
							}else if ("B".equalsIgnoreCase(answer.getAnswer())) {
								disagree++;
							}else {
								waiver++;
							}
						}
						VoteVO voteVO = new VoteVO();
						voteVO.setNum(questionId);
						voteVO.setProgram(programs.get(i));
						voteVO.setAgree(agree);
						voteVO.setDisagree(disagree);
						voteVO.setWaiver(waiver);
						voteVos.add(voteVO);
					}else {
						keyBarMap[0] = questionId; //题号
						int agree = 0;//赞成
						int disagree = 0; //反对
						int waiver = 0; //弃权
						VoteVO voteVO = new VoteVO();
						voteVO.setNum(questionId);
						voteVO.setProgram(programs.get(i));
						voteVO.setAgree(agree);
						voteVO.setDisagree(disagree);
						voteVO.setWaiver(waiver);
						voteVos.add(voteVO);
					}
				}
		}
		}	
    }
	
	/**
	 * 获取柱状图需要的数据
	 * @param score
	 */
	public static String getVoteInfoBar(){
		dealBarInfo();
		logger.info("投票数据"+JSONArray.fromObject(voteVos).toString());
		return JSONArray.fromObject(voteVos).toString();
    }
	
	public static void main(String[] args) {
		Vote score = new Vote();
		List<String> programs = new ArrayList<String>();
		programs.add("张三");
		programs.add("李四");
		programs.add("王五");
		score.setPrograms(programs);
		score.setTitle("测试主题");
		score.setDescribe("测试描述");
		addVoteInfo(score);
		
		
		List<StudentInfo> studentInfos = new ArrayList<StudentInfo>();
		StudentInfo studentInfo = new StudentInfo();
		studentInfo.setIclickerId("0000001");
		studentInfos.add(studentInfo);
		StudentInfo studentInfo2 = new StudentInfo();
		studentInfo2.setIclickerId("0000002");
		studentInfos.add(studentInfo2);
		Global.setStudentInfos(studentInfos);
		
		JSONArray jsonData = new JSONArray();
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fun", "update_answer_list");
		jsonObject.put("card_id", "0000001");
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject2 = new JSONObject();
		jsonObject2.put("type", "s");
		jsonObject2.put("id", "1");
		jsonObject2.put("answer", "A");
		jsonArray.add(jsonObject2);
		JSONObject jsonObject3 = new JSONObject();
		jsonObject3.put("type", "s");
		jsonObject3.put("id", "2");
		jsonObject3.put("answer", "B");
		jsonArray.add(jsonObject3);
		jsonObject.put("answers", jsonArray);
		
		
		JSONObject jsonObject_1 = new JSONObject();
		jsonObject_1.put("fun", "update_answer_list");
		jsonObject_1.put("card_id", "0000002");
		JSONArray jsonArray_1 = new JSONArray();
		JSONObject jsonObject2_1 = new JSONObject();
		jsonObject2_1.put("type", "s");
		jsonObject2_1.put("id", "1");
		jsonObject2_1.put("answer", "A");
		jsonArray_1.add(jsonObject2_1);
		JSONObject jsonObject3_1 = new JSONObject();
		jsonObject3_1.put("type", "s");
		jsonObject3_1.put("id", "2");
		jsonObject3_1.put("answer", "C");
		jsonArray_1.add(jsonObject3_1);
		jsonObject_1.put("answers", jsonArray_1);
		
		jsonData.add(jsonObject);
		jsonData.add(jsonObject_1);
		System.out.println(jsonData);
		
		addVoteDetailInfo(jsonData.toString());
		
		System.out.println(getVoteInfoBar());
	}
	
	/**
	 * 判断该答题器编号是否属于当前班级
	 */
	public static boolean verifyCardId(String cardId){
		for (int i = 0; i < Global.studentInfos.size(); i++) {
    		if (cardId.equals(Global.studentInfos.get(i).getIclickerId())) { //是否属于当前班级
				return true;
			}
		}
		return false;
	}
}
