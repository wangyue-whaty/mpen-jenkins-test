/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mp.shared.common.SuccessResult;
import com.mpen.TestBase;
import com.mpen.api.bean.UserSession;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.PePenMapper;
import com.mpen.api.service.PrPenCustomService;

/**
 * PrPenCustomServiceImplTest.
 * 
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PrPenCustomServiceImplTest extends TestBase {
    @InjectMocks
    @Autowired
    PrPenCustomService prPenCustomService;
    @Autowired
    PePenMapper pePenMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCheckBindRelationshipSuccess() throws SdkException {
        DdbPePen pen = getTestDdbPePen();
        UserSession user = getTestUserSession();
        SuccessResult result = prPenCustomService.checkBindRelationship(pen.getIdentifiaction(), user);
        Assert.assertEquals(result.getSuccess(), true);
    }

    @Test
    public void testCheckBindRelationshipNoMachingPenError() throws SdkException {
        DdbPePen pen = getTestNoMachingDdbPePen();
        UserSession user = getTestUserSession();
        try {
            prPenCustomService.checkBindRelationship(pen.getIdentifiaction(), user);
        } catch (Exception exception) {
            Assert.assertEquals(exception.getMessage(), Constants.NO_MACHING_PEN);
        }
    }
}
