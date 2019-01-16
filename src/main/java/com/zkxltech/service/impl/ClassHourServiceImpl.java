package com.zkxltech.service.impl;

import com.ejet.cache.RedisMapAttendance;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.domain.ClassHour;
import com.zkxltech.domain.ClassInfo;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.StudentInfo;
import com.zkxltech.service.ClassHourService;
import com.zkxltech.sql.ClassHourSql;
import com.zkxltech.ui.util.StringUtils;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassHourServiceImpl implements ClassHourService{
	private static final Logger logger = LoggerFactory.getLogger(ClassHourServiceImpl.class);
	private static Result result;
	private ClassHourSql classHourSql = new ClassHourSql();

	@Override
	public Result selectClassInfo(Object classId, Object subjectName) {
		result = new Result();
		try {
			result = classHourSql.selectClassHour((String)classId, (String)subjectName);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Query the course list successfully!");
			}else {
				result.setMessage("Failed to query the course list！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to query the course list！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result insertClassInfo(Object object) {
		result = new Result();
		Result r = new Result();
		try {
			ClassHour classHour =  (ClassHour) StringUtils.parseJSON(object, ClassHour.class);
			r = classHourSql.selectClassHour(classHour);
			List<ClassHour> classHours = (List<ClassHour>) r.getItem();
			if (classHours.size() != 0){
				result.setMessage("This scenario already exists, please fill in again！");//此场景已有，请重新填写!

			}else {
				classHour.setClassHourId(com.ejet.core.util.StringUtils.getUUID());
				classHour.setSubjectId(com.ejet.core.util.StringUtils.getUUID());
				classHour.setStartTime(com.ejet.core.util.StringUtils.formatDateTime(new Date()));
				Global.setClassHour(classHour);
				result = classHourSql.insertClassHour(classHour);
				if (Constant.SUCCESS.equals(result.getRet())) {
					result.setMessage("New scenario successfully added!");//新增课程成功
				} else {
					result.setMessage("New scenario failed！");//新增课程失败
				}
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("New scenario failed！");//新增课程失败
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result deleteClassInfo(Object object) {
		result = new Result();
		try {
			ClassHour classHour =  (ClassHour) StringUtils.parseJSON(object, ClassHour.class);
			result = classHourSql.deleteAnswerInfo(classHour);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("scenario deleted successfully!");//删除课程成功
			}else {
				result.setMessage("Failed to delete scenario！");//删除课程失败
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to delete scenario！");//删除课程失败
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result updateClassInfoTime(Object object) {
		result = new Result();
		try {
			ClassHour classHour =  (ClassHour) StringUtils.parseJSON(object, ClassHour.class);
			classHour.setEndTime(com.ejet.core.util.StringUtils.formatDateTime(new Date()));
			result = classHourSql.updateTestPaper(classHour);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("delete the class sucessfully!");
			}else {
				result.setMessage("delete the class failed！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("delete the class failed！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result startClass(Object classHourObj) {
		try {
			
			ClassHour classHour = (ClassHour) StringUtils.parseJSON(classHourObj, ClassHour.class);
			
			Global.setClassId(classHour.getClassId());
			
			Global.setClassHour(classHour);

			result = refreshGload();
			
			if (Constant.ERROR.equals(result.getRet())) {
				return result;
			}
			result.setRet(Constant.SUCCESS);
			result.setMessage("Start the class！");
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Start the class failed！");
			logger.error(IOUtils.getError(e));
		}
		return result;
	}
	
	/**
	 * 更新当前学生信息和班级信息
	 * @param classId
	 * @return
	 */
	public static Result refreshGload(){
		result = new Result();
		result.setRet(Constant.SUCCESS);
		if (!StringUtils.isEmpty(Global.classId)) {
			ClassInfo classInfo = new ClassInfo();
			classInfo.setClassId(Global.classId);
			result = new ClassInfoServiceImpl().selectClassInfo(classInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				List<ClassInfo>  classInfos = (List<ClassInfo>) result.getItem();
				if (classInfos != null && classInfos.size()>0) {
					Global.setClassInfo(classInfos.get(0));
				}else {
					result.setMessage("Class information not found！");
					result.setRet(Constant.ERROR);
					return result;
				}
			}else {
				result.setMessage("Failed to query class information！");
				result.setRet(Constant.ERROR);
				return result;
			}
			
			StudentInfo studentInfo = new StudentInfo();
			studentInfo.setClassId(Global.classId);
			result = new StudentInfoServiceImpl().selectStudentInfo(studentInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				if (StringUtils.isEmptyList(result.getItem())) {
					result.setMessage("There are no perosons in the group！");
					result.setRet(Constant.ERROR);
					return result;
				}
				List<StudentInfo> studentInfos = (List<StudentInfo>)result.getItem();
				List<StudentInfo> bindStudentInfos = new ArrayList<StudentInfo>();
				List<StudentInfo> allStudentInfos = new ArrayList<StudentInfo>();
				for (int i=0;i<studentInfos.size();i++){
					StudentInfo stu = studentInfos.get(i);
					if("1".equals(stu.getStatus())){
						bindStudentInfos.add(stu);
					}
					if ("************".equals(stu.getIclickerId()) || StringUtils.isEmpty(stu.getIclickerId())){
						stu.setIclickerId("*********"+i);
						
					}
					allStudentInfos.add(stu);
				}
				Global.setStudentInfos(bindStudentInfos);
				Global.setAllStudentInfos(allStudentInfos);
			}else {
				result.setMessage("Failed to search student information！");
				result.setRet(Constant.ERROR);
				return result;
			}
		}
		logger.info("当前班级学生信息>>>"+JSONArray.fromObject(Global.studentInfos));
		return result;
	}

	public static boolean isStartClass() {
		return !StringUtils.isEmpty(Global.classId);
	}

	@Override
	public Result endClass() {
		result = new Result();
		result.setRet(Constant.SUCCESS);
		try {
			ClassHour classHour = Global.getClassHour();
			classHour.setEndTime(com.ejet.core.util.StringUtils.formatDateTime(new Date()));
			result = classHourSql.updateTestPaper(classHour);
			Global.setClassHour(null);
			Global.setClassId(null);
			Global.setClassInfo(null);
			Global.setStudentInfos(null);
            //每次调用签到先清空数据
            RedisMapAttendance.clearAttendanceMap();
            RedisMapAttendance.clearCardIdSet();
			RedisMapAttendance.clearSignMap();
			//将题号清零
            Constant.QUESTION_ID =0;
			result.setRet(Constant.SUCCESS);
			result.setMessage("End the class！");//结束课程
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("End the class failed！");
			logger.error(IOUtils.getError(e));
		}
		return result;
	}

	@Override
	public Result getClassInfo() {
		result = new Result();
		result.setRet(Constant.SUCCESS);
		try {
			result.setItem(Global.getClassInfo());
			result.setRet(Constant.SUCCESS);
			result.setMessage("The current class information was obtained successfully！");//当前类信息获得成功!
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to obtain current class information！");//未能获得当前类信息!
			logger.error(IOUtils.getError(e));
		}
		return result;
	}

	@Override
	public Result getSubject(Object classHourObj){
		result = new Result();
		ClassHour classHour = (ClassHour) StringUtils.parseJSON(classHourObj, ClassHour.class);
		ClassHourSql classHourSql = new ClassHourSql();
		try {
			result = classHourSql.selectClassHour(classHour);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("Successfully obtained the corresponding subject scene information of the current class!");//成功获取当前类对应的主题场景信息!
			}else {
				result.setMessage("Failed to obtain the corresponding subject scene information of the current class！");//未能获得相应的主题场景信息的当前类!
        }
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to obtain the corresponding subject scene information of the current class！");//未能获得相应的主题场景信息的当前类!
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}
}
