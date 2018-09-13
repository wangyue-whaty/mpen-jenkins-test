/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.mpen.TestBase;
import com.mpen.api.bean.PenInfo;
import com.mpen.api.domain.DdbPeCustom;
import com.mpen.api.domain.DdbPePen;

/**
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PePenControllerTest extends TestBase {
    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URI = "/v1/pens/";

    @Test
    public void testCheckBindSuccess() throws Exception {
        DdbPePen pen = getTestDdbPePen();
        DdbPeCustom peCustom = getTestDdbPeCustom();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.add("Cookie", "loginId=" + peCustom.getLoginId());
        // 通过REST URL 发送请求
        String identifiaction = pen.getIdentifiaction();
        HttpEntity<PenInfo> request = new HttpEntity<PenInfo>(headers);
        ResponseEntity<String> entity = restTemplate.exchange(BASE_URI + identifiaction + "?action=checkBind",
            HttpMethod.GET, request, String.class);
        // 校验返回状态吗和结果
        Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testUpgradeAppSuccess() throws Exception {
        DdbPePen pen = getTestDdbPePen();
        DdbPeCustom peCustom = getTestDdbPeCustom();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.add("Cookie", "loginId=" + peCustom.getLoginId());
        // 通过REST URL发送请求
        String identifiaction = pen.getIdentifiaction();
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> entity = restTemplate.exchange(BASE_URI + identifiaction + "?action=upgradeApp",
            HttpMethod.GET, request, String.class); // 校验返回状态吗和结果
        Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testUpgradeRomSuccess() throws Exception {
        DdbPePen pen = getTestDdbPePen();
        DdbPeCustom peCustom = getTestDdbPeCustom();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.add("Cookie", "loginId=" + peCustom.getLoginId());
        // 通过REST URL发送请求
        String identifiaction = pen.getIdentifiaction();
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> entity = restTemplate.exchange(BASE_URI + identifiaction + "?action=upgradeRom",
            HttpMethod.GET, request, String.class); // 校验返回状态吗和结果
        Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testAdbAdmitSuccess() throws Exception {
        DdbPePen pen = getTestDdbPePen();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        // 通过REST URL发送请求
        String identifiaction = pen.getIdentifiaction();
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> entity = restTemplate.exchange(BASE_URI + identifiaction + "?action=adbAdmit",
            HttpMethod.GET, request, String.class); // 校验返回状态吗和结果
        Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testUnBindPenSuccess() throws Exception {
        DdbPeCustom peCustom = getTestDdbPeCustom();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.add("Cookie", "loginId=" + peCustom.getLoginId());
        // 通过REST URL发送请求
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> entity = restTemplate.exchange(BASE_URI
            + "?action=unBindPen&macAddress=MPEN56:CF:B4:9B:C3:8A__MPEN7B:24:B7:38:C3:8A", HttpMethod.GET, request,
            String.class); // 校验返回状态吗和结果
        Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testCompleteMacSuccess() throws Exception {
        DdbPeCustom peCustom = getTestDdbPeCustom();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.add("Cookie", "loginId=" + peCustom.getLoginId());
        // 通过REST URL发送请求
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> entity = restTemplate.exchange(BASE_URI + "?action=completeMac&macAddress=56CFB4",
            HttpMethod.GET, request, String.class); // 校验返回状态吗和结果
        Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
    }
}
