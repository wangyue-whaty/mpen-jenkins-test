/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.common;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.mp.shared.common.BookInfo;
import com.mp.shared.common.FullBookInfo;
import com.mp.shared.common.LearnWordStructureInfo;
import com.mp.shared.common.NetworkResult;
import com.mp.shared.common.Page;
import com.mp.shared.common.PageInfo;
import com.mp.shared.service.MpResourceDecoder.UrlGenerator;
import com.mpen.api.domain.DdbResourceCode;
import com.mpen.api.util.FileUtils;

public class Constants {
    public static volatile Page<FullBookInfo> fullBookInfos;
    public static volatile Page<BookInfo> bookInfos;
    public static volatile Page<LearnWordStructureInfo> learnWordStructureInfos;
    public static volatile Page<PageInfo> pageInfos;
    public static Boolean bookInfoIsLock = false;
    public static Boolean createCodeIsLock = false;
    public static Map<String, ArrayList<DdbResourceCode>> codeMap;
    public static final Gson GSON = new Gson();
    public static final ExecutorService CACHE_THREAD_POOL = Executors.newCachedThreadPool();
    // TODO 最终通过验证用户group来决定访问权限
    public static final String ACCESS_CONTROL_KEY = "mpen2017..";
    public static final String CDN_SECRET = "mpen201705";
    public static final String BOOK_KEY = "J+K/+>*N$/%\"$66F";
    public static final String USERCENTER_LOGIN_ADDS = "http://ddb.webtrn.cn/uc/user/login.do";
    public static final String SCHOOL_NO = "ddb";
    public static final String CONTENT_TYPE = "text/html;charset=UTF-8";
    public static final String GBK_ENCODING = "GBK";
    public static final String UTF8_ENCODING = "UTF-8";
    public static final String TRUE = "true";
    public static final String SO_PATH = "config/libmpdecoder.so";
    public static final String SSL_SO_PATH = "config/libcrypto.so.1.1";
    public static final Integer DEFAULT_PAGENO = 1;
    public static final String[] CODE_FILES = { "ff80808152f8de080152f8e542cf0002.txt",
        "ff80808156ca3d900156caac68a50007.txt", "ff80808156ca3d900156cac8350c0009.txt",
        "ff80808156ca3d900156cacd16e8000d.txt", "ff80808156ca3d900156cace3056000f.txt",
        "ff80808156ca3d900156cacee3fc0011.txt", "ff80808156ca3d900156cacf93420013.txt",
        "ff80808156ca3d900156cb19feff004c.txt", "ff808081533ba5a801533bbd358f0003.txt",
        "ff808081533ba5a801533bbddf220006.txt", "ff808081533ba5a801533bbe58360008.txt",
        "ff808081581deb4101581e74ac7d0088.txt", "ff8080815847b3010158489abb5600ae.txt",
        "ff808081567761c2015691ef1d2e06a6.txt", "ff808081567761c2015691fd171d06a9.txt",
        "ff808081567761c2015691ff8b7e06ab.txt", "ff808081567761c20156920b17fb06bd.txt",
        "ff808081567761c20156920d03f706c0.txt", "ff808081567761c20156920f6c5006c4.txt",
        "ff808081567761c201569201cfde06ad.txt", "ff808081567761c201569206d9d206b7.txt",
        "ff808081567761c201569212caef06ca.txt", "ff808081567761c201569213fdca06cc.txt",
        "ff808081567761c2015692106cfa06c6.txt", "ff808081567761c2015692119de006c8.txt",
        "ff808081567761c2015692154c5506ce.txt", "ff808081567761c201569203814e06af.txt",
        "ff808081567761c201569208005d06b9.txt", "ff808081567761c201569205449706b5.txt",
        "ff808081567761c201569209573106bb.txt" };
    public static final String LIMIT_APP_VERSION = "3.55";

    public static final DateTimeFormatter DATA_FORMART = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String RECORDSKEY = "records";
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final Integer MAX_PAGESIZE = 10000;

    public static final int READING_LIMIT = 1000;

    public static final Integer INT_ZERO = 0;
    public static final Integer INT_ONE = 1;
    public static final Integer INT_SEVEN = 7;
    public static final Integer INT_TEN = 10;
    public static final Integer INT_TWENTY = 20;
    public static final Integer INT_FIFTY = 50;
    public static final Integer INT_HUNDRED = 100;
    public static final Float FLOAT_ZERO = 0.0f;
    public static final Float FLOAT_ONE = 1.0f;
    public static final Float FLOAT_SIXTY = 60.0f;
    public static final String LAST_READING_PREFIX = " 点读";
    public static final String LAST_READING_SUFFIX = "分钟";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String P_STRING = "P";

