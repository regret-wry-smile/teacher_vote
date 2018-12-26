package com.zkxltech.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import com.zkxltech.domain.Record;
import com.zkxltech.domain.Record2;
import com.zkxltech.domain.Result;
import com.zkxltech.jdbc.DBHelper;
import com.zkxltech.ui.util.StringUtils;



public class RecordSql {
	private DBHelper<Record> dbHelper = new DBHelper<Record>();
	/*批量插入答案*/
	public Result insertRecords(List<Record> records) throws IllegalArgumentException, IllegalAccessException{
		List<String> sqls = new ArrayList<String>();
		Record record = null;
		for (int i = 0; i < records.size(); i++) {
			record = records.get(i);
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("insert into record (");
			Field[] files = dbHelper.getFields(record);
			for (int j = 0; j < files.length; j++) {
				Object obj = dbHelper.getFiledValues(files[j], record);
				if (!StringUtils.isEmpty(obj)) {
					sqlBuilder.append(dbHelper.HumpToUnderline(files[j].getName())+",");
				}
			}
			sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
			sqlBuilder.append(") values (");
			for (int  j= 0; j < files.length; j++) {
				Object obj = dbHelper.getFiledValues(files[j], record);
				if (!StringUtils.isEmpty(obj)) {
					sqlBuilder.append("'"+dbHelper.getFiledValues(dbHelper.getFields(record)[j], record)+"'");
					sqlBuilder.append(",");
				}
			}
			sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
			sqlBuilder.append(")");
			sqls.add(sqlBuilder.toString());
			System.out.println(sqlBuilder.toString());
		}
		return dbHelper.onUpdateByGroup(sqls);
	}
	
	/*查询答题信息*/
	public Result selectRecord(Record record) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select * from record");
		Field[] files = dbHelper.getFields(record);
		int index = 0;
		for (int i = 0; i < files.length; i++) {
			Object obj = dbHelper.getFiledValues(files[i], record);
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
		return dbHelper.onQuery(sqlBuilder.toString(), record);
	}
	
	/**
	 * 根据班级id，科目，课时id，试卷id,学生id修改记录上传状态（客观题）
	 * 
	 */
	public Result updateObjectiveRecord(List<Record> records) throws IllegalArgumentException, IllegalAccessException{
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < records.size(); i++) {
			StringBuilder sqlBuilder = new StringBuilder();
			Record record = records.get(i);
			sqlBuilder.append("update record set is_objective_upload = '"+record.getIsObjectiveUpload()+"' where test_id = '"+ record.getTestId()+"'"+
					"and class_id = '"+record.getClassId()+"' and subject = '"+record.getSubject()+"' and class_hour_id = '"+record.getClassHourId()+"' and student_id = '"+record.getStudentId()+"'");
			sqls.add(sqlBuilder.toString());
		}
		return DBHelper.onUpdateByGroup(sqls);
	}
	
	/**
	 * 根据班级id，科目，课时id，试卷id,学生id修改记录上传状态（主观题）
	 * 
	 */
	public Result updateSubjectiveRecord(List<Record> records) throws IllegalArgumentException, IllegalAccessException{
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < records.size(); i++) {
			StringBuilder sqlBuilder = new StringBuilder();
			Record record = records.get(i);
			sqlBuilder.append("update record set is_subjective_upload = '"+record.getIsSubjectiveUpload()+"' where test_id = '"+ record.getTestId()+"'"+
					"and class_id = '"+record.getClassId()+"' and subject = '"+record.getSubject()+"' and class_hour_id = '"+record.getClassHourId()+"' and student_id = '"+record.getStudentId()+"'");
			sqls.add(sqlBuilder.toString());
		}
		return DBHelper.onUpdateByGroup(sqls);
	}
//	
//	
//	/*删除答题信息*/
//	public Result deleteAnswerInfo(AnswerInfo answerInfo) throws IllegalArgumentException, IllegalAccessException{
//		StringBuilder sqlBuilder = new StringBuilder();
//		sqlBuilder.append("delete from answer_info");
//		Field[] files = dbHelper.getFields(answerInfo);
//		int index = 0;
//		for (int i = 0; i < files.length; i++) {
//			Object obj = dbHelper.getFiledValues(files[i], answerInfo);
//			if (!StringUtils.isEmpty(obj)) {
//				if (index == 0) {
//					sqlBuilder.append(" where ");
//				}else {
//					sqlBuilder.append(" and ");
//				}
//				sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName())+" = ?");
//				index++;
//			}
//		}
//		return dbHelper.onUpdate(sqlBuilder.toString(), answerInfo);
//	}
	
	public Result deleteRecord(Record record) throws IllegalArgumentException, IllegalAccessException{
	     StringBuilder sqlBuilder = new StringBuilder();
          sqlBuilder.append("delete from record");
          Field[] files = dbHelper.getFields(record);
          int index = 0;
          for (int i = 0; i < files.length; i++) {
  			Object obj = dbHelper.getFiledValues(files[i], record);
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
        return dbHelper.onUpdate(sqlBuilder.toString(), record);
  }

    public Result deleteRecordByStudentId(Record record) throws IllegalArgumentException, IllegalAccessException {
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> studentIds = record.getStudentIds();
        sqlBuilder.append("delete from record where test_id = \""+record.getTestId()+"\" and student_id in (");
        for (int i = 0; i< studentIds.size();i++) {
            sqlBuilder.append(studentIds.get(i));
            if (i != studentIds.size()-1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(")");
      return dbHelper.onUpdate(sqlBuilder.toString(), null);
    }
	
}
