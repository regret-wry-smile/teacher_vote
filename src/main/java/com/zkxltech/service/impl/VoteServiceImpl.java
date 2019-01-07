package com.zkxltech.service.impl;

import com.ejet.cache.RedisMapVote;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.device.DeviceComm;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.Vote;
import com.zkxltech.service.VoteService;
import com.zkxltech.thread.BaseThread;
import com.zkxltech.thread.ThreadManager;
import com.zkxltech.thread.VoteThread;
import com.zkxltech.ui.util.PageConstant;
import com.zkxltech.ui.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoteServiceImpl implements VoteService{
    private static final Logger logger = LoggerFactory.getLogger(VoteServiceImpl.class);
	private Result result = new Result();
	private static Thread thread ;
	public static Thread getThread() {
        return thread;
    }

    public static void setThread(Thread thread) {
        VoteServiceImpl.thread = thread;
    }

    @Override
	public Result startVote(Object object) {
		result = new Result();
		RedisMapVote.clearMap();//清除投票缓存
		try {
		    Vote vote =  (Vote) StringUtils.parseJSON(object, Vote.class);
		    RedisMapVote.addVoteInfo(vote); //投票评分主题信息
		    Result reStart = startVote(vote.getPrograms().size());
		    if (reStart.getRet().equals(Constant.ERROR)) {
               result.setMessage(reStart.getMessage());
               return result;
            }
				
			
			//将评分主题相关信息保存到缓存
			Global.setModeMsg(Constant.BUSINESS_VOTE);
			result.setMessage("开始投票！");
			result.setRet(Constant.SUCCESS);
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("开始投票失败！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result getVote() {
		result = new Result();
		try {
			result.setItem(RedisMapVote.getVoteInfoBar());
			result.setRet(Constant.SUCCESS);
			result.setMessage("获取投票数据成功！");
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("获取投票数据失败！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result stopVote() {
        Result r = new Result();
        Global.setModeMsg(Constant.BUSINESS_NORMAL);
        /*停止所有线程管理*/
        ThreadManager.getInstance().stopAllThread();
        
        if (thread != null && thread instanceof VoteThread) {
            VoteThread c =  (VoteThread)thread;
            c.setFLAG(false);
            logger.info("投票线程停止成功");
        }else{
            logger.error("投票线程停止失败");
        }
        r = EquipmentServiceImpl.getInstance().answer_stop();
        if (r.getRet().equals(Constant.ERROR)) {
            return r;
        }
        r.setRet(Constant.SUCCESS);
        r.setMessage("停止成功");
        return r;
    }

	@Override
	public Result startVote(int questionNum) {

		Result r = new Result();
		try {
			/* 停止所有线程 */
			ThreadManager.getInstance().stopAllThread();

			r.setRet(Constant.ERROR);
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("[");
			for (int i = 0; i < questionNum; i++) {
				strBuilder.append("{");
				String id = String.valueOf(i + 1);
				String type = "s";
				String range = "";
				try {
					range = "A-C";
				} catch (Exception e2) {
					range = "";
				}
				strBuilder.append("'id':'" + id + "',");
				strBuilder.append("'type':'" + type + "',");
				strBuilder.append("'range':'" + range + "'");
				strBuilder.append("}");

				if (questionNum - 1 != i) {
					strBuilder.append(",");
				}
			}
			strBuilder.append("]");
			int ret = DeviceComm.answerStart(strBuilder.toString());
			if (ret != 0) {
				r.setRet(Constant.ERROR);
				r.setMessage("Instruction sending failed");
				return r;
			}

			BaseThread thread = new VoteThread();
			thread.start();
			/* 添加到线程管理 */
			ThreadManager.getInstance().addThread(thread);
			r.setRet(Constant.SUCCESS);
			r.setMessage("Send successfully");
			return r;
		} catch (Exception e) {
			logger.error(IOUtils.getError(e));
			r.setRet(Constant.ERROR);
			r.setMessage("Instruction sending failed");
			return r;
		}

	}
	
	@Override
	public Result getVoteTitleInfo() {
		result = new Result();
		try {
			result.setItem(RedisMapVote.getVoteInfo()); //保存评分主题信息
			result.setRet(Constant.SUCCESS);
			result.setMessage("获取投票主题成功！");
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("获取投票主题失败！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result toVote(){
		result = new Result();
		try {
			PageConstant.browser.setUrl(PageConstant.VOTE_ANSWER_URL_START);
			result.setRet(Constant.SUCCESS);
			result.setMessage("进入投票页面成功！");
			result.setRemak("5");
			return result;
		}catch (Exception e){
			result.setRet(Constant.ERROR);
			result.setMessage("进入投票页面成功失败！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}
	
}
