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
import com.mpen.api.bean.Pen;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.PenSerialNumberRelationshipMapper;
import com.mpen.api.service.SerialNumberService;

/**
 * AnrFileServiceImplTest.
 * 
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SerialNumberServiceImplTest extends TestBase {
    @InjectMocks
    @Autowired
    SerialNumberService serialNumberService;
    @Autowired
    PenSerialNumberRelationshipMapper penSerialNumberRelationshipMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveSuccess() throws SdkException {
        Pen pen = getTestPen();
        Boolean result = serialNumberService.saveRelationship(pen);
        Assert.assertEquals(result, true);
        // 删除测试数据
        penSerialNumberRelationshipMapper.deleteBySerialNumber(pen.getSerialNumber());
    }

    @Test
    public void testSaveWithWrongPenIdError() throws SdkException {
        Pen pen = getTestOnePen();
        try {
            serialNumberService.saveRelationship(pen);
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "该笔已绑定过序列号！");
        }
    }

    @Test
    public void testSaveWithWrongSerialNumberError() throws SdkException {
        Pen pen = getTestTwoPen();
        try {
            serialNumberService.saveRelationship(pen);
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "该序列号无效！");
        }
    }

    @Test
    public void testSaveWithAlreadyUseSerialNumberError() throws SdkException {
        Pen pen = getTestThreePen();
        try {
            serialNumberService.saveRelationship(pen);
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "该序列号已被占用！");
        }
    }

}
