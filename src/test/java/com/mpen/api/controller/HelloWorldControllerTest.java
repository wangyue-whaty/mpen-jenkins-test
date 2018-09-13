package com.mpen.api.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.mpen.MpenApiApplication;

/**
 * 
 * HelloWorldController 测试类
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=MpenApiApplication.class,webEnvironment =WebEnvironment.RANDOM_PORT )
public class HelloWorldControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private static final String [][] URL_MSG= {{"/test/sen/","sen is coming....."},{"/test/liangxiong/","liangxiong is coming....."},{"/test/wangyue/","wangyue is coming....."}};
    private static final HttpHeaders headers = new HttpHeaders();
    /**
     * 这是一个hellocontroller的测试升级版，
     * 主要解决代码重复，及运行优化，使用数组代替map
    */
    @Test
    public void testURL(){
       for(int i = 0;i <URL_MSG.length;i++){
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<Object> entity = restTemplate.exchange(URL_MSG[i][0] ,HttpMethod.GET, request, Object.class);
            Map<String, String> body = (Map<String, String>) entity.getBody();
            String resdata = body.get("data");
            Assert.assertEquals(entity.getStatusCode(), HttpStatus.OK);
            Assert.assertEquals(resdata, URL_MSG[i][1]);
        }
    }
}
