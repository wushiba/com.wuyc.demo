package com.yfshop.admin.oss;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
	 * STS临时授权访问OSS
	 */
	@Data
	@Accessors(chain = true)
	public class StsSecurityTokenEntity implements Serializable {
		private static final long serialVersionUID = 1L;
		private String accessKeyId;
		private String accessKeySecret;
		private String securityToken;
	    private String expiration;
	    private String requestId;
	    private String bucketName;
	    private String region;

	}
