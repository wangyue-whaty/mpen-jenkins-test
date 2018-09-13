/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service;

import com.mpen.api.bean.PenInfo;
import com.mpen.api.bean.PenMac;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.domain.DdbPenCmd;
import com.mpen.api.exception.SdkException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface PePenService {
    /**
     * 保存笔信息.
     * 
     */
    Boolean savePen(String identifiaction, PenInfo penInfo) throws SdkException;

    /**
     * 删除笔信息.
     * 
     */
    void deleteByIdentification(String identifiaction);

    /**
     * 查询笔是否允许打开adb.
     * 
     */
    Boolean adbAdmit(String identifiaction) throws SdkException;

    /**
     * 获取未绑定笔列表.
     * 
     */
    List<PenMac> getUnBindPen(String macStr) throws SdkException;

    /**
     * 获取笔完整mac地址.
     * 
     */
    PenMac getCompleteMac(String mac) throws SdkException;

    DdbPePen getPenByIdentifiaction(String identifiaction);

    /**
     * 未绑定时，默认允许云点读100次.
     * 
     */
    boolean checkReadingAllow(HttpServletRequest httpRequest);
    
    /**
     * 获取笔需要执行的命令 
     * @throws SdkException 
     */
    DdbPenCmd getPenCmd(String penId, String cmdId, String result) throws SdkException;
    
    boolean updateCmdUrl(String comdId,String url, String description);
    
    boolean checkAppVersion(HttpServletRequest request);
}
