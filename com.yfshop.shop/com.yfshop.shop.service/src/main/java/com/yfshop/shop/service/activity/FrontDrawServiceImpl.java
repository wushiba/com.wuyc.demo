package com.yfshop.shop.service.activity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.DrawActivity;
import com.yfshop.code.model.DrawPrize;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.activity.service.FrontDrawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
@Service
public class FrontDrawServiceImpl implements FrontDrawService {

    private static final Logger logger = LoggerFactory.getLogger(FrontDrawServiceImpl.class);

    @Resource
    private DrawPrizeMapper drawPrizeMapper;

    @Resource
    private DrawActivityMapper drawActivityMapper;

    @Resource
    private RedisService redisService;

    @Override
    public YfDrawActivityResult getDrawActivityDetailById(Integer id) throws ApiException {
        DrawActivity drawActivity = null;
        Object activityObject = redisService.get(CacheConstants.DRAW_ACTIVITY_PREFIX + id);
        if (activityObject != null) {
            drawActivity = JSON.parseObject(activityObject.toString(), DrawActivity.class);
        } else {
            drawActivity = drawActivityMapper.selectById(id);
        }
        if (drawActivity == null) {
            return null;
        }

        List<DrawPrize> prizeList = null;
        Object prizeObject = redisService.get(CacheConstants.DRAW_PRIZE_NAME_PREFIX + id);
        if (prizeObject != null) {
            prizeList = JSON.parseArray(prizeObject.toString(), DrawPrize.class);
        } else {
            prizeList = drawPrizeMapper.selectList(Wrappers.lambdaQuery(DrawPrize.class)
                    .eq(DrawPrize::getActId, id));
            redisService.set(CacheConstants.DRAW_PRIZE_NAME_PREFIX + id,
                    JSON.toJSONString(prizeList), 60 * 60 * 24 * 30);
        }

        YfDrawActivityResult activityResult = BeanUtil.convert(drawActivity, YfDrawActivityResult.class);
        activityResult.setPrizeList(BeanUtil.convertList(prizeList, YfDrawPrizeResult.class));
        return activityResult;
    }

}

