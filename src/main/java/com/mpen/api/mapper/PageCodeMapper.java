/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.mpen.api.domain.DdbResourcePageCode;

@Mapper
public interface PageCodeMapper {
    @Insert("INSERT INTO DDB_RESOURCE_PAGE_CODE (ID,CREATE_DATETIME,PAGE_NUM,NAME,FK_BOOK_ID,WIDTH,HEIGHT) "
        + "VALUES (#{id},#{createDatetime},#{pageNum},#{name},#{fkBookId},#{width},#{height})")
    void save(DdbResourcePageCode page);

    @Select("SELECT ID,CREATE_DATETIME,PAGE_NUM,NAME,FK_BOOK_ID,WIDTH,HEIGHT FROM DDB_RESOURCE_PAGE_CODE WHERE FK_BOOK_ID=#{fkBookId} ORDER BY PAGE_NUM")
    List<DdbResourcePageCode> getBookPages(@Param("fkBookId") String fkBookId);

}
