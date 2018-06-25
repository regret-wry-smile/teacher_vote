package com.zkxltech.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.io.IOUtils;
import com.ejet.core.util.io.ImportExcelUtils;
import com.zkxltech.domain.QuestionInfo;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.TestPaper;
import com.zkxltech.service.TestPaperService;
import com.zkxltech.sql.QuestionInfoSql;
import com.zkxltech.sql.TestPaperSql;
import com.zkxltech.ui.util.StringUtils;

import net.sf.json.JSONArray;

public class TestPaperServiceImpl implements TestPaperService{
	private Result result;
	private TestPaperSql testPaperSql = new TestPaperSql();
	private QuestionInfoSql questionInfoSql = new QuestionInfoSql();
	@Override
	public Result insertTestPaper(Object testInfo, Object questionInfos) {
		result = new Result();
		try {
			TestPaper testPaper =  (TestPaper) StringUtils.parseJSON(testInfo, TestPaper.class);
			List<QuestionInfo> questionInfos1  = (List<QuestionInfo>) JSONArray.toCollection(JSONArray.fromObject(questionInfos), QuestionInfo.class);
			//插入试卷
			result = testPaperSql.insertTestPaper(testPaper);
			if (Constant.ERROR.equals(result.getRet())) {
				result.setMessage("插入试卷信息失败！");
				return result;
			}
			result = questionInfoSql.insertQuestionInfo(questionInfos1);
			if (Constant.ERROR.equals(result.getRet())) {
				//回滚
				testPaperSql.deleteTestPaper();
				result.setMessage("插入题目信息失败！");
				return result;
			}
			result.setMessage("添加试卷成功！");
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("添加试卷失败！");
			result.setDetail(IOUtils.getError(e));
			return result;
		}
	}
	@Override
	public Result importTestPaper(Object object) {
		result = new Result();
		try {
			String fileName = String.valueOf(object);
			result = questionInfoSql.importQuestion(ImportExcelUtils.getBankListByExcel2(new FileInputStream(new File(fileName)), fileName));
			if (Constant.ERROR.equals(result.getRet())) {
				result.setMessage("添加试卷信息失败！");
				return result;
			}
			result.setMessage("添加试卷成功！");
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("添加试卷失败！");
			result.setDetail(IOUtils.getError(e));
			return result;
		}
	}
	@Override
	public Result selectTestPaper(Object object) {
		result = new Result();
		try {
			TestPaper testPaper =  (TestPaper) StringUtils.parseJSON(object, TestPaper.class);
			result = testPaperSql.selectTestPaper(testPaper);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("查询试卷成功!");
			}else {
				result.setMessage("查询试卷失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("查询试卷失败！");
			result.setDetail(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result deleteTestPaper(Object object) {
		result = new Result();
		try {
			TestPaper testPaper =  (TestPaper) StringUtils.parseJSON(object, TestPaper.class);
			/*根据试卷id删除题目*/
			QuestionInfo questionInfo = new QuestionInfo();
			questionInfo.setTestId(testPaper.getTestId());
			result = questionInfoSql.deleteQuestionInfo(questionInfo);
			if(Constant.ERROR.equals(result.getRet())){
				result.setMessage("删除题目失败!");
				return result;
			}
			result = testPaperSql.deleteTestPaper(testPaper);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("删除试卷成功!");
			}else {
				result.setMessage("删除试卷失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("删除班级失败！");
			result.setDetail(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result updateTestPaper(Object object, Object questionInfos) {
		result = new Result();
		try {
			TestPaper testPaper =  (TestPaper) StringUtils.parseJSON(object, TestPaper.class);
			List<QuestionInfo> questionInfos1  = (List<QuestionInfo>) JSONArray.toCollection(JSONArray.fromObject(questionInfos), QuestionInfo.class);
			Result result = questionInfoSql.updateStudentsById(questionInfos1);
			if (Constant.ERROR.equals(result.getRet())) {
				result.setMessage("修改题目信息成功!");
				return result;
			}
			result = testPaperSql.updateTestPaper(testPaper);
			if (Constant.SUCCESS.equals(result.getRet())) {
				result.setMessage("修改成功!");
			}else {
				result.setMessage("修改失败！");
			}
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("修改失败！");
			result.setDetail(IOUtils.getError(e));
			return result;
		}
	}

}