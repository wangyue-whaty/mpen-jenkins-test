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

import com.mpen.TestBase;
import com.mpen.api.bean.PenInfo;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.PePenMapper;
import com.mpen.api.service.PePenService;
import com.mpen.api.util.CommUtil;

/**
 * PePenServiceImplTest.
 * 
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PePenServiceImplTest extends TestBase {
    @InjectMocks
    @Autowired
    PePenService pePenService;
    @Autowired
    PePenMapper pePenMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSavePenSuccess() throws SdkException {
        DdbPePen pen = getTestNoMachingDdbPePen();
        PenInfo penInfo = new PenInfo();
        penInfo.setCode(CommUtil.getPenCode(pen.getIdentifiaction()));
        penInfo.setMacAddress(pen.getMacAddress());
        Boolean success = pePenService.savePen(pen.getIdentifiaction(), penInfo);
        Assert.assertEquals(success, true);
        pePenService.deleteByIdentification(pen.getIdentifiaction());
    }

    @Test
    public void testSavePenWrongIdentifiactionError() throws SdkException {
        DdbPePen pen = getTestDdbPePen();
        PenInfo penInfo = new PenInfo();
        penInfo.setCode("a");
        penInfo.setMacAddress(pen.getMacAddress());
        try {
            pePenService.savePen(pen.getIdentifiaction(), penInfo);
        } catch (SdkException e) {
            Assert.assertEquals(Constants.WRONG_IDENTIFIACTION, e.getMessage());
        }
    }

}
