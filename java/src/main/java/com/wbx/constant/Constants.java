package com.wbx.constant;

public class Constants {

    private Constants(){}

    public static final String PV_ROOT_DIR = "E:/bigdata/wiki/pageviews/";

    public static final String DOWNLOAD_DIR = "E:/bigdata/wiki/download/";

    public static final String YEAR = "2023";

    public static final String MONTH = "/2023-11/";

    public static final String PAGE_VIEW_URL = "https://dumps.wikimedia.org/other/pageviews/";

    public static final String PROXY_HOST = "127.0.0.1";

    public static final int PROXY_PORT = 1080;

    public static final String FULL_URL = PAGE_VIEW_URL + YEAR + MONTH;

    public static final String FULL_DOWNLOAD_PATH = DOWNLOAD_DIR + YEAR + MONTH;

    public static final String FULL_PV_PATH = PV_ROOT_DIR + YEAR + MONTH;

    public static final boolean USE_PROXY = true;

}
