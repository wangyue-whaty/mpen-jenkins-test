/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.EnumTypeHandler;

import com.mpen.api.domain.DdbResourceVideo;

@Mapper
public interface ResourceVideoMapper {
    @Result(column = "TYPE", property = "type", typeHandler = EnumTypeHandler.class)
    @Select("SELECT * FROM DDB_RESOURCE_VEDIO WHERE CODE=#{code} AND FK_BOOK_ID=#{bookId}")
    List<DdbResourceVideo> getUrl(@Param("code") int code, @Param("bookId") String bookId);
}
