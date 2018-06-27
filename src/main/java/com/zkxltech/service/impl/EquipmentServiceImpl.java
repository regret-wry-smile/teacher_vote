package com.zkxltech.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ejet.cache.BrowserManager;
import com.ejet.cache.RedisMapAttendance;
import com.ejet.cache.RedisMapBind;
import com.ejet.cache.RedisMapClassTest;
import com.ejet.cache.RedisMapQuick;
import com.ejet.core.util.comm.ListUtils;
import com.ejet.core.util.comm.StringUtils;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.domain.EquipmentParam;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.StudentInfo;
import com.zkxltech.service.EquipmentService;
import com.zkxltech.sql.StudentInfoSql;
import com.zkxlteck.scdll.AnswerThread;
import com.zkxlteck.scdll.AttendanceThread;
import com.zkxlteck.scdll.CardInfoThread;
import com.zkxlteck.scdll.ScDll;
import com.zkxlteck.scdll.ScoreThread;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author: ZhouWei
 * @date:2018年6月21日 下午2:55:17
 */
public class EquipmentServiceImpl implements EquipmentService{
    //private ExecutorService threadPool = Executors.newSingleThreadExecutor(); //单线程池
    public static final int SUCCESS = 0 ;//
    public static final int ERROR = -1 ; //
    /*有答案*/
    public static final String IS_ANSWER_YES = "YES";
    /*无答案*/
    public static final String IS_ANSWER_NO = "NO";
    /*抢答用-字符答案*/
    public static final String TYPE_CHAR = "CHAR";
    /*抢答用-数字答案*/
    public static final String TYPE_NUMBER = "NUMBER";
    /*抢答用-判断答案*/
    public static final String TYPE_JUDGE = "JUDGE";
    /*抢答用-字符答案*/
    public static final String QUICK_CHAR = "[{'type':'s','id':'0002','range':'A-F'}]";
    /*抢答用-数字答案*/
    public static final String QUICK_NUMBER = "[{'type':'d','id':'0002','range':'0-9'}]";
    /*抢答用-判断答案*/
    public static final String QUICK_JUDGE = "[{'type':'j','id':'0002','range':''}]";
    /*抢答用-无答案*/
    public static final String QUICK_COMMON = "[{'type':'g','id':'0002','range':''}]";
    public Thread t ;
    private static final EquipmentServiceImpl SINGLE = new EquipmentServiceImpl();  
    
