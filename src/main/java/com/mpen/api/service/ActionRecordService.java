package com.mpen.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mp.shared.record.ActionRecords;
import com.mpen.api.bean.UserSession;
import com.mpen.api.exception.SdkException;

public interface ActionRecordService {
    boolean save(ActionRecords actionRecords, UserSession userSession) throws SdkException, JsonProcessingException;
}
