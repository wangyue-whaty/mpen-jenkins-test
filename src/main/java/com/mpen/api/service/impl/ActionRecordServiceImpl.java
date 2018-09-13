package com.mpen.api.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mp.shared.record.ActionRecord;
import com.mp.shared.record.ActionRecords;
import com.mp.shared.record.TaskRecord;
import com.mpen.api.bean.UserSession;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbActionRecord;
import com.mpen.api.domain.DdbPePen;
import com.mpen.api.exception.SdkException;
import com.mpen.api.service.ActionRecordService;
import com.mpen.api.service.PePenService;
import com.mpen.api.service.RecordUserBookService;

/**
 * ActionRecord服务.
 *
 * @author zyt
 *
 */
@Component
public class ActionRecordServiceImpl implements ActionRecordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRecordServiceImpl.class);
    @Autowired
    private PePenService pePenService;
    @Autowired
    private RecordUserBookService recordUserBookService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ActionRecords actionRecords, UserSession userSession)
        throws SdkException, JsonProcessingException {
        final List<ActionRecord> actionRecordList = actionRecords.getRecords();
        if (actionRecordList == null || actionRecordList.size() <= 0) {
            return true;
        }
        final String uploadUuid = actionRecords.getUploadUuid();
        final String penId = actionRecords.getPenId();
        final DdbPePen pen = pePenService.getPenByIdentifiaction(penId);
        if (pen == null) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        DdbActionRecord record = null;
        ActionRecord actionRecord = null;
        for (int i = 0; i < actionRecordList.size(); i++) {
            actionRecord = actionRecordList.get(i);
            record = new DdbActionRecord();
            record.setUploadUuid(uploadUuid);
            record.setSequceNumInBatch(i);
            record.setFkPenId(pen.getId());
            record.setUploadTime(new Date());
            record.setType(actionRecord.type);
            record.setSubType(actionRecord.subType);
            if (ActionRecord.Type.TASK == actionRecord.type
                && (ActionRecord.Subtype.FetchCodeInfo == actionRecord.subType
                    || ActionRecord.Subtype.ReadEvalGroup == actionRecord.subType)
                && userSession != null) {
                recordUserBookService.save(TaskRecord.fromActionRecord(actionRecord), userSession, pen.getId());
            }
            record.setData(Constants.GSON.toJson(actionRecord.data));
            record.setVersion(actionRecord.version);
            LOGGER.info(Constants.GSON.toJson(record));
        }
        return true;
    }
}
