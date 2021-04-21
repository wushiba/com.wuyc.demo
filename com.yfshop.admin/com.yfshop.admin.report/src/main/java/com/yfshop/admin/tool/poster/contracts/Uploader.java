package com.yfshop.admin.tool.poster.contracts;

import com.yfshop.admin.tool.poster.kernal.UploadResult;

import java.io.File;
import java.io.IOException;

public interface Uploader {
    public UploadResult upload(File file) throws IOException;
}
