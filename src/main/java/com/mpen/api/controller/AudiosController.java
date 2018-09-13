/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.controller;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mp.shared.common.CodeInfo;
import com.mp.shared.common.HotArea;
import com.mp.shared.common.QuickCodeInfo;
import com.mp.shared.common.NetworkResult;
import com.mpen.api.bean.UserSession;
import com.mpen.api.common.Constants;
import com.mpen.api.common.RsHelper;
import com.mpen.api.common.Uris;
import com.mpen.api.service.DecodeService;
import com.mpen.api.service.PePenService;
import com.mpen.api.service.UserSessionService;
import com.mpen.api.util.FileUtils;
import com.mpen.api.util.LogUtil;

/**
 * TODO 语音资源相关API.
 *
 * @author zyt
 *
 */
@RestController
@EnableAsync
@RequestMapping(Uris.V1_AUDIOS)
public class AudiosController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AudiosController.class);

    @Autowired
    private DecodeService decodeService;
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private PePenService pePenService;

    /**
     * 点读获取语音信息接口.
     * 
     */
    @PostMapping("/")
    public @ResponseBody Callable<NetworkResult<Object>> getCodeInfo(@RequestBody final QuickCodeInfo quickCodeInfo,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                NetworkResult<Object> result;
                if (!pePenService.checkAppVersion(request)) {
                    final CodeInfo codeInfoResult = new CodeInfo();
                    codeInfoResult.languageInfos = new HotArea.LanguageInfo[1];
                    codeInfoResult.languageInfos[0] = new HotArea.LanguageInfo();
                    codeInfoResult.languageInfos[0].soundFile = FileUtils
                        .getFullRequestPath(Constants.UPDATE_PROMPT_VOICE);
                    result = RsHelper.success(codeInfoResult);
                    return result;
                }
                final UserSession user = userSessionService.getUser(request, response);
                result = RsHelper.success(decodeService.getCodeInfo(quickCodeInfo));
                LogUtil.printLog(request, null, user, result);
                return result;
            }
        };
    }

    /**
     * 点读获取视频信息接口.
     * 
     */
    @PostMapping("/video")
    public @ResponseBody Callable<NetworkResult<Object>> getVideoCodeInfo(@RequestBody final CodeInfo codeInfo,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                final UserSession user = userSessionService.getUser(request, response);
                NetworkResult<Object> result = null;
                if (!decodeService.getVideo(codeInfo) || codeInfo.isVideo()) {
                    result = RsHelper.success(decodeService.getCodeInfo(codeInfo.quickCodeInfo));
                } else {
                    result = RsHelper.success(codeInfo);
                }
                LogUtil.printLog(request, null, user, result);
                return result;
            }
        };
    }
}
