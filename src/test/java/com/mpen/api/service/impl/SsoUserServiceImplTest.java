/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import com.mpen.TestBase;
import com.mpen.api.domain.SsoUser;
import com.mpen.api.mapper.SsoUserMapper;
import com.mpen.api.service.SsoUserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringBootTest
public class SsoUserServiceImplTest extends TestBase {

    @InjectMocks
    @Autowired
    SsoUserService ssoUserService;

    @Autowired
    SsoUserMapper userMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateSsoUserSuccess() {

        SsoUser expectUser = getTestSsoUser();

        String uid = ssoUserService.create(expectUser);

        SsoUser gotUser = ssoUserService.getById(uid);

        Assert.assertEquals(uid, gotUser.getId());

        Assert.assertEquals(expectUser.getLoginId(), gotUser.getLoginId());

    }

}
