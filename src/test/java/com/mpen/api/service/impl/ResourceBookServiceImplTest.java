/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import java.io.IOException;
import java.util.ArrayList;
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

import com.mp.shared.common.BookInfo;
import com.mp.shared.common.MpCode;
import com.mp.shared.common.Page;
import com.mp.shared.common.PageInfo;
import com.mpen.api.bean.PreBook;
import com.mpen.api.bean.Unit;
import com.mpen.api.exception.CacheException;
import com.mpen.api.exception.SdkException;
import com.mpen.api.service.ResourceBookService;

/**
 * ResourceBookServiceImplTest.
 * 
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ResourceBookServiceImplTest {
    @InjectMocks
    @Autowired
    ResourceBookService resourceBookService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetBookContentSuccess() throws SdkException, CacheException {
        final List<Unit> bookContent = resourceBookService.getBookContent("ff808081581deb4101581e74ac7d0088",
            "13661309890");
        Assert.assertEquals(bookContent.size() > 0, true);
    }

    @Test
    public void testGetBookInfoSuccess() throws Exception {
        final BookInfo bookInfo = resourceBookService.getBookInfo("ff808081581deb4101581e74ac7d0088");
        Assert.assertEquals(bookInfo != null, true);
    }

}
