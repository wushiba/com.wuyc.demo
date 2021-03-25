package com.yfshop.admin.service.draw;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.draw.request.QueryDrawPrizeReq;
import com.yfshop.admin.api.draw.result.YfDrawPrizeResult;
import com.yfshop.admin.api.draw.service.AdminDrawPrizeService;
import com.yfshop.code.mapper.DrawPrizeMapper;
import com.yfshop.code.model.DrawPrize;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title:抽奖活动奖品Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:14:43
 * @Version:1.1.0
 */
@Service(dynamic = true)
public class AdminDrawPrizeServiceImpl implements AdminDrawPrizeService {

	@Resource
	private DrawPrizeMapper drawPrizeMapper;

	@Override
	public YfDrawPrizeResult getYfDrawPrizeById(Integer id) throws ApiException {
		if (id == null || id <= 0) return null;
		YfDrawPrizeResult yfDrawPrizeResult = null;
		DrawPrize yfDrawPrize = this.drawPrizeMapper.selectById(id);
		if (yfDrawPrize != null) {
			yfDrawPrizeResult = new YfDrawPrizeResult();
			BeanUtil.copyProperties(yfDrawPrize, yfDrawPrizeResult);
		}	
		return yfDrawPrizeResult;
	}

	@Override
	public List<YfDrawPrizeResult> getAll(QueryDrawPrizeReq req) throws ApiException {
		LambdaQueryWrapper<DrawPrize> queryWrapper = Wrappers.lambdaQuery(DrawPrize.class)
				.eq(req.getActId() != null, DrawPrize::getActId, req.getActId())
				.eq(req.getPrizeLevel() != null, DrawPrize::getPrizeLevel, req.getPrizeLevel())
				.eq(req.getPrizeTitle() != null, DrawPrize::getPrizeTitle, req.getPrizeTitle())
				.orderByDesc(DrawPrize::getId);

		List<DrawPrize> dataList = drawPrizeMapper.selectList(queryWrapper);
		return BeanUtil.convertList(dataList, YfDrawPrizeResult.class);
	}

}

