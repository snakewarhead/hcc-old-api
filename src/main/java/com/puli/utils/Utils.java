package com.puli.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lin on 2018/6/27.
 */
public class Utils {

    public static String getWebInfPath() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        path = path.replace("file:/", ""); //去掉file:
        path = path.replace("classes/", ""); //去掉class\
        return path;
    }

    public static String getDatetimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        return sdf.format(new Date());
    }
}
