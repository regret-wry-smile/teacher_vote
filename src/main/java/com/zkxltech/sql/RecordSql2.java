package com.zkxltech.sql;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import com.zkxltech.domain.Record;
import com.zkxltech.domain.Record2;
import com.zkxltech.domain.Result;
import com.zkxltech.jdbc.DBHelper;
import com.zkxltech.ui.util.StringUtils;



public class RecordSql2 {
	
	private DBHelper<Record2> dbHelper = new DBHelper<Record2>();
	/*批量插入答案*/
	public Result insertRecords(List<Record2> records) throws IllegalArgumentException, IllegalAccessException{
		List<String> sqls = new ArrayList<String>();
		Record2 record = null;
		for (int i = 0; i < records.size(); i++) {
			record = records.get(i);
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("insert into record2 (");
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
	public Result selectRecord(Record2 record) throws IllegalArgumentException, IllegalAccessException{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select * from record2");
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
				if("answer_start".equals(dbHelper.HumpToUnderline(files[i].getName()))){
					sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName())+" >= ?");
				}else if ("answer_end".equals(dbHelper.HumpToUnderline(files[i].getName()))){
					
					String d = record.getAnswerEnd();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					//加一天
					try {
						Date dd = df.parse(d);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(dd);
						calendar.add(Calendar.DAY_OF_MONTH, 1);//加一天
						record.setAnswerEnd(df.format(calendar.getTime()).toString());
						
						} catch (Exception e) {
						e.printStackTrace();
					}
					sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName())+" <= ?");
					
				}else{
					sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName())+" = ?");
				}
				
				index++;
			}
		}
		System.out.println(sqlBuilder.toString());
		return dbHelper.onQuery(sqlBuilder.toString(), record);
	}
	
	public Result deleteRecord(Record2 record) throws IllegalArgumentException, IllegalAccessException{
	     StringBuilder sqlBuilder = new StringBuilder();
          sqlBuilder.append("delete from record2");
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

    public Result deleteRecordByStudentId(Record2 record) throws IllegalArgumentException, IllegalAccessException {
        StringBuilder sqlBuilder = new StringBuilder();
        List<Integer> Ids = record.getIds();
        sqlBuilder.append("delete from record2 where id in (");
        for (int i = 0; i< Ids.size();i++) {
            sqlBuilder.append(Ids.get(i));
            if (i != Ids.size()-1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(")");
      return dbHelper.onUpdate(sqlBuilder.toString(), null);
    }
	
}
