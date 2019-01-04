package com.zkxltech.ui.functions;

import com.ejet.core.util.constant.Constant;
import com.zkxltech.domain.Result;
import com.zkxltech.service.VoteService;
import com.zkxltech.service.impl.VoteServiceImpl;
import com.zkxltech.ui.util.PageConstant;
import net.sf.json.JSONObject;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * 【投票模块页面调用方法】
 *
 */
public class VoteFunctionManage extends BrowserFunction{
	private VoteService voteService = new VoteServiceImpl();
	
	public VoteFunctionManage(Browser browser, String name) {
		super(browser, name);
	}
	@Override
	public Object function(Object[] params) {
		Result result = new Result();
		if (params.length>0) {
			String method = (String) params[0]; //页面要调用的方法
			switch (method) {
			case "start_vote":
				if (params.length != 2) {
					result.setRet(Constant.ERROR);
					result.setMessage("参数个数有误！");
					break;
				}
				result = voteService.startVote(params[1]);
				break;
			case "stop_vote":
			    result = voteService.stopVote();
                break;
			case "get_vote":
				result = voteService.getVote();
				break;
			case "get_voteTitleInfo":
				result = voteService.getVoteTitleInfo();
				break;
			case "to_vote":
				PageConstant.browser.setUrl(PageConstant.VOTE_ANSWER_URL_START);
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
