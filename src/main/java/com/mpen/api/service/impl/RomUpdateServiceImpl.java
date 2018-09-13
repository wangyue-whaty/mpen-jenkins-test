/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import com.mpen.api.bean.RomUpdate;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.domain.DdbRomUpdate;
import com.mpen.api.domain.DdbRomVersion;
import com.mpen.api.exception.CacheException;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.PePenMapper;
import com.mpen.api.mapper.RomUpdateMapper;
import com.mpen.api.mapper.RomVersionMapper;
import com.mpen.api.service.MemCacheService;
import com.mpen.api.service.PePenService;
import com.mpen.api.service.RomUpdateService;
import com.mpen.api.util.CommUtil;
import com.mpen.api.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rom升级服务.
 *
 * @author zyt
 *
 */
@Component
public class RomUpdateServiceImpl implements RomUpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppServiceImpl.class);
    @Autowired
    private PePenMapper pePenMapper;
    @Autowired
    private PePenService pePenService;
    @Autowired
    private RomUpdateMapper romUpdateMapper;
    @Autowired
    private RomVersionMapper romVersionMapper;
    @Autowired
    private MemCacheService memCacheService;

    @Override
    public List<RomUpdate> getUpdateMessage(String penId, String version) throws SdkException {
        // 校验笔信息是否存在.
        final DdbPePen pen = pePenService.getPenByIdentifiaction(penId);
        if (pen == null || StringUtils.isBlank(pen.getId())) {
            throw new SdkException(Constants.NO_MACHING_PEN);
        }
        // 收集版本信息
        try {
            memCacheService.delete(CommUtil.getCacheKey(Constants.CACHE_PENINFO_KEY_PREFIX + penId));
            pePenMapper.updateRomVersionByIdentifiaction(penId, version);
        } catch (CacheException e) {
            e.printStackTrace();
        }
        DdbRomUpdate ddbRomUpdate = null;
        final DdbPePen.Type penType = pen.getType() == null ? DdbPePen.Type.ANDROID : pen.getType();
        if (StringUtils.isBlank(pen.getItem())) {
            ddbRomUpdate = romUpdateMapper.get(version, penType);
        } else {
            // 笔有灰度标记时获取笔升级信息.
            ddbRomUpdate = romUpdateMapper.getByItem(pen.getItem(), version, penType);
        }
        if (ddbRomUpdate == null) {
            return null;
        }
        final List<RomUpdate> list = new ArrayList<RomUpdate>();
        final DdbRomVersion fromVersion = romVersionMapper.getById(ddbRomUpdate.getFromVersionId());
        final DdbRomVersion toVersion = romVersionMapper.getById(ddbRomUpdate.getToVersionId());
        final RomUpdate rom = new RomUpdate();
        rom.setIndex(0);
        rom.setVersionFrom(fromVersion.getName());
        rom.setVersionTo(toVersion.getName());
        rom.setDescription(ddbRomUpdate.getDescription());
        rom.setDownloadUrl(FileUtils.getFullRequestPath(ddbRomUpdate.getDownloadUrl()));
        rom.setSize(ddbRomUpdate.getFileSize());
        rom.setMd5(ddbRomUpdate.getFileMd5());
        rom.setIsForce(ddbRomUpdate.getIsForce());
        list.add(rom);
        return list;
    }
}
