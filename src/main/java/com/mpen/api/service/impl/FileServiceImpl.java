/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mp.shared.common.FileModule;
import com.mp.shared.service.MpResourceDecoder;
import com.mpen.api.bean.FileParam;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.PersistenceFile;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.PersistenceFileMapper;
import com.mpen.api.service.FileService;
import com.mpen.api.util.FileUtils;

/**
 * App升级服务.
 *
 * @author zyt
 *
 */
@Component
public class FileServiceImpl implements FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
    @Autowired
    private PersistenceFileMapper persistenceFileMapper;

    @Override
    public String saveFile(MultipartFile file, String type) throws IOException, Exception {
        // 有文件才上传
        final String fileName = file.getOriginalFilename();
        final String fileSavePath = FileUtils.uploadFile(file.getBytes(), fileName, FileUtils.ROOTPATH, type);
        return fileSavePath;
    }

    @Override
    public List<FileModule> fileDownload(String path) throws Exception {
        final String savePath = FileUtils.getFilePartsSaveRealPath(path);
        path = FileUtils.getFileSaveRealPath(path, false);
        final List<FileModule> fileMoudles = MpResourceDecoder.getFileMoudles(path, savePath);
        if (fileMoudles == null) {
            throw new SdkException(Constants.GET_INFO_FAILURE);
        }
        fileMoudles.forEach((fileModule) -> {
            fileModule.url = FileUtils.getFullRequestPath(fileModule.url);
        });
        return fileMoudles;
    }

    @Override
    public boolean insertFile(FileParam param, String address) throws Exception {
        final String fileSavePath = saveFile(param.getFile(), param.getProgect() + param.getType());
        final PersistenceFile file = new PersistenceFile(param, fileSavePath, address);
        persistenceFileMapper.insert(file);
        return true;
    }

}
