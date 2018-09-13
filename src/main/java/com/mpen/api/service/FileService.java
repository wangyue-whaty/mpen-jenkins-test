/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mp.shared.common.FileModule;
import com.mpen.api.bean.FileParam;

public interface FileService {
    /**
     * 上传文件.
     * 
     */
    String saveFile(MultipartFile file, String type) throws IOException, Exception;

    List<FileModule> fileDownload(String path) throws Exception;

    boolean insertFile(FileParam param, String address) throws Exception;

}
