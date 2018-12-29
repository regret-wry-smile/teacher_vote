package com.zkxltech.sql;

import com.zkxltech.domain.Result;
import com.zkxltech.domain.Score;
import com.zkxltech.jdbc.DBHelper;
import com.zkxltech.ui.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PeopleScoreSql {
    private DBHelper<Score> dbHelper = new DBHelper<Score>();

    /*插入评分信息*/
    public Result insertScore(Score score) throws IllegalArgumentException, IllegalAccessException{
        StringBuilder sqlBuilder = new StringBuilder(0);
        sqlBuilder.append("insert into people_score ");
        Field[] files = dbHelper.getFields(score);
        sqlBuilder.append("(");
        for (int i = 0; i < files.length; i++) {
            Object obj = dbHelper.getFiledValues(files[i], score);
            if (!StringUtils.isEmpty(obj)) {
                sqlBuilder.append(dbHelper.HumpToUnderline(files[i].getName()));
                sqlBuilder.append(",");
            }
        }
        sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
        sqlBuilder.append(") values (");
        for (int i = 0; i < files.length; i++) {
            Object obj = dbHelper.getFiledValues(files[i], score);
            if (!StringUtils.isEmpty(obj)) {
                sqlBuilder.append("?");
                sqlBuilder.append(",");
            }
        }
        sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
        sqlBuilder.append(")");
        return dbHelper.onUpdate(sqlBuilder.toString(), score);
    }

    /*批量插入评分信息*/
    public Result insertScores(List<Score> scores) throws IllegalArgumentException, IllegalAccessException{
        List<String> sqls = new ArrayList<String>();
        Score score = null;
        for (int i = 0; i < scores.size(); i++) {
            score = scores.get(i);
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("insert into people_score (");
            Field[] files = dbHelper.getFields(score);
            for (int j = 0; j < files.length; j++) {
                Object obj = dbHelper.getFiledValues(files[j], score);
                if (!StringUtils.isEmpty(obj)) {
                    sqlBuilder.append(dbHelper.HumpToUnderline(files[j].getName())+",");
                }
            }
            sqlBuilder = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.lastIndexOf(",")));
            sqlBuilder.append(") values (");
            for (int  j= 0; j < files.length; j++) {
                Object obj = dbHelper.getFiledValues(files[j], score);
                if (!StringUtils.isEmpty(obj)) {
                    sqlBuilder.append("'"+dbHelper.getFiledValues(dbHelper.getFields(score)[j], score)+"'");
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


    /*查询评分信息*/
    public Result selectScore(Score score) throws IllegalArgumentException, IllegalAccessException{
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from people_score");
        Field[] files = dbHelper.getFields(score);
        int index = 0;
        for (int i = 0; i < files.length; i++) {
            Object obj = dbHelper.getFiledValues(files[i], score);
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
        return dbHelper.onQuery(sqlBuilder.toString(), score);
    }

    public Result deleteScore(Score score) throws IllegalArgumentException, IllegalAccessException{
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("delete from people_score");
        Field[] files = dbHelper.getFields(score);
        int index = 0;
        for (int i = 0; i < files.length; i++) {
            Object obj = dbHelper.getFiledValues(files[i], score);
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
        return dbHelper.onUpdate(sqlBuilder.toString(), score);
    }

//    public Result deleteRecordByStudentId(Record2 record) throws IllegalArgumentException, IllegalAccessException {
//        StringBuilder sqlBuilder = new StringBuilder();
//        List<String> studentIds = record.getStudentIds();
//        sqlBuilder.append("delete from record2 where test_id = \""+record.getTestId()+"\" and student_id in (");
//        for (int i = 0; i< studentIds.size();i++) {
//            sqlBuilder.append(studentIds.get(i));
//            if (i != studentIds.size()-1) {
//                sqlBuilder.append(",");
//            }
//        }
//        sqlBuilder.append(")");
//        return dbHelper.onUpdate(sqlBuilder.toString(), null);
//    }

}
