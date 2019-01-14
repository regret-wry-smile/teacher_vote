package com.zkxltech.service.impl;

import com.ejet.cache.BrowserManager;
import com.ejet.cache.RedisMapAttendance;
import com.ejet.cache.RedisMapQuick;
import com.ejet.core.util.ICallBack;
import com.ejet.core.util.StringUtils;
import com.ejet.core.util.comm.ListUtils;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.ejet.core.util.io.ImportExcelUtils;
import com.zkxltech.domain.ClassInfo;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.StudentInfo;
import com.zkxltech.service.StudentInfoService;
import com.zkxltech.sql.StudentInfoSql;
import com.zkxltech.thread.AttendanceThread;
import com.zkxltech.thread.BaseThread;
import com.zkxltech.thread.QuickThread;
import com.zkxltech.thread.ThreadManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentInfoServiceImpl implements StudentInfoService{
	private static final Logger log = LoggerFactory.getLogger(StudentInfoServiceImpl.class);
	private Result result;
	private StudentInfoSql studentInfoSql = new StudentInfoSql();
	
    @Override
	public Result importStudentInfo2(Object fileNameObj,Object classInfoObj,ICallBack icallback) {
		result = new Result();
		try {
			String fileName = String.valueOf(fileNameObj);
			ClassInfo classInfo =  (ClassInfo) StringUtils.parseToBean(JSONObject.fromObject(classInfoObj), ClassInfo.class);
			studentInfoSql.deleteStudent(new StudentInfo());
			List<List<Object>> list = ImportExcelUtils.getBankListByExcel(new FileInputStream(new File(fileName)), fileName);
			result = studentInfoSql.importStudent(list,classInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Import success!");//导入成功
//				BrowserManager.refreshClass();
//				BrowserManager.selectClass(classId);
//				BrowserManager.refreshStudent(classId);
			}else {
//				icallback.onResult(-1, "导入学生失败", "");
				result.setMessage("Failed to import students！");//导入失败
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to import students！");
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}finally {
			icallback.onResult(StringUtils.StringToInt(result.getRet()), result.getMessage(),  result.getRemak());
		}
	}
	
	@Override
	public Result importStudentInfo(Object fileNameObj,Object classInfoObj) {
		result = new Result();
		try {
			String fileName = String.valueOf(fileNameObj);
			ClassInfo classInfo =  (ClassInfo) StringUtils.parseToBean(JSONObject.fromObject(classInfoObj), ClassInfo.class);
			List<List<Object>> list = ImportExcelUtils.getBankListByExcel(new FileInputStream(new File(fileName)), fileName);
			result = studentInfoSql.importStudent(list,classInfo);
			
			if (list.isEmpty()) {
				result.setRet(Constant.ERROR);
				result.setMessage("Student number length should be less than 5");
			}else if(Constant.SUCCESS.equals(result.getRet())){
				result.setMessage("Import success!");
			}
			else {
				result.setMessage(result.getMessage());
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to import students！");
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}finally {
			System.out.println(JSONObject.fromObject(result));
		}
	}
	@Override
	public Result importStudentInfoByServer(Object object) {
		result = new Result();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String classId = (String) object;
					result = copeServerStudentData(classId);
					if(Constant.SUCCESS.equals(result.getRet())){
//						BrowserManager.refreshClass();
						BrowserManager.refreshStudent(classId);
						BrowserManager.showMessage(true,"Student information was obtained from the server successfully！");
					}else{
						BrowserManager.showMessage(false,result.getMessage());
					}
				} catch (Exception e) {
					BrowserManager.showMessage(false,"Failed to retrieve student information from server！");
					log.error(IOUtils.getError(e));
				}finally {
					BrowserManager.removeLoading();
				}
			}
		}).start();
		return result;
	}
	@Override
	public Result selectStudentInfo(Object param) {
		result = new Result();
		try {
			StudentInfo studentInfo =  (StudentInfo) com.zkxltech.ui.util.StringUtils.parseJSON(param, StudentInfo.class);
			result = studentInfoSql.selectStudentInfo(studentInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Query student information successfully!");
			}else {
				result.setMessage("Failed to search student information！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to search student information！");
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result insertStudentInfo(Object param) {
		try {
			
			StudentInfo studentInfo =  (StudentInfo) StringUtils.parseToBean(JSONObject.fromObject(param), StudentInfo.class);
			StudentInfo studentInfoParam = new StudentInfo();
			studentInfoParam.setClassId(studentInfo.getClassId());
			result = studentInfoSql.selectStudentInfo(studentInfoParam);
			if (Constant.ERROR.equals(result.getRet())) {
				result.setMessage("Failed to check and query the class information!");
				return result;
			}else {
				if (((List<StudentInfo>)result.getItem()).size()>=120) {
					result.setRet(Constant.ERROR);
					result.setMessage("No more than 120 students are allowed in each class!");
					return result;
				}
			}
//			/*判断该班中学生学号是否存在*/
//			StudentInfo paramStudentInfo1 = new StudentInfo();
//			paramStudentInfo1.setClassId(studentInfo.getClassId());
//			paramStudentInfo1.setStudentId(studentInfo.getStudentId());
//			result = studentInfoSql.selectStudentInfo(paramStudentInfo1);
//			if(Constant.ERROR.equals(result.getRet())){
//				result.setMessage("查询学生失败!");
//				return result;
//			}else {
//				 if(!com.zkxltech.ui.util.StringUtils.isEmptyList(result.getItem())){
//					 result.setMessage("该班该学号已经存在!");
//					 result.setRet(Constant.ERROR);
//					 return result;
//				 }
//			}
//			
//			/*判断该班中答题器编号是否存在*/
//			StudentInfo paramStudentInfo2= new StudentInfo();
//			paramStudentInfo2.setClassId(studentInfo.getClassId());
//			paramStudentInfo2.setIclickerId(studentInfo.getIclickerId());
//			result = studentInfoSql.selectStudentInfo(paramStudentInfo2);
//			if(Constant.ERROR.equals(result.getRet())){
//				result.setMessage("查询学生失败!");
//				return result;
//			}else {
//				 if(!com.zkxltech.ui.util.StringUtils.isEmptyList(result.getItem())){
//					 result.setMessage("该班该答题器编号已经存在!");
//					 result.setRet(Constant.ERROR);
//					 return result;
//				 }
//			}
			result = studentInfoSql.insertStudentInfo(studentInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Added student information successfully!");
			}else {
				result.setMessage("Failed to add student information！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to add student information！");
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result deleteStudentById(Object param) {
		try {
			JSONArray jsonArray = JSONArray.fromObject(param);
			List<Integer> ids = new ArrayList<Integer>();
			for (int i = 0; i < jsonArray.size(); i++) {
				ids.add(jsonArray.getInt(i));
			}
			result = studentInfoSql.deleteStudentById(ids);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Delete student information successfully!");
			}else {
				result.setMessage("Failed to delete student information！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to delete student information！");
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result updateStudentById(Object param) {
		try {
			StudentInfo studentInfo =  (StudentInfo) StringUtils.parseToBean(JSONObject.fromObject(param), StudentInfo.class);
			String iclickerId = studentInfo.getIclickerId();
			if (!StringUtils.isEmpty(iclickerId)) {
			    //修改学生信息的时候,有可能改了卡号,但是这个卡号不一定是绑定的状态,所以要检查设备中是否绑定了
			    List<String> iclickerIds = Global.getIclickerIds();
			    String bindStatus = "";
			    if (ListUtils.isEmpty(iclickerIds)||!iclickerIds.contains(iclickerId)) {
			        bindStatus = Constant.BING_NO;
                }else{
                    bindStatus = Constant.BING_YES;
                }
			    StudentInfoSql sql = new StudentInfoSql();
			    iclickerIds = new ArrayList<>();
			    iclickerIds.add(iclickerId);
			    result = sql.updateStatusByIclickerIds(iclickerIds, bindStatus);
			    if (result.getRet().equals(Constant.ERROR)) {
			        return result;
                }
			    
            }
			result = studentInfoSql.updateStudentById(studentInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Modify student information successfully!");//修改学生信息成功!
			}else {
				result.setMessage("Failed to modify student information！");//未能修改学生信息!
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to modify student information！");
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}
	
	/**
	 * 服务器中获取学生
	 */
	public Result copeServerStudentData(String classId) {
		Result result = new Result();
		try {
			result = studentInfoSql.getServerStudent(classId);//获取服务器学生
			if (Constant.ERROR.equals(result.getRet())) {
				return result;
			}
			if (com.zkxltech.ui.util.StringUtils.isEmptyList(result.getItem())) {
				result.setMessage("There are no perosons in the group！");//这个班级没有学生
				result.setRet(Constant.ERROR);
				return result;
			}
			List<Map<String, Object>> listMaps = (List<Map<String, Object>>) result.getItem();
			StudentInfo studentInfo = new StudentInfo();
			studentInfo.setClassId(classId);
			studentInfoSql.deleteStudent(studentInfo); //清除本地该班学生信息
			result = studentInfoSql.saveStudentByGroup(listMaps); //保存服务器上该班的学生信息
			if(Constant.ERROR.equals(result.getRet())){
				result.setMessage("Failed to fetch student in server！");//服务器中获取学生失败
			}else {
				result.setMessage("Failed to fetch student in server！");
			}
			
			return result;
		} catch (Exception e) {
			log.error(IOUtils.getError(e));
			result.setRet(Constant.ERROR);
			result.setDetail(IOUtils.getError(e));
			result.setMessage("Failed to fetch student in server！");
			return result;
		}
	}

	@Override
	public Result clearStudentByIds(Object object) {
		try {
			JSONArray jsonArray = JSONArray.fromObject(object);
			List<Integer> ids = new ArrayList<Integer>();
			for (int i = 0; i < jsonArray.size(); i++) {
				ids.add(jsonArray.getInt(i));
			}
			result = studentInfoSql.clearStudentByIds(ids);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Unbundling success!");
				BrowserManager.refreshBindCard();
			}else {
				result.setMessage("Unbundling failure！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Unbundling failure！");
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}

    @Override
    public Result signInStart(Object param) {
        Result r = new Result();
        try {
        	r.setRet(Constant.ERROR);
//        	 //每次调用签到先清空数据
//            RedisMapAttendance.clearAttendanceMap();
//            RedisMapAttendance.clearCardIdSet();
            /*停止所有线程*/
            ThreadManager.getInstance().stopAllThread();
            if (param == null) {
                r.setMessage("Lack of parameter");
                return r;
            }
            JSONObject jo = JSONObject.fromObject(param);
            if (jo.containsKey("classId")) {
                String classId = jo.getString("classId");
                if (StringUtils.isEmpty(classId)) {
                    r.setMessage("Class ID is empty");
                    return r;
                }
            }
            //开始签到接口有问题,暂用按任意键
	        r = EquipmentServiceImpl.getInstance().answer_start(0, Constant.ANSWER_STR);
	        if (r.getRet().equals(Constant.ERROR)) {
	            return r;
	        }
	        List<StudentInfo> studentInfos = Global.getAllStudentInfos();
	        if (ListUtils.isEmpty(studentInfos)) {
	            r.setMessage("No student information was obtained");
	            return r;
	        }
	        /**将查出来的学生信息按卡的id进行分类,并存入静态map中*/
	        for (StudentInfo studentInfo : studentInfos) {
	            Map<String, String> studentInfoMap = new HashMap<>();
	            studentInfoMap.put("studentName", studentInfo.getStudentName());
	            studentInfoMap.put("studentId",studentInfo.getStudentId());//学生id
	            Map<String, String> attendMap = RedisMapAttendance.getAttendanceMap().get(studentInfo.getIclickerId());
	            if(StringUtils.isEmpty(attendMap)){
	            	studentInfoMap.put("status", Constant.ATTENDANCE_NO);	
	            }else{
	            	studentInfoMap.put("status", attendMap.get("status"));	
	            }
	            RedisMapAttendance.getAttendanceMap().put(studentInfo.getIclickerId(), studentInfoMap);
	        }
	        BaseThread thread = new AttendanceThread();
	        thread.start();
	        ThreadManager.getInstance().addThread(thread);
	    	Global.setModeMsg(Constant.BUSINESS_ATTENDEN);
            r.setRet(Constant.SUCCESS);
            r.setMessage("Operation is successful");
        }catch (Exception e) {
            log.error("", e);
            r.setMessage("A system exception");
            r.setDetail(IOUtils.getError(e));
        }
        return r;
    }
    
    
    @Override
    public Result signInStop() {
        Result r = new Result();
        try {
            ThreadManager.getInstance().stopAllThread();
	        r.setRet(Constant.ERROR);
	        Global.setModeMsg(Constant.BUSINESS_NORMAL);
            r = EquipmentServiceImpl.getInstance().answer_stop();
            if (r.getRet().equals(Constant.ERROR)) {
                return r;
            }
            r.setRet(Constant.SUCCESS);
            r.setMessage("Stop success");
        } catch (Exception e) {
            log.error("", e);
            r.setMessage("system exception");
            r.setDetail(IOUtils.getError(e));
        }
        return r;
    }
    @Override
    public Result quickAnswer(Object param) {
        //开始答题前先清空
        RedisMapQuick.clearQuickMap();
        RedisMapQuick.clearStudentInfoMap();
        RedisMapQuick.getQuickMap().put("studentName", "");
        /*停止所有线程*/
        ThreadManager.getInstance().stopAllThread();
        Result r = new Result();
        r.setRet(Constant.ERROR);
        if (param == null) {
            r.setMessage("The parameter class id cannot be empty");
            return r;
        }
        try{
            r = EquipmentServiceImpl.getInstance().answer_start(0, Constant.ANSWER_STR);
            if (r.getRet().equals(Constant.ERROR)) {
                return r;
            }
            Global.setModeMsg(Constant.BUSINESS_ANSWER);
    //            StudentInfoServiceImpl impl = new StudentInfoServiceImpl();
    //            Result result = impl.selectStudentInfo(param);
    //            List<Object> item = (List<Object>) result.getItem();
            List<StudentInfo> studentInfos = Global.getStudentInfos();
            for (StudentInfo studentInfo : studentInfos) {
                RedisMapQuick.getStudentInfoMap().put(studentInfo.getIclickerId(), studentInfo);
            }
            BaseThread thread = new QuickThread();
            thread.start();
            /*添加到线程管理*/
            ThreadManager.getInstance().addThread(thread);
            r.setRet(Constant.SUCCESS);
            r.setMessage("Send a success");
        }catch (Exception e) {
            log.error("", e);
            r.setMessage("A system exception");
            r.setDetail(IOUtils.getError(e));
        }
        return r;
    }
    @Override
    public Result stopQuickAnswer() {
        Result r = new Result();
        r.setRet(Constant.ERROR);
        Global.setModeMsg(Constant.BUSINESS_NORMAL);
        /*停止所有线程*/
        ThreadManager.getInstance().stopAllThread();
        try{
            r = EquipmentServiceImpl.getInstance().answer_stop();
            if (r.getRet().equals(Constant.ERROR)) {
                return r;
            }
            
            r.setRet(Constant.SUCCESS);
            r.setMessage("Send a success");
        }catch (Exception e) {
            log.error("", e);
            r.setMessage("A system exception");
            r.setDetail(IOUtils.getError(e));
        }
        return r;
    }
    

}
