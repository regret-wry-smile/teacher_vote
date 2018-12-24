package com.zkxltech.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.cache.RedisMapScore;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.constant.Global;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.device.DeviceComm;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.Score;
import com.zkxltech.service.ScoreService;
import com.zkxltech.thread.BaseThread;
import com.zkxltech.thread.ScoreThread;
import com.zkxltech.thread.ThreadManager;
import com.zkxltech.ui.util.StringUtils;

public class ScoreServiceImpl implements ScoreService {
	private static final Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);
	private Result result = new Result();

	@Override
	public Result startScore(Object object) {
		result = new Result();
		RedisMapScore.clearMap();// 清除评分缓存
		try {
			Score score = (Score) StringUtils.parseJSON(object, Score.class);
			RedisMapScore.addScoreInfo(score); // 保存评分主题信息
			Result reStart = scoreStart(score.getPrograms().size());
			if (reStart.getRet().equals(Constant.ERROR)) {
				result.setMessage(reStart.getMessage());
				return result;
			}
			Global.setModeMsg(Constant.BUSINESS_SCORE);
			// 将评分主题相关信息保存到缓存
			result.setMessage("开始评分！");
			result.setRet(Constant.SUCCESS);
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("开始评分失败！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result getScore() {
		result = new Result();
		try {
			result.setItem(RedisMapScore.getScoreInfoBar());
			result.setRet(Constant.SUCCESS);
			result.setMessage("获取评分数据成功！");
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("获取评分数据失败！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result getScoreTitleInfo() {
		result = new Result();
		try {
			result.setItem(RedisMapScore.getScoreInfo()); // 保存评分主题信息
			result.setRet(Constant.SUCCESS);
			result.setMessage("获取评分主题成功！");
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("获取评分主题失败！");
			result.setDetail(IOUtils.getError(e));
			logger.error(IOUtils.getError(e));
			return result;
		}
	}

	@Override
	public Result stopScore() {
		Result r = new Result();
		Global.setModeMsg(Constant.BUSINESS_NORMAL);
		/* 停止线程管理 */
		ThreadManager.getInstance().stopAllThread();

		r = EquipmentServiceImpl.getInstance().answer_stop();
		if (r.getRet().equals(Constant.ERROR)) {
			return r;
		}
		r.setRet(Constant.SUCCESS);
		r.setMessage("停止成功");
		return r;
	}

	@Override
	public Result scoreStart(int questionNum) {
		Result r = new Result();
		try {
			r.setRet(Constant.ERROR);
			/* 停止所有线程 */
			ThreadManager.getInstance().stopAllThread();
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("[");
			for (int i = 0; i < questionNum; i++) {
				strBuilder.append("{");
				String id = String.valueOf(i + 1);
				String type = "d";
				String range = "";
				try {
					range = "0-5";
				} catch (Exception e2) {
					range = "";
				}
                strBuilder.append("'type':'" + type + "',");
				strBuilder.append("'id':'" + id + "',");
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
				r.setMessage("指令发送失败");
				return r;
			}

			BaseThread thread = new ScoreThread();
			thread.start();
			/* 添加到线程管理 */
			ThreadManager.getInstance().addThread(thread);
			r.setRet(Constant.SUCCESS);
			r.setMessage("发送成功");
			return r;
		} catch (Exception e) {
			logger.error(IOUtils.getError(e));
			r.setRet(Constant.ERROR);
			r.setMessage("指令发送失败");
			return r;
		}
	}
}
