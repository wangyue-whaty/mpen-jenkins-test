/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.service;

import java.util.List;

import com.mp.shared.common.BookInfo;
import com.mp.shared.common.Exam;
import com.mp.shared.common.FileModule;
import com.mp.shared.common.LearnWordStructureInfo;
import com.mp.shared.common.MpCode;
import com.mp.shared.common.Page;
import com.mp.shared.common.PageInfo;
import com.mp.shared.common.ResourceVersion;
import com.mp.shared.common.SuccessResult;
import com.mpen.api.bean.Book;
import com.mpen.api.bean.ExamResult;
import com.mpen.api.bean.PreBook;
import com.mpen.api.bean.Unit;
import com.mpen.api.common.Progress;
import com.mpen.api.domain.DdbPeCustom;
import com.mpen.api.domain.DdbResourceBook;
import com.mpen.api.exception.CacheException;
import com.mpen.api.exception.SdkException;

/**
 * ResourceBookService接口.
 *
 * @author kai
 *
 */
public interface ResourceBookService {

    /**
     * 获取书籍列表.
     * 
     * @param <T>
     * 
     * @throws Exception
     * 
     */
    <T> Page<T> getCacheInfos(Class<T> c, ResourceVersion version, String versionKey, Page<T> cachePage)
        throws Exception;

    void cacheBookInfo();

    /**
     * 获取书籍.
     * 
     */
    DdbResourceBook getById(String id) throws CacheException;

    /**
     * 获取预下载书籍列表.
     * 
     * @throws Exception
     * 
     */
    List<PreBook> getPrepownloadBooks() throws Exception;

    /**
     * 获取书籍目录结构.
     * 
     */
    List<Unit> getBookContent(String bookId, String str) throws SdkException, CacheException;

    Page<PageInfo> getBookPages(String bookId, ResourceVersion version) throws Exception;

    BookInfo getBookInfo(String bookId) throws Exception;

    PageInfo getPageInfoByMpCode(MpCode code) throws Exception;

    LearnWordStructureInfo getStructureInfo(String bookId) throws Exception;

    SuccessResult addBook(Book book) throws SdkException;

    SuccessResult createCode(Book book) throws SdkException, CacheException;

    Progress getProgress(Book book) throws CacheException;

    SuccessResult updateBookLink(String bookId, String path, long size) throws CacheException, SdkException;

    void updateBookDetail() throws Exception;

    List<FileModule> downloadBook(String id) throws Exception;
    
    List<FileModule> downloadBookZip(String id) throws Exception;

    Exam[] getAllOralTestInfo(String bookId) throws Exception;

    ExamResult getOralTestInfo(String bookId, int num, DdbPeCustom peCustom) throws Exception;
}