    // 解码相关
    public static final String MPCODE = "MP";
    public static final String SHCODE = "SH";
    public static final String XORPATH = "/test/dp.xor";

    public static final String STUDY_STRING = "课本学习";
    public static final String READ_STRING = "课外阅读";
    public static final String TEST_STRING = "课后练习";
    public static final String SPOKEN_STRING = "口语评测";
    public static final String MODULE = "Module";
    public static final String UNIT = "Unit";
    public static final String ACTIVITY = "Activity";
    public static final String CACHE_STUDY_PREFIX = "bookStudy_";
    public static final String CACHE_SPOKEN_PREFIX = "bookSpoken_";
    public static final String CACHE_USER_STUDY_PREFIX = "userStudy_";
    public static final String CACHE_USER_WEEKLY_PREFIX = "weekly";
    public static final String CACHE_FULLBOOKINFO_VERSION_KEY = "ddb_fullBookInfo_version";
    public static final String CACHE_BOOKINFO_VERSION_KEY = "ddb_bookInfo_version";
    public static final String CACHE_PAGEINFO_VERSION_KEY = "ddb_pageInfo_version";
    public static final String CACHE_STRUCTUREINFO_VERSION_KEY = "ddb_structureInfo_version";
    public static final String CACHE_THREAD_BOOKINFO_KEY = "threadBookInfos";
    public static final String CACHE_WRONG_TIME_PEN = "wrong_time_pen";
    // 返回值相关参数.
    public static final String ID = "id";
    public static final String RESOURCE_SIZE = "resSize";
    public static final String NAME = "name";
    public static final String DOWNLOAD_URL = "downloadUrl";
    public static final String CODE = "code";
    public static final String PHOTO = "photo";
    public static final String PATH = "path";
    public static final String VIDEOS = "videos";
    public static final String DEFAULT_BATTERY = "20";
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String PUSH_ERROR = "推送失败";
    public static final String VEDIO_PUSH = "打开App观看视频！";
    public static final String PRE_BATTERY_PUSH = "电量低于";
    public static final String SUF_BATTERY_PUSH = "%，请充电!";
    public static final String NO_MACHING_PEN = "笔信息不存在！";
    public static final String NO_MACHING_BOOK = "书籍信息不存在！";
    public static final String IS_BUSY = "系统繁忙，请稍后再试！";
    public static final String INDEX = "index";
    public static final String VERSION_FROM = "versionFrom";
    public static final String VERSION_TO = "versionTo";
    public static final String DESCRIPTION = "description";
    public static final String SIZE = "size";
    public static final String MD5 = "md5";
    public static final String PASSWORD = "password";
    public static final String SITECODE = "siteCode";
    public static final String JSON = "json";
    public static final String IP_PARAM = "ip";
    public static final String RESULT = "result";
    public static final String MPEN = "MPEN";
    public static final String DATE = "date";
    public static final String TOTAL_TIME = "totalTime";
    public static final String DAYS = "days";
    public static final String DATE_MAP = "dateMap";
    public static final String BOOK_LIST = "bookList";
    public static final String TIP = "tip";
    public static final String TICKET = "ticket";
    public static final String NEST_STUDY_DETAIL = "nextStydyDetail";
    public static final String WEEKLY = "weekly";
    public static final String BATTERY = "battery";

