package com.zkxltech.ui.functions;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import com.ejet.cache.RedisMapMultipleAnswer;
import com.ejet.cache.RedisMapSingleAnswer;
import com.ejet.core.util.constant.Constant;
import com.zkxltech.domain.Result;
import com.zkxltech.service.AnswerInfoService;
import com.zkxltech.service.ServerService;
import com.zkxltech.service.impl.AnswerInfoServiceImpl;
import com.zkxltech.service.impl.ServerServiceImpl;

import net.sf.json.JSONObject;

/**
 * 【答题模块页面调用方法】
 *
 */
public class AnswerFunctionManage extends BrowserFunction{
	private AnswerInfoService answerInfoService = new AnswerInfoServiceImpl();
	private ServerService serverService = new ServerServiceImpl();
	public AnswerFunctionManage(Browser browser, String name) {
		super(browser, name);
	}
	@Override
	public Object function(Object[] params) {
		Result result = new Result();
		if (params.length>0) {
			String method = (String) params[0]; //页面要调用的方法
			switch (method) {
			case "upload_server":
				result = serverService.uploadServer(params[1]);
				break;
			case "single_answer": //开始单题单选答题
			    result = answerInfoService.singleAnswer(params[1]);
                break;
			case "stop_single_answer":
			    //获取每个答案对应的作答人数
			    result = answerInfoService.stopSingleAnswer();
			    break;
			case "get_single_answer_num":
			    //获取单选作答人数
			    return RedisMapSingleAnswer.getSingleAnswerNum();
			case "get_single_answer":
			    //获取每个答案对应的作答人数
			    return RedisMapSingleAnswer.getSingleAnswer();
			case "get_single_answer_studentName":
                //获取每个答案对应的学生名称
                return RedisMapSingleAnswer.getSingleAnswerStudentName(params[1]);
			case "get_single_range":
				//获取多选范围
               return RedisMapSingleAnswer.getRange();
			case "start_multiple_answer": //开始多选答题
                result = answerInfoService.startMultipleAnswer(params[1]);
                break;
			case "stop_multiple_answer":
			    //获取每个答案对应的作答人数
			    result = answerInfoService.stopMultipleAnswer();
			    break;
			case "get_multiple_range":
				//获取多选范围
               return RedisMapMultipleAnswer.getRange();
			case "get_multiple_answer_num":
				//获取多选作答人数
               return RedisMapMultipleAnswer.getAnswerNum();
			case "get_multiple_answer_detail":
				//获取多选作答详情
               return RedisMapMultipleAnswer.getEveryAnswerInfoBar();
			case "get_answer_info_sum":
				//获取多选作答详情 选择答案对应的人员
               return RedisMapMultipleAnswer.getAnswerInfoSum();
			case "start_class_test_objective": //开始客观答题	
                result = answerInfoService.startObjectiveAnswer(params[1]);
                break;
			case "stop_class_test_objective":
				//停止客观题答题
				result = answerInfoService.stopObjectiveAnswer(params[1]);
				break;
			case "get_everybody_answerInfo":
				//获取每个人的作答统计
				result = answerInfoService.getEverybodyAnswerInfo();
				break;
			case "start_class_test_subjective": //开始主答题
                result = answerInfoService.startSubjectiveAnswer(params[1]);
                break;
			case "stop_class_test_subjective":
				//停止主答题答题
				result = answerInfoService.stopSubjectiveAnswer(params[1]);
				break;
			case "check_equipment_status_start":
                //每隔两秒检查一次设备状态
                result = answerInfoService.checkEquipmentStatusStart();
                break;
			case "check_equipment_status_stop":
			    //停止检查设备状态
			    result = answerInfoService.checkEquipmentStatusStop();
			    break;
			default:
				result.setRet(Constant.ERROR);
				result.setMessage("【"+method+"】The instruction was not found");//未找到该指令！
			}
		}else {
			result.setRet(Constant.ERROR);
			result.setMessage("The parameter cannot be empty");//参数不能为空！
		}
		return JSONObject.fromObject(result).toString();
	}
}
