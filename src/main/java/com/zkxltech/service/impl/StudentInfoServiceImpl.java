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
				result.setMessage("导入成功!");
//				BrowserManager.refreshClass();
//				BrowserManager.selectClass(classId);
//				BrowserManager.refreshStudent(classId);
			}else {
//				icallback.onResult(-1, "导入学生失败", "");
				result.setMessage("导入学生失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("导入学生失败！");
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
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("导入成功!");
			}else {
				result.setMessage(result.getMessage());
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("导入学生失败！");
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
						BrowserManager.showMessage(true,"从服务器中获取学生信息成功！");
					}else{
						BrowserManager.showMessage(false,result.getMessage());
					}
				} catch (Exception e) {
					BrowserManager.showMessage(false,"从服务器中获取学生信息失败！");
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
				result.setMessage("查询学生信息成功!");
			}else {
				result.setMessage("查询学生信息失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("查询学生信息失败！");
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
				result.setMessage("校验查询该班学生信息失败!");
				return result;
			}else {
				if (((List<StudentInfo>)result.getItem()).size()>=120) {
					result.setRet(Constant.ERROR);
					result.setMessage("每个班学生不能超过120人!");
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
				result.setMessage("新增学生信息成功!");
			}else {
				result.setMessage("新增学生信息失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("新增学生信息失败！");
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
				result.setMessage("删除学生信息成功!");
			}else {
				result.setMessage("删除学生信息失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("删除学生信息失败！");
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
				result.setMessage("修改学生信息成功!");
			}else {
				result.setMessage("修改学生信息失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("修改学生信息失败！");
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
				result.setMessage("该班级没有学生！");
				result.setRet(Constant.ERROR);
				return result;
			}
			List<Map<String, Object>> listMaps = (List<Map<String, Object>>) result.getItem();
			StudentInfo studentInfo = new StudentInfo();
			studentInfo.setClassId(classId);
			studentInfoSql.deleteStudent(studentInfo); //清除本地该班学生信息
			result = studentInfoSql.saveStudentByGroup(listMaps); //保存服务器上该班的学生信息
			if(Constant.ERROR.equals(result.getRet())){
				result.setMessage("服务器中获取学生失败！");
			}else {
				result.setMessage("服务器中获取学生成功！");
			}
			
			return result;
		} catch (Exception e) {
			log.error(IOUtils.getError(e));
			result.setRet(Constant.ERROR);
			result.setDetail(IOUtils.getError(e));
			result.setMessage("服务器中获取学生失败！");
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
				result.setMessage("解绑成功!");
				BrowserManager.refreshBindCard();
			}else {
				result.setMessage("解绑失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("解绑失败！");
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
        	 //每次调用签到先清空数据
            RedisMapAttendance.clearAttendanceMap();
            RedisMapAttendance.clearCardIdSet();
            /*停止所有线程*/
            ThreadManager.getInstance().stopAllThread();
            if (param == null) {
                r.setMessage("缺少参数");
                return r;
            }
            JSONObject jo = JSONObject.fromObject(param);
            if (jo.containsKey("classId")) {
                String classId = jo.getString("classId");
                if (StringUtils.isEmpty(classId)) {
                    r.setMessage("班级ID为空");
                    return r;
                }
            }
            //开始签到接口有问题,暂用按任意键
	        r = EquipmentServiceImpl.getInstance().answer_start(0, Constant.ANSWER_STR);
	        if (r.getRet().equals(Constant.ERROR)) {
	            return r;
	        }
	        List<StudentInfo> studentInfos = Global.getStudentInfos();
	        if (ListUtils.isEmpty(studentInfos)) {
	            r.setMessage("未获取到学生信息");
	            return r;
	        }
	        /**将查出来的学生信息按卡的id进行分类,并存入静态map中*/
	        for (StudentInfo studentInfo : studentInfos) {
	            Map<String, String> studentInfoMap = new HashMap<>();
	            studentInfoMap.put("studentName", studentInfo.getStudentName());
	            studentInfoMap.put("studentId",studentInfo.getStudentId());//学生id
	            studentInfoMap.put("status", Constant.ATTENDANCE_NO);
	            RedisMapAttendance.getAttendanceMap().put(studentInfo.getIclickerId(), studentInfoMap);
	        }
	        BaseThread thread = new AttendanceThread();
	        thread.start();
	        ThreadManager.getInstance().addThread(thread);
	    	Global.setModeMsg(Constant.BUSINESS_ATTENDEN);
            r.setRet(Constant.SUCCESS);
            r.setMessage("操作成功");
        }catch (Exception e) {
            log.error("", e);
            r.setMessage("系统异常");
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
            r.setMessage("停止成功");
        } catch (Exception e) {
            log.error("", e);
            r.setMessage("系统异常");
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
            r.setMessage("参数班级id不能为空");
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
            r.setMessage("发送成功");
        }catch (Exception e) {
            log.error("", e);
            r.setMessage("系统异常");
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
            r.setMessage("停止成功");
        }catch (Exception e) {
            log.error("", e);
            r.setMessage("系统异常");
            r.setDetail(IOUtils.getError(e));
        }
        return r;
    }
    

}
