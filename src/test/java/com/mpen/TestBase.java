/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen;

import java.util.ArrayList;
import java.util.Date;

import com.mp.shared.common.Code;
import com.mp.shared.common.ShCode;
import com.mp.shared.record.ActionRecord;
import com.mp.shared.record.ActionRecords;
import com.mp.shared.record.ClickRecord;
import com.mp.shared.record.TaskRecord;
import com.mpen.api.bean.Pen;
import com.mpen.api.bean.UserSession;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPeCustom;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.domain.DdbResourceBook;
import com.mpen.api.domain.SsoUser;
import com.mpen.api.util.CommUtil;

/**
 * 测试基础类.
 *
 * @author kai
 *
 */
public class TestBase {
    public SsoUser getTestSsoUser() {
        SsoUser u = new SsoUser();
        u.setId("stu1");
        u.setBindemail("test@test.com");
        u.setBindmobile("134");
        u.setCreateDate(new Date());
        u.setEmail("test@test.com");
        u.setFkRoleId(CommUtil.genRecordKey());
        u.setFlagBak("flagbak");
        u.setFlagIsvalid("flagIsvalid");
        u.setFlagSystem("flagSystem");
        u.setFlagUserType("flagUserType");
        u.setGuid("guid");
        u.setLastLoginDate(new Date());
        u.setLastLoginIp("127.0.0.1");
        u.setLoginId(CommUtil.genRecordKey());
        u.setLoginNum(100);
        u.setMobile("134");
        u.setMobileAlias("134");
        u.setNickName("nickName");
        u.setOnlineTime("10");
        u.setPassword("xxxx");
        u.setPhoto("123.png");
        u.setSitecode("001");
        u.setToken(CommUtil.genRecordKey());
        u.setTrueName("test");
        u.setUpdateDate(new Date());
        u.setWeiboidentifier(CommUtil.genRecordKey());
        u.setWeixinidentifier(CommUtil.genRecordKey());
        return u;
    }

    public DdbPeCustom getTestDdbPeCustom(SsoUser ssoUser) {
        DdbPeCustom custom = new DdbPeCustom();
        custom.setAddress("北京");
        custom.setBrithday("20160167");
        custom.setCardNo("001");
        custom.setEmail("test@test.com");
        custom.setFkLabelId(CommUtil.genRecordKey());
        custom.setFkLabelOneId(CommUtil.genRecordKey());
        custom.setFkLabelThreeId(CommUtil.genRecordKey());
        custom.setFlagGender("");
        custom.setFkUserId(ssoUser.getId());
        custom.setLoginId(ssoUser.getLoginId());
        custom.setMobilephone("134");
        custom.setNickName("nickName");
        custom.setPhone("134");
        custom.setPost("100876");
        custom.setQq("11111111");
        custom.setRegistrationDate("20160111");
        custom.setRegNo("009");
        custom.setTrueName("testuser");
        custom.setWorkUnit("test");
        custom.setZipAddress("100768");
        return custom;
    }

    public DdbPeCustom getTestDdbPeCustom() {
        DdbPeCustom custom = new DdbPeCustom();
        custom.setLoginId("111111@qq.com");
        custom.setId("custom1");
        custom.setFkLabelId("ff80818149ea3d200149eab3e7980012");
        return custom;
    }

    public DdbResourceBook getTestDdbResourceBook() {
        DdbResourceBook resourceBook = new DdbResourceBook();
        resourceBook.setAuthor("author");
        resourceBook.setName("test book");
        resourceBook.setCreateDatetime(new Date());
        return resourceBook;
    }

    public DdbPePen getTestDdbPePen() {
        DdbPePen pen = new DdbPePen();
        pen.setIdentifiaction("27b4231d--1da14004-00000000-9200108f");
        pen.setMacAddress("21:3D:4D:BE:C3:8A");
        return pen;
    }

    public DdbPePen getTestIsBindDdbPePen() {
        DdbPePen pen = new DdbPePen();
        pen.setIdentifiaction("3");
        return pen;
    }

    public DdbPePen getTestNoMachingDdbPePen() {
        DdbPePen pen = new DdbPePen();
        pen.setIdentifiaction("12165231");
        pen.setMacAddress("21:3D:4D:BE:C3:8A");
        return pen;
    }

    public UserSession getTestUserSession() {
        UserSession user = new UserSession();
        user.setSsoUser(getTestSsoUser());
        user.setPeCustom(getTestDdbPeCustom());
        user.setLoginId("13581637228");
        return user;
    }

    public TaskRecord getTestTaskRecord() {
        TaskRecord record = new TaskRecord();
        record.id = 0;
        record.name = ActionRecord.Subtype.PlayAudio;
        record.createdRealTime = System.currentTimeMillis();
        record.duration = 100l;
        ClickRecord clickRecord = new ClickRecord();
        clickRecord.bookId = "4028ac305804c097015804c143730001";
        clickRecord.clickTime = System.currentTimeMillis();
        clickRecord.language = 0;
        clickRecord.code = new Code();
        clickRecord.code.type = Code.Type.SH;
        clickRecord.code.shCode = new ShCode(10017, 1);
        clickRecord.text = "What#1s your favourite song, Ms Smart?";
        clickRecord.score = 90;
        record.extra = Constants.GSON.toJson(clickRecord);
        return record;
    }

    public ActionRecords getTestActionRecords() {
        TaskRecord record = getTestTaskRecord();
        ActionRecord actionRecord = record.toActionRecord();
        ActionRecords ar = new ActionRecords();
        ar.setPenId("27b4231d--1da14004-00000000-9200108f");
        ar.setUploadUuid(CommUtil.genRecordKey());
        ArrayList<ActionRecord> list = new ArrayList<ActionRecord>();
        list.add(actionRecord);
        ar.setRecords(list);
        ar.setNumRecords(list.size());
        return ar;
    }

    public Pen getTestPen() {
        Pen pen = new Pen();
        pen.setIdentifiaction("2a552127--1da14004-00000000-9200108f");
        pen.setSerialNumber("V917050000003");
        return pen;
    }

    public Pen getTestOnePen() {
        Pen pen = new Pen();
        pen.setIdentifiaction("27b4231d--1da14004-00000000-9200108f");
        pen.setSerialNumber("V917050000002");
        return pen;
    }

    public Pen getTestTwoPen() {
        Pen pen = new Pen();
        pen.setIdentifiaction("2a552127--1da14004-00000000-9200108f");
        pen.setSerialNumber("V917060000001");
        return pen;
    }
    
    public Pen getTestThreePen() {
        Pen pen = new Pen();
        pen.setIdentifiaction("2a552127--1da14004-00000000-9200108f");
        pen.setSerialNumber("V917050000001");
        return pen;
    }

}