    // 方法请求相关参数.
    public static final String PASSQA = "passQa";
    public static final String UPLOAD_SERIAL_NUMBER = "uploadSerialNumber";
    public static final String PREDOWNLOAD = "preDownload";
    public static final String AUDIOS_TEST = "audiosTest";
    public static final String PUSH_APP = "pushApp";
    public static final String CHECK_BIND = "checkBind";
    public static final String UPGRADE_APP = "upgradeApp";
    public static final String UPGRADE_ROM = "upgradeRom";
    public static final String UPLOAD_CMD_FILE = "uploadCmdFile";
    public static final String UPLOAD_ORALTEST_RECORDING = "uploadOralTestRecording";
    public static final String UPLOAD_PERSISTENCE_FILE = "uploadPersistenceFile";
    public static final String DOWNLOAD_BOOK = "downloadBook";
    public static final String DOWNLOAD_BOOK_ZIP = "downloadBookZip";
    public static final String SAVE_BINDRELATIONSHIP = "saveBindRelationship";
    public static final String UN_BINDRELATIONSHIP = "unBindRelationship";
    public static final String ADB_ADMIT = "adbAdmit";
    public static final String GET_CMD = "getCmd";
    public static final String READ = "read";
    public static final String UN_BIND_PEN = "unBindPen";
    public static final String COMPLETE_MAC = "completeMac";
    public static final String GET_TOP_GOODS = "getTopGoods";
    public static final String GET_GOODS = "getGoods";
    public static final String COMPLETE_USER_STUDY_INFO = "completeUserStudyInfo";
    public static final String USER_DATE_STUDY_TIME = "userDateStudyTime";
    public static final String BOOK_STUDY_INFO = "bookStudyInfo";
    public static final String BOOK_CONTENT_STUDY_DETAIL = "bookContentStudyDetail";
    public static final String BOOK_CONTENT_SPOKEN_DETAIL = "bookContentSpokenDetail";
    public static final String GET_WEEKLY_LIST = "getWeeklyList";
    public static final String GET_WEEKLY = "getWeekly";
    public static final String CHANGE_USER_INFO = "changeUserInfo";
    public static final String SAVE_ADDRESS = "saveAddress";
    public static final String GET_BOOKS_PHOTO = "getBooksPhoto";
    public static final String GET_USER_LABELS = "getUserLabels";
    public static final String GET_GOODS_BY_BOOK_ID = "getGoodsByBookId";
    public static final String GET_GOODS_BY_GOOD_ID = "getGoodsByGoodId";
    public static final String GET_VALID_BOOKS = "getValidBooks";
    public static final String GET_BOOK_PAGES = "getBookPages";
    public static final String GET_LEARNWORD_STRUCTUREINFO = "getLearnWordStructureInfo";
    public static final String GET_ALL_ORALTEST_INFO = "getAllOralTestInfo";
    public static final String GET_ORALTEST_INFO = "getOralTestInfo";
    public static final String CREATE_BOOK = "createBook";
    public static final String CREATE_CODE = "createCode";
    public static final String CREATE_GOODS = "createGoods";
    public static final String REMOVE_GOODS = "removeGoods";
    public static final String GET_PROGRESS = "getProgress";
    public static final String UPLOAD_BOOK = "uploadBook";

    // audiosTest相关参数.
    public static final String AUDIOS1_NAME = "燕归巢 - 许嵩";
    public static final String AUDIOS1_PATH = "http://cc.stream.qqmusic.qq.com/C100000Nz08A0aZNuz.m4a?fromtag=52";
    public static final String AUDIOS2_NAME = "Sorry - Justin Bieber";
    public static final String AUDIOS2_PATH = "http://cc.stream.qqmusic.qq.com/C100002homRe0dmkTn.m4a?fromtag=52";
    public static final String AUDIOS3_NAME = "What Do You Mean? (2015维多利亚的秘密秀秀场音乐) - Justin Bieber";
    public static final String AUDIOS3_PATH = "http://cc.stream.qqmusic.qq.com/C100004HAF5J0HxEZk.m4a?fromtag=52";
    public static final String AUDIOS4_NAME = "单词1";
    public static final String AUDIOS4_PATH = FileUtils.cdnDomain + "/incoming/course/voice/s_001.mp3";
    public static final String AUDIOS5_NAME = "单词2";
    public static final String AUDIOS5_PATH = FileUtils.cdnDomain + "/incoming/course/voice/s_002.mp3";
    public static final String AUDIOS6_NAME = "单词3";
    public static final String AUDIOS6_PATH = FileUtils.cdnDomain + "/incoming/course/voice/s_003.mp3";
    public static final String AUDIOS7_NAME = "单词4";
    public static final String AUDIOS7_PATH = FileUtils.cdnDomain + "/incoming/course/voice/s_004.mp3";
    public static final String AUDIOS8_NAME = "单词5";
    public static final String AUDIOS8_PATH = FileUtils.cdnDomain + "/incoming/course/voice/s_005.mp3";
    public static final String AUDIOS9_NAME = "单词6";
    public static final String AUDIOS9_PATH = FileUtils.cdnDomain + "/incoming/course/voice/z1-001.mp3";
    public static final String UPDATE_PROMPT_VOICE = "/incoming/updateprompt.mp3";
    // Medal
    public static final String MEDAL_ID = "1234567890";
    public static final String MEDAL_NAME = "好学";
    public static final String MEDAL_PHOTO = "http://q.qlogo.cn/qqapp/1105541442/0296DAC896E89A0FF5A7422B664F3CB4/40";

    // GetTopBook
    public static final String POSTER1_PATH = "/incoming/weidian.png";
    public static final String WINDIAN_PATH = "https://www.mpen.com.cn/wdtj.html";

