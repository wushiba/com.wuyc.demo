package com.yfshop.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.code.mapper.ActCodeBatchDetailMapper;
import com.yfshop.code.model.ActCodeBatchDetail;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.result.YfActCodeBatchDetailResult;
import com.yfshop.shop.service.ActivityActCodeBatchService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @Title:用户Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-26 17:10:17
 * @Version:1.1.0
 */
@Service
public class ActivityActCodeBatchServiceImpl implements ActivityActCodeBatchService {

	@Resource
	private ActCodeBatchDetailMapper actCodeBatchDetailMapper;

	@Resource
	private RedisService redisService;


	@Override
	public YfActCodeBatchDetailResult getYfActCodeBatchDetailByActCode(String actCode) throws ApiException {
		Asserts.assertStringNotBlank(actCode, 500, "请扫描正确的券码");

		ActCodeBatchDetail actCodeBatchDetail = null;
		Object userObject = redisService.get(CacheConstants.ACT_CODE_BATCH_ACT_NO + actCode);
		if (userObject != null) {
			actCodeBatchDetail = JSON.parseObject(userObject.toString(), ActCodeBatchDetail.class);
		} else {
			actCodeBatchDetail = actCodeBatchDetailMapper.selectOne(Wrappers
					.lambdaQuery(ActCodeBatchDetail.class).eq(ActCodeBatchDetail::getActCode, actCode));
			redisService.set(CacheConstants.ACT_CODE_BATCH_ACT_NO + actCode,
					JSON.toJSONString(actCodeBatchDetail), 60 * 30);
		}
		return actCodeBatchDetail == null ? null : BeanUtil.convert(actCodeBatchDetail, YfActCodeBatchDetailResult.class) ;
	}
}

