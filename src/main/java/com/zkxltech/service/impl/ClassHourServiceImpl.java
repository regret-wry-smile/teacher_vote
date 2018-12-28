package com.zkxltech.service.impl;

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
				result.setMessage("查询课程列表成功!");
			}else {
				result.setMessage("查询课程列表失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("查询课程列表失败！");
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
				result.setMessage("This scene already exists, please fill in again！");//此场景已有，请重新填写!

			}else {
				classHour.setClassHourId(com.ejet.core.util.StringUtils.getUUID());
				classHour.setStartTime(com.ejet.core.util.StringUtils.formatDateTime(new Date()));
				Global.setClassHour(classHour);
				result = classHourSql.insertClassHour(classHour);
				if (Constant.SUCCESS.equals(result.getRet())) {
					result.setMessage("New courses successfully added!");//新增课程成功
				} else {
					result.setMessage("New course failed！");//新增课程失败
				}
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("New course failed！");//新增课程失败
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
				result.setMessage("Course deleted successfully!");//删除课程成功
			}else {
				result.setMessage("Failed to delete course！");//删除课程失败
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("Failed to delete course！");//删除课程失败
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
				result.setMessage("删除课程成功!");
			}else {
				result.setMessage("删除课程失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("删除课程失败！");
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
			result.setMessage("开始上课！");
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("上课失败！");
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
				if (StringUtils.isEmptyList(result.getItem())) {
					result.setMessage("该班级没有学生！");
					result.setRet(Constant.ERROR);
					return result;
				}
				List<StudentInfo> studentInfos = (List<StudentInfo>)result.getItem();
				for (int i=0;i<studentInfos.size();i++){
					StudentInfo stu = studentInfos.get(i);
					if ("************".equals(stu.getIclickerId())){
						stu.setIclickerId("************");
					}
				}
				Global.setStudentInfos(studentInfos);
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
		result = new Result();
		result.setRet(Constant.SUCCESS);
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
			result.setMessage("获取当前班级信息成功！");
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("获取当前班级信息失败！");
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
				result.setMessage("获取当前班级对应的科目场景信息成功!");
			}else {
				result.setMessage("获取当前班级对应的科目场景信息失败！");
        }
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("获取当前班级对应的科目场景信息失败！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}
}
