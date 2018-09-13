/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.mpen.api.domain.OralTestDetail;

/**
 * AppMapper对象.
 * 
 * @author zyt
 *
 */
@Mapper
public interface OralTestDetailMapper {
    @Insert("INSERT INTO ORAL_TEST_DETAIL (LOGIN_ID,PEN_ID,FK_BOOK_ID,NUM,UPLOAD_TIME,RECORDING_URL,ANSWER_PEN_TIME,IS_DEAL,SHARD_NUM) VALUES "
        + "(#{loginId},#{penId},#{fkBookId},#{num},#{uploadTime},#{recordingUrl},#{answerPenTime},#{isDeal},#{shardNum})")
    void save(OralTestDetail detail);

    @Update("UPDATE ORAL_TEST_DETAIL SET RECOGNIZE_TXT=#{recognizeTxt},SCORE=#{score},IS_DEAL=#{isDeal}, FLUENCY=#{fluency},INTEGRITY=#{integrity},PRONUNCIATION=#{pronunciation} WHERE ID=#{id}")
    void update(OralTestDetail detail);

    @Select("SELECT * FROM ORAL_TEST_DETAIL WHERE LOGIN_ID=#{loginId} AND FK_BOOK_ID=#{bookId} AND NUM=#{num} ORDER BY SCORE DESC LIMIT 1")
    OralTestDetail get(@Param("loginId") String loginId, @Param("bookId") String bookId, @Param("num") int num);

    @Select("SELECT * FROM ORAL_TEST_DETAIL WHERE SHARD_NUM=#{shardNum} AND IS_DEAL=0")
    List<OralTestDetail> getNotDeal(@Param("shardNum") int shardNum);
}
