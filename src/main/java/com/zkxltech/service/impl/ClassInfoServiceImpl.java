package com.zkxltech.service.impl;

import com.ejet.cache.RedisMapBind;
import com.ejet.core.util.comm.ListUtils;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.device.DeviceComm;
import com.zkxltech.domain.ClassInfo;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.StudentInfo;
import com.zkxltech.service.ClassInfoService;
import com.zkxltech.sql.ClassInfoSql;
import com.zkxltech.sql.StudentInfoSql;
import com.zkxltech.thread.BaseThread;
import com.zkxltech.thread.CardInfoThread;
import com.zkxltech.thread.ThreadManager;
import com.zkxltech.ui.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassInfoServiceImpl implements ClassInfoService{
    private static final Logger log = LoggerFactory.getLogger(ClassInfoServiceImpl.class);
	private Result result;
	private ClassInfoSql classInfoSql = new ClassInfoSql();
	private StudentInfoSql studentInfoSql = new StudentInfoSql();
	
    @Override
	public Result insertClassInfo(Object object) {
		result = new Result();
		try {
			ClassInfo classInfo =  (ClassInfo) StringUtils.parseJSON(object, ClassInfo.class);
			if (classInfo.getAtype().equals(Constant.CLASS_LOCAL)) {
				classInfo.setClassId(com.ejet.core.util.StringUtils.getUUID());//自动生成班级id
			}
			result = classInfoSql.insertClassInfo(classInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("New class successfully added!");//新增班级成功
			}else {
				result.setMessage("Failed to add new classes！");//新增班级失败
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to add new classes！");//新增班级失败
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result selectClassInfo(Object object) {
		result = new Result();
		try {
			ClassInfo classInfo =  (ClassInfo) StringUtils.parseJSON(object, ClassInfo.class);
			result = classInfoSql.selectClassInfo(classInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Query class success!");//查询班级成功
			}else {
				result.setMessage("Class query failed！");//查询班级失败
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Class query failed！");//查询班级失败
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result deleteClassInfo(Object object) {
		result = new Result();
		try {
			ClassInfo classInfo =  (ClassInfo) StringUtils.parseJSON(object, ClassInfo.class);
			result = classInfoSql.deleteClassInfo(classInfo); //删除班级
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Class deleted successfully!");//删除班级成功
			}else {
				result.setMessage("Class deletion failed！");//删除班级失败
				return result;
			}
			StudentInfo studentInfo = new StudentInfo();
			studentInfo.setClassId(classInfo.getClassId());  //删除学生
			studentInfoSql.deleteStudent(studentInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Class deleted successfully!");//删除班级成功
			}else {
				result.setMessage("Class deletion failed！");//删除班级失败
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Class deletion failed！");//删除班级失败
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result updateClassInfo(Object object) {
		result = new Result();
		try {
			ClassInfo classInfo =  (ClassInfo) StringUtils.parseJSON(object, ClassInfo.class);
			result = classInfoSql.updateClassInfo(classInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("修改班级成功!");
			}else {
				result.setMessage("修改班级失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("修改班级失败！");
			result.setDetail(IOUtils.getError(e));
			log.error(IOUtils.getError(e));
			return result;
		}
	}

	/**清除白名单*/
    @Override
    public Result clearWl(Object param) {
        Result r = new Result();
        r.setRet(Constant.ERROR);
        
        try {
        	r = EquipmentServiceImpl.getInstance().clear_wl();
            if (Constant.ERROR.equals(r.getRet())) {
                r.setMessage("Removal of failure");//清除失败
                return r;
            }
            StudentInfoSql studentInfoSql = new StudentInfoSql();
            r = studentInfoSql.updateStatus(Constant.BING_NO);
            if (r.getRet().equals(Constant.ERROR)) {
                return r;
            }
            if (Constant.SUCCESS == r.getRet()) {
//                JSONObject jsono = JSONObject.fromObject(param);
//                if (jsono.containsKey("classId")) {
//                    BrowserManager.refreshStudent(jsono.getString("classId"));
//                }
                r.setRet(Constant.SUCCESS);
                r.setMessage("Clear success");//清除成功
                return r;
            }
        } catch (Exception e) {
            r.setDetail(IOUtils.getError(e));
            log.error(IOUtils.getError(e));
        }
        r.setMessage("Removal of failure");//清除失败
        return r;
    }

	@Override
	public Result bindStart(Object param) {
		Result r = new Result();
		r.setRet(Constant.ERROR);
		// 每次调用绑定方法先清空,再存
		RedisMapBind.clearCardIdMap();
		RedisMapBind.clearBindMap();
		/* 停止所有线程 */
		ThreadManager.getInstance().stopAllThread();
		try {
			/** 根据班级id查询学生信息 */
			StudentInfoServiceImpl sis = new StudentInfoServiceImpl();
			Result result = sis.selectStudentInfo(param);
			List<StudentInfo> studentInfos = (List) result.getItem();//查询所有的学生信息
			if (result == null || ListUtils.isEmpty(studentInfos)) {
				r.setMessage("You have not uploaded the student information");//您还未上传学生信息
				return r;
			}
			/** 将查出来的学生信息按学生编号进行分类,并存入静态map中 */
			Map<String, StudentInfo> studentInfoMap = new HashMap<>();
			/** 按绑定状态进行分类 */
			int bind = 0, notBind = 0;
			//未绑定的学生信息
			List<StudentInfo> noStudentInfos = new ArrayList<>();
			//已绑定的学生信息
			List<StudentInfo> studentInfosl = new ArrayList<>();
			for (StudentInfo studentInfo : studentInfos) {
				if (studentInfo.getStatus().equals(Constant.BING_YES)) {
					++bind;
					studentInfosl.add(studentInfo);
				} else {
					++notBind;
					noStudentInfos.add(studentInfo);
				}
				if (!StringUtils.isEmpty(studentInfo.getIclickerId())) {
					studentInfoMap.put(studentInfo.getIclickerId(), studentInfo);
				}
			}

			int str = DeviceComm.wirelessBindStart(1, "");
			if (str < 0) {
				r.setRet(Constant.ERROR);
				r.setMessage("指令发送失败");
				return r;
			}
			RedisMapBind.setNoStudentInfos(noStudentInfos);
			RedisMapBind.setStudentInfosl(studentInfosl);
			RedisMapBind.setStudentInfoMap(studentInfoMap);
			BaseThread thread = new CardInfoThread();
			thread.start();
			/* 添加线程管理 */
			ThreadManager.getInstance().addThread(thread);

			Global.setModeMsg(Constant.BUSINESS_BIND);
			r.setRet(Constant.SUCCESS);
			r.setMessage("operate successfully\n");//操作成功

			/** 存入静态map */
			RedisMapBind.getBindMap().put("studentName", null);
			RedisMapBind.getBindMap().put("code", str);
			RedisMapBind.getBindMap().put("accomplish", bind);
			RedisMapBind.getBindMap().put("notAccomplish", notBind);
		} catch (Exception e) {
			log.error(IOUtils.getError(e));
			r.setRet(Constant.ERROR);
			r.setMessage("指令发送失败");
		}

		return r;

	}
    
    @Override
    public Result bindStop() {
        Result r = new Result();
        Global.setModeMsg(Constant.BUSINESS_NORMAL);
        try{
            /*停止所有线程*/
            ThreadManager.getInstance().stopAllThread();
          r = EquipmentServiceImpl.getInstance().bind_stop();
        }catch (Exception e) {
            log.error("", e);
            r.setMessage("系统异常");
            r.setDetail(IOUtils.getError(e));
        }
        return r;
    }
}
