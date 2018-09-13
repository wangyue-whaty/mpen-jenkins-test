/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mpen.TestBase;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbApp;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.exception.SdkException;
import com.mpen.api.service.AppService;

/**
 * AppServiceImplTest.
 * 
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppServiceImplTest extends TestBase {
    @InjectMocks
    @Autowired
    AppService appService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpgradeAppWithVersionSuccess() throws Exception {
        DdbPePen pen = getTestDdbPePen();
        DdbApp map = appService.getAppMessageByPenId(pen.getIdentifiaction(), "1.32");
        Assert.assertEquals(StringUtils.isNotBlank(map.getId()), true);
    }

    @Test
    public void testUpgradeAppWithoutVersionSuccess() throws Exception {
        DdbPePen pen = getTestDdbPePen();
        DdbApp map = appService.getAppMessageByPenId(pen.getIdentifiaction(), "");
        Assert.assertEquals(StringUtils.isNotBlank(map.getId()), true);
    }

    @Test
    public void testUpgradeAppNoMachingPenError() throws SdkException {
        DdbPePen pen = getTestNoMachingDdbPePen();
        try {
            appService.getAppMessageByPenId(pen.getIdentifiaction(), "");
        } catch (Exception exception) {
            Assert.assertEquals(exception.getMessage(), Constants.NO_MACHING_PEN);
        }
    }

}
