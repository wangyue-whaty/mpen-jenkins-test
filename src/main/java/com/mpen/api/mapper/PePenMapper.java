/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.mpen.api.bean.DataAnalysisResult.Version;
import com.mpen.api.domain.DdbPePen;

/**
 * PePen Mapper对象.
 * 
 * @author zyt
 *
 */
@Mapper
public interface PePenMapper {
    @Select("SELECT * FROM DDB_PE_PEN WHERE IDENTIFIACTION=#{identifiaction} LIMIT 1")
    DdbPePen getByIdentifiaction(String identifiaction);

    @Select("SELECT * FROM DDB_PE_PEN WHERE ID=#{id} LIMIT 1")
    DdbPePen getById(String id);

    @Update("UPDATE DDB_PE_PEN SET CODE=#{code},NAME=#{name},IDENTIFIACTION=#{identifiaction},"
        + "FLAG_ACTIVIE=#{flagActivie},CREATE_DATETIME=#{createDatetime},MAC_ADDRESS=#{macAddress},"
        + "ISBIND=#{isBind},IS_TEST=#{isTest},ITEM=#{item},LABEL=#{label},APP_VERSION=#{appVersion},"
        + "ROM_VERSION=#{romVersion},PEN_ADMIT=#{penAdmit},PUBLIC_KEY=#{publicKey},ACTIVE_ADD=#{activeAdd}"
        + " WHERE ID=#{id}")
    void update(DdbPePen pen);

    @Insert("INSERT INTO DDB_PE_PEN (ID,CODE,NAME,IDENTIFIACTION,FLAG_ACTIVIE,CREATE_DATETIME,"
        + "MAC_ADDRESS,PUBLIC_KEY,ACTIVE_ADD,TYPE) VALUES (#{id},#{code},#{name},#{identifiaction},"
        + "#{flagActivie},#{createDatetime}," + "#{macAddress},#{publicKey},#{activeAdd},#{type})")
    void create(DdbPePen pen);

    @Delete("DELETE FROM DDB_PE_PEN WHERE IDENTIFIACTION=#{identifiaction}")
    void deleteByIdentifiaction(String identifiaction);

    @Update("UPDATE DDB_PE_PEN SET APP_VERSION=#{version} WHERE IDENTIFIACTION=#{identifiaction}")
    void updateAppVersionByIdentifiaction(@Param("identifiaction") String identifiaction,
        @Param("version") String version);

    @Update("UPDATE DDB_PE_PEN SET ROM_VERSION=#{version} WHERE IDENTIFIACTION=#{identifiaction}")
    void updateRomVersionByIdentifiaction(@Param("identifiaction") String identifiaction,
        @Param("version") String version);

    @Select("SELECT * FROM DDB_PE_PEN WHERE MAC_ADDRESS=#{mac} LIMIT 1")
    DdbPePen getByMac(String mac);

    @Select("SELECT * FROM DDB_PE_PEN WHERE MAC_ADDRESS LIKE #{mac} LIMIT 1")
    DdbPePen getByDefaultMac(String mac);

    @Select("SELECT APP_VERSION NAME,COUNT(*) VALUE FROM DDB_PE_PEN WHERE APP_VERSION IS NOT NULL "
        + "AND TRIM(APP_VERSION) !='' GROUP BY APP_VERSION ORDER BY COUNT(*) DESC LIMIT 5")
    List<Version> getAppVersion();

    @Select("SELECT ROM_VERSION NAME,COUNT(*) VALUE FROM DDB_PE_PEN WHERE ROM_VERSION IS NOT NULL "
        + "AND TRIM(ROM_VERSION) !='' GROUP BY ROM_VERSION ORDER BY COUNT(*) DESC LIMIT 5")
    List<Version> getRomVersion();
}
