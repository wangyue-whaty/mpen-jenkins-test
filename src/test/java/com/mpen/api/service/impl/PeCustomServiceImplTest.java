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
import com.mpen.api.bean.Label;
import com.mpen.api.service.PeCustomService;

/**
 * PeCustomServiceImplTest.
 * 
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PeCustomServiceImplTest extends TestBase {
    @InjectMocks
    @Autowired
    PeCustomService peCustomService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCustomLabelsSuccess() {
        List<Label> customLabels = peCustomService.getCustomLabels(getTestUserSession());
        Assert.assertEquals(customLabels != null, true);
    }
}
