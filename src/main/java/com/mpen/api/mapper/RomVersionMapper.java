/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.mapper;

import com.mpen.api.domain.DdbRomVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.EnumTypeHandler;

@Mapper
public interface RomVersionMapper {
    @Result(column = "TYPE", property = "type", typeHandler = EnumTypeHandler.class)
    @Select("SELECT * FROM DDB_ROM_VERSION WHERE ID=#{id} LIMIT 1")
    DdbRomVersion getById(String id);
}
