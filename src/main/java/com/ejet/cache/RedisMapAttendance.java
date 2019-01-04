package com.ejet.cache;

import com.ejet.core.util.RedisMapUtil;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.io.IOUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
/**
 * 考勤相关
 * @author zhouwei
 *
 */
public class RedisMapAttendance {
	private static final Logger logger = LoggerFactory.getLogger(RedisMapAttendance.class);
	/**卡号为key  value为 学生名称和考勤的状态*/
	private static Map<String, Map<String,String>> attendanceMap = Collections.synchronizedMap(new HashMap<>());
	/**已签到人员的信息*/
	private static Map<String,Object> SignMap = Collections.synchronizedMap(new HashMap<String, Object>());
	private static String[] SignBodyMap = {"iclicker"};
    /**绑定时用来去除重复的提交,代表当前提交的人*/
    private static Set<String> cardIdSet = new HashSet<>();
	public static void addAttendance(String jsonData){
		try {
			logger.info("【签到接收到的数据】"+jsonData);
			JSONArray jsonArray = JSONArray.fromObject(jsonData);
	        for (int j = 0; j < jsonArray.size(); j++) {
	            JSONObject jo = (JSONObject) jsonArray.get(j);
	            if (!jo.containsKey("result")) {
	            	String card_id = jo.getString("card_id");
		            /*如果attendanceMap里没有该卡号,表示不是本班学生,如果cardIdSet里有值表示已经提交过了*/
		            if (!attendanceMap.containsKey(card_id) || cardIdSet.contains(card_id)) {
		                continue;
		            }
		            cardIdSet.add(card_id);
					SignBodyMap[0] = card_id;
		            Map<String, String> map = attendanceMap.get(card_id);
		            for (String key : map.keySet()) {
		                if (key.equals("status")) {
		                    map.put(key, Constant.ATTENDANCE_YES);
							RedisMapUtil.setRedisMap(SignMap,SignBodyMap,0,map);
		                }
		            }
		            BrowserManager.refresAttendance();
				}
	        }
		} catch (Exception e) {
			logger.info("【签到】"+IOUtils.getError(e));
		}
        
    }
//    /*获取当前班级中签到人员的信息*/
//    public static String getAttendances(){
//		String[] keString = new String[2];
//		keString[0] = "0";
//		keString[1] = "1";
//		for (String )
//		return null;
//    }
	/*获取当前班级中所有人的考勤状态*/
	public static String getAttendance(){
	    List<Map<String,String>> list = new ArrayList<>();
	    Set<String> keySet = attendanceMap.keySet();
	    for (String key : keySet) {
            list.add(attendanceMap.get(key));
        }
//	    Collections.sort(list, new Comparator<Map<String, String>>() {
//
//			@Override
//			public int compare(Map<String, String> o1, Map<String, String> o2) {
//				int name1 = Integer.parseInt(o1.get("studentName").substring(1, 4));
//				int name2 = Integer.parseInt(o2.get("studentName").substring(1, 4));
//				if (name1>name2) {
//					return 1;
//				}else {
//					return -1;
//				}
//			}
//		});
	    return JSONArray.fromObject(list).toString();
	}
	/*获取当前班级提交的人数*/
	public static Integer getSubmitNum(){
	    return cardIdSet.size();
	}
    public static Map<String, Map<String, String>> getAttendanceMap() {
        return attendanceMap;
    }
    public static void setAttendanceMap(Map<String, Map<String, String>> attendanceMap) {
        RedisMapAttendance.attendanceMap = attendanceMap;
    }

	public static Map<String, Object> getSignMap() {
		return SignMap;
	}
	public static void setSignMap(Map<String, Object> signMap) {
		SignMap = signMap;
	}
	public static void clearSignMap(){
		SignMap.clear();
	}
	public static Set<String> getCardIdSet() {
        return cardIdSet;
    }
    public static void setCardIdSet(Set<String> cardIdSet) {
        RedisMapAttendance.cardIdSet = cardIdSet;
    }
    public static void clearCardIdSet() {
        cardIdSet.clear();
    }
    public static void clearAttendanceMap() {
        attendanceMap.clear();
    }
}
