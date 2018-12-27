package com.zkxltech.ui.functions;

import com.ejet.core.util.constant.Constant;
import com.zkxltech.domain.Result;
import com.zkxltech.service.ClassHourService;
import com.zkxltech.service.RecordService;
import com.zkxltech.service.impl.ClassHourServiceImpl;
import com.zkxltech.service.impl.RecordServiceImpl;
import net.sf.json.JSONObject;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * 【答题记录模块页面调用方法】
 *
 */
public class RecordFunctionManage extends BrowserFunction{
	
	private ClassHourService classHourService = new ClassHourServiceImpl();
	private RecordService recordService = new RecordServiceImpl();
	public RecordFunctionManage(Browser browser, String name) {
		super(browser, name);
	}
	@Override
	public Object function(Object[] params) {
		Result result = new Result();
		if (params.length>0) {
			String method = (String) params[0]; //页面要调用的方法
			switch (method) {
			case "start_class":
				//上课
				if (params.length != 2) {
					result.setRet(Constant.ERROR);
					result.setMessage("参数个数有误！");
					break;
				}
				result = classHourService.startClass(params[1]);
				break;
			case "end_class":
				//下课
				result = classHourService.endClass();
				break;
			case "is_start_class":
				/*是否开始上课*/
				return ClassHourServiceImpl.isStartClass();
			case "get_classInfo":
				/*获取当前班级信息*/
				result = classHourService.getClassInfo();
				break;
			case "get_subject":
				/*获取当前班级对应的科目场景信息*/
				if (params.length != 2){
					result.setRet(Constant.ERROR);
					result.setMessage("参数个数有误！");
					break;
				}
				result = classHourService.getSubject(params[1]);
				break;
			case "select_class_hour":
				/*查询课程列表列表*/
				if (params.length != 3) {
					result.setRet(Constant.ERROR);
					result.setMessage("参数个数有误！");
					break;
				}
				result = classHourService.selectClassInfo(params[1], params[2]);
				break;
			case "insert_class_hour":
				/*查询课程列表列表*/
				if (params.length != 2) {
					result.setRet(Constant.ERROR);
					result.setMessage("参数个数有误！");
					break;
				}
				result = classHourService.insertClassInfo(params[1]);
				break;
			case "delete_class_hour":
				/*查询课程列表列表*/
				if (params.length != 2) {
					result.setRet(Constant.ERROR);
					result.setMessage("参数个数有误！");
					break;
				}
				result = classHourService.deleteClassInfo(params[1]);
				break;
			case "select_subjective_record":
			    /*查询主观题答题记录*/
			    result = recordService.selectSubjectiveRecord(params[1]);
			    break;
			case "select_objective_record":
			    /*查询客观题答题记录*/
			    result = recordService.selectObjectiveRecord(params[1]);
			    break;
			case "select_record":
			    /*查询每个学生的试卷答题记录情况所占比*/
			    result = recordService.selectRecord(params[1]);
			    break;
			case "select_student_record_detail":
			    /*查询每个学生的试卷答题详情*/
			    result = recordService.selectStudentRecordDetail(params[1]);
			    break;
			case "delete_record":
			    /*批量删除学生的试卷作答记录*/
			    result = recordService.deleteRecord(params[1]);
			    break;
			case "test_export":
			    /*导出作答记录*/
			    result = recordService.testExport(params[1]);
			    break ;
			case "select_record2":
				/*查询单项单选结果*/
				result = recordService.selectRecord2(params[1]);
				break;
			default:
				result.setRet(Constant.ERROR);
				result.setMessage("【"+method+"】未找到该指令！");
			}
		}else {
			result.setRet(Constant.ERROR);
			result.setMessage("参数不能为空！");
		}
		return JSONObject.fromObject(result).toString();
	}
}