    // 错误码相关参数.
    // TODO ZYT 在别的地方直接引用 Result.xxx 删除对应 Constants.xxxx
    public static final String MSG_SUCCESS_CODE = NetworkResult.MSG_SUCCESS_CODE;// "200";
    public static final String BAD_REQUEST_ERROR_CODE = NetworkResult.BAD_REQUEST_ERROR_CODE;// "400";
    public static final String BAD_REQUEST_ERROR_MSG = NetworkResult.BAD_REQUEST_ERROR_MSG;// "Bad
                                                                                           // request!";
    public static final String ACCESS_DENIED_ERROR_CODE = NetworkResult.ACCESS_DENIED_ERROR_CODE;// "401";
    public static final String ACCESS_DENIED_ERROR_MSG = NetworkResult.ACCESS_DENIED_ERROR_MSG;// "Access
                                                                                               // denied!";
    public static final String ACCESS_FORBIDDEN_ERROR_CODE = NetworkResult.ACCESS_FORBIDDEN_ERROR_CODE;// "403";
    public static final String ACCESS_FORBIDDEN_ERROR_MSG = NetworkResult.ACCESS_FORBIDDEN_ERROR_MSG;// "Access
                                                                                                     // forbidden!";
    public static final String NO_MACHING_ERROR_CODE = NetworkResult.NO_MACHING_ERROR_CODE;// "404";
    public static final String NO_MACHING_ERROR_MSG = NetworkResult.NO_MACHING_ERROR_MSG;// "No
                                                                                         // maching
                                                                                         // resource!";

    public static final String CACHE_USERSESSION_KEY_PREFIX = "UserSession_";
    public static final String CACHE_PENINFO_KEY_PREFIX = "PenInfo_";
    public static final String CACHE_BOOKINFO_KEY_PRIFIX = "BookInfo_";
    public static final String CACHE_CODEINFO_KEY_PRIFIX = "CodeInfo_";
    public static final String CACHE_POINT_NUM_PRIFIX = "PointNum_";
    public static final String CACHE_SEND_SMS_KEY = "sendSms";

    public static final String UCENTERKEY = "ucenterKey";
    public static final String LOGINIDKEY = "loginId";
    public static final String SESSIONKEY = "sessionId";
    public static final String PENKEY = "penId";

    public static final String LOGINTOKENKEY = "token";

    public static final String PENREQUESTFLAGKEY = "pen";

    public static final String ONE = "1";

    public static final String ZERO = "0";

    public static final String TWO = "2";

    public static final String DECODE_FAILED_NO = "4294967295";

    public static final String PEN_FLAG_ACTIVE = "FlagActive";

    public static final String GOODS_FLAG_PUBLISH = "FlagPublish";

    public static final String INVALID_LOGINID_ERROR = "{\n" + "    \"httpStatus\": \"403\",\n"
        + "    \"message\": \"Invalid login id in cookies\"\n" + "}";
    public static final String INVALID_PARAMETER_ERROR = "{\n" + "    \"httpStatus\": \"402\",\n"
        + "    \"message\": \"Invalid parameter\"\n" + "}";
    public static final String INVALID_PARAMRTER_MESSAGE = "Invalid parameter";
    // seconds 0 not expir
    public static final Integer DEFAULT_CACHE_EXPIRATION = 0;
    public static final Integer FOUR_HOUR_CACHE_EXPIRATION = 4 * 60 * 60;
    public static final String NO_MATCHING_METHOD = "No matching method";
    public static final String WRONG_IDENTIFIACTION = "Wrong identifiaction";
    public static final String WRONG_REFERER = "Wrong referer";
    public static final String WRONG_USERAGENT = "Wrong user-agent";
    public static final String TIME_ERROR = "Time error";

    public static final String NO_MATCHING_USER = "用户名不存在！";

    public static final String WRONG_PASSWORD = "密码错误!";

    public static final String UCENTER_ERROR = "服务器繁忙!";
    public static final String UNKNOW = "未知";
    public static final String PEN_IS_BIND = "笔已经被绑定了!";
    public static final String NO_RESOURCE = "没有资源!";
    public static final String GET_INFO_FAILURE = "获取信息失败！";
    public static final String FILE_OPEN_FAILED = "文件打开失败！";
    public static final String WEEKLY_MSG = "点击查看本周学情周报";
    public static final String CACHE_ERROR = "缓存异常";

    public static final UrlGenerator URL_GENERATOR = new UrlGenerator() {
        @Override
        public String getUrl(String savePath, String localFileName) {
            return FileUtils.getFullRequestPath((savePath + "/" + localFileName).replace(FileUtils.root, ""));
        }

        @Override
        public String getFileSavePath(String url) {
            try {
                if (StringUtils.isNotBlank(url)) {
                    return FileUtils.getFileSaveRealPath(url, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    };

}