    private EquipmentServiceImpl() {
    }
    public static EquipmentServiceImpl getInstance() {  
        return SINGLE;  
    }
    @Override
    public Result get_device_info() {
        Result r = new Result();
        String get_device_info = ScDll.intance.get_device_info();
        if(!StringUtils.isBlank(get_device_info)) {
            r.setRet(Constant.SUCCESS);
            r.setItem(get_device_info);
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("查询失败");
        return r;
    }
    /**清除白名单*/
    @Override
    public Result clearWl(Object param) {
        //FIXME 增加事务
        Result r = new Result();
        r.setRet(Constant.ERROR);
        String get_device_info = ScDll.intance.get_device_info();
        if (StringUtils.isBlank(get_device_info)) {
            r.setMessage("设备故障,请重启设备");
            return r;
        }
        JSONObject jsono = JSONObject.fromObject(param);
        if (!jsono.containsKey("classId")) {
            r.setMessage("缺少班级id参数");
            return r;
        }
        List<String> uids = new ArrayList<>();
        Object list = JSONObject.fromObject(get_device_info).get("list");
        JSONArray jsonArray = JSONArray.fromObject(list);
        for (Object object : jsonArray) {
            JSONObject jo = JSONObject.fromObject(object);
            String uid = jo.getString("uid");
            if (!StringUtils.isBlank(uid)) {
                uids.add(uid);
            }
        }
        try {
            if (ListUtils.isEmpty(uids)) {
                r.setRet(Constant.SUCCESS);
                r.setMessage("清除成功");
                return r;
            }
            StudentInfoSql studentInfoSql = new StudentInfoSql();
            Result result = studentInfoSql.updateByIclickerIds(uids);
            if (result.getRet().equals(Constant.ERROR)) {
                r.setRet(Constant.ERROR);
                r.setMessage("清除白名单失败");
                return r;
            }
            int clear_wl = ScDll.intance.clear_wl();
            if (clear_wl == SUCCESS) {
                r.setRet(Constant.SUCCESS);
                r.setMessage("清除成功");
                BrowserManager.refreshStudent(jsono.getString("classId"));
                return r;
            }
        } catch (Exception e) {
            r.setDetail(IOUtils.getError(e));
        }
        r.setMessage("清除失败");
        return r;
    }
    @Override
    public Result bindStart(Object param) {
        Result r = new Result();
        r.setRet(Constant.ERROR);
        try {
            int bind_start = ScDll.intance.wireless_bind_start(1,"") ;
            if (bind_start < 1) {
                r.setMessage("操作失败");
                return r;
            }
            /**根据班级id查询学生信息*/
            StudentInfoServiceImpl sis= new StudentInfoServiceImpl();
            Result result = sis.selectStudentInfo(param);
            List<StudentInfo> studentInfos = (List)result.getItem();
            if (result== null || ListUtils.isEmpty(studentInfos)) {
                r.setMessage("您还未上传学生信息");
                return r;
            }
            /**将查出来的学生信息按卡的id进行分类,并存入静态map中*/
            Map<String, StudentInfo> studentInfoMap = new HashMap<>();
            /**按绑定状态进行分类*/
            int bind = 0,notBind = 0 ;
            for (StudentInfo studentInfo : studentInfos) {
                String iclickerId = studentInfo.getIclickerId();
                String status = studentInfo.getStatus();
                if (StringUtils.isBlank(iclickerId)) {
                    r.setMessage("学生:"+studentInfo.getStudentName()+",的答题器编号不能为空");
                    return r;
                }
                if (StringUtils.isBlank(status)) {
                    r.setMessage("学生:"+studentInfo.getStudentName()+",的绑定状态不能为空");
                    return r;
                }
                studentInfoMap.put(iclickerId, studentInfo);
                if (studentInfo.getStatus().equals(Constant.BING_YES)) {
                    ++bind;
                }else{
                    ++notBind;
                }
            }
            /**存入静态map*/
            //每次调用绑定方法先清空,再存
            RedisMapBind.clearCardIdMap();
            RedisMapBind.clearBindMap();
            RedisMapBind.getBindMap().put("studentName", null);
            RedisMapBind.getBindMap().put("code", bind_start);
            RedisMapBind.getBindMap().put("accomplish", bind);
            RedisMapBind.getBindMap().put("notAccomplish",notBind);
            RedisMapBind.setStudentInfoMap(studentInfoMap);
            t = new CardInfoThread();
            t.start();
            r.setItem(bind_start);
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
        } catch (Exception e) {
            r.setMessage("操作失败");
            r.setDetail(e.getMessage());
        } 
        return r;
    }
    @Override
    public Result bindStop() {
        Result r = new Result();
        if (t != null && t instanceof CardInfoThread) {
            CardInfoThread c =  (CardInfoThread)t;
            c.setFLAG(false);
        }
        int bind_stop = ScDll.intance.wireless_bind_stop();
        if (bind_stop == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("停止成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("停止失败");
        return r;
    }
    @Override
    public Result answerStart(Object param) {
        Result r = new Result();
        r.setRet(Constant.ERROR);
        EquipmentParam ep =  (EquipmentParam) com.zkxltech.ui.util.StringUtils.parseJSON(param, EquipmentParam.class);
        Integer isQuickResponse = ep.getIsQuickResponse();
        String answerStr = ep.getAnswerStr();
        if(isQuickResponse == null || StringUtils.isBlank(answerStr)){
            r.setMessage("缺少参数");
        }
        int answer_start = ScDll.intance.answer_start(isQuickResponse,answerStr);
        if (answer_start == SUCCESS) {
            //开始答题前先清空
            RedisMapClassTest.classTestAnswerMap.clear();
            t = new AnswerThread();
            t.start();
            r.setRet(Constant.SUCCESS);
            r.setMessage("发送成功");
            return r;
        }
        r.setMessage("发送失败");
        return r;
    }
    @Override
    public Result answer_start_with_raise_hand(int is_quick_response, int raise_hand, String answer_str) {
        Result r = new Result();
        int answer_start_with_raise_hand = ScDll.intance.answer_start_with_raise_hand(is_quick_response, raise_hand, answer_str);
        if (answer_start_with_raise_hand == SUCCESS) {
            t = new AnswerThread();
            t.start();
            r.setRet(Constant.SUCCESS);
            r.setMessage("发送成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("发送失败");
        return r;
    }
    @Override
    public Result set_raise_hand(int raise_hand) {
        Result r = new Result();
        int set_raise_hand = ScDll.intance.set_raise_hand(raise_hand);
        if (set_raise_hand == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("设置成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("设置失败");
        return r;
    }
    @Override
    public Result set_sign_in(int attendance) {
        Result r = new Result();
        int set_sign_in = ScDll.intance.set_sign_in(attendance);
        if (set_sign_in == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("设置成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("设置失败");
        return r;
    }

//    public Result get_answer_list() {
//        Result r = new Result();
//        MachineThread t = new MachineThread(true, MachineThread.GET_ANSWER);
//        t.start();
//        map.put("thread", t);
//        return r;
//    }
    @Override
    public Result answerStop() {
        Result r = new Result();
        int answer_stop = ScDll.intance.answer_stop();
        if (answer_stop == SUCCESS) {
            //FIXME
            if (t != null) {
                AnswerThread m = (AnswerThread)t;
                m.setFLAG(false);
            }
            r.setRet(Constant.SUCCESS);
            r.setMessage("停止成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("停止失败");
        return r;
    }
    @Override
    public Result set_student_id(String student_id_str) {
        Result r = new Result();
        int set_student_id = ScDll.intance.set_student_id(student_id_str);
        if (set_student_id == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("操作失败");
        return r;
    }
    @Override
    public Result get_student_id_info() {
        Result r = new Result();
        String get_student_id_info = ScDll.intance.get_student_id_info();
        if (!StringUtils.isBlank(get_student_id_info)) {
            r.setItem(get_student_id_info);
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("操作失败");
        return r;
    }
    @Override
    public Result set_channel(int tx_ch, int rx_ch) {
        Result r = new Result();
        int set_channel = ScDll.intance.set_channel(tx_ch,rx_ch);
        if (set_channel == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("操作失败");
        return r;
    }
    @Override
    public Result set_tx_power(int tx_power) {
        Result r = new Result();
        int set_tx_power = ScDll.intance.set_tx_power(tx_power);
        if (set_tx_power == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("操作失败");
        return r;
    }

//    public Result attendance_24g(int is_open, int pro_index) {
//        Result r = new Result();
//        int attendance_24g = ScDll.intance.attendance_24g(is_open,pro_index);
//        if (attendance_24g == SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("操作成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("操作失败");
//        return r;
//    }
    @Override
    public Result read_card_uid_start() {
        Result r = new Result();
        int read_card_uid_start = ScDll.intance.read_card_uid_start();
        if (read_card_uid_start == SUCCESS) {
            r.setItem(read_card_uid_start);
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("操作失败");
        return r;
    }
    @Override
    public Result get_card_uid_Info() {
        Result r = new Result();
        String get_card_uid_Info = ScDll.intance.get_card_uid_Info();
        //FIXME
        if (!StringUtils.isBlank(get_card_uid_Info)) {
            r.setItem(get_card_uid_Info);
            r.setRet(Constant.SUCCESS);
            r.setMessage("读取成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("读取失败");
        return r;
    }
    @Override
    public Result read_card_uid_stop() {
        Result r = new Result();
        int read_card_uid_stop = ScDll.intance.read_card_uid_stop();
        if (read_card_uid_stop == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("停止成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("停止失败");
        return r;
    }
    @Override
    public Result get_wireless_bind_info() {
        Result r = new Result();
        String get_wireless_bind_info = ScDll.intance.get_wireless_bind_info();
        if (!StringUtils.isBlank(get_wireless_bind_info)) {
            r.setItem(get_wireless_bind_info);
            r.setRet(Constant.SUCCESS);
            r.setMessage("获取成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("获取失败");
        return r;
    }

    @Override  
    public Result raise_hand_start() {
        Result r = new Result();
        int raise_hand_start = ScDll.intance.raise_hand_start();
        //FIXME
        if (raise_hand_start == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("操作失败");
        return r;
    }

    @Override    
    public Result raise_hand_stop() {
        Result r = new Result();
        int raise_hand_stop = ScDll.intance.raise_hand_stop();
        if (raise_hand_stop == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("停止成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("停止失败");
        return r;
    }

    @Override
    public Result get_raise_hand_list() {
        Result r = new Result();
        String get_raise_hand_list = ScDll.intance.get_raise_hand_list();
        if (!StringUtils.isBlank(get_raise_hand_list)) {
            r.setItem(get_raise_hand_list);
            r.setRet(Constant.SUCCESS);
            r.setMessage("获取成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("获取失败");
        return r;
    }

    @Override
    public Result signInStart(Object param) {
        Result r = new Result();
        r.setRet(Constant.ERROR);
        if (param == null) {
            r.setMessage("缺少参数");
            return r;
        }
        JSONObject jo = JSONObject.fromObject(param);
        if (jo.containsKey("classId")) {
            String classId = jo.getString("classId");
            if (StringUtils.isBlank(classId)) {
                r.setMessage("班级ID为空");
                return r;
            }
        }
        //int sign_in_start = ScDll.intance.sign_in_start();
        //开始签到接口有问题,暂用按任意键
        int answer_start = ScDll.intance.answer_start(0, Constant.ANSWER_STR);
        if (answer_start == SUCCESS) {
            //每次调用签到先清空数据
            RedisMapAttendance.clearAttendanceMap();
            RedisMapAttendance.clearCardIdSet();
            StudentInfoServiceImpl si = new StudentInfoServiceImpl();
            Result result = si.selectStudentInfo(param);
            List<StudentInfo> studentInfos = (List)result.getItem();
            if (result== null || ListUtils.isEmpty(studentInfos)) {
                r.setMessage("您还未上传学生信息");
                return r;
            }
            /**将查出来的学生信息按卡的id进行分类,并存入静态map中*/
            for (StudentInfo studentInfo : studentInfos) {
                Map<String, String> studentInfoMap = new HashMap<>();
                studentInfoMap.put("studentName", studentInfo.getStudentName());
                studentInfoMap.put("status", Constant.ATTENDANCE_NO);
                RedisMapAttendance.getAttendanceMap().put(studentInfo.getIclickerId(), studentInfoMap);
            }
            t = new AttendanceThread();
            t.start();
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
            return r;
        }
        r.setMessage("操作失败");
        return r;
    }

    @Override
    public Result signInStop() {
        Result r = new Result();
        if (t != null && t instanceof AttendanceThread ) {
            AttendanceThread a = (AttendanceThread)t;
            a.setFLAG(false);
        }
        int answer_stop = ScDll.intance.answer_stop();
        if (answer_stop == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("停止成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("停止失败");
        return r;
    }

    @Override
    public Result get_sign_in_list() {
        Result r = new Result();
        String get_sign_in_list = ScDll.intance.get_sign_in_list();
        if (!StringUtils.isBlank(get_sign_in_list)) {
            r.setItem(get_sign_in_list);
            r.setRet(Constant.SUCCESS);
            r.setMessage("获取成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("获取失败");
        return r;
    }

    @Override
    public Result set_attendance_24g(int is_open, int pro_index) {
      Result r = new Result();
      int set_attendance_24g = ScDll.intance.set_attendance_24g(is_open,pro_index);
      if (set_attendance_24g == SUCCESS) {
          r.setRet(Constant.SUCCESS);
          r.setMessage("设置成功");
          return r;
      }
      r.setRet(Constant.ERROR);
      r.setMessage("设置失败");
      return r;
    }

    @Override
    public Result set_wireless_student_id(String uid_str, String student_id_str) {
        Result r = new Result();
        int set_wireless_student_id = ScDll.intance.set_wireless_student_id(uid_str,student_id_str);
        if (set_wireless_student_id == SUCCESS) {
            r.setRet(Constant.SUCCESS);
            r.setMessage("设置成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("设置失败");
        return r;
    }

    @Override
    public Result get_wireless_student_id_info() {
        Result r = new Result();
        String get_wireless_student_id_info = ScDll.intance.get_wireless_student_id_info();
        if (!StringUtils.isBlank(get_wireless_student_id_info)) {
            r.setItem(get_wireless_student_id_info);
            r.setRet(Constant.SUCCESS);
            r.setMessage("获取成功");
            return r;
        }
        r.setRet(Constant.ERROR);
        r.setMessage("获取失败");
        return r;
    }
    @Override
    public Result quickAnswer(Object param) {
        Result r = new Result();
        r.setRet(Constant.ERROR);
        if (param == null) {
            r.setMessage("参数班级id不能为空");
            return r;
        }
        int answer_start = ScDll.intance.answer_start(0, QUICK_COMMON);
        if (answer_start == SUCCESS) {
            //开始答题前先清空
            RedisMapQuick.clearQuickMap();
            RedisMapQuick.clearStudentInfoMap();
            StudentInfoServiceImpl impl = new StudentInfoServiceImpl();
            Result result = impl.selectStudentInfo(param);
            List<Object> item = (List<Object>) result.getItem();
            for (Object object : item) {
                StudentInfo studentInfo =  (StudentInfo) com.zkxltech.ui.util.StringUtils.parseJSON(object, StudentInfo.class);
                RedisMapQuick.getStudentInfoMap().put("studentName", studentInfo.getStudentName());
                RedisMapQuick.getStudentInfoMap().put("iclickerId", studentInfo.getIclickerId());
            }
            t = new AnswerThread();
            t.start();
            r.setRet(Constant.SUCCESS);
            r.setMessage("发送成功");
            return r;
        }
        r.setMessage("发送失败");
        return r;
    }
    public Result scoreStart(int questionNum) {
        Result r = new Result();
        r.setRet(Constant.ERROR);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[");
        for (int i = 0; i < questionNum; i++) {
          strBuilder.append("{");
          String id = String.valueOf(i+1);
          String type = "d";
          String range = "";
          try {
            range = "0-9";
          } catch (Exception e2) {
            range = "";
          }
          strBuilder.append("'id':'"+id+"',");
          strBuilder.append("'type':'"+type+"',");
          strBuilder.append("'range':'"+range+"'");
          strBuilder.append("}");
          
          if (questionNum-1 != i) {
            strBuilder.append(",");
          }
        }
        strBuilder.append("]");
        int answer_start = ScDll.intance.answer_start(0, strBuilder.toString());
        if (answer_start == ERROR) {
            r.setMessage("指令发送失败");
            return r;
        }
        t = new ScoreThread();
        t.start();
        r.setRet(Constant.SUCCESS);
        r.setMessage("发送成功");
        return r;
    }
}
