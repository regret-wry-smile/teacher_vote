package com.zkxltech.service;

import java.util.List;

import com.zkxltech.domain.AnswerInfo;
import com.zkxltech.domain.Result;

/**
 * 【作答记录】
 *
 */
public interface RecordService {
	/**
	 * 导出文件
	 * @param object 生成的文件名
	 */
	Result exportRecord(Object object);
	/**
	 * 查询主观题作答记录（随堂检测专用）
	 * @param object
	 * @return
	 */
	Result selectSubjectiveRecord(Object object);
	
	/**
	 * 查询客观题作答记录（随堂检测专用）
	 * @param object
	 * @return
	 */
	Result selectObjectiveRecord(Object object);
	/**
	 * 查询每个学生的答题记录情况
	 * @param object
	 * @return
	 */
    Result selectRecord(Object object);
    /**
     * 批量删除学生的试卷作答记录
     * @param object
     * @return
     */
    Result deleteRecord(Object object);
    /**
     * 导出试卷
     * @param object
     * @return
     */
    Result testExport(Object object);
    /**
     * 查询学生对应的试卷答题详情
     * @param object
     * @return
     */
    Result selectStudentRecordDetail(Object object);
    /**
     * 查询单项单选答题结果
     * @param object
     * @return
     */
    Result selectRecord2(Object object);
    /**
     * 导出试卷
     * @param object
     * @return
     */
	Result testExport2(Object object);
}
