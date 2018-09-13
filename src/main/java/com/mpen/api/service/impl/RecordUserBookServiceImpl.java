/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mp.shared.common.CodeInfo;
import com.mp.shared.common.Exam.SubTopic;
import com.mp.shared.common.QuickCodeInfo;
import com.mp.shared.common.ShCode;
import com.mp.shared.record.ClickRecord;
import com.mp.shared.record.TaskRecord;
import com.mpen.api.bean.Activity;
import com.mpen.api.bean.ActivityStudyDetail;
import com.mpen.api.bean.Book;
import com.mpen.api.bean.BookLearningInfo;
import com.mpen.api.bean.BookStudyInfo;
import com.mpen.api.bean.DateStudyTime;
import com.mpen.api.bean.FileParam;
import com.mpen.api.bean.Medal;
import com.mpen.api.bean.Sentence;
import com.mpen.api.bean.StudyTrace;
import com.mpen.api.bean.Unit;
import com.mpen.api.bean.UserInfo;
import com.mpen.api.bean.UserSession;
import com.mpen.api.bean.UserStudyDetail;
import com.mpen.api.bean.WeeklyDetail;
import com.mpen.api.bean.WeeklyParam;
import com.mpen.api.bean.Weeklys;
import com.mpen.api.bean.WrongTimePenInfo;
import com.mpen.api.common.Constants;
import com.mpen.api.domain.DdbPeCustom;
import com.mpen.api.domain.DdbPeLabel;
import com.mpen.api.domain.DdbRecordUserBook;
import com.mpen.api.domain.DdbResourceBook;
import com.mpen.api.domain.DdbResourceCode;
import com.mpen.api.domain.OralTestDetail;
import com.mpen.api.domain.SsoUser;
import com.mpen.api.exception.CacheException;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.OralTestDetailMapper;
import com.mpen.api.mapper.PeLabelMapper;
import com.mpen.api.mapper.RecordUserBookMapper;
import com.mpen.api.mapper.SsoUserMapper;
import com.mpen.api.service.DecodeService;
import com.mpen.api.service.MemCacheService;
import com.mpen.api.service.RecordUserBookService;
import com.mpen.api.service.ResourceBookService;
import com.mpen.api.util.CommUtil;
import com.mpen.api.util.FileUtils;
import com.mpen.api.util.OralEvaluationUtil;
import com.mpen.api.util.OralEvaluationUtil.EvalResult;

/**
 * 点读记录服务.
 *
 * @author zyt
 *
 */
