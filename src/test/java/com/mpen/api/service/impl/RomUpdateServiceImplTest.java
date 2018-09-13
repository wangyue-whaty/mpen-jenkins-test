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

import com.mpen.TestBase;
import com.mpen.api.bean.RomUpdate;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.exception.SdkException;
import com.mpen.api.service.RomUpdateService;

/**
 * RomUpdateServiceImplTest.
 * 
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RomUpdateServiceImplTest extends TestBase {
    @InjectMocks
    @Autowired
    RomUpdateService romUpdateService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUpdateMessageWithVersionSuccess() throws SdkException {
        DdbPePen pen = getTestDdbPePen();
        List<RomUpdate> list = romUpdateService.getUpdateMessage(pen.getIdentifiaction(),
            "Mpen-V2.0FHWV1.5-20160829.113548");
        Assert.assertEquals(list.size() > 0, true);
    }

    @Test
    public void testUpgradeAppWithoutVersionSuccess() throws SdkException {
        DdbPePen pen = getTestDdbPePen();
        List<RomUpdate> list = romUpdateService.getUpdateMessage(pen.getIdentifiaction(), null);
        Assert.assertEquals(list.size() > 0, true);
    }

    @Test
    public void testUpgradeAppNoMachingPenError() throws SdkException {
        DdbPePen pen = getTestNoMachingDdbPePen();
        try {
            romUpdateService.getUpdateMessage(pen.getIdentifiaction(), null);
        } catch (Exception exception) {
            Assert.assertEquals(exception.getMessage(), Constants.NO_MACHING_PEN);
        }
    }
}
