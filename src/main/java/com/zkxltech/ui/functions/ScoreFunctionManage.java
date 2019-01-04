package com.zkxltech.ui.functions;

import com.ejet.core.util.constant.Constant;
import com.zkxltech.domain.Result;
import com.zkxltech.service.ScoreService;
import com.zkxltech.service.impl.ScoreServiceImpl;
import net.sf.json.JSONObject;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * 【评分模块页面调用方法】
 *
 */
public class ScoreFunctionManage extends BrowserFunction{
	private ScoreService scoreService = new ScoreServiceImpl();
	
	public ScoreFunctionManage(Browser browser, String name) {
		super(browser, name);
	}
	@Override
	public Object function(Object[] params) {
		Result result = new Result();
		if (params.length>0) {
			String method = (String) params[0]; //页面要调用的方法
			switch (method) {
			case "start_score":
				if (params.length != 2) {
					result.setRet(Constant.ERROR);
					result.setMessage("参数个数有误！");
					break;
				}
				result = scoreService.startScore(params[1]);
				break;
			case "stop_score":
			    result = scoreService.stopScore();
                break;
			case "get_score":
				result = scoreService.getScore();
				break;
			//获取柱状图需要的数据
			case "get_scoreTitleInfo":
				result = scoreService.getScoreTitleInfo();
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
