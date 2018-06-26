package com.zkxltech.service.impl;

import java.util.Date;

import com.ejet.cache.RedisMapScore;
import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.domain.ClassHour;
import com.zkxltech.domain.Result;
import com.zkxltech.domain.Score;
import com.zkxltech.service.ScoreService;
import com.zkxltech.ui.util.StringUtils;

public class ScoreServiceImpl implements ScoreService{
	private Result result = new Result();

	@Override
	public Result startScore(Object object) {
		result = new Result();
		try {
			Score score =  (Score) StringUtils.parseJSON(object, Score.class);
			RedisMapScore.addScoreInfo(score); //保存评分主题信息
			
			//将评分主题相关信息保存到缓存
			return result;
		} catch (Exception e) {
			result.setRet(Constant.ERROR);
			result.setMessage("新增课程失败！");
			result.setDetail(IOUtils.getError(e));
			return result;
		}
	}
	
}