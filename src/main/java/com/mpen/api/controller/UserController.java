/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.controller;

import com.mp.shared.common.NetworkResult;
import com.mp.shared.common.SuccessResult;
import com.mpen.api.bean.Book;
import com.mpen.api.bean.Pen;
import com.mpen.api.bean.User;
import com.mpen.api.bean.UserSession;
import com.mpen.api.bean.WeeklyParam;
import com.mpen.api.common.Constants;
import com.mpen.api.common.RsHelper;
import com.mpen.api.common.Uris;
import com.mpen.api.service.PeCustomService;
import com.mpen.api.service.PrPenCustomService;
import com.mpen.api.service.RecordUserBookService;
import com.mpen.api.service.SsoUserService;
import com.mpen.api.service.UserSessionService;
import com.mpen.api.util.LogUtil;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO 用户相关API.
 *
 * @author zyt
 *
 */
@RestController
@EnableAsync
@RequestMapping(Uris.V1_USER)
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private SsoUserService ssoUserService;
    @Autowired
    private PrPenCustomService prPenCustomService;
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private RecordUserBookService recordUserBookService;
    @Autowired
    private PeCustomService peCustomService;

    /**
     * 保存绑定关系,解除绑定关系.
     * 
     */
    @PostMapping(Uris.PEN)
    public @ResponseBody Callable<NetworkResult<Object>> bindRelationship(@RequestBody Pen pen,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                final UserSession user = userSessionService.getUser(request, response);
                NetworkResult<Object> networkResult;
                switch (pen.getAction()) {
                case Constants.SAVE_BINDRELATIONSHIP:
                    if (StringUtils.isBlank(pen.getIdentifiaction()) || StringUtils.isBlank(pen.getMacAddress())) {
                        LOGGER.error(Constants.INVALID_PARAMETER_ERROR, request.getRequestURL());
                        networkResult = RsHelper.error(Constants.INVALID_PARAMRTER_MESSAGE);
                    } else {
                        networkResult = RsHelper.success(prPenCustomService.saveBindRelationship(pen, user));
                    }
                    break;
                case Constants.UN_BINDRELATIONSHIP:
                    final Boolean temp = prPenCustomService.unBindRelationship(pen, user);
                    final SuccessResult result = new SuccessResult();
                    result.setSuccess(temp);
                    networkResult = RsHelper.success(result);
                    break;
                default:
                    networkResult = RsHelper.error(Constants.NO_MACHING_ERROR_CODE);
                    break;
                }
                LogUtil.printLog(request, pen.getAction(), user, networkResult);
                return networkResult;
            }
        };
    }

    /**
     * 登陆接口.
     * 
     * 
     */
    @PostMapping(Uris.LOGIN)
    public @ResponseBody Callable<NetworkResult<Object>> login(@RequestBody final User user,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                if (StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getPassword())) {
                    return RsHelper.error(Constants.BAD_REQUEST_ERROR_CODE, Constants.INVALID_PARAMRTER_MESSAGE);
                }
                return RsHelper.success(ssoUserService.login(user, request, response));
            }
        };
    }

    /**
     * 获取用户学习信息.
     * 
     * 
     */
    @GetMapping(Uris.BOOK)
    public @ResponseBody Callable<NetworkResult<Object>> getUserStudyInfo(final Book book,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                final UserSession user = userSessionService.getUser(request, response);
                switch (book.getAction()) {
                case Constants.COMPLETE_USER_STUDY_INFO:
                    return RsHelper.success(recordUserBookService.getCompleteUserStudyInfo(user));
                case Constants.USER_DATE_STUDY_TIME:
                    return RsHelper.success(recordUserBookService.getDateStudyTime(user, book));
                case Constants.BOOK_STUDY_INFO:
                    return RsHelper.success(recordUserBookService.getBookStudyInfo(user, book.getBookId()));
                case Constants.BOOK_CONTENT_STUDY_DETAIL:
                    return RsHelper
                        .success(recordUserBookService.getBookContentStudyDetailByBookId(user, book.getBookId()));
                case Constants.BOOK_CONTENT_SPOKEN_DETAIL:
                    return RsHelper
                        .success(recordUserBookService.getBookContentSpokenDetailByBookId(user, book.getBookId()));
                default:
                    return RsHelper.error(Constants.NO_MACHING_ERROR_CODE);
                }
            }
        };
    }

    /**
     * 更改用户信息.
     * 
     * 
     */
    @PostMapping("/")
    public @ResponseBody Callable<NetworkResult<Object>> changeUserInfo(@RequestBody final User user,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                final UserSession userSession = userSessionService.getUser(request, response);
                switch (user.getAction()) {
                case Constants.CHANGE_USER_INFO:
                    return RsHelper.success(peCustomService.update(userSession, user));
                case Constants.SAVE_ADDRESS:
                    return RsHelper.success(peCustomService.saveAddress(user, userSession));
                default:
                    return RsHelper.error(Constants.NO_MACHING_ERROR_CODE);
                }
            }
        };
    }

    /**
     * 获取用户信息.
     * 
     * 
     */
    @GetMapping("/")
    public @ResponseBody Callable<NetworkResult<Object>> getUserInfo(final User user, final HttpServletRequest request,
        final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                final UserSession userSession = userSessionService.getUser(request, response);
                switch (user.getAction()) {
                case Constants.GET_USER_LABELS:
                    return RsHelper.success(peCustomService.getCustomLabels(userSession));
                default:
                    return RsHelper.error(Constants.NO_MACHING_ERROR_CODE);
                }
            }
        };
    }

    /**
     * 获取用户周报信息.
     * 
     * 
     */
    @GetMapping("/weekly")
    public @ResponseBody Callable<NetworkResult<Object>> getUserWeeklyInfo(final WeeklyParam weeklyParam,
        final HttpServletRequest request, final HttpServletResponse response) {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                final UserSession userSession = userSessionService.getUser(request, response);
                switch (weeklyParam.getAction()) {
                case Constants.GET_WEEKLY_LIST:
                    return RsHelper.success(recordUserBookService.getWeeklyList(userSession));
                case Constants.GET_WEEKLY:
                    return RsHelper.success(recordUserBookService.getWeekly(weeklyParam, userSession));
                default:
                    return RsHelper.error(Constants.NO_MACHING_ERROR_CODE);
                }
            }
        };
    }
}