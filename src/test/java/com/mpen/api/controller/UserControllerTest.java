/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.controller;

import java.util.HashMap;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpen.TestBase;
import com.mpen.api.bean.Pen;
import com.mpen.api.bean.User;
import com.mpen.api.bean.WeeklyParam;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPeCustom;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.mapper.PePenMapper;
import com.mpen.api.service.PeCustomService;
import com.mpen.api.service.SsoUserService;
import com.mpen.api.service.impl.PrPenCustomServiceImpl;

/**
 * @author zyt
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTest extends TestBase {
	@Autowired
	private TestRestTemplate restTemplate;

	private static final String BASE_URI = "/v1/user";
	private static final String SAVE_SAVEBINDRELATIONSHIP_URI = BASE_URI + "/pen";
	private static final String GET_USER_INFO = BASE_URI + "/book";
	private static final String LOGIN = BASE_URI + "/login";
	private static final String CHANGE_USER_INFO = BASE_URI + "/";
	private static final String WEEKLY = BASE_URI + "/weekly";

	@Autowired
	SsoUserService ssoUserService;

	@Autowired
	PeCustomService peCustomService;

	@Autowired
	PrPenCustomServiceImpl prPenCustomService;

	@Autowired
	PePenMapper pePenMapper;

	@Test
	public void testSaveBindRelationshipSuccess() throws Exception {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		DdbPePen pen = getTestDdbPePen();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		Pen penParam = new Pen();
		penParam.setAction(Constants.SAVE_BINDRELATIONSHIP);
		penParam.setIdentifiaction(pen.getIdentifiaction());
		penParam.setMacAddress(pen.getMacAddress());
		// 通过REST URL 发送请求
		HttpEntity<Pen> request = new HttpEntity<Pen>(penParam, headers);
		ResponseEntity<String> entity = restTemplate.exchange(SAVE_SAVEBINDRELATIONSHIP_URI, HttpMethod.POST, request,
				String.class);
		System.out.println(entity);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testSaveBindRelationshipWithoutIdOrMacError() {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		Pen penParam = new Pen();
		penParam.setAction(Constants.SAVE_BINDRELATIONSHIP);
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<Pen> request = new HttpEntity<Pen>(penParam, headers);
		restTemplate.exchange(SAVE_SAVEBINDRELATIONSHIP_URI, HttpMethod.POST, request, String.class);
	}

	@Test
	public void testLoginSuccess() throws Exception {
		User user = new User();
		user.setUserName("123123213");
		user.setPassword("b59c67bf196a4758191e42f76670ceba");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		// 通过REST URL 发送请求
		HttpEntity<User> request = new HttpEntity<User>(user, headers);
		ResponseEntity<String> entity = restTemplate.exchange(LOGIN, HttpMethod.POST, request, String.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testLoginWithWrongLoginIdError() throws Exception {
		User user = new User();
		user.setUserName("1");
		user.setPassword("b59c67bf196a4758191e42f76670ceba");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		// 通过REST URL 发送请求
		HttpEntity<User> request = new HttpEntity<User>(user, headers);
		ResponseEntity<String> entity = restTemplate.exchange(LOGIN, HttpMethod.POST, request, String.class);
		// 校验返回状态吗和结果
		ObjectMapper mapper = new ObjectMapper();
		String code = (String) mapper.readValue(entity.getBody(), HashMap.class).get("errorCode");
		String msg = (String) mapper.readValue(entity.getBody(), HashMap.class).get("errorMsg");
		Assert.assertEquals(code, "400");
		Assert.assertEquals(msg, Constants.NO_MATCHING_USER);
	}

	@Test
	public void testLoginWithWrongPasswordError() throws Exception {
		User user = new User();
		user.setUserName("123123213");
		user.setPassword("a");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		// 通过REST URL 发送请求
		HttpEntity<User> request = new HttpEntity<User>(user, headers);
		ResponseEntity<String> entity = restTemplate.exchange(LOGIN, HttpMethod.POST, request, String.class);
		// 校验返回状态吗和结果
		ObjectMapper mapper = new ObjectMapper();
		String code = (String) mapper.readValue(entity.getBody(), HashMap.class).get("errorCode");
		String msg = (String) mapper.readValue(entity.getBody(), HashMap.class).get("errorMsg");
		Assert.assertEquals(code, "400");
		Assert.assertEquals(msg, Constants.WRONG_PASSWORD);
	}

	@Test
	public void testUnBindRelationshipSuccess() throws Exception {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		DdbPePen pen = getTestDdbPePen();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		Pen penParam = new Pen();
		penParam.setAction(Constants.UN_BINDRELATIONSHIP);
		penParam.setIdentifiaction(pen.getIdentifiaction());
		// 通过REST URL 发送请求
		HttpEntity<Pen> request = new HttpEntity<Pen>(penParam, headers);
		ResponseEntity<String> entity = restTemplate.exchange(SAVE_SAVEBINDRELATIONSHIP_URI, HttpMethod.POST, request,
				String.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testGetUserStudyInfoSuccess() throws Exception {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> entity = restTemplate.exchange(
				GET_USER_INFO + "?action=" + Constants.COMPLETE_USER_STUDY_INFO, HttpMethod.GET, request, String.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testGetBooktudyInfoSuccess() throws Exception {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> entity = restTemplate.exchange(
				GET_USER_INFO + "?action=" + Constants.BOOK_STUDY_INFO + "&bookId=4028ac305804c097015804c143730001",
				HttpMethod.GET, request, String.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testGetBooktudyInfoWithWrongBookIdError() throws Exception {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> entity = restTemplate.exchange(
				GET_USER_INFO + "?action=" + Constants.BOOK_STUDY_INFO + "&bookId=123", HttpMethod.GET, request,
				String.class);
		// 校验返回状态吗和结果
		ObjectMapper mapper = new ObjectMapper();
		String value = (String) mapper.readValue(entity.getBody(), HashMap.class).get("errorCode");
		Assert.assertEquals(value, "400");
	}

	@Test
	public void testBookContentStudyDetailSuccess() throws Exception {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> entity = restTemplate.exchange(GET_USER_INFO + "?action="
				+ Constants.BOOK_CONTENT_STUDY_DETAIL + "&bookId=4028ac305804c097015804c143730001", HttpMethod.GET,
				request, String.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testBookContentSpokenDetailSuccess() throws Exception {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> entity = restTemplate.exchange(GET_USER_INFO + "?action="
				+ Constants.BOOK_CONTENT_SPOKEN_DETAIL + "&bookId=4028ac305804c097015804c143730001", HttpMethod.GET,
				request, String.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testChangeUserInfoSuccess() throws Exception {
		User user = new User();
		user.setNickName("zyt");
		user.setGrade("624c1a98-fd8c-11e6-9f75-c81f66dbee68");
		user.setAction(Constants.CHANGE_USER_INFO);
		user.setAge(12);
		user.setSchool("北大");
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<User> request = new HttpEntity<User>(user, headers);
		ResponseEntity<String> entity = restTemplate.exchange(CHANGE_USER_INFO, HttpMethod.POST, request, String.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testGetUserLabelsSuccess() throws Exception {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<User> request = new HttpEntity<User>(headers);
		ResponseEntity<String> entity = restTemplate.exchange(CHANGE_USER_INFO + "?action=getUserLabels",
				HttpMethod.GET, request, String.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void testGetAllWeeklys() {
		// 创建一个测试用户
		DdbPeCustom peCustom = getTestDdbPeCustom();
		// 设置认证信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.add("Cookie", "loginId=" + peCustom.getLoginId());
		// 通过REST URL 发送请求
		HttpEntity<WeeklyParam> request = new HttpEntity<WeeklyParam>(headers);
		ResponseEntity<Map> entity = restTemplate.exchange(WEEKLY + "?action=getAllWeeklys", HttpMethod.GET, request,
				Map.class);
		// 校验返回状态吗和结果
		Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}
}
