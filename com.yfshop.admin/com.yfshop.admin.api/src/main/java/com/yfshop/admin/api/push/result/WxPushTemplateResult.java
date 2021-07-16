package com.yfshop.admin.api.push.result;

import lombok.Data;
import java.io.Serializable;

@Data
public class WxPushTemplateResult implements Serializable {

        private static final long serialVersionUID = 1L;

        private Integer id;

        /**
         * 模板标题
         */
        private String title;

        /**
         * 模板消息id
         */
        private String templateId;

        /**
         * 模板数据
         */
        private String templateData;



    }
