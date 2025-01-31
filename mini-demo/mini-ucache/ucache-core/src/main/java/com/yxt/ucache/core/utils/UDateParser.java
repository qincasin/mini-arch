package com.yxt.ucache.core.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDateParser {

    private UDateParser() {

    }

    protected static Logger logger = LoggerFactory.getLogger(UDateParser.class);
    /**
     * 注意：这些常量不要删除，是通过反射加载到 FMT_ARRAY 中，并使用的
     */
    public static final String FMT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String FMT_Y_M_D_H_M_S_S = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FMT_UTC_Y_M_D_H_M_S = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FMT_UTC_Y_M_D_H_M_S_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String FMT_UTC_Y_M_D_H_M_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FMT_DEFAULT_SLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String FMT_Y_M_D_H_M_S_S_SLASH = "yyyy/MM/dd HH:mm:ss.SSS";
    public static final String FMT_CHINESE_Y_M_D_H_M_S = "yyyy年MM月dd日 HH:mm:ss";
    public static final String FMT_CHINESE_Y_M_D_H_M_S_2 = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String FMT_CHINESE_Y_M_D_H_M_S_S = "yyyy年MM月dd日 HH:mm:ss.SSS";
    public static final String FMT_YMD_HMS = "yyyyMMddHHmmss";
    public static final String FMT_YMD_HMS_S = "yyyyMMddHHmmssSSS";
    public static final String FMT_Y_M_D = "yyyy-MM-dd";
    public static final String FMT_Y_M_D_SLASH = "yyyy/MM/dd";
    public static final String FMT_YM = "yyyyMM";
    public static final String FMT_Y_M = "yyyy-MM";
    public static final String FMT_YMD = "yyyyMMdd";
    public static final String FMT_HMS = "HHmmss";
    public static final String FMT_H_M_S = "HH:mm:ss";
    public static final String FMT_HMS_S = "HHmmssSSS";
    public static final String FMT_H_M_S_S = "HH:mm:ss.SSS";
    public static final String FMT_CHINESE_Y_M_D = "yyyy年MM月dd日";
    public static final String FMT_CHINESE_H_M_S_2 = "HH时mm分ss秒";

    public static final LinkedList<String> FMT_ARRAY;

    static {
        LinkedList<String> fmtList = new LinkedList<>();
        for (Field declaredField : UDateParser.class.getDeclaredFields()) {
            if (declaredField.getName().startsWith("FMT") && declaredField.getType() == String.class) {
                declaredField.setAccessible(true);
                try {
                    fmtList.add(declaredField.get(null).toString());
                } catch (IllegalAccessException e) {
                    logger.error("UDateParser FMT_ARRAY ", e);
                }
            }
        }
        FMT_ARRAY = fmtList;
    }

    public static Date parseDate(final String str) throws ParseException {
        return parseDateWithLeniency(str, null, FMT_ARRAY.toArray(new String[0]));
    }

    public static Date parseDateWithLeniency(final String str, final Locale locale) throws ParseException {
        return parseDateWithLeniency(str, locale, FMT_ARRAY.toArray(new String[0]));
    }

    /**
     * 根据format反解析date
     *
     * 基于其改编 {@link org.apache.commons.lang3.time.DateUtils#parseDateWithLeniency(String, Locale, String[], boolean)}
     * 增加了LRU提高效率
     *
     * @param str
     * @param locale
     * @param parsePatterns
     * @return
     * @throws ParseException
     */
    public static Date parseDateWithLeniency(final String str, final Locale locale, final String[] parsePatterns)
            throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        final TimeZone tz = TimeZone.getDefault();
        final Locale lcl = locale == null ? Locale.getDefault() : locale;
        final ParsePosition pos = new ParsePosition(0);
        final Calendar calendar = Calendar.getInstance(tz, lcl);
        calendar.setLenient(true);

        // 热区数据占前20%，最常用的format
        double hotArea = 0.2 * (double) parsePatterns.length;

        int i = 0;
        for (; i < parsePatterns.length; i++) {
            String parsePattern = parsePatterns[i];
            FastDateFormat instance = FastDateFormat.getInstance(parsePattern, tz, lcl);
            calendar.clear();
            try {
                if (instance.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
                    if (i <= hotArea) {
                        // 热区数据需要反应快
                        balanceSensitivity(parsePatterns[i]);
                    } else {
                        // 非热区数据需要一定次数积累才能成为热区数据
                        balanceStability(parsePatterns[i]);
                    }
                    return calendar.getTime();
                }
            } catch (final IllegalArgumentException ignore) {
                // leniency is preventing calendar from being set
            }
            pos.setIndex(0);
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    /**
     * 敏感性 LRU 思路，调整链表顺序，降低下次匹配的次数，提高性能
     * 命中后，直接提到第一位
     *
     * @param ele
     */
    private static void balanceSensitivity(String ele) {
        if (FMT_ARRAY.getFirst().equals(ele)) {
            return;
        }
        // 防止出现并发读，读不到的情况，允许重复也不允许缺失
        synchronized (UDateParser.class) {
            if (FMT_ARRAY.getFirst().equals(ele)) {
                // DCL
                return;
            }
            FMT_ARRAY.addFirst(ele);
            FMT_ARRAY.removeLastOccurrence(ele);
        }
    }

    /**
     * 稳定性 LRU 思路，调整链表顺序，降低下次匹配的次数，提高性能
     * 命中后，只往前移动一位
     *
     * @param ele
     */
    private static void balanceStability(String ele) {
        if (FMT_ARRAY.getFirst().equals(ele)) {
            return;
        }
        // 防止出现并发读，读不到的情况，允许重复也不允许缺失
        synchronized (UDateParser.class) {
            if (FMT_ARRAY.getFirst().equals(ele)) {
                return;
            }
            int pos = FMT_ARRAY.indexOf(ele);
            FMT_ARRAY.add(pos - 1, ele);
            FMT_ARRAY.removeLastOccurrence(ele);
        }
    }
}
