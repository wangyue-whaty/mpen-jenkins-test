/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mp.shared.common.Page;
import com.mpen.TestBase;
import com.mpen.api.bean.Goods;
import com.mpen.api.bean.GoodsInfo;
import com.mpen.api.exception.CacheException;
import com.mpen.api.exception.SdkException;
import com.mpen.api.service.ShopGoodsService;

/**
 * ShopGoodsServiceImplTest.
 * 
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopGoodsServiceImplTest extends TestBase {
    @InjectMocks
    @Autowired
    ShopGoodsService shopGoodsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

}
