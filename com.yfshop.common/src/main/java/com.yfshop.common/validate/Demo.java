package com.yfshop.common.validate;

import com.yfshop.common.validate.annotation.CheckEnum;
import com.yfshop.common.validate.annotation.MustInCandidateValue;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Xulg
 * Created in 2019-09-20 14:26
 */
class Demo {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static void main(String[] args) {
        PackageConfigCreateRequest request = new PackageConfigCreateRequest();
        request.setPackageType("123");
        request.setIsEnable("y");
        Set<ConstraintViolation<PackageConfigCreateRequest>> violations = VALIDATOR.validate(request);
        for (ConstraintViolation<PackageConfigCreateRequest> violation : violations) {
            System.err.println(violation);
        }

        request.setPackageType(TestEnum.NIAN_KA_LI_BAO.code);
        request.setIsEnable("Y");
        violations = VALIDATOR.validate(request);
        for (ConstraintViolation<PackageConfigCreateRequest> violation : violations) {
            System.err.println(violation);
        }
    }

    private static class PackageConfigCreateRequest implements Serializable {
        private static final long serialVersionUID = 1L;

        @NotEmpty(message = "礼包类型不能为空")
        @CheckEnum(value = TestEnum.class, message = "非法的礼包类型值")
        private String packageType;

        @NotNull(message = "商户礼包id不能为空")
        private Integer merchantPackageId;

        @NotEmpty(message = "是否上架不能为空不能为空")
        @MustInCandidateValue(candidateValue = {"Y", "N"}, message = "是否上架的取值只能是Y|N")
        private String isEnable;

        public String getPackageType() {
            return packageType;
        }

        private void setPackageType(String packageType) {
            this.packageType = packageType;
        }

        public Integer getMerchantPackageId() {
            return merchantPackageId;
        }

        public void setMerchantPackageId(Integer merchantPackageId) {
            this.merchantPackageId = merchantPackageId;
        }

        public String getIsEnable() {
            return isEnable;
        }

        public void setIsEnable(String isEnable) {
            this.isEnable = isEnable;
        }
    }

    public enum TestEnum {

        /**
         * 新手礼包
         */
        XIN_SHOU_LI_BAO("XinShouLiBao", "新手礼包"),

        /**
         * 年卡礼包
         */
        NIAN_KA_LI_BAO("NianKaLiBao", "年卡礼包"),
        ;

        /**
         * 枚举编码
         */
        private final String code;

        /**
         * 枚举描述
         */
        private final String description;

        TestEnum(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public static TestEnum getByCode(String code) {
            if (code == null) {
                return null;
            }
            for (TestEnum value : values()) {
                if (value.getCode().equals(code)) {
                    return value;
                }
            }
            return null;
        }

        public String getCode() {
            return this.code;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
