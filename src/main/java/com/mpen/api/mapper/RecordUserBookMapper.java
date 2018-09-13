/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.mapper;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.EnumTypeHandler;

import com.mpen.api.bean.ActivityStudyDetail;
import com.mpen.api.bean.DataAnalysisResult.BookRanking;
import com.mpen.api.bean.GoodsInfo;
import com.mpen.api.domain.DdbRecordUserBook;

/**
 * AppMapper对象.
 * 
 * @author zyt
 *
 */
@Mapper
public interface RecordUserBookMapper {
    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Insert("INSERT INTO DDB_RECORD_USER_BOOK (ID,LOGIN_ID,FK_BOOK_ID,CODE,CLICK_TIME,TYPE,END_TIME,VOICE_TYPE,"
        + "FUNCTION,FK_ACTIVITY_ID,PAGE,TEXT,SCORE,TIME,CODE_TYPE) VALUES(#{id},#{loginId},#{fkBookId},#{code},"
        + "#{clickTime},#{type},#{endTime},#{voiceType},#{function},#{fkActivityId},#{page},#{text},#{score},#{time},"
        + "#{codeType})")
    void save(DdbRecordUserBook userBook);

    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Select("SELECT FK_BOOK_ID BOOK_ID,COUNT(DISTINCT(LOGIN_ID)) READNUM  FROM DDB_RECORD_USER_BOOK GROUP BY "
        + "FK_BOOK_ID")
    List<GoodsInfo> getBookCustomTimes();

    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Select("SELECT * FROM DDB_RECORD_USER_BOOK WHERE LOGIN_ID=#{loginId} ORDER BY CLICK_TIME DESC")
    List<DdbRecordUserBook> getByLoginId(String loginId);

    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Select("SELECT * FROM DDB_RECORD_USER_BOOK WHERE LOGIN_ID=#{loginId} AND CLICK_TIME>=#{date} ORDER BY "
        + "CLICK_TIME DESC")
    List<DdbRecordUserBook> getByLoginIdAndDate(@Param("loginId") String loginId, @Param("date") Date date);

    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Select("SELECT * FROM DDB_RECORD_USER_BOOK WHERE LOGIN_ID=#{loginId} AND FK_BOOK_ID=#{bookId} ORDER BY "
        + "CLICK_TIME DESC")
    List<DdbRecordUserBook> getByLoginIdAndBookId(@Param("loginId") String loginId, @Param("bookId") String bookId);

    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Select("SELECT COUNT(*) COUNT_TIMES,MAX(CLICK_TIME) DATE,FK_ACTIVITY_ID,SUM(TIME) TIME FROM "
        + "DDB_RECORD_USER_BOOK WHERE LOGIN_ID=#{loginId} AND FK_BOOK_ID=#{bookId} GROUP BY " + "FK_ACTIVITY_ID")
    List<ActivityStudyDetail> getStudyDetailByLoginIdAndBookId(@Param("loginId") String loginId,
        @Param("bookId") String bookId);

    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Select("SELECT COUNT(*) COUNT_TIMES,MAX(CLICK_TIME) DATE,TEXT,MAX(SCORE) SCORE FROM DDB_RECORD_USER_BOOK "
        + "WHERE LOGIN_ID=#{loginId} AND FK_BOOK_ID=#{bookId} AND FUNCTION='2' GROUP BY TEXT")
    List<ActivityStudyDetail> getSpokenDetailByLoginIdAndBookId(@Param("loginId") String loginId,
        @Param("bookId") String bookId);

    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Select("SELECT * FROM DDB_RECORD_USER_BOOK WHERE LOGIN_ID=#{loginId} AND CLICK_TIME>=#{startDate} AND CLICK_TIME<#{endDate} "
        + "ORDER BY CLICK_TIME DESC")
    List<DdbRecordUserBook> getWeeklyRecord(@Param("loginId") String loginId, @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    @Select("SELECT A.NAME NAME,COUNT(*) NUMBER FROM (SELECT B.NAME NAME,U.LOGIN_ID LOGINID FROM DDB_RECORD_USER_BOOK U "
        + "JOIN DDB_RESOURCE_BOOK B WHERE U.FK_BOOK_ID=B.ID AND U.CLICK_TIME>=#{startDate} AND U.CLICK_TIME<#{endDate} "
        + "GROUP BY B.NAME,U.LOGIN_ID) AS A GROUP BY A.NAME ORDER BY COUNT(*) DESC LIMIT 10")
    List<BookRanking> getBookRanding(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Result(column = "CODE_TYPE", property = "codeType", typeHandler = EnumTypeHandler.class)
    @Select("SELECT * FROM DDB_RECORD_USER_BOOK WHERE CLICK_TIME>=#{startDate} AND CLICK_TIME<#{endDate} "
        + "ORDER BY CLICK_TIME DESC")
    List<DdbRecordUserBook> getDailyRecord(@Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}
