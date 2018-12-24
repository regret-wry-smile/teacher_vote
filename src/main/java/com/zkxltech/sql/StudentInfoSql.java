package com.zkxltech.sql;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ejet.core.util.comm.ListUtils;
import com.ejet.core.util.constant.Constant;
import com.zkxltech.domain.ClassInfo;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.StudentInfo;
import com.zkxltech.jdbc.DBHelper;
import com.zkxltech.jdbc.DBHelper2;
import com.zkxltech.ui.util.StringUtils;



public class StudentInfoSql {
	private DBHelper<StudentInfo> dbHelper = new DBHelper<StudentInfo>();
	
	/*批量插入学生*/
	@SuppressWarnings("static-access")
	public Result importStudent(List<List<Object>> rowList,ClassInfo classInfo){
		Result result = verifyStudent(rowList);
		if (Constant.ERROR.equals(result.getRet())) {
			return result;
		}
		List<String> sqls = new ArrayList<String>();
		String sql = "";
		String classId = classInfo.getClassId();
		String className = classInfo.getClassName();
//		sqls.add("delete from class_info where class_id = '" + classId+"'"); //删除原来班级信息
//		sqls.add("insert into class_info (class_id,class_name,atype) values('"+classId+"','"+
//				className+"','0')"); //添加班级信息
		sqls.add("delete from student_info where class_id = '" + classId+"'"); //删除原来的班级学生
		for (int i = 0; i < (rowList.size()>120?120:rowList.size()); i++) {
			sql = "insert into student_info (class_id,class_name,student_id,student_name,status) values('"+classId+"','"+
					className+"','"+rowList.get(i).get(0)+"','"+rowList.get(i).get(1)+"','"+"0')";
			sqls.add(sql);
		}
		result = dbHelper.onUpdateByGroup(sqls);
		result.setRemak(classId);
		return result;
	}
	
	
	public Result verifyStudent(List<List<Object>> rowList){
		Result result = new Result();
		result.setRet(Constant.ERROR);
		int rows = rowList.size();
		
		
		List<String> studentIds = new ArrayList<String>();
		for (int i = 0; i < rowList.size(); i++) {
//			if (rowList.get(i).size() != 3) {
//				result.setMessage("第"+(i+1)+"行格式错误！");
//				return result;
//			}
			String studentId = (String) rowList.get(i).get(0);
			String studentName = (String) rowList.get(i).get(1);
            if (studentId.length() > 10) {
            	  result.setMessage("第"+(i+2)+"行学生编号错误！");
                  return result;
			}    
            if (studentName.length() > 10) {
          	  result.setMessage("第"+(i+2)+"行学生姓名错误！");
                return result;
			}
            
            studentIds.add(studentId);
		}

		if (studentIds.stream().distinct().collect(Collectors.toList()).size() != studentIds.size()) {
			  result.setMessage("学号有重复！");
              return result;
		};

		result.setRet(Constant.SUCCESS);
		return result;
	}

