package com.mpen.api.controller;

import java.util.concurrent.Callable;


import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mp.shared.common.NetworkResult;
import com.mpen.api.common.RsHelper;

/**
 *  熟悉开发流程创建的controller
 *  @author wangyue
 */
@RestController
@EnableAsync
@RequestMapping("/test")
public class HelloWorldController {

    @GetMapping("/wangyue")
    public @ResponseBody Callable<NetworkResult<Object>> getWYName() {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                return RsHelper.success("wangyue is coming.....");
            }
        };
    }
    @GetMapping("/sen")
    public @ResponseBody Callable<NetworkResult<Object>> getSenName() {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                return RsHelper.success("sen is coming.....");
            }
        };
    }
    @GetMapping("/liangxiong")
    public @ResponseBody Callable<NetworkResult<Object>> getLXName() {
        return new Callable<NetworkResult<Object>>() {
            @Override
            public NetworkResult<Object> call() throws Exception {
                return RsHelper.success("liangxiong");
            }
        };
    }
}
