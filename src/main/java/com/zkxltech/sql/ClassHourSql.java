package com.zkxltech.sql;

import com.alibaba.fastjson.JSONObject;
import com.zkxltech.domain.ClassHour;
import com.zkxltech.domain.Result;
import com.zkxltech.jdbc.DBHelper;
import com.zkxltech.ui.util.StringUtils;

import java.lang.reflect.Field;


/**
 * 课程
 * @author zkxl
 *
 */
public class ClassHourSql {
	private DBHelper<ClassHour> dbHelper = new DBHelper<ClassHour>();
	
	
	/*查询课时信息*/
	public Result selectClassHour(ClassHour classHour) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select * from class_hour");
		Field[] files = dbHelper.getFields(classHour);
		int index = 0;
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], classHour);
			if (!StringUtils.isEmpty(obj)) {
				if (index == 0) {
					sqlBuilder.append(" where ");
				}else {
					sqlBuilder.append(" and ");
				}
				sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName())+" = ?");
				index++;
			}
		}
		return dbHelper.onQuery(sqlBuilder.toString(), classHour);
	}
	
	
	/*查询课时列表*/
	public Result selectClassHour(String classId, String subjectName) throws IllegalArgumentException, IllegalAccessException{
		String sql = "select DISTINCT(class_hour_id),class_hour_name from class_hour where class_id = '"+classId+"' and subject_name = '"+subjectName+"'";
		String[] key = {"class_hour_id","class_hour_name"};
		return DBHelper.onQuery(sql, key, null);
	}
	
	public static void main(String[] args) {
		try {
			Result result = new ClassHourSql().selectClassHour("1", "语文");
			System.out.println(JSONObject.toJSONString(result));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增课程
	 * @param classHour
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Result insertClassHour(ClassHour classHour) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder(0);
		sqlBuilder.append("insert into class_hour");
		Field[] files = dbHelper.getFields(classHour);
		sqlBuilder.append("(");
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], classHour);
			if (!StringUtils.isEmpty(obj)) {
				sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName()));
				sqlBuilder.append(",");
			}
		}
		sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
		sqlBuilder.append(") values (");
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], classHour);
			if (!StringUtils.isEmpty(obj)) {
				sqlBuilder.append("?");
				sqlBuilder.append(",");
			}
		}
		sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
		sqlBuilder.append(")");
		return dbHelper.onUpdate(sqlBuilder.toString(), classHour);
	}
	
	/**
	 * 删除课程
	 * @param classHour
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Result deleteAnswerInfo(ClassHour classHour) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("delete from class_hour");
		Field[] files = dbHelper.getFields(classHour);
		int index = 0;
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], classHour);
			if (!StringUtils.isEmpty(obj)) {
				if (index == 0) {
					sqlBuilder.append(" where ");
				}else {
					sqlBuilder.append(" and ");
				}
				sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName())+" = ?");
				index++;
			}
		}
		return dbHelper.onUpdate(sqlBuilder.toString(), classHour);
	}
	
	/**
	 * 根据主键更新课程
	 * 
	 * */
	public Result updateTestPaper(ClassHour classHour) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("update class_hour");
		Field[] files = dbHelper.getFields(classHour);
		int index = 0;
		for (int i = 1; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], classHour);
			if (!StringUtils.isEmpty(obj)) {
				if (index == 0) {
					sqlBuilder.append(" set ");
				}else {
					sqlBuilder.append(" , ");
				}
				sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName())+" =  '"+obj+"'");
				index++;
			}
		}
		sqlBuilder.append(" where class_name = '"+classHour.getClassName()+"' and subject_name ='"+classHour.getSubjectName()+"'");
		return dbHelper.onUpdate(sqlBuilder.toString());
	}
}
