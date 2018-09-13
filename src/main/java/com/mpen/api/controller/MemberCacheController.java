/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.controller;

import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mp.shared.common.NetworkResult;
import com.mpen.api.bean.Cache;
import com.mpen.api.common.Constants;
import com.mpen.api.common.RsHelper;
import com.mpen.api.common.Uris;
import com.mpen.api.domain.DdbPeCustom;
import com.mpen.api.domain.DdbResourceBook;
import com.mpen.api.mapper.PeCustomMapper;
import com.mpen.api.mapper.ResourceBookMapper;
import com.mpen.api.service.MemCacheService;
import com.mpen.api.service.RecordUserBookService;
import com.mpen.api.service.ResourceBookService;
import com.mpen.api.util.CommUtil;

/**
 * TODO 缓存API.
 *
 * @author zyt
 *
 */
@RestController
@EnableAsync
@RequestMapping(Uris.V1_MEMECHACHE)
public class MemberCacheController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberCacheController.class);

    @Autowired
    private MemCacheService memCacheService;
    @Autowired
    private ResourceBookMapper resourceBookMapper;
    @Autowired
    private PeCustomMapper peCustomMapper;
    @Autowired
    private ResourceBookService resourceBookService;
    @Autowired
    private RecordUserBookService recordUserBookService;

    /**
     * TODO 临时清除缓存接口.
     * 
     */
    @GetMapping("/")
    public @ResponseBody Callable<NetworkResult<Object>> sendMessage(final Cache cache,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                String key = "";
                if ("book".equals(cache.getKey())) {
                    final List<DdbResourceBook> validBooks = resourceBookMapper.getValidBooks();
                    for (DdbResourceBook ddbResourceBook : validBooks) {
                        key = CommUtil.getCacheKey(Constants.CACHE_BOOKINFO_KEY_PRIFIX + ddbResourceBook.getId());
                        memCacheService.delete(key);
                        key = CommUtil.getCacheKey(Constants.CACHE_POINT_NUM_PRIFIX + ddbResourceBook.getId());
                        memCacheService.delete(key);
                        key = CommUtil.getCacheKey(Constants.CACHE_SPOKEN_PREFIX + ddbResourceBook.getId());
                        memCacheService.delete(key);
                        key = CommUtil.getCacheKey(Constants.CACHE_STUDY_PREFIX + ddbResourceBook.getId());
                        memCacheService.delete(key);
                    }
                    Constants.CACHE_THREAD_POOL.execute(() -> {
                        try {
                            resourceBookService.updateBookDetail();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        validBooks.forEach((book) -> {
                            try {
                                resourceBookService.getBookContent(book.getId(), Constants.CACHE_STUDY_PREFIX);
                                resourceBookService.getBookContent(book.getId(), Constants.CACHE_SPOKEN_PREFIX);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    });
                } else if ("allUser".equals(cache.getKey())) {
                    final List<DdbPeCustom> list = peCustomMapper.get();
                    for (DdbPeCustom ddbPeCustom : list) {
                        key = CommUtil.getCacheKey(Constants.CACHE_USERSESSION_KEY_PREFIX + ddbPeCustom.getLoginId());
                        memCacheService.delete(key);
                        key = CommUtil.getCacheKey(Constants.CACHE_USER_STUDY_PREFIX + ddbPeCustom.getLoginId());
                        memCacheService.delete(key);
                    }
                    Constants.CACHE_THREAD_POOL.execute(() -> {
                        list.forEach((custom) -> {
                            try {
                                recordUserBookService.getUserStydyMap(custom.getLoginId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    });
                } else if ("User".equals(cache.getKey())) {
                    key = CommUtil.getCacheKey(Constants.CACHE_USERSESSION_KEY_PREFIX + cache.getLoginId());
                    memCacheService.delete(key);
                    key = CommUtil.getCacheKey(Constants.CACHE_USER_STUDY_PREFIX + cache.getLoginId());
                    memCacheService.delete(key);
                } else if ("pen".equals(cache.getKey())) {
                    key = CommUtil.getCacheKey(Constants.CACHE_PENINFO_KEY_PREFIX + cache.getId());
                    memCacheService.delete(key);
                } else if ("sms".equals(cache.getKey())) {
                    key = CommUtil.getCacheKey(Constants.CACHE_SEND_SMS_KEY);
                    memCacheService.delete(key);
                } 
                return RsHelper.success(true);
            }
        };
    }
}
