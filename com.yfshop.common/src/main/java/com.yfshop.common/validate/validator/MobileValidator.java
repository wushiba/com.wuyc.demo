package com.yfshop.common.validate.validator;

import cn.hutool.core.lang.PatternPool;
import com.yfshop.common.validate.annotation.Mobile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 自定义手机号的校验器
 *
 * @author Xulg
 * Created in 2019-09-20 13:57
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {

    /**
     * 正则：手机号（精确）
     * <p>移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188、198</p>
     * <p>联通：130、131、132、145、155、156、175、176、185、186、166</p>
     * <p>电信：133、153、173、177、180、181、189、199</p>
     * <p>全球星：1349</p>
     * <p>虚拟运营商：170</p>
     */
    private static final String STRICT_REGEX_MOBILE = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";

    private static final Pattern STRICT_PATTERN = Pattern.compile(STRICT_REGEX_MOBILE);

    private boolean isStrict;

    @Override
    public void initialize(Mobile annotation) {
        isStrict = annotation.isStrict();
    }

    @Override
    public boolean isValid(String mobile, ConstraintValidatorContext context) {
        if (mobile == null) {
            return true;
        }
        if (isStrict) {
            return STRICT_PATTERN.matcher(mobile).matches();
        } else {
            return PatternPool.MOBILE.matcher(mobile).matches();
        }
    }
}