@Component
public class RecordUserBookServiceImpl implements RecordUserBookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordUserBookServiceImpl.class);

    @Autowired
    private RecordUserBookMapper recordUserBookMapper;
    @Autowired
    private ResourceBookService resourceBookService;
    @Autowired
    private SsoUserMapper ssoUserMapper;
    @Autowired
    private MemCacheService memCacheService;
    @Autowired
    private PeLabelMapper peLabelMapper;
    @Autowired
    private OralTestDetailMapper oralTestDetailMapper;
    @Autowired
    private DecodeService decodeService;

    final private SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(TaskRecord taskRecord, UserSession userSession, String penId) throws SdkException {
        if (!taskRecord.isSuccessful) {
            return true;
        }
        if (taskRecord.createdRealTime - System.currentTimeMillis() > 120000) {
            // TODO 点读时间不正确时收集错误信息
            final String key = CommUtil.getCacheKey(Constants.CACHE_WRONG_TIME_PEN);
            try {
                List<WrongTimePenInfo> list = memCacheService.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                }
                // list保护，限制大小
                if (list.size() < 1000) {
                    final WrongTimePenInfo penInfo = new WrongTimePenInfo(penId, taskRecord.createdRealTime);
                    list.add(penInfo);
                    memCacheService.set(key, list, Constants.DEFAULT_CACHE_EXPIRATION);
                } else {
                    LOGGER.info("WrongTimePenInfoList is too long!");
                }
            } catch (CacheException e) {
                e.printStackTrace();
            }
            return true;
        }
        final ClickRecord clickRecord = Constants.GSON.fromJson(taskRecord.extra, ClickRecord.class);
        if (clickRecord == null) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        final DdbRecordUserBook ddbRecordUserBook = getByClickRecord(clickRecord);
        ddbRecordUserBook.setId(CommUtil.genRecordKey());
        ddbRecordUserBook.setTime(taskRecord.duration / 1000);
        ddbRecordUserBook.setLoginId(userSession.getLoginId());
        ddbRecordUserBook.setClickTime(new Date(taskRecord.createdRealTime));
        switch (taskRecord.name) {
        case ReadEvalGroup:
            ddbRecordUserBook.setFunction(Constants.TWO);
            ddbRecordUserBook.setText(clickRecord.text);
            ddbRecordUserBook.setScore(clickRecord.score);
            break;
        default:
            ddbRecordUserBook.setFunction(Constants.ZERO);
            break;
        }
        recordUserBookMapper.save(ddbRecordUserBook);
        return true;
    }

    @Override
    public Integer getPointTimes(String bookId) throws CacheException {
        final String key = CommUtil.getCacheKey(Constants.CACHE_POINT_NUM_PRIFIX + bookId);
        Map<String, Integer> map = memCacheService.get(key);
        /*
         * if (map == null) { map = new HashMap<String, Integer>(); final
         * List<GoodsInfo> countResult =
         * recordUserBookMapper.getBookCustomTimes(); for (GoodsInfo goodsInfo :
         * countResult) { map.put(goodsInfo.getBookId(),
         * goodsInfo.getReadnum()); } memCacheService.set(key, map,
         * Constants.DEFAULT_CACHE_EXPIRATION); }
         */
        if (map.containsKey(bookId)) {
            return map.get(bookId);
        } else {
            return Constants.INT_ZERO;
        }
    }

    @Override
    public UserStudyDetail getCompleteUserStudyInfo(UserSession userSession) throws SdkException, CacheException {
        final Map<String, Object> map = getUserStydyMap(userSession.getLoginId());
        final float totalTime = (float) map.get(Constants.TOTAL_TIME);
        final Set<String> days = (Set<String>) map.get(Constants.DAYS);
        final Map<String, DateStudyTime> dateMap = (Map<String, DateStudyTime>) map.get(Constants.DATE_MAP);
        final List<BookLearningInfo> bookList = (List<BookLearningInfo>) map.get(Constants.BOOK_LIST);
        final UserInfo userInfo = new UserInfo();
        final SsoUser ssoUser = ssoUserMapper.getById(userSession.getSsoUser().getId());
        final DdbPeCustom custom = userSession.getPeCustom();
        userInfo.setLoginId(userSession.getLoginId());
        userInfo.setLearnedBooks(bookList.size());
        userInfo.setLearnedDurations(totalTime);
        userInfo.setLearnedDays(days.size());
        userInfo.setPhoto(FileUtils.getFullRequestPath(ssoUser.getPhoto()));
        userInfo.setTrueName(custom.getNickName() == null ? custom.getLoginId() : custom.getNickName());
        userInfo.setSex(custom.getFlagGender());
        userInfo.setAge(custom.getAge());
        final DdbPeLabel ddbPeLabel = peLabelMapper.getById(custom.getFkLabelId());
        if (ddbPeLabel != null) {
            userInfo.setGrade(ddbPeLabel.getName());
        }
        userInfo.setSchool(custom.getSchool());
        // TODO 临时数据
        userInfo.setIntegral(Constants.INT_HUNDRED);
        final UserStudyDetail userStudyDetail = new UserStudyDetail();
        userStudyDetail.setUserInfo(userInfo);
        // 勋章
        final List<Medal> medalsList = new ArrayList<Medal>();
        // TODO 临时数据
        for (int i = Constants.INT_ZERO; i < 5; i++) {
            final Medal medal = new Medal();
            medal.setId(Constants.MEDAL_ID);
            medal.setName(Constants.MEDAL_NAME);
            medal.setPhoto(Constants.MEDAL_PHOTO);
            medalsList.add(medal);
        }
        userStudyDetail.setMedals(medalsList);
        // 不同书籍学习时间
        bookList.forEach((book) -> {
            book.setPhoto(FileUtils.getFullRequestPath(book.getPhoto()));
        });
        userStudyDetail.setBooks(bookList);
        // 不同日期学习时间
        final List<DateStudyTime> dateList = new ArrayList<DateStudyTime>();
        final String[] now = StringUtils.split(sdf.format(new Date()), "-");
        dateMap.forEach((key, value) -> {
            final String[] date = StringUtils.split(key, "-");
            if (now[0].equals(date[0]) && now[1].equals(date[1])) {
                DateStudyTime dateStudyTime = value;
                dateStudyTime.setDate(date[2]);
                dateList.add(dateStudyTime);
            }
        });
        userStudyDetail.setDateStudyTimes(dateList);
        return userStudyDetail;
    }

    @Override
    public List<DateStudyTime> getDateStudyTime(UserSession userSession, Book book) throws CacheException {
        final Map<String, Object> map = getUserStydyMap(userSession.getLoginId());
        final Map<String, DateStudyTime> dateMap = (Map<String, DateStudyTime>) map.get(Constants.DATE_MAP);
        final List<DateStudyTime> dateList = new ArrayList<DateStudyTime>();
        String[] dateParam = null;
        if (StringUtils.isBlank(book.getDate())) {
            dateParam = StringUtils.split(sdf.format(new Date()), "-");
        } else {
            dateParam = StringUtils.split(book.getDate(), "-");
        }
        final String[] now = dateParam;
        dateMap.forEach((key, value) -> {
            final String[] date = StringUtils.split(key, "-");
            if (now[0].equals(date[0]) && now[1].equals(date[1])) {
                final DateStudyTime dateStudyTime = value;
                dateStudyTime.setDate(date[2]);
                dateList.add(dateStudyTime);
            }
        });
        return dateList;
    }

    @Override
    public Map<String, Object> getUserStydyMap(String loginId) throws CacheException {
        final String key = CommUtil.getCacheKey(Constants.CACHE_USER_STUDY_PREFIX + loginId);
        Map<String, Object> map = memCacheService.get(key);
        List<DdbRecordUserBook> list = null;
        float totalTime = Constants.INT_ZERO;
        Set<String> days = null;
        Map<String, DateStudyTime> dateMap = null;
        List<BookLearningInfo> bookList = null;
        DdbRecordUserBook nodeDeatil = null;
        if (map == null) {
            map = new HashMap<String, Object>();
            days = new HashSet<String>();
            dateMap = new HashMap<String, DateStudyTime>();
            bookList = new ArrayList<BookLearningInfo>();
        } else {
            nodeDeatil = (DdbRecordUserBook) map.get(Constants.NEST_STUDY_DETAIL);
            totalTime = (float) map.get(Constants.TOTAL_TIME);
            days = (Set<String>) map.get(Constants.DAYS);
            dateMap = (Map<String, DateStudyTime>) map.get(Constants.DATE_MAP);
            bookList = (List<BookLearningInfo>) map.get(Constants.BOOK_LIST);
        }
        if (nodeDeatil == null) {
            list = recordUserBookMapper.getByLoginId(loginId);
        } else {
            list = recordUserBookMapper.getByLoginIdAndDate(loginId, nodeDeatil.getClickTime());
        }
        dealRecordUserBook(nodeDeatil, totalTime, dateMap, list, map, days, bookList);
        memCacheService.set(key, map, Constants.DEFAULT_CACHE_EXPIRATION);
        return map;
    }

    @Override
    public void dealRecordUserBook(DdbRecordUserBook nodeDeatil, float totalTime, Map<String, DateStudyTime> dateMap,
        List<DdbRecordUserBook> list, Map<String, Object> map, Set<String> days, List<BookLearningInfo> bookList)
        throws CacheException {
        // TODO 学情处理方法还需要调整
        if (list != null && list.size() > 0) {
            final List<BookLearningInfo> tempBookList = new ArrayList<BookLearningInfo>();
            if (nodeDeatil != null) {
                totalTime = delNodeDetail(nodeDeatil, totalTime, dateMap);
            }
            DdbRecordUserBook lastDetail = null;
            for (DdbRecordUserBook detail : list) {
                final DdbResourceBook book = resourceBookService.getById(detail.getFkBookId());
                if (book == null) {
                    continue;
                }
                float time = getTime(lastDetail, detail);
                if (lastDetail == null) {
                    map.put(Constants.NEST_STUDY_DETAIL, detail);
                }
                getDateStudyDetail(detail, dateMap, time, book);
                // 用来统计不同书的学习时长
                BookLearningInfo bookIn = null;
                for (BookLearningInfo bookInfo : tempBookList) {
                    if (book.getId().equals(bookInfo.getId())) {
                        bookIn = bookInfo;
                    }
                }
                if (bookIn == null) {
                    bookIn = new BookLearningInfo();
                    bookIn.setId(book.getId());
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    final String formatTime = simpleDateFormat.format(detail.getClickTime());
                    bookIn.setLastReading(formatTime.startsWith(String.valueOf(LocalDate.now().getYear()))
                        ? formatTime.substring(5) : formatTime);
                    bookIn.setName(book.getName());
                    bookIn.setPhoto(book.getPhoto() == null ? book.getSuitImage() : book.getPhoto());
                    tempBookList.add(bookIn);
                }
                // 用来统计学习总时长
                totalTime += time;
                // 用来统计累计多少天
                days.add(sdf.format(detail.getClickTime()));
                lastDetail = detail;
            }
            for (BookLearningInfo bookInfo : bookList) {
                boolean temp = true;
                for (BookLearningInfo bookIn : tempBookList) {
                    if (bookInfo.getId().equals(bookIn.getId())) {
                        temp = false;
                        break;
                    }
                }
                if (temp) {
                    tempBookList.add(bookInfo);
                }
            }
            bookList = tempBookList;
        }
        map.put(Constants.TOTAL_TIME, totalTime);
        map.put(Constants.DAYS, days);
        map.put(Constants.DATE_MAP, dateMap);
        map.put(Constants.BOOK_LIST, bookList);
    }

    @Override
    public BookStudyInfo getBookStudyInfo(UserSession session, String bookId) throws CacheException, SdkException {
        final DdbResourceBook book = resourceBookService.getById(bookId);
        if (book == null) {
            throw new SdkException(Constants.NO_MACHING_BOOK);
        }
        final List<DdbRecordUserBook> list = recordUserBookMapper.getByLoginIdAndBookId(session.getLoginId(), bookId);
        final Map<String, DateStudyTime> dateMap = new HashMap<String, DateStudyTime>();
        final List<StudyTrace> traceList = new ArrayList<StudyTrace>();
        float studayTime = Constants.FLOAT_ZERO;
        float spokenTime = Constants.FLOAT_ZERO;
        DdbRecordUserBook lastDetail = null;
        for (DdbRecordUserBook detail : list) {
            float recordTime = getTime(lastDetail, detail);
            getDateStudyDetail(detail, dateMap, recordTime, book);
            if (Constants.TWO.equals(detail.getFunction())) {
                spokenTime += recordTime;
            }
            studayTime += recordTime;
            // 统计学习轨迹信息
            final String date = sdf.format(detail.getClickTime());
            StudyTrace trace = null;
            for (StudyTrace studyTrace : traceList) {
                final String time = studyTrace.getDate();
                if (date.equals(time)) {
                    trace = studyTrace;
                    break;
                }
            }
            if (trace == null) {
                trace = new StudyTrace();
                trace.setDate(date);
                if (Constants.TWO.equals(detail.getFunction())) {
                    if (StringUtils.isNotBlank(detail.getPage())) {
                        trace.getSpeakingPages().add(Constants.P_STRING + detail.getPage());
                    }
                } else {
                    if (StringUtils.isNotBlank(detail.getPage())) {
                        trace.getLearningPages().add(Constants.P_STRING + detail.getPage());
                    }
                }
                traceList.add(trace);
            } else {
                if (Constants.TWO.equals(detail.getFunction())) {
                    if (StringUtils.isNotBlank(detail.getPage())) {
                        trace.getSpeakingPages().add(Constants.P_STRING + detail.getPage());
                    }
                } else {
                    if (StringUtils.isNotBlank(detail.getPage())) {
                        trace.getLearningPages().add(Constants.P_STRING + detail.getPage());
                    }
                }
            }

            lastDetail = detail;
        }
        final BookLearningInfo bookInfo = new BookLearningInfo();
        bookInfo.setId(bookId);
        bookInfo.setName(book.getName());
        bookInfo
            .setPhoto(FileUtils.getFullRequestPath(book.getPhoto() == null ? book.getSuitImage() : book.getPhoto()));
        bookInfo.setLearnedDuration(studayTime);
        bookInfo.setSpeakingDuration(spokenTime);
        // TODO 临时数据
        bookInfo.setGold(Constants.INT_FIFTY);
        bookInfo.setRanking(Constants.INT_TWENTY);
        bookInfo.setType(book.getType());
        BookStudyInfo bookStudyInfo = new BookStudyInfo();
        bookStudyInfo.setBookLearningInfo(bookInfo);
        // 不同日期学习时间
        final Set<DateStudyTime> dateList = new TreeSet<DateStudyTime>();
        final String[] now = StringUtils.split(sdf.format(new Date()), "-");
        dateMap.forEach((key, value) -> {
            final String[] date = StringUtils.split(key, "-");
            if (now[0].equals(date[0]) && now[1].equals(date[1])) {
                DateStudyTime dateStudyTime = value;
                dateStudyTime.setDate(date[2]);
                dateList.add(dateStudyTime);
            }
        });
        bookStudyInfo.setDateStudyTimes(dateList);
        bookStudyInfo.setStudyTrace(traceList);
        return bookStudyInfo;
    }

    /**
     * 去除节点数据
     */
    private float delNodeDetail(DdbRecordUserBook nodeDeatil, float totalTime, Map<String, DateStudyTime> dateMap)
        throws CacheException {
        final String date = sdf.format(nodeDeatil.getClickTime());
        final DdbResourceBook book = resourceBookService.getById(nodeDeatil.getFkBookId());
        DateStudyTime dateStudyTime = dateMap.get(date);
        float studyTime = nodeDeatil.getTime() / Constants.FLOAT_SIXTY;
        if (studyTime < 1) {
            studyTime = 1;
        }
        totalTime = totalTime - studyTime;
        float time = dateStudyTime.getCountTime();
        time -= studyTime;
        dateStudyTime.setCountTime(time);
        if (Constants.TWO.equals(nodeDeatil.getFunction())) {
            time = dateStudyTime.getSpokenTestTime();
            time -= studyTime;
            dateStudyTime.setSpokenTestTime(time);
        } else {
            switch (book.getType()) {
            case READ:
                time = dateStudyTime.getReadingTime();
                time -= studyTime;
                dateStudyTime.setReadingTime(time);
                break;
            case TEST:
                time = dateStudyTime.getExercisesTime();
                time -= studyTime;
                dateStudyTime.setExercisesTime(time);
                break;
            case STUDY:
                time = dateStudyTime.getBookStudyTime();
                time -= studyTime;
                dateStudyTime.setBookStudyTime(time);
                break;
            default:
                time = dateStudyTime.getOtherTime();
                time -= studyTime;
                dateStudyTime.setOtherTime(time);
                break;
            }
        }
        dateMap.put(date, dateStudyTime);
        return totalTime;
    }

    /**
     * 用来统计不同日期的学习时长.
     * 
     */
    private void getDateStudyDetail(DdbRecordUserBook detail, Map<String, DateStudyTime> dateMap, float recordTime,
        DdbResourceBook book) {
        final String date = sdf.format(detail.getClickTime());
        DateStudyTime dateStudyTime = dateMap.get(date);
        Float time;
        if (dateStudyTime == null) {
            dateStudyTime = new DateStudyTime();
            dateStudyTime.setCountTime(recordTime);
            if (Constants.TWO.equals(detail.getFunction())) {
                dateStudyTime.setSpokenTestTime(recordTime);
            } else {
                switch (book.getType()) {
                case READ:
                    dateStudyTime.setReadingTime(recordTime);
                    break;
                case TEST:
                    dateStudyTime.setExercisesTime(recordTime);
                    break;
                case STUDY:
                    dateStudyTime.setBookStudyTime(recordTime);
                    break;
                default:
                    dateStudyTime.setOtherTime(recordTime);
                    break;
                }
            }
            dateStudyTime.setDate(date);
        } else {
            time = dateStudyTime.getCountTime();
            if (time == null) {
                time = Constants.FLOAT_ZERO;
            }
            time += recordTime;
            dateStudyTime.setCountTime(time);
            if (Constants.TWO.equals(detail.getFunction())) {
                time = dateStudyTime.getSpokenTestTime();
                if (time == null) {
                    time = Constants.FLOAT_ZERO;
                }
                time += recordTime;
                dateStudyTime.setSpokenTestTime(time);
            } else {
                switch (book.getType()) {
                case READ:
                    time = dateStudyTime.getReadingTime();
                    if (time == null) {
                        time = Constants.FLOAT_ZERO;
                    }
                    time += recordTime;
                    dateStudyTime.setReadingTime(time);
                    break;
                case TEST:
                    time = dateStudyTime.getExercisesTime();
                    if (time == null) {
                        time = Constants.FLOAT_ZERO;
                    }
                    time += recordTime;
                    dateStudyTime.setExercisesTime(time);
                    break;
                case STUDY:
                    time = dateStudyTime.getBookStudyTime();
                    if (time == null) {
                        time = Constants.FLOAT_ZERO;
                    }
                    time += recordTime;
                    dateStudyTime.setBookStudyTime(time);
                    break;
                default:
                    time = dateStudyTime.getOtherTime();
                    if (time == null) {
                        time = Constants.FLOAT_ZERO;
                    }
                    time += recordTime;
                    dateStudyTime.setOtherTime(time);
                    break;
                }
            }
        }
        dateMap.put(date, dateStudyTime);
    }

    @Override
    public List<Unit> getBookContentStudyDetailByBookId(UserSession session, String bookId)
        throws SdkException, CacheException {
        final List<Unit> unitsList = resourceBookService.getBookContent(bookId, Constants.CACHE_STUDY_PREFIX);
        if (unitsList == null) {
            return null;
        }
        final List<ActivityStudyDetail> list = recordUserBookMapper
            .getStudyDetailByLoginIdAndBookId(session.getLoginId(), bookId);
        final Map<String, ActivityStudyDetail> tempMap = new HashMap<String, ActivityStudyDetail>();
        for (ActivityStudyDetail obj : list) {
            tempMap.put(obj.getFkActivityId(), obj);
        }
        for (Unit unit : unitsList) {
            final List<Activity> activityList = unit.getActivities();
            if (activityList == null || activityList.size() == 0) {
                continue;
            }
            for (Activity activity : activityList) {
                final ActivityStudyDetail activityStudyDetail = tempMap.get(activity.getId());
                if (activityStudyDetail == null) {
                    activity.setNumber(Constants.INT_ZERO);
                    activity.setDuration(Constants.FLOAT_ZERO);
                    continue;
                }
                final Float time = activityStudyDetail.getTime() / Constants.FLOAT_SIXTY;
                final int number = activityStudyDetail.getCountTimes();
                String date = sdf.format(activityStudyDetail.getDate());
                final String str = unit.getDate();
                if (StringUtils.isNotBlank(str)) {
                    if (date.compareTo(str) < 0) {
                        date = str;
                    }
                }
                activity.setNumber(number);
                activity.setDuration(time);
                unit.setLearn(Constants.INT_ONE);
                unit.setDate(date);
            }
        }
        return unitsList;
    }

    @Override
    public List<Unit> getBookContentSpokenDetailByBookId(UserSession userSession, String bookId)
        throws SdkException, CacheException {
        final List<Unit> unitsList = resourceBookService.getBookContent(bookId, Constants.CACHE_SPOKEN_PREFIX);
        if (unitsList == null) {
            return null;
        }
        final List<ActivityStudyDetail> list = recordUserBookMapper
            .getSpokenDetailByLoginIdAndBookId(userSession.getLoginId(), bookId);
        final Map<String, ActivityStudyDetail> tempMap = new HashMap<String, ActivityStudyDetail>();
        for (ActivityStudyDetail obj : list) {
            tempMap.put(obj.getText().replace("#1", "'"), obj);
        }
        for (Unit unit : unitsList) {
            final List<Activity> activityList = unit.getActivities();
            if (activityList == null || activityList.size() == 0) {
                continue;
            }
            for (Activity activity : activityList) {
                final List<Sentence> sentences = activity.getSentences();
                if (sentences != null && sentences.size() != 0) {
                    for (Sentence sentence : sentences) {
                        if (sentence == null) {
                            continue;
                        }
                        final String text = sentence.getTitle();
                        final ActivityStudyDetail activityStudyDetail = tempMap.get(text);
                        if (activityStudyDetail == null) {
                            sentence.setNumber(Constants.INT_ZERO);
                            sentence.setScore(Constants.INT_ZERO);
                            continue;
                        }
                        String date = sdf.format(activityStudyDetail.getDate());
                        sentence.setNumber(activityStudyDetail.getCountTimes());
                        sentence.setScore(activityStudyDetail.getScore());
                        final String str = unit.getDate();
                        if (StringUtils.isNotBlank(str)) {
                            if (date.compareTo(str) < 0) {
                                date = str;
                            }
                        }
                        unit.setLearn(Constants.INT_ONE);
                        unit.setDate(date);
                    }
                }
            }
        }
        return unitsList;
    }

    private DdbRecordUserBook getByClickRecord(ClickRecord clickRecord) {
        final DdbRecordUserBook record = new DdbRecordUserBook();
        record.setFkBookId(clickRecord.bookId);
        record.setCodeType(clickRecord.code.type);
        DdbResourceCode resourceCode = null;
        switch (record.getCodeType()) {
        case OID3:
        case SH:
            record.setCode(String.valueOf(clickRecord.code.shCode.code));
            resourceCode = DdbResourceCode.getCode(record.getFkBookId(), clickRecord.code.shCode.code);
            break;
        case MP:
            // TODO 暂不支持MP码学情统计
            /*
             * record.setCode(String.valueOf(clickRecord.code.mpCode.baseCode));
             * record.setX(clickRecord.code.mpPoint.x);
             * record.setY(clickRecord.code.mpPoint.y); resourceCode =
             * resourceCodeMapper.getByXY(record.getFkBookId(), record.getX(),
             * record.getY());
             */
            break;
        default:
            return record;
        }
        if (resourceCode != null) {
            record.setPage(resourceCode.getPage());
            record.setFkActivityId(resourceCode.getFkCatalogId());
        }
        return record;
    }

    @Override
    public List<Weeklys> getWeeklyList(UserSession userSession) {
        final LocalDateTime now = LocalDateTime.now();
        final Map<Integer, List<WeeklyDetail>> map = new TreeMap<Integer, List<WeeklyDetail>>();
        LocalDateTime startTime = LocalDateTime.of(2017, 5, 1, 0, 0);
        // TODO 改成多对多后，记录起始时间可以考虑和笔绑定的时间
        final Date createDate = userSession.getSsoUser().getCreateDate();
        if (createDate != null) {
            final Calendar c = Calendar.getInstance();
            c.setTime(createDate);
            final int year = c.get(Calendar.YEAR);
            final int month = c.get(Calendar.MONTH) + 1;
            final int day = c.get(Calendar.DATE);
            if (year > 2016 && month > 4) {
                startTime = LocalDateTime.of(year, month, day, 0, 0);
            }
        }
        int weekValue = startTime.getDayOfWeek().getValue();
        if (weekValue == 7) {
            weekValue = 0;
        }
        LocalDateTime endTime = startTime.plusDays(7 - weekValue);
        List<WeeklyDetail> weeklyDetailList = null;
        WeeklyDetail detail = null;
        while (endTime.compareTo(now) <= 0) {
            weeklyDetailList = map.get(endTime.getMonthValue());
            if (weeklyDetailList == null) {
                weeklyDetailList = new ArrayList<WeeklyDetail>();
            }
            detail = new WeeklyDetail();
            detail.setStartDate(startTime.toLocalDate().toString());
            detail.setEndDate(endTime.toLocalDate().toString());
            weeklyDetailList.add(detail);
            map.put(endTime.getMonthValue(), weeklyDetailList);
            startTime = endTime;
            endTime = endTime.plusWeeks(1);
        }
        final List<Weeklys> weeklysList = new ArrayList<Weeklys>();
        map.forEach((key, value) -> {
            Weeklys weeklys = new Weeklys(key, value);
            weeklysList.add(weeklys);
        });
        Collections.reverse(weeklysList);
        return weeklysList;
    }

    @Override
    public WeeklyDetail getWeekly(WeeklyParam weeklyParam, UserSession userSession)
        throws CacheException, ParseException {
        final String key = CommUtil
            .getCacheKey(Constants.CACHE_USER_STUDY_PREFIX + userSession.getLoginId() + "_" + weeklyParam.getEndDate());
        WeeklyDetail detail = memCacheService.get(key);
        if (detail == null) {
            final List<DdbRecordUserBook> weeklyRecord = recordUserBookMapper.getWeeklyRecord(userSession.getLoginId(),
                sdf.parse(weeklyParam.getStartDate()), sdf.parse(weeklyParam.getEndDate()));
            float totalTime = 0;
            float studyTime = 0;
            float readTime = 0;
            float testTime = 0;
            float spokenTime = 0;
            int spokenTimes = 0;
            float spokenTotalScore = 0;
            final float[] timeArray = new float[24];
            DdbRecordUserBook lastRecord = null;
            for (DdbRecordUserBook record : weeklyRecord) {
                final DdbResourceBook book = resourceBookService.getById(record.getFkBookId());
                if (book == null) {
                    continue;
                }
                final LocalDateTime dateTime = LocalDateTime.ofInstant(record.getClickTime().toInstant(),
                    ZoneId.systemDefault());
                final float time = getTime(lastRecord, record);
                timeArray[dateTime.getHour()] += time;
                if (Constants.TWO.equals(record.getFunction())) {
                    spokenTime += time;
                    spokenTimes++;
                    spokenTotalScore += record.getScore();
                }
                switch (book.getType()) {
                case STUDY:
                    studyTime += time;
                    break;
                case READ:
                    testTime += time;
                    break;
                case TEST:
                    testTime += time;
                    break;
                default:
                    break;
                }
                totalTime += time;
            }
            detail = new WeeklyDetail();
            detail.setUserName(StringUtils.isBlank(userSession.getPeCustom().getNickName())
                ? userSession.getPeCustom().getTrueName() : userSession.getPeCustom().getNickName());
            detail.setTotalTime(totalTime);
            detail.setReadTime(readTime);
            detail.setStudyTime(studyTime);
            detail.setTestTime(testTime);
            detail.setSpokenTime(spokenTime);
            detail.setSpokenTimes(spokenTimes);
            final float averageScopre = (float) Math.round(spokenTotalScore / spokenTimes * 100) / 100;
            detail.setSpokenScore(averageScopre);
            detail.setTimeArray(timeArray);
            memCacheService.set(key, detail, Constants.DEFAULT_CACHE_EXPIRATION);
        }
        return detail;
    }

    private float getTime(DdbRecordUserBook lastRecord, DdbRecordUserBook record) {
        float time = Constants.FLOAT_ONE;
        if (lastRecord != null) {
            time = lastRecord.getClickTime().getTime() / 1000 - record.getClickTime().getTime() / 1000
                - record.getTime();
            if (time > 120) {
                time = record.getTime() / Constants.FLOAT_SIXTY;
                if (time < 1) {
                    time = 1;
                }
            } else {
                time = (lastRecord.getClickTime().getTime() - record.getClickTime().getTime()) / 1000
                    / Constants.FLOAT_SIXTY;
            }
        } else {
            time = record.getTime() / Constants.FLOAT_SIXTY;
            if (time < 1) {
                time = 1;
            }
        }
        return time;
    }

    @Override
    public boolean saveOralTest(HttpServletRequest request, String filePath, FileParam fileParam) {
        final String fkPenId = (String) request.getAttribute(Constants.PENKEY);
        final String loginId = (String) request.getAttribute(Constants.LOGINIDKEY);
        if (StringUtils.isBlank(fkPenId) || StringUtils.isBlank(loginId)) {
            return false;
        }
        final OralTestDetail detail = new OralTestDetail();
        detail.setUploadTime(Instant.now());
        detail.setPenId(fkPenId);
        detail.setLoginId(loginId);
        detail.setFkBookId(fileParam.getUuid());
        detail.setAnswerPenTime(Instant.ofEpochMilli(fileParam.getTime()));
        detail.setNum(fileParam.getNum());
        detail.setRecordingUrl(filePath);
        detail.setIsDeal(Constants.INT_ZERO);
        // TODO 指定服务Num,现在只指定一台服务
        detail.setShardNum(fkPenId.hashCode() % FileUtils.numOralEvalShards);
        oralTestDetailMapper.save(detail);
        return true;
    }

    @Override
    public void oralEvaluate(OralTestDetail detail) {
        try {
            final CodeInfo codeInfo = decodeService.getCodeInfo(QuickCodeInfo
                .getSimpleSHQuickCodeInfo(detail.getFkBookId(), 0, new ShCode(detail.getNum(), ShCode.OID3_BASE)));
            if (codeInfo == null) {
                return;
            }
            final SubTopic subTopic = Constants.GSON.fromJson(codeInfo.languageInfos[0].extra, SubTopic.class);
            final EvalResult evaluation = OralEvaluationUtil.evaluation(detail, subTopic.getAnswer());
            if (evaluation != null && evaluation.lines != null && evaluation.lines.length > 0) {
                detail.setScore(evaluation.score * subTopic.points / 100);
                detail.setFluency(evaluation.lines[0].fluency);
                detail.setIntegrity(evaluation.lines[0].integrity);
                detail.setPronunciation(evaluation.lines[0].pronunciation);
                detail.setRecognizeTxt(Constants.GSON.toJson(evaluation.lines[0].words));
                detail.setIsDeal(Constants.INT_ONE);
                oralTestDetailMapper.update(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}