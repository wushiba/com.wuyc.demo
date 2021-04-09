package com.yfshop.wx.api.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;

import java.io.Serializable;

/**
 * 支付结果通知.
 * 文档见：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7&index=8
 * https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=9_7
 *
 * @author aimilin6688
 * @since 2.5.0
 */
@Data
public class WxPayOrderNotifyReq implements Serializable {

    private String promotionDetail;

    private String deviceInfo;

    private String openid;

    /**
     * <pre>
     * 字段名：是否关注公众账号.
     * 变量名：is_subscribe
     * 是否必填：否
     * 类型：String(1)
     * 示例值：Y
     * 描述：用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效
     * </pre>
     */
    private String isSubscribe;
    /**
     * <pre>
     * 字段名：用户子标识.
     * 变量名：sub_openid
     * 是否必填：是
     * 类型：String(128)
     * 示例值：wxd930ea5d5a258f4f
     * 描述：用户在子商户appid下的唯一标识
     * </pre>
     */
    private String subOpenid;

    /**
     * <pre>
     * 字段名：是否关注子公众账号.
     * 变量名：sub_is_subscribe
     * 是否必填：否
     * 类型：String(1)
     * 示例值：Y
     * 描述：用户是否关注子公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效
     * </pre>
     */
    private String subIsSubscribe;


    /**
     * <pre>
     * 字段名：交易类型.
     * 变量名：trade_type
     * 是否必填：是
     * 类型：String(16)
     * 示例值：JSAPI
     * JSA描述：PI、NATIVE、APP
     * </pre>
     */
    private String tradeType;

    /**
     * <pre>
     * 字段名：付款银行.
     * 变量名：bank_type
     * 是否必填：是
     * 类型：String(16)
     * 示例值：CMC
     * 描述：银行类型，采用字符串类型的银行标识，银行类型见银行列表
     * </pre>
     */
    private String bankType;

    /**
     * <pre>
     * 字段名：订单金额.
     * 变量名：total_fee
     * 是否必填：是
     * 类型：Int
     * 示例值：100
     * 描述：订单总金额，单位为分
     * </pre>
     */
    private Integer totalFee;
    /**
     * <pre>
     * 字段名：应结订单金额.
     * 变量名：settlement_total_fee
     * 是否必填：否
     * 类型：Int
     * 示例值：100
     * 描述：应结订单金额=订单金额-非充值代金券金额，应结订单金额<=订单金额。
     * </pre>
     */
    private Integer settlementTotalFee;
    /**
     * <pre>
     * 字段名：货币种类.
     * 变量名：fee_type
     * 是否必填：否
     * 类型：String(8)
     * 示例值：CNY
     * 描述：货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
     * </pre>
     */
    private String feeType;
    /**
     * <pre>
     * 字段名：现金支付金额.
     * 变量名：cash_fee
     * 是否必填：是
     * 类型：Int
     * 示例值：100
     * 描述：现金支付金额订单现金支付金额，详见支付金额
     * </pre>
     */
    private Integer cashFee;
    /**
     * <pre>
     * 字段名：现金支付货币类型.
     * 变量名：cash_fee_type
     * 是否必填：否
     * 类型：String(16)
     * 示例值：CNY
     * 描述：货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
     * </pre>
     */
    private String cashFeeType;
    /**
     * <pre>
     * 字段名：总代金券金额.
     * 变量名：coupon_fee
     * 是否必填：否
     * 类型：Int
     * 示例值：10
     * 描述：代金券金额<=订单金额，订单金额-代金券金额=现金支付金额，详见支付金额
     * </pre>
     */
    private Integer couponFee;

    /**
     * <pre>
     * 字段名：代金券使用数量.
     * 变量名：coupon_count
     * 是否必填：否
     * 类型：Int
     * 示例值：1
     * 描述：代金券使用数量
     * </pre>
     */
    private Integer couponCount;

    /**
     * <pre>
     * 字段名：微信支付订单号.
     * 变量名：transaction_id
     * 是否必填：是
     * 类型：String(32)
     * 示例值：1217752501201407033233368018
     * 描述：微信支付订单号
     * </pre>
     */
    private String transactionId;

    /**
     * <pre>
     * 字段名：商户订单号.
     * 变量名：out_trade_no
     * 是否必填：是
     * 类型：String(32)
     * 示例值：1212321211201407033568112322
     * 描述：商户系统的订单号，与请求一致。
     * </pre>
     */
    private String outTradeNo;
    /**
     * <pre>
     * 字段名：商家数据包.
     * 变量名：attach
     * 是否必填：否
     * 类型：String(128)
     * 示例值：123456
     * 描述：商家数据包，原样返回
     * </pre>
     */
    private String attach;

    /**
     * <pre>
     * 字段名：支付完成时间.
     * 变量名：time_end
     * 是否必填：是
     * 类型：String(14)
     * 示例值：20141030133525
     * 描述：支付完成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
     * </pre>
     */
    private String timeEnd;

    /**
     * <pre>
     * 字段名：接口版本号.
     * 变量名：version
     * 类型：String(32)
     * 示例值：1.0
     * 更多信息，详见文档：https://pay.weixin.qq.com/wiki/doc/api/danpin.php?chapter=9_101&index=1
     * </pre>
     */
    private String version;

    /**
     * <pre>
     * 字段名：汇率.
     * 变量名：rate_value
     * 类型：String(16)
     * 示例值：650000000
     * 标价币种与支付币种的兑换比例乘以10的8次方即为此值，例如美元兑换人民币的比例为6.5，则rate_value=650000000
     * </pre>
     */
    private String rateValue;

    /**
     * <pre>
     * 字段名：签名类型.
     * 变量名：sign_type
     * 类型：String(32)
     * 示例值：HMAC-SHA256
     * 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
     * </pre>
     */
    private String signType;


    @Override
    public String toString() {
        return WxGsonBuilder.create().toJson(this);
    }
}