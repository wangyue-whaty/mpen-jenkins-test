/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service.impl;

import java.io.File;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mp.shared.common.BookInfo;
import com.mp.shared.common.Code;
import com.mp.shared.common.CodeInfo;
import com.mp.shared.common.Exam;
import com.mp.shared.common.Exam.Topic;
import com.mp.shared.common.FileModule;
import com.mp.shared.common.FullBookInfo;
import com.mp.shared.common.LearnWordStructureInfo;
import com.mp.shared.common.MpCode;
import com.mp.shared.common.Page;
import com.mp.shared.common.PageInfo;
import com.mp.shared.common.PageInfo.SubPageInfo;
import com.mp.shared.common.QuickCodeInfo;
import com.mp.shared.common.ResourceVersion;
import com.mp.shared.common.ShCode;
import com.mp.shared.common.SuccessResult;
import com.mp.shared.common.Utils;
import com.mp.shared.service.MpResourceDecoder;
import com.mp.shared.utils.FUtils;
import com.mpen.api.bean.Activity;
import com.mpen.api.bean.Book;
import com.mpen.api.bean.ExamResult;
import com.mpen.api.bean.ExamResult.SubTopicResult;
import com.mpen.api.bean.ExamResult.TopicResult;
import com.mpen.api.bean.PageScope;
import com.mpen.api.bean.PreBook;
import com.mpen.api.bean.Sentence;
import com.mpen.api.bean.Unit;
import com.mpen.api.common.Constants;
import com.mpen.api.common.Progress;
import com.mpen.api.domain.CacheInfos;
import com.mpen.api.domain.DdbBookCoreDetail;
import com.mpen.api.domain.DdbBookDetail;
import com.mpen.api.domain.DdbLearnWordStructureDetail;
import com.mpen.api.domain.DdbPageDetail;
import com.mpen.api.domain.DdbPeCustom;
import com.mpen.api.domain.DdbResourceBook;
import com.mpen.api.domain.DdbResourceBookCatalog;
import com.mpen.api.domain.DdbResourceBookPrint;
import com.mpen.api.domain.DdbResourceCode;
import com.mpen.api.domain.DdbResourcePageCode;
import com.mpen.api.domain.DdbResourcePageScope;
import com.mpen.api.domain.OralTestDetail;
import com.mpen.api.exception.CacheException;
import com.mpen.api.exception.SdkException;
import com.mpen.api.mapper.BookCoreDetailMapper;
import com.mpen.api.mapper.BookDetailMapper;
import com.mpen.api.mapper.LearnWordStructureDetailMapper;
import com.mpen.api.mapper.OralTestDetailMapper;
import com.mpen.api.mapper.PageCodeMapper;
import com.mpen.api.mapper.PageDetailMapper;
import com.mpen.api.mapper.PageScopeMapper;
import com.mpen.api.mapper.ResourceBookCatalogMapper;
import com.mpen.api.mapper.ResourceBookMapper;
import com.mpen.api.mapper.ResourceBookPrintMapper;
import com.mpen.api.service.DecodeService;
import com.mpen.api.service.FileService;
import com.mpen.api.service.MemCacheService;
import com.mpen.api.service.ResourceBookService;
import com.mpen.api.service.ResourceCodeService;
import com.mpen.api.util.CommUtil;
import com.mpen.api.util.FileUtils;
import com.mpen.api.util.MpCodeBuilder;
import com.mpen.api.util.MpCodeBuilder.FileType;

/**
 * ResourceBookService服务.
 *
 * @author kai
 *
 */
@Component
public class ResourceBookServiceImpl implements ResourceBookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceBookServiceImpl.class);

    @Autowired
    private ResourceBookMapper resourceBookMapper;
    @Autowired
    private MemCacheService memCacheService;
    @Autowired
    private ResourceBookCatalogMapper resourceBookCatalogMapper;
    @Autowired
    private PageScopeMapper pageScopeMapper;
    @Autowired
    private PageCodeMapper pageCodeMapper;
    @Autowired
    private ResourceBookPrintMapper resourceBookPrintMapper;
    @Autowired
    private BookDetailMapper bookDetailMapper;
    @Autowired
    private LearnWordStructureDetailMapper learnWordStructureDetailMapper;
    @Autowired
    private PageDetailMapper pageDetailMapper;
    @Autowired
    private BookCoreDetailMapper bookCoreDetailMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private ResourceCodeService resourceCodeService;
    @Autowired
    private DecodeService decodeService;
    @Autowired
    private OralTestDetailMapper oralTestDetailMapper;

    /*
     * TODO currentVersion应该是和ArrayList<BookInfo>关联的。 应该： 1）增加一个
     * GlobalDianduData table 这个table就是一个 key：value。一行key是 BookInfoList，value存储
     * Page<BookInfo> 2）public void cacaheBookInfo() 里面会设置这个table的BookInfoList
     * 3）getAllValidBooks 如果有cache
     * miss，就直接从table读取数据，设置Constants.CACHE_BOOKINFO_VERSION_KEY 和
     * Constants.CACHE_BOOKINFO_KEY
     */
    @Override
    public <T> Page<T> getCacheInfos(Class<T> c, ResourceVersion version, String versionKey, Page<T> cachePage)
        throws Exception {
        final String key = CommUtil.getCacheKey(versionKey);
        ResourceVersion currentVersion = null;
        try {
            currentVersion = memCacheService.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentVersion == null) {
            cacheBookInfo();
            return cachePage;
        }
        if (version != null && version.compareTo(currentVersion) == 0) {
            return null;
        }
        {
            final Page<T> localPage = cachePage;
            if (localPage != null && localPage.getVersion() != null
                && localPage.getVersion().compareTo(currentVersion) == 0) {
                return localPage;
            }
        }
        cacheBookInfo();
        return cachePage;
    }

    public void cacheBookInfo() {
        // LOGGER.error("cacaheBookInfo - 1");
        if (Constants.bookInfoIsLock) {
            return;
        }
        // LOGGER.error("cacaheBookInfo - 2");
        synchronized (ResourceBookServiceImpl.class) {
            if (Constants.bookInfoIsLock) {
                return;
            }
            Constants.bookInfoIsLock = true;
        }
        // LOGGER.error("cacaheBookInfo - 3");
        try {
            // 从数据库中查询booklists
            final DdbBookDetail bookDetails = bookDetailMapper.get();
            final DdbLearnWordStructureDetail learnWordStructureDetails = learnWordStructureDetailMapper.get();
            final DdbPageDetail pageDetails = pageDetailMapper.get();
            final DdbBookCoreDetail bookCoreDetails = bookCoreDetailMapper.get();
            if (bookDetails == null || bookCoreDetails == null || learnWordStructureDetails == null
                || pageDetails == null) {
                return;
            }
            final Page<FullBookInfo> localFullBookPage = bookDetails.formDetail();
            final Page<LearnWordStructureInfo> localLearnWordPage = learnWordStructureDetails.formDetail();
            final Page<PageInfo> localPage = pageDetails.formDetail();
            final Page<BookInfo> localBookPage = bookCoreDetails.formDetail();
            String key = CommUtil.getCacheKey(Constants.CACHE_FULLBOOKINFO_VERSION_KEY);
            memCacheService.set(key, localFullBookPage.getVersion(), Constants.DEFAULT_CACHE_EXPIRATION);
            Constants.fullBookInfos = localFullBookPage;
            key = CommUtil.getCacheKey(Constants.CACHE_BOOKINFO_VERSION_KEY);
            memCacheService.set(key, localBookPage.getVersion(), Constants.DEFAULT_CACHE_EXPIRATION);
            Constants.bookInfos = localBookPage;
            key = CommUtil.getCacheKey(Constants.CACHE_STRUCTUREINFO_VERSION_KEY);
            memCacheService.set(key, localLearnWordPage.getVersion(), Constants.DEFAULT_CACHE_EXPIRATION);
            Constants.learnWordStructureInfos = localLearnWordPage;
            key = CommUtil.getCacheKey(Constants.CACHE_PAGEINFO_VERSION_KEY);
            memCacheService.set(key, localPage.getVersion(), Constants.DEFAULT_CACHE_EXPIRATION);
            Constants.pageInfos = localPage;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            synchronized (ResourceBookServiceImpl.class) {
                Constants.bookInfoIsLock = false;
            }
        }
    }

    @Override
    public DdbResourceBook getById(String id) throws CacheException {
        DdbResourceBook book = null;
        final String key = CommUtil.getCacheKey(Constants.CACHE_BOOKINFO_KEY_PRIFIX + id);
        try {
            book = memCacheService.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (book == null) {
            book = resourceBookMapper.getId(id);
            if (book != null) {
                try {
                    memCacheService.set(key, book, Constants.DEFAULT_CACHE_EXPIRATION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return book;
    }

    @Override
    public List<PreBook> getPrepownloadBooks() throws Exception {
        final Page<FullBookInfo> allBookInfoPage = getCacheInfos(FullBookInfo.class, null,
            Constants.CACHE_BOOKINFO_VERSION_KEY, Constants.fullBookInfos);
        if (allBookInfoPage == null) {
            return null;
        }
        final List<FullBookInfo> books = allBookInfoPage.getItems();
        if (books != null && books.size() > 0) {
            final List<PreBook> list = new ArrayList<PreBook>();
            PreBook preBook = null;
            for (FullBookInfo fullBookInfo : books) {
                preBook = new PreBook();
                preBook.setId(fullBookInfo.bookInfo.id);
                preBook.setResSize(fullBookInfo.bookInfo.downloadSize);
                preBook.setName(fullBookInfo.bookInfo.name);
                list.add(preBook);
            }
            return list;
        }
        return null;
    }

    @Override
    public List<Unit> getBookContent(String bookId, String str) throws SdkException, CacheException {
        final String key = CommUtil.getCacheKey(str + bookId);
        List<Unit> unitsList = null;
        unitsList = memCacheService.get(key);
        if (unitsList != null) {
            return unitsList;
        }
        final ResourceBookCatalogMapper bookCatalogMapper = resourceBookCatalogMapper;
        final ResourceCodeService codeService = resourceCodeService;
        final MemCacheService cacheService = memCacheService;
        Constants.CACHE_THREAD_POOL.execute(() -> {
            final List<DdbResourceBookCatalog> units = bookCatalogMapper.getByBookIdAndName(bookId, Constants.UNIT);
            if (units == null || units.size() == 0) {
                return;
            }
            final List<Unit> unitsLists = new ArrayList<Unit>();
            for (DdbResourceBookCatalog unitCatalog : units) {
                final Unit studyUnit = new Unit();
                final DdbResourceBookCatalog catalog = bookCatalogMapper.getById(unitCatalog.getFkCatalogId());
                studyUnit.setModel(StringUtils.isBlank(catalog.getItem()) ? Constants.MODULE + " " + catalog.getNumber()
                    : catalog.getItem());
                studyUnit.setUnit(unitCatalog.getNumber() == 0 ? "" : Constants.UNIT + " " + unitCatalog.getNumber());
                studyUnit.setName(unitCatalog.getItem() == null ? "" : unitCatalog.getItem().replace("#1", "'"));
                final List<DdbResourceBookCatalog> activitys = bookCatalogMapper
                    .getByBookIdAndNameAndFkCatalogId(bookId, Constants.ACTIVITY, unitCatalog.getId());
                final List<Activity> activityList = new ArrayList<Activity>();
                for (DdbResourceBookCatalog activityCatalog : activitys) {
                    Activity activity = new Activity();
                    activity.setId(activityCatalog.getId());
                    activity
                        .setName(activityCatalog.getItem() == null ? "" : activityCatalog.getItem().replace("#1", "'"));
                    activity.setSort(Constants.ACTIVITY + " "
                        + (activityCatalog.getNumber() == 0 ? 1 : activityCatalog.getNumber()));
                    if (!Constants.CACHE_SPOKEN_PREFIX.equals(str)) {
                        activityList.add(activity);
                    } else {
                        final List<DdbResourceCode> list = codeService.getByCatalogId(bookId, activityCatalog.getId());
                        if (list != null && list.size() > 0) {
                            List<Sentence> textList = new ArrayList<Sentence>();
                            for (DdbResourceCode code : list) {
                                final Sentence sentence = new Sentence();
                                sentence.setTitle(code.getText().replace("#1", "'"));
                                textList.add(sentence);
                            }
                            activity.setSentences(textList);
                            activityList.add(activity);
                        }
                    }
                }
                studyUnit.setActivities(activityList);
                unitsLists.add(studyUnit);
            }
            try {
                cacheService.set(key, unitsLists, Constants.DEFAULT_CACHE_EXPIRATION);
            } catch (CacheException e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    @Override
    public Page<PageInfo> getBookPages(String bookId, ResourceVersion version) throws Exception {
        final Page<PageInfo> allPageInfos = getCacheInfos(PageInfo.class, version, Constants.CACHE_PAGEINFO_VERSION_KEY,
            Constants.pageInfos);
        if (allPageInfos != null) {
            final List<PageInfo> list = new ArrayList<>();
            for (PageInfo pageInfo : allPageInfos.getItems()) {
                if (pageInfo.bookId.equals(bookId)) {
                    list.add(pageInfo);
                }
            }
            final Page<PageInfo> page = new Page<PageInfo>(list, list.size());
            page.setVersion(allPageInfos.getVersion());
            return page;
        }
        return null;
    }

    private ArrayList<PageInfo> getPageInfos() throws CacheException {
        final long time = System.currentTimeMillis();
        ArrayList<PageInfo> pageInfos = new ArrayList<PageInfo>();
        final List<PageScope> pageScopes = pageScopeMapper.get();
        for (PageScope pageScope : pageScopes) {
            PageInfo pageInfo = new PageInfo();
            pageInfo.id = pageScope.getId();
            pageInfo.bookId = pageScope.getBookId();
            pageInfo.pageNum = pageScope.getPageNum();
            pageInfo.startCode = new MpCode(pageScope.getCodeStart(), 0, new Byte("5"));
            pageInfo.matrixSize = pageScope.getMatrixSize().byteValue();
            pageInfo.matrixGap = pageScope.getMatrixGap().byteValue();
            pageInfo.dotDistanceInPixels = pageScope.getDotDistanceInPixels().byteValue();
            pageInfo.dotSize = pageScope.getDotSize().byteValue();
            pageInfo.dotShift = pageScope.getDotShift().byteValue();
            pageInfo.version = new ResourceVersion(time, 0);
            // 对铺码过的旧书做兼容处理
            if (StringUtils.isBlank(pageScope.getSubPages())) {
                pageInfo.xCodeNum = pageScope.getxCodeNum();
                pageInfo.yCodeNum = pageScope.getyCodeNum();
                pageInfo.margin = new int[4];
                pageInfo.margin[0] = pageScope.getLeftMargin();
                pageInfo.margin[1] = pageScope.getTopMargin();
                pageInfo.margin[2] = pageScope.getRightMargin();
                pageInfo.margin[3] = pageScope.getBottomMargin();
            } else {
                pageInfo.subPageInfos = Constants.GSON.fromJson(pageScope.getSubPages(), SubPageInfo[].class);
                pageInfo.xCodeNum = 1;
                pageInfo.yCodeNum = (int) (pageScope.getCodeEnd() - pageScope.getCodeStart() + 1);
            }
            pageInfos.add(pageInfo);
        }
        return pageInfos;
    }

    @Override
    public BookInfo getBookInfo(String bookId) throws Exception {
        if (StringUtils.isBlank(bookId)) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        final Page<BookInfo> bookInfoPage = getCacheInfos(BookInfo.class, null, Constants.CACHE_BOOKINFO_VERSION_KEY,
            Constants.bookInfos);
        if (bookInfoPage == null) {
            return null;
        }
        final ArrayList<BookInfo> bookInfos = (ArrayList<BookInfo>) bookInfoPage.getItems();
        BookInfo bookInfo = new BookInfo();
        bookInfo.id = bookId;
        final int index = queryBookInfoFromBookInfos(bookInfos, bookInfo);
        if (index < 0) {
            throw new SdkException(Constants.NO_MACHING_ERROR_MSG);
        }
        bookInfo = bookInfos.get(index);
        return bookInfo;
    }

    @Override
    public LearnWordStructureInfo getStructureInfo(String bookId) throws Exception {
        if (StringUtils.isBlank(bookId)) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        final List<LearnWordStructureInfo> structureInfos = getCacheInfos(LearnWordStructureInfo.class, null,
            Constants.CACHE_STRUCTUREINFO_VERSION_KEY, Constants.learnWordStructureInfos).getItems();
        if (structureInfos == null) {
            return null;
        }
        for (LearnWordStructureInfo learnWordStructureInfo : structureInfos) {
            if (learnWordStructureInfo.bookId.equals(bookId)) {
                return learnWordStructureInfo;
            }
        }
        return null;
    }

    @Override
    public PageInfo getPageInfoByMpCode(MpCode mpCode) throws Exception {
        final Page<PageInfo> page = getCacheInfos(PageInfo.class, null, Constants.CACHE_PAGEINFO_VERSION_KEY,
            Constants.pageInfos);
        if (page == null) {
            return null;
        }
        final ArrayList<PageInfo> pageInfos = (ArrayList<PageInfo>) page.getItems();
        PageInfo pageInfo = new PageInfo();
        pageInfo.startCode = mpCode;
        if (pageInfos == null || pageInfos.size() == 0) {
            throw new SdkException(Constants.NO_MACHING_ERROR_MSG);
        }
        pageInfo = queryPageInfoFromPageInfos(pageInfos, pageInfo);
        if (pageInfo == null) {
            throw new SdkException(Constants.NO_MACHING_ERROR_MSG);
        }
        return pageInfo;
    }

    /**
     * 二分法查找所在索引.
     * 
     */
    private int queryIndexFromPageInfos(ArrayList<PageInfo> arrayList, PageInfo pageInfo) {
        if (arrayList == null || pageInfo == null) {
            return -1;
        }
        final int index = Collections.binarySearch(arrayList, pageInfo);
        return index;
    }

    /**
     * 二分法查找所在索引PageInfo对象.
     * 
     */
    private PageInfo queryPageInfoFromPageInfos(ArrayList<PageInfo> arrayList, PageInfo pageInfo) {
        if (arrayList == null || pageInfo == null) {
            return null;
        }
        int index = queryIndexFromPageInfos(arrayList, pageInfo);
        if (index >= 0) {
            return arrayList.get(index);
        } else if (index < -1) {
            index = Math.abs(index) - 2;
            // TODO checkInPage之后会用于计算笔尖位置
            int[] checkInPage = arrayList.get(index).checkInPage(pageInfo.startCode);
            if (checkInPage != null) {
                return arrayList.get(index);
            }
        }
        return null;
    }

    /**
     * 二分法查找所在索引.
     * 
     */
    private int queryBookInfoFromBookInfos(ArrayList<BookInfo> arrayList, BookInfo bookInfo) {
        if (arrayList == null || bookInfo == null) {
            return -1;
        }
        final Comparator<BookInfo> c = (BookInfo u1, BookInfo u2) -> u1.id.compareTo(u2.id);
        int index = Collections.binarySearch(arrayList, bookInfo, c);
        return index;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessResult addBook(Book bookParam) throws SdkException {
        if (StringUtils.isBlank(bookParam.getBookId()) || StringUtils.isBlank(bookParam.getName())) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        DdbResourceBook book = resourceBookMapper.getId(bookParam.getBookId());
        if (book == null) {
            book = new DdbResourceBook();
            book.setId(bookParam.getBookId());
            book.setName(bookParam.getName());
            book.setIsbn(bookParam.getIsbn());
            book.setCreateDatetime(Date.from(Instant.now()));
            book.setType(bookParam.getType() == null ? FullBookInfo.Type.OTHER : bookParam.getType());
            resourceBookMapper.create(book);
        }
        final SuccessResult result = new SuccessResult();
        result.setSuccess(true);
        result.setUuid(book.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessResult createCode(Book bookParam) throws SdkException, CacheException {
        // 1、校验参数合法性
        if (StringUtils.isBlank(bookParam.getBookId()) || bookParam.getPageNum() == 0
            || StringUtils.isBlank(bookParam.getVersion()) || bookParam.getPageParam() == null) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        final DdbResourceBook book = resourceBookMapper.getId(bookParam.getBookId());
        if (book == null || StringUtils.isBlank(bookParam.getVersion())) {
            throw new SdkException(Constants.NO_MACHING_BOOK);
        }
        DdbResourceBookPrint version = resourceBookPrintMapper.getByBookIdAndName(bookParam.getBookId(),
            bookParam.getVersion());
        final DdbResourceBookPrint baseVersion = resourceBookPrintMapper.getByBookIdAndName(bookParam.getBookId(),
            bookParam.getBaseVersion());
        if (version != null || (baseVersion == null && bookParam.getPageNum() != bookParam.getPageParam().length)) {
            throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
        }
        if (Constants.createCodeIsLock) {
            throw new SdkException(Constants.IS_BUSY);
        }
        synchronized (this) {
            if (Constants.createCodeIsLock) {
                throw new SdkException(Constants.IS_BUSY);
            }
            Constants.createCodeIsLock = true;
        }
        final String key = CommUtil.getCacheKey(Constants.CREATE_CODE + book.getId());
        final Progress cacheProgress = new Progress(book.getId(), bookParam.getPageNum(), 0, false, Constants.SUCCESS,
            bookParam.getVersion());
        memCacheService.set(key, cacheProgress, Constants.DEFAULT_CACHE_EXPIRATION);

        try {
            // 2、创建版本信息
            version = new DdbResourceBookPrint(CommUtil.genRecordKey(), book.getId(), bookParam.getVersion());
            resourceBookPrintMapper.save(version);
            // 3、初始化获取页信息
            List<DdbResourcePageCode> bookPages = pageCodeMapper.getBookPages(book.getId());
            if (bookPages == null || bookPages.size() == 0) {
                bookPages = new ArrayList<>(bookParam.getPageNum());
                final Book.PageParam[] pageParam = bookParam.getPageParam();
                for (int i = 0; i < pageParam.length; i++) {
                    final DdbResourcePageCode page = new DdbResourcePageCode(
                        book.getName() + "第" + pageParam[i].getNum() + "页", pageParam[i].getNum(), book.getId(),
                        pageParam[i].getWidthMm(), pageParam[i].getHeightMm());
                    pageCodeMapper.save(page);
                    bookPages.add(page);
                }
            } else if (bookPages != null && bookParam.getPageNum() != bookPages.size()) {
                throw new SdkException(Constants.INVALID_PARAMRTER_MESSAGE);
            }
            // 4、新建线程生成点读码
            final DdbResourceBookPrint versionTemp = version;
            final List<DdbResourcePageCode> bookPagesTemp = bookPages;
            Constants.CACHE_THREAD_POOL.execute(() -> {
                createTif(pageScopeMapper, pageCodeMapper, resourceBookPrintMapper, memCacheService, cacheProgress,
                    versionTemp, baseVersion, key, book, bookParam, bookPagesTemp);
            });
        } catch (Exception e) {
            // 出现异常时需要解锁
            cacheProgress.msg = Constants.FAIL;
            cacheProgress.finish = true;
            memCacheService.set(key, cacheProgress, Constants.DEFAULT_CACHE_EXPIRATION);
            Constants.createCodeIsLock = false;
            throw e;
        }
        return new SuccessResult(true);
    }

    private void createTif(PageScopeMapper pageScopeMapper, PageCodeMapper pageCodeMapper,
        ResourceBookPrintMapper resourceBookPrintMapper, MemCacheService cache, Progress progress,
        DdbResourceBookPrint version, DdbResourceBookPrint baseVersion, String key, DdbResourceBook book,
        Book bookParam, List<DdbResourcePageCode> bookPages) {
        try {
            final FileType fileType = bookParam.getFileType() == null ? FileType.TIF : bookParam.getFileType();
            final MpCodeBuilder builder = new MpCodeBuilder(book);

            // 获取铺码起始码值与起始代号
            final Map<String, Object> temp = pageScopeMapper.getSignAndCode();
            int sign = (int) temp.get("SIGN") + 1;
            long code = (long) temp.get("CODE_END") + 1;

            final List<String> fileList = new ArrayList<String>();

            progress.uuid = book.getId();
            progress.allItems = bookPages.size();
            final Book.PageParam[] pageParams = bookParam.getPageParam();
            for (DdbResourcePageCode page : bookPages) {
                DdbResourcePageScope pageScope = null;
                inner: for (Book.PageParam pageParam : pageParams) {
                    if (pageParam.getNum() == page.getPageNum()) {
                        if (pageParam.getDotParam() == null) {
                            pageParam.setDotParam(bookParam.getDefaultDotParam());
                        }
                        if (page.getWidth() == 0 || page.getHeight() == 0) {
                            page.setWidth(book.getWidth());
                            page.setHeight(book.getHeight());
                        }
                        if (pageParam.getIsNewCode() == Constants.INT_ONE) {
                            pageScope = pageScopeMapper.getByTypeAndPage(baseVersion.getId(), page.getId());
                            // 生成点读码，保存码段信息
                            pageScope = builder.createCode(pageScope.getCodeStart(), sign, pageParam, page);
                        } else {
                            pageScope = builder.createCode(code, sign, pageParam, page);
                            code = pageScope.getCodeEnd() + 1;
                        }
                        break inner;
                    } else if (pageParam.getNum() > page.getPageNum()) {
                        break inner;
                    }
                }
                if (pageScope == null) {
                    pageScope = pageScopeMapper.getByTypeAndPage(baseVersion.getId(), page.getId());
                    pageScope.setId(CommUtil.genRecordKey());
                    pageScope.setSign(sign);
                    pageScope.setRebuild(Constants.ZERO);
                }
                pageScope.setFkTypeId(version.getId());
                pageScopeMapper.save(pageScope);
                sign++;

                final String tifFileRealPath = FileUtils.getFileSaveRealPath(pageScope.getTifLink(), false);
                String fileRealPath = "";
                switch (fileType) {
                case TIF:
                    fileRealPath = tifFileRealPath;
                    break;
                case PNG:
                    fileRealPath = tifFileRealPath.replace(FileType.TIF.getSuffix(), FileType.PNG.getSuffix());
                    MpCodeBuilder.changeFileType(tifFileRealPath, FileType.TIF, fileRealPath, FileType.PNG);
                    break;
                }
                fileList.add(fileRealPath);
                // 更新进度
                progress.nowItems = page.getPageNum();
                cache.set(key, progress, Constants.DEFAULT_CACHE_EXPIRATION);
            }
            final String fileSavePath = FileUtils.getBookFolderSavePath(FileUtils.BOOK_ZIP, book.getId());
            final String filePath = FileUtils.getFileSaveRealPath(fileSavePath);
            final String fileName = version.getName() + fileType.getTypeName() + ".zip";
            FileUtils.zipFile(fileList, filePath + fileName);
            final String zipFilePath = fileSavePath + fileName;
            version.setZipLink(zipFilePath);
            resourceBookPrintMapper.updateZipLink(version);
            progress.msg = FileUtils.getFullRequestPath(zipFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            progress.msg = Constants.FAIL;
        } finally {
            // 解锁
            try {
                progress.finish = true;
                cache.set(key, progress, Constants.DEFAULT_CACHE_EXPIRATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (this) {
                Constants.createCodeIsLock = false;
            }
        }
    }

    @Override
    public Progress getProgress(Book book) throws CacheException {
        final String key = CommUtil.getCacheKey(Constants.CREATE_CODE + book.getBookId());
        final Progress progress = memCacheService.get(key);
        return progress;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessResult updateBookLink(String bookId, String path, long size) throws CacheException, SdkException {
        final DdbResourceBook book = getById(bookId);
        if (book == null) {
            throw new SdkException(Constants.NO_MACHING_ERROR_MSG);
        }
        book.setBookLink(path);
        book.setResSize(size);
        book.setVersion(String.valueOf(Clock.systemDefaultZone().millis()));
        resourceBookMapper.update(book);
        final String key = CommUtil.getCacheKey(Constants.CACHE_BOOKINFO_KEY_PRIFIX + bookId);
        memCacheService.delete(key);
        final SuccessResult result = new SuccessResult();
        result.setSuccess(true);
        return result;
    }

    @Override
    public void updateBookDetail() throws Exception {
        // LOGGER.error("cacaheBookInfo - 4");
        final List<DdbResourceBook> books = resourceBookMapper.getValidBooks();
        final List<FullBookInfo> fullBookInfos = new ArrayList<>();
        final List<BookInfo> bookInfos = new ArrayList<>();
        final List<LearnWordStructureInfo> learnWordStructureInfos = new ArrayList<>();
        FullBookInfo fullBookInfo = null;
        // int numBooks = 0;
        for (DdbResourceBook book : books) {
            // LOGGER.error("cacaheBookInfo - 5 - " + (numBooks++));
            fullBookInfo = new FullBookInfo();
            fullBookInfo.bookInfo = new BookInfo();
            if (StringUtils.isNotBlank(book.getBookLink()) && book.getBookLink().toLowerCase().endsWith(".mp")) {
                fullBookInfo.sequence = book.getSequence();
                final String localPath = FileUtils.getFileSaveRealPath(book.getBookLink(), false);
                final File file = new File(localPath);
                if (!file.exists()) {
                    // LOGGER.error("cacaheBookInfo - 5 - not existing: " +
                    // file.toString());
                    continue;
                }
                if (!StringUtils.isEmpty(book.getMppLink())
                    && new File(FileUtils.getFileSaveRealPath(book.getMppLink(), false)).exists()) {
                    fullBookInfo.hasMpp = true;
                }
                // LOGGER.error("cacaheBookInfo - 5 -
                // 解析包："+bookInfo.localPath);
                fullBookInfo.bookInfo.downloadSize = file.length();
                fullBookInfo.bookInfo.id = book.getId();
                fullBookInfo.bookInfo.fullName = book.getName();
                fullBookInfo.numPages = book.getBookPageNum();
                fullBookInfo.bookInfo.name = book.getName();
                fullBookInfo.photo = StringUtils.isBlank(book.getPhoto()) ? book.getSuitImage() : book.getPhoto();
                fullBookInfo.author = book.getAuthor();
                fullBookInfo.grade = book.getGrade();
                fullBookInfo.type = book.getType();
                fullBookInfo.introduction = book.getIntroduction();
                fullBookInfo.isPreDownload = book.getIsPreDownload();
                if (MpResourceDecoder.getFullBookInfo(fullBookInfo, localPath)) {
                    if (!fullBookInfo.bookInfo.isValid()) {
                        continue;
                    }
                    fullBookInfos.add(fullBookInfo);
                    bookInfos.add(fullBookInfo.bookInfo);
                    // 生成LearnWordStructureInfo
                    if (LearnWordStructureInfo.isLearnWord(fullBookInfo.bookInfo)) {
                        final String fileSavePath = FileUtils.getTempFileSavePath(fullBookInfo.bookInfo,
                            Code.Type.SH + "");
                        final LearnWordStructureInfo structureInfo = MpResourceDecoder
                            .getLearnWordStructure(fullBookInfo, localPath, fileSavePath);
                        if (structureInfo != null) {
                            learnWordStructureInfos.add(structureInfo);
                        }
                    }
                }
            }
        }
        // 对处理结果排序
        final Comparator<FullBookInfo> c1 = (FullBookInfo u1, FullBookInfo u2) -> u1.sequence - u2.sequence;
        final Comparator<BookInfo> c2 = (BookInfo u1, BookInfo u2) -> u1.id.compareTo(u2.id);
        Collections.sort(fullBookInfos, c1);
        Collections.sort(bookInfos, c2);
        Collections.sort(learnWordStructureInfos);
        // 判断生成的booklist是否有效
        if (!Utils.isValid(bookInfos) || !Utils.isContentValid(bookInfos)) {
            return;
        }
        final Page<FullBookInfo> fullBookPage = new Page<>(fullBookInfos, fullBookInfos.size());
        final Page<BookInfo> bookPage = new Page<>(bookInfos, bookInfos.size());
        final Page<LearnWordStructureInfo> learnWordPage = new Page<>(learnWordStructureInfos,
            learnWordStructureInfos.size());
        final ArrayList<PageInfo> pageInfos = getPageInfos();
        final Page<PageInfo> page = new Page<>(pageInfos, pageInfos.size());
        // TODO version生成需要修改
        final ResourceVersion version = new ResourceVersion(System.currentTimeMillis(), 0);
        fullBookPage.setVersion(version);
        bookPage.setVersion(version);
        learnWordPage.setVersion(version);
        page.setVersion(version);
        saveDetail(fullBookPage, bookPage, learnWordPage, page);
    }

    @Transactional(rollbackFor = Exception.class)
    private void saveDetail(Page<FullBookInfo> fullBookPage, Page<BookInfo> bookPage,
        Page<LearnWordStructureInfo> learnWordPage, Page<PageInfo> page) {
        final DdbBookDetail bookDetail = new DdbBookDetail(fullBookPage);
        final DdbBookDetail localBookDetail = bookDetailMapper.get();
        if (checkDetail(FullBookInfo.class, fullBookPage, localBookDetail)) {
            bookDetailMapper.save(bookDetail);
        }
        final DdbBookCoreDetail bookCoreDetail = new DdbBookCoreDetail(bookPage);
        final DdbBookCoreDetail localBookCoreDetail = bookCoreDetailMapper.get();
        if (checkDetail(BookInfo.class, bookPage, localBookCoreDetail)
            && checkBookInfoList(bookPage, localBookCoreDetail)) {
            bookCoreDetailMapper.save(bookCoreDetail);
        }
        final DdbLearnWordStructureDetail learnWordStructureDetail = new DdbLearnWordStructureDetail(learnWordPage);
        final DdbLearnWordStructureDetail localLearnWordStructureDetail = learnWordStructureDetailMapper.get();
        if (checkDetail(LearnWordStructureInfo.class, learnWordPage, localLearnWordStructureDetail)) {
            learnWordStructureDetailMapper.save(learnWordStructureDetail);
        }
        final DdbPageDetail pageDetail = new DdbPageDetail(page);
        final DdbPageDetail localPageDetail = pageDetailMapper.get();
        if (checkDetail(PageInfo.class, page, localPageDetail)) {
            pageDetailMapper.save(pageDetail);
        }
        cacheBookInfo();
    }

    private boolean checkBookInfoList(Page<BookInfo> bookPage, DdbBookCoreDetail localBookCoreDetail) {
        if (localBookCoreDetail == null) {
            return true;
        }
        final ArrayList<BookInfo> bookInfos = (ArrayList<BookInfo>) bookPage.getItems();
        final List<BookInfo> localBookInfos = localBookCoreDetail.formDetail().getItems();
        int number = 0;
        for (BookInfo bookInfo : localBookInfos) {
            final int index = queryBookInfoFromBookInfos(bookInfos, bookInfo);
            if (index < 0) {
                number++;
            }
        }
        // 当减少的书籍大于两本时，认为BookList有误，不允许更新
        if (number > 2) {
            return false;
        }
        return true;
    }

    private <T> boolean checkDetail(Class<T> c, Page<T> page, CacheInfos<T> localDetail) {
        if (localDetail == null) {
            return true;
        } else {
            final Page<T> localPage = localDetail.formDetail();
            if ((page.getItems().size() - localPage.getItems().size()) > -5
                && !Constants.GSON.toJson(localPage.getItems()).equals(Constants.GSON.toJson(page.getItems()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<FileModule> downloadBook(String id) throws Exception {
        final DdbResourceBook book = getById(id);
        if (book == null) {
            throw new SdkException(Constants.NO_MACHING_BOOK);
        }
        return fileService.fileDownload(book.getBookLink());
    }

    @Override
    public Exam[] getAllOralTestInfo(String bookId) throws Exception {
        final CodeInfo codeInfo = decodeService
            .getCodeInfo(QuickCodeInfo.getSimpleSHQuickCodeInfo(bookId, 0, MpResourceDecoder.ORAL_TEST_CODE));
        if (codeInfo == null) {
            return null;
        }
        final String json = FUtils
            .fileToString(FileUtils.getFileSaveRealPath(codeInfo.languageInfos[0].getVoice(), false));
        return Constants.GSON.fromJson(json, Exam[].class);
    }

    @Override
    public ExamResult getOralTestInfo(String bookId, int num, DdbPeCustom peCustom) throws Exception {
        final CodeInfo codeInfo = decodeService
            .getCodeInfo(QuickCodeInfo.getSimpleSHQuickCodeInfo(bookId, num, MpResourceDecoder.ORAL_TEST_CODE));
        if (codeInfo == null) {
            return null;
        }
        final String json = FUtils
            .fileToString(FileUtils.getFileSaveRealPath(codeInfo.languageInfos[0].getVoice(), false));
        final Exam exam = Constants.GSON.fromJson(json, Exam.class);
        final ExamResult result = new ExamResult();
        result.userName = StringUtils.isBlank(peCustom.getNickName()) ? peCustom.getTrueName() : peCustom.getNickName();
        result.examName = exam.name;
        result.topic = new ArrayList<>(exam.topic.size());
        for (Topic topic : exam.topic) {
            final TopicResult topicResult = new TopicResult();
            topicResult.title = topic.title;
            topicResult.subTopic = new ArrayList<>(topic.subTopic.size());
            dealTopic(topic.subTopic, bookId, peCustom.getLoginId(), topicResult, result);
            result.topic.add(topicResult);
        }
        return result;
    }

    private void dealTopic(ArrayList<Exam.SubTopic> subTopics, String bookId, String loginId, TopicResult topicResult,
        ExamResult exam) {
        for (Exam.SubTopic topic : subTopics) {
            final SubTopicResult subTopicResult = new SubTopicResult();
            subTopicResult.question = topic.question;
            subTopicResult.type = topic.type;
            topicResult.subTopic.add(subTopicResult);
            subTopicResult.refAnswer = topic.refAnswer;
            if (topic.type == 0) {
                CodeInfo codeInfo = null;
                try {
                    codeInfo = decodeService.getCodeInfo(
                        QuickCodeInfo.getSimpleSHQuickCodeInfo(bookId, 1, new ShCode(topic.num, ShCode.OID3_BASE)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (codeInfo == null) {
                    continue;
                }
                subTopicResult.refAnswer.add(codeInfo.languageInfos[0].getVoice());
            }
            final OralTestDetail detail = oralTestDetailMapper.get(loginId, bookId, topic.num);
            if (detail == null) {
                continue;
            }
            subTopicResult.userVoice = FileUtils.getFullRequestPath(detail.getRecordingUrl());
            subTopicResult.userRecognizeTxt = detail.getRecognizeTxt();
            subTopicResult.userPoints = detail.getScore();
            subTopicResult.fluency = detail.getFluency();
            subTopicResult.integrity = detail.getIntegrity();
            subTopicResult.pronunciation = detail.getPronunciation();
            exam.userTotalPoints += subTopicResult.userPoints;
            if (exam.userDate == 0 || detail.getUploadTime().toEpochMilli() > exam.userDate) {
                exam.userDate = detail.getUploadTime().toEpochMilli();
            }
        }
    }

    @Override
    public List<FileModule> downloadBookZip(String id) throws Exception {
        final DdbResourceBook book = getById(id);
        if (book == null || StringUtils.isBlank(book.getBookLink()) || StringUtils.isBlank(book.getMppLink())) {
            throw new SdkException(Constants.NO_MACHING_BOOK);
        }
        final String mpLink = FileUtils.getFileSaveRealPath(book.getBookLink());
        final String mppLink = FileUtils.getFileSaveRealPath(book.getMppLink());
        if (!new File(mpLink).exists() || !new File(mppLink).exists()) {
            return null;
        }
        final String realPath = FileUtils.getFileSaveRealPath(FileUtils.BOOK_ZIP_FOLDER);
        final String zipName = book.getName() + "_" + book.getVersion() + ".zip";
        if (!new File(realPath + zipName).exists()) {
            final List<String> list = new ArrayList<>();
            list.add(mpLink);
            list.add(mppLink);
            if (StringUtils.isNotBlank(book.getMpvLink())) {
                list.add(FileUtils.getFileSaveRealPath(book.getMpvLink()));
            }
            FileUtils.zipFile(list, realPath + zipName);
        }
        return fileService.fileDownload(FileUtils.BOOK_ZIP_FOLDER + zipName);
    }

}
