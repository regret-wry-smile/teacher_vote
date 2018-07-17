//package com.zkxltech.service.impl;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.ejet.core.util.SerialPortManager;
//import com.ejet.core.util.comm.ListUtils;
//import com.ejet.core.util.comm.StringUtils;
//import com.ejet.core.util.constant.Constant;
//import com.ejet.core.util.constant.Global;
//import com.ejet.core.util.io.IOUtils;
//import com.zkxltech.domain.RequestVo;
//import com.zkxltech.domain.Result;
//import com.zkxltech.scdll.ScDll;
//import com.zkxltech.service.EquipmentService2;
//import com.zkxltech.sql.StudentInfoSql;
//import com.zkxltech.thread.BaseThread;
//import com.zkxltech.thread.MultipleAnswerThread;
//import com.zkxltech.thread.ThreadManager;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
///**
// * @author: luozheng
// *@date:2018年7月17日 下午2:54:45
// */
//public class EquipmentServiceImpl2 implements EquipmentService2{
//    private static final Logger log = LoggerFactory.getLogger(EquipmentServiceImpl2.class);
//    private static final EquipmentServiceImpl2 SINGLE = new EquipmentServiceImpl2();  
//    
//    private EquipmentServiceImpl2() {
//    }
//    public static EquipmentServiceImpl2 getInstance() {  
//        return SINGLE;  
//    }
//    @Override
//    public Result get_device_info() {
//        Result r = new Result();
//        SerialPortManager.sendToPort("{'fun': 'get_device_info'}");
//        if(!StringUtils.isBlank(get_device_info)) {
//            r.setRet(Constant.SUCCESS);
//            r.setItem(get_device_info);
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("查询失败");
//        return r;
//    }
//    
////    }
//    @Override
//    public Result set_raise_hand(int raise_hand) {
//        Result r = new Result();
//        int set_raise_hand = ScDll.intance.set_raise_hand(raise_hand);
//        if (set_raise_hand == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("设置成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("设置失败");
//        return r;
//    }
//    @Override
//    public Result set_sign_in(int attendance) {
//        Result r = new Result();
//        int set_sign_in = ScDll.intance.set_sign_in(attendance);
//        if (set_sign_in == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("设置成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("设置失败");
//        return r;
//    }
//
//    
//    @Override
//    public Result set_student_id(String student_id_str) {
//        Result r = new Result();
//        int set_student_id = ScDll.intance.set_student_id(student_id_str);
//        if (set_student_id == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("操作成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("操作失败");
//        return r;
//    }
//    @Override
//    public Result get_student_id_info() {
//        Result r = new Result();
//        String get_student_id_info = ScDll.intance.get_student_id_info();
//        if (!StringUtils.isBlank(get_student_id_info)) {
//            r.setItem(get_student_id_info);
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("操作成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("操作失败");
//        return r;
//    }
//    @Override
//    public Result set_channel(int tx_ch, int rx_ch) {
//        Result r = new Result();
//        int set_channel = ScDll.intance.set_channel(tx_ch,rx_ch);
//        if (set_channel == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("操作成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("操作失败");
//        return r;
//    }
//    @Override
//    public Result set_tx_power(int tx_power) {
//        Result r = new Result();
//        int set_tx_power = ScDll.intance.set_tx_power(tx_power);
//        if (set_tx_power == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("操作成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("操作失败");
//        return r;
//    }
//
//    @Override
//    public Result read_card_uid_start() {
//        Result r = new Result();
//        int read_card_uid_start = ScDll.intance.read_card_uid_start();
//        if (read_card_uid_start == Constant.SEND_SUCCESS) {
//            r.setItem(read_card_uid_start);
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("操作成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("操作失败");
//        return r;
//    }
//    @Override
//    public Result get_card_uid_Info() {
//        Result r = new Result();
//        String get_card_uid_Info = ScDll.intance.get_card_uid_Info();
//        //FIXME
//        if (!StringUtils.isBlank(get_card_uid_Info)) {
//            r.setItem(get_card_uid_Info);
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("读取成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("读取失败");
//        return r;
//    }
//    @Override
//    public Result read_card_uid_stop() {
//        Result r = new Result();
//        int read_card_uid_stop = ScDll.intance.read_card_uid_stop();
//        if (read_card_uid_stop == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("停止成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("停止失败");
//        return r;
//    }
//    @Override
//    public Result get_wireless_bind_info() {
//        Result r = new Result();
//        String get_wireless_bind_info = ScDll.intance.get_wireless_bind_info();
//        if (!StringUtils.isBlank(get_wireless_bind_info)) {
//            r.setItem(get_wireless_bind_info);
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("获取成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("获取失败");
//        return r;
//    }
//
//    @Override  
//    public Result raise_hand_start() {
//        Result r = new Result();
//        int raise_hand_start = ScDll.intance.raise_hand_start();
//        //FIXME
//        if (raise_hand_start == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("操作成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("操作失败");
//        return r;
//    }
//
//    @Override    
//    public Result raise_hand_stop() {
//        Result r = new Result();
//        int raise_hand_stop = ScDll.intance.raise_hand_stop();
//        if (raise_hand_stop == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("停止成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("停止失败");
//        return r;
//    }
//
//    @Override
//    public Result get_raise_hand_list() {
//        Result r = new Result();
//        String get_raise_hand_list = ScDll.intance.get_raise_hand_list();
//        if (!StringUtils.isBlank(get_raise_hand_list)) {
//            r.setItem(get_raise_hand_list);
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("获取成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("获取失败");
//        return r;
//    }
//
//
//
//    @Override
//    public Result get_sign_in_list() {
//        Result r = new Result();
//        String get_sign_in_list = ScDll.intance.get_sign_in_list();
//        if (!StringUtils.isBlank(get_sign_in_list)) {
//            r.setItem(get_sign_in_list);
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("获取成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("获取失败");
//        return r;
//    }
//
//    @Override
//    public Result set_attendance_24g(int is_open, int pro_index) {
//      Result r = new Result();
//      int set_attendance_24g = ScDll.intance.set_attendance_24g(is_open,pro_index);
//      if (set_attendance_24g == Constant.SEND_SUCCESS) {
//          r.setRet(Constant.SUCCESS);
//          r.setMessage("设置成功");
//          return r;
//      }
//      r.setRet(Constant.ERROR);
//      r.setMessage("设置失败");
//      return r;
//    }
//
//    @Override
//    public Result set_wireless_student_id(String uid_str, String student_id_str) {
//        Result r = new Result();
//        int set_wireless_student_id = ScDll.intance.set_wireless_student_id(uid_str,student_id_str);
//        if (set_wireless_student_id == Constant.SEND_SUCCESS) {
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("设置成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("设置失败");
//        return r;
//    }
//
//    @Override
//    public Result get_wireless_student_id_info() {
//        Result r = new Result();
//        String get_wireless_student_id_info = ScDll.intance.get_wireless_student_id_info();
//        if (!StringUtils.isBlank(get_wireless_student_id_info)) {
//            r.setItem(get_wireless_student_id_info);
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("获取成功");
//            return r;
//        }
//        r.setRet(Constant.ERROR);
//        r.setMessage("获取失败");
//        return r;
//    }
//    /**设备和数据库绑定的状态同步*/
//    @Override
//    public Result equipmentDatabaseSynchronization() {
//        Result r = new Result();
//        r.setRet(Constant.ERROR);
//        try{
//            String get_device_info = ScDll.intance.get_device_info();
//            if (StringUtils.isBlank(get_device_info)) {
//                r.setMessage("设备故障,请重启设备");
//                return r;
//            }
//            StudentInfoSql studentInfoSql = new StudentInfoSql();
//            List<String> iclickerIds = getEquipmentAllUid(get_device_info);
//            //将结果保存到项目全局变量中
//            
//            Global.setIclickerIds(iclickerIds);
//            
//            /**如果设备没有值,直接将库全改为未绑定*/
//            if (ListUtils.isEmpty(iclickerIds)) {
//                r = studentInfoSql.updateStatus(Constant.BING_NO);
//           }else{
//               /**有值的将库里对应的学生改为绑定,没值的全部是未绑定*/
//               r = studentInfoSql.updateStatusByIclickerIds(iclickerIds,Constant.BING_YES);
//               if (r.getRet().equals(Constant.ERROR)) {
//                   return r;
//               }
//               r = studentInfoSql.updateStatusByIclickerIds(iclickerIds, Constant.BING_NO," not in");
//               if (r.getRet().equals(Constant.ERROR)) {
//                   return r;
//               }
//               r.setRet(Constant.SUCCESS);
//           }
//        } catch (Exception e) {
//            log.error("", e);
//            r.setMessage("同步设备与数据库绑定状态失败");
//            r.setDetail(IOUtils.getError(e));
//        }
//        return r;
//    }
//    
//    private List<String> getEquipmentAllUid(String get_device_info) {
//        List<String> uids = new ArrayList<>();
//        Object list = JSONObject.fromObject(get_device_info).get("list");
//        JSONArray jsonArray = JSONArray.fromObject(list);
//        for (Object object : jsonArray) {
//            JSONObject jo = JSONObject.fromObject(object);
//            String uid = jo.getString("uid");
//            if (!StringUtils.isBlank(uid)) {
//                uids.add(uid);
//            }
//        }
//        return uids;
//    }
//    
//	@Override
//	public Result answerStart2(String answerType,Object param) {
//		Result r = new Result();
//        r.setRet(Constant.ERROR);
//        /*停止所有线程*/
//        ThreadManager.getInstance().stopAllThread();
//        List<RequestVo> requestVos = (List<RequestVo>) JSONArray.toCollection(JSONArray.fromObject(param), RequestVo.class);
//        StringBuilder strBuilder = new StringBuilder();
//		strBuilder.append("[");
//
//		for (int i = 0; i < requestVos.size(); i++) {
//			RequestVo requestVo = requestVos.get(i);
//			String id = requestVo.getId();
//			String type = requestVo.getType();
//			strBuilder.append("{");
//			if ("单选".equals(type)) {
//				type = "s";
//			} else if ("多选".equals(type)) {
//				type = "m";
//			} else if ("判断".equals(type)) {
//				type = "j";
//			} else if ("数字".equals(type)) {
//				type = "d";
//			}
//			String range = requestVo.getRange();
//			strBuilder.append("'id':'" + id + "',");
//			strBuilder.append("'type':'" + type + "',");
//			strBuilder.append("'range':'" + range + "'");
//			strBuilder.append("}");
//
//			strBuilder.append(",");
//
//		}
//		strBuilder = new StringBuilder(strBuilder.substring(0, strBuilder.lastIndexOf(",")));
////		System.out.println(strBuilder);
//        //int answer_start = ScDll.intance.answer_start(1,strBuilder.toString());
//        r = answer_start(1, strBuilder.toString());
//        if ( r.getRet()== Constant.SUCCESS) {
//            BaseThread thread = new MultipleAnswerThread(answerType);
//            thread.start();
//            //添加到线程管理
//            ThreadManager.getInstance().addThread(thread);
//            r.setRet(Constant.SUCCESS);
//            r.setMessage("发送成功");
//            return r;
//        }
//        
//        r.setMessage("发送失败");
//        return r;
//	}
//	
//	@Override
//    public Result answer_stop() {
//        Result r = new Result();
//        int answer_stop = ScDll.intance.answer_stop();
//        if (answer_stop == Constant.SEND_ERROR) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                log.error("线程休眠失败[尝试第二次发送停止答题指令]");
//            }
//            int answer_stop2 = ScDll.intance.answer_stop();
//            if (answer_stop2 == Constant.SEND_ERROR) {
//                r.setRet(Constant.ERROR);
//                r.setMessage("指令发送失败");
//                log.error("\"停止答题\"指令发送失败");
//                return r;
//            }
//        }
//        log.info("\"停止答题\"指令发送成功");
//        r.setRet(Constant.SUCCESS);
//        r.setMessage("停止成功");
//        return r;
//    }
//    public Result answer_start(int is_quick_response, String answer_str) {
//        Result r = new Result();
//        r.setRet(Constant.ERROR);
//        int answer_start = ScDll.intance.answer_start(is_quick_response, answer_str);
//        if (answer_start == Constant.SEND_ERROR) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                log.error("线程休眠失败[尝试第二次发送答题指令]");
//            }
//            int answer_start2 = ScDll.intance.answer_start(is_quick_response, answer_str);
//            if (answer_start2 == Constant.SEND_ERROR) {
//                log.error("开始答题指令发送失败");
//                r.setMessage("指令发送失败");
//                return r;
//            }
//        }
//        log.info("开始答题指令发送成功");
//        r.setRet(Constant.SUCCESS);
//        r.setMessage("指令发送成功");
//        return r;
//    }
//	
////	public static void main(String[] args) {
////		List<RequestVo> list = new ArrayList<RequestVo>();
////		RequestVo requestVo1 = new RequestVo();
////		requestVo1.setId("1");
////		requestVo1.setRange("A-F");
////		requestVo1.setType("m");
////		list.add(requestVo1);
////		new EquipmentServiceImpl().answerStart2(list);
////	}
//}
