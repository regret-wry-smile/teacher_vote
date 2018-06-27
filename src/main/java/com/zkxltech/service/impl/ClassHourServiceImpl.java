package com.zkxltech.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.cache.RedisMapScore;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.domain.ClassHour;
import com.zkxltech.domain.ClassInfo;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.StudentInfo;
import com.zkxltech.domain.TestPaper;
import com.zkxltech.service.ClassHourService;
import com.zkxltech.sql.ClassHourSql;
import com.zkxltech.ui.util.StringUtils;

import net.sf.json.JSONArray;

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
				result.setMessage("查询课程列表成功!");
			}else {
				result.setMessage("查询课程列表失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("查询课程列表失败！");
			result.setDetail(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result insertClassInfo(Object object) {
		result = new Result();
		try {
			ClassHour classHour =  (ClassHour) StringUtils.parseJSON(object, ClassHour.class);
			classHour.setClassHourId(com.ejet.core.util.StringUtils.getUUID());
			classHour.setStartTime(com.ejet.core.util.StringUtils.formatDateTime(new Date()));
			result = classHourSql.insertClassHour(classHour);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("新增课程成功!");
			}else {
				result.setMessage("新增课程失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("新增课程失败！");
			result.setDetail(IOUtils.getError(e));
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
				result.setMessage("删除课程成功!");
			}else {
				result.setMessage("删除课程失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("删除课程失败！");
			result.setDetail(IOUtils.getError(e));
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
				result.setMessage("删除课程成功!");
			}else {
				result.setMessage("删除课程失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("删除课程失败！");
			result.setDetail(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result startClass(Object classId, Object classHourObj) {
		try {
			
			ClassHour classHour = (ClassHour) StringUtils.parseJSON(classHourObj, ClassHour.class);
			
			Global.setClassId((String)classId);
			
			classHour.setClassId((String)classId);
			
			result = refreshGload();
			
			Global.setClassHour(classHour);
			
//			classHourSql.insertClassHour(classHour);
			
			result.setRet(Constant.SUCCESS);
			result.setMessage("开始上课！");
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("上课失败！");
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
					result.setMessage("未找到班级信息！");
					result.setRet(Constant.ERROR);
					return result;
				}
			}else {
				result.setMessage("查询班级信息失败！");
				result.setRet(Constant.ERROR);
				return result;
			}
			
			StudentInfo studentInfo = new StudentInfo();
			studentInfo.setClassId(Global.classId);
			result = new StudentInfoServiceImpl().selectStudentInfo(studentInfo);
			if (Constant.SUCCESS.equals(result.getRet())) {
				Global.setStudentInfos((List<StudentInfo>)result.getItem());
			}else {
				result.setMessage("查询学生信息失败！");
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
		try {
			Global.setClassHour(null);
			Global.setClassId(null);
			Global.setClassInfo(null);
			Global.setStudentInfos(null);
			
			result.setRet(Constant.SUCCESS);
			result.setMessage("下课！");
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("下课失败！");
		}
		return result;
	}

}
