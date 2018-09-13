/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.mpen.api.domain.DdbPageDetail;

@Mapper
public interface PageDetailMapper {
    @Select("SELECT * FROM DDB_PAGE_DETAIL ORDER BY VERSION DESC LIMIT 1")
    DdbPageDetail get();

    @Insert("INSERT INTO DDB_PAGE_DETAIL (VERSION,PAGE_INFOS) VALUES(#{version},#{pageInfos})")
    void save(DdbPageDetail pageDetail);

}
