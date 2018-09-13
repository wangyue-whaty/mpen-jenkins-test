/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.mapper;

import com.mpen.api.domain.SsoUser;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * SsoUser Mapper对象.
 *
 * @author kai
 *
 */
@Mapper
public interface SsoUserMapper {

    @Select("SELECT * FROM SSO_USER WHERE ID=#{id} LIMIT 1")
    @ResultType(SsoUser.class)
    SsoUser getById(String id);

    @Select("(SELECT * FROM SSO_USER WHERE LOGIN_ID=#{loginId} LIMIT 1) UNION (SELECT * FROM SSO_USER WHERE"
        + " BINDMOBILE=#{loginId} LIMIT 1 )")
    SsoUser getByLoginId(String loginId);

    @Insert("INSERT INTO SSO_USER(ID,WEIBOIDENTIFIER,FLAG_BAK,TRUE_NAME,FK_ROLE_ID,GUID,"
        + "NICK_NAME,MOBILE_ALIAS,CREATE_DATE,LOGIN_NUM,QQIDENTIFIER,ONLINE_TIME,"
        + "LOGIN_ID,PASSWORD,FLAG_USER_TYPE,SITECODE,WEIXINIDENTIFIER,FLAG_SYSTEM,BINDEMAIL,"
        + "EMAIL,LAST_LOGIN_DATE,LAST_LOGIN_IP,TOKEN,MOBILE,BINDMOBILE,FLAG_ISVALID," + "PHOTO,UPDATE_DATE) "
        + "VALUES ( #{id},#{weiboidentifier},#{flagBak},#{trueName},#{fkRoleId},#{guid},"
        + "#{nickName},#{mobileAlias},#{createDate},#{loginNum},#{qqidentifier},,"
        + "#{onlineTime},#{loginId},#{password},#{flagUserType},#{sitecode},"
        + "#{weixinidentifier},#{flagSystem},#{bindemail},#{email},#{lastLoginDate},"
        + "#{lastLoginIp},#{token},#{mobile},#{bindmobile},#{flagIsvalid}," + "#{photo},#{updateDate})")
    void create(SsoUser ssoUser);

    @Update("UPDATE SSO_USER SET TRUE_NAME=#{trueName},FK_ROLE_ID=#{fkRoleId},LOGIN_ID=#{loginId} WHERE ID=#{id}")
    void update(SsoUser ssoUser);

    @Delete("DELETE FROM SSO_USER WHERE ID=#{id}  ")
    void delete(String id);

}
