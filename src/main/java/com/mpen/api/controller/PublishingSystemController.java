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

import com.mp.shared.common.NetworkResult;
import com.mpen.api.bean.Book;
import com.mpen.api.bean.FileParam;
import com.mpen.api.common.Constants;
import com.mpen.api.common.RsHelper;
import com.mpen.api.common.Uris;
import com.mpen.api.service.FileService;
import com.mpen.api.service.ResourceBookService;
import com.mpen.api.util.FileUtils;

/**
 * TODO 出版系统相关API.
 *
 * @author zyt
 *
 */
@RestController
@EnableAsync
@RequestMapping(Uris.V1_PUBLISHING)
public class PublishingSystemController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishingSystemController.class);

    @Autowired
    private ResourceBookService resourceBookService;
    @Autowired
    private FileService fileService;

    /**
     * TODO 创建资源接口（action:createBook），生成点读码接口（action:createCode），查看生成进度几口（action:getProgress）
     *
     * @param book
     *        书籍资源参数bean
     * @param request
     *        HttpServletRequest
     * @param response
     *        HttpServletResponse
     * @return
     *        NetworkResult
     */
    @PostMapping(Uris.BOOK)
    public @ResponseBody Callable<NetworkResult<Object>> createTif(@RequestBody final Book book,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                switch (book.getAction()) {
                case Constants.CREATE_BOOK:
                    return RsHelper.success(resourceBookService.addBook(book));
                case Constants.CREATE_CODE:
                    return RsHelper.success(resourceBookService.createCode(book));
                case Constants.GET_PROGRESS:
                    return RsHelper.success(resourceBookService.getProgress(book));
                default:
                    return RsHelper.error(Constants.NO_MACHING_ERROR_CODE);
                }
            }
        };
    }

    /**
     * TODO 书籍MP包上传接口（action:uploadBook）
     * 
     * @param fileParam
     *        文件参数bean
     * @param request
     *        HttpServletRequest
     * @param response
     *        HttpServletResponse
     * @return
     *        NetworkResult
     */
    @PostMapping("/file")
    public @ResponseBody Callable<NetworkResult<Object>> uploadFile(final FileParam fileParam,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                switch (fileParam.getAction()) {
                case Constants.UPLOAD_BOOK:
                    final String filePath = fileService.saveFile(fileParam.getFile(), FileUtils.BOOK);
                    return RsHelper.success(resourceBookService.updateBookLink(fileParam.getUuid(), filePath,
                        fileParam.getFile().getSize()));
                default:
                    return RsHelper.error(Constants.NO_MACHING_ERROR_CODE);
                }
            }
        };
    }
}