	/**
	 * 校验答题器编号
	 * @param answer
	 * @return
	 */
	private boolean verifyIclickerId(String iclickerId){
		if (StringUtils.isEmpty(iclickerId)) {
			return true;
		}
		Pattern p = Pattern.compile("^[0-9]{10}+$");
		Matcher m = p.matcher(iclickerId);
		return m.matches();
	}
	
	
	/*查询学生*/
	public Result selectStudentInfo(StudentInfo studentInfo) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select * from student_info");
		Field[] files = dbHelper.getFields(studentInfo);
		int index = 0;
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], studentInfo);
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
		sqlBuilder.append(" order by id asc");
		return dbHelper.onQuery(sqlBuilder.toString(), studentInfo);
	}
	
	/*新增学生*/
	public Result insertStudentInfo(StudentInfo studentInfo) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder(0);
		sqlBuilder.append("insert into student_info ");
		Field[] files = dbHelper.getFields(studentInfo);
		sqlBuilder.append("(");
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], studentInfo);
			if (!StringUtils.isEmpty(obj)) {
				sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName()));
				sqlBuilder.append(",");
			}
		}
		sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
		sqlBuilder.append(") values (");
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], studentInfo);
			if (!StringUtils.isEmpty(obj)) {
				sqlBuilder.append("?");
				sqlBuilder.append(",");
			}
		}
		sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
		sqlBuilder.append(")");
		return dbHelper.onUpdate(sqlBuilder.toString(), studentInfo);
	}
	
	/**
	 * 主键删除学生
	 * @param studentInfo
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Result deleteStudentById(List<Integer> ids) throws IllegalArgumentException, IllegalAccessException{
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < ids.size(); i++) {
			sqls.add("delete from student_info where id = "+ids.get(i));
		}
		return DBHelper.onUpdateByGroup(sqls);
	}
	
	/*删除学生*/
	public Result deleteStudent(StudentInfo studentInfo) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("delete from student_info");
		Field[] files = dbHelper.getFields(studentInfo);
		int index = 0;
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], studentInfo);
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
		return dbHelper.onUpdate(sqlBuilder.toString(), studentInfo);
	}
	
	/*根据主键更新学生*/
	public Result updateStudentById(StudentInfo studentInfo) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("update student_info");
		Field[] files = dbHelper.getFields(studentInfo);
		int index = 0;
		for (int i = 1; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], studentInfo);
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
		sqlBuilder.append(" where id = '"+studentInfo.getId()+"'");
		return dbHelper.onUpdate(sqlBuilder.toString(), null);
	}
	/**
	 * 批量解绑学生
	 * @param studentInfo
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Result clearStudentByIds(List<Integer> ids) throws IllegalArgumentException, IllegalAccessException{
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < ids.size(); i++) {
			sqls.add("update student_info set iclicker_id = '************',status = '0'  where id = "+ids.get(i));
		}
		return DBHelper.onUpdateByGroup(sqls);
	}
	/**
	 * 根据班级名称获取服务器上的学生
	 * @param className 班级id
	 * @return
	 */
	public static Result getServerStudent(String classId){
		List<Object> params = new ArrayList<Object>();
		String sql = "select * from hz_student t1 LEFT JOIN  hz_student_keybroad t2 ON t1.sid = t2.sid where t1.bjid = ?";
		String[] key = {"xm","sid","kh","sex","bjid","id","KID"};
		params.add(classId);
		return DBHelper2.onQuery(sql, key, params);
	}
	
	/**
	 * 批量保存学生信息
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public Result saveStudentByGroup(List<Map<String, Object>> listMaps) throws ClassNotFoundException, SQLException{
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < listMaps.size(); i++) {
			int id = Integer.parseInt((String) listMaps.get(i).get("id"));
			String classId = String.valueOf((int) listMaps.get(i).get("bjid"));
			String studentId = (String) listMaps.get(i).get("kh");
			String studentName = (String) listMaps.get(i).get("xm");
			String kid = (String) listMaps.get(i).get("KID");
			String string = "insert into student_info (class_id,class_name,student_id,student_name,iclicker_id) values('"+classId+"','"
					+classId+"','"+studentId+"','"+studentName+"','"+kid+"');";
			sqls.add(string);
		}
		return DBHelper.onUpdateByGroup(sqls);
	}
	public Result updateStatusByIclickerIds(List<String> iclickerIds,String status) {
	    return updateStatusByIclickerIds(iclickerIds, status, "in");
	}

    public Result updateStatusByIclickerIds(List<String> iclickerIds,String status,String inOrNotIN) {
        if (StringUtils.isEmpty(status)) {
            Result r = new Result();
            r.setRet(Constant.ERROR);
            r.setMessage("缺少参数 :绑定状态不能为空");
            return r;
        }
        if (ListUtils.isEmpty(iclickerIds)) {
            Result r = new Result();
            r.setRet(Constant.ERROR);
            r.setMessage("缺少参数 :卡的编号不能为空");
            return r;
        }
        
        //update student_info set status = '0' where iclicker_id in('3429469477','6666660002','************')
        StringBuilder sb = new StringBuilder("update student_info set status = "+status+" where iclicker_id "+inOrNotIN+"(");
        for (int i = 0; i< iclickerIds.size();i++) {
            sb.append("'");
            sb.append(iclickerIds.get(i));
            sb.append("'");
            if (i != iclickerIds.size()-1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return dbHelper.onUpdate(sb.toString(), null);
    }

    public Result updateStatus(String status) {
        if (StringUtils.isEmpty(status)) {
            Result r = new Result();
            r.setRet(Constant.ERROR);
            r.setMessage("缺少参数 :绑定状态不能为空");
            return r;
        }
        String sb ="update student_info set status = "+status;
        return dbHelper.onUpdate(sb.toString(), null);
    }
}
