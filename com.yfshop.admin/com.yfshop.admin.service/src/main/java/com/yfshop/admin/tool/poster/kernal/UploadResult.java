package com.yfshop.admin.tool.poster.kernal;

import com.yfshop.admin.tool.poster.contracts.Result;
import lombok.Data;

@Data
public class UploadResult extends JsonAble implements Result {
    private final String url;
    private String msg;

    public UploadResult(String url) {
        this.url = url;
        this.msg = Result.SUCCESSFUL;
    }

    public UploadResult(String url, String msg) {
        this.url = url;
        this.msg = msg;
    }

    public static UploadResult fail(String msg) {
        return new UploadResult(null, msg);
    }

    @Override
    public boolean isSuccessful() {
        return msg.equals(Result.SUCCESSFUL);
    }
}
