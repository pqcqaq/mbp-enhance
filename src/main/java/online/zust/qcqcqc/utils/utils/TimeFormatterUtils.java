package online.zust.qcqcqc.utils.utils;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author qcqcqc
 * @date 2023-10-6 11:14
 * 日期工具类
 */
public class TimeFormatterUtils {
    private static final Logger log = Logger.getLogger(TimeFormatterUtils.class.getName());

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter SDF_1 = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final DateTimeFormatter SDF_2 = DateTimeFormatter.ofPattern(YYYY_MM_DD);


    /**
     * 将日期格式化为默认格式
     *
     * @param date yyyy-MM-dd HH:mm:ss
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String dateToString(LocalDateTime date) {
        try {
            return SDF_1.format(date);
        } catch (Exception e) {
            log.info("日期转换失败");
        }
        return null;
    }

    /**
     * 将String转换为Date，String格式为yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd
     */
    public static LocalDateTime stringToDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString, SDF_1);
        } catch (Exception e) {
            return LocalDateTime.parse(dateString, SDF_2);
        }
    }

    /**
     * 将String转换为Date，String格式为yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd <br>
     * 如果endTime为当天的0点，加上24*60*60-1秒，使得endTime为当天的23:59:59
     * 如果String为空或者null，则对应的Date为null
     *
     * @param startTime yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd
     * @param endTime   yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd
     * @return List<Date>，第一个元素为startTime，第二个元素为endTime
     */
    public static List<LocalDateTime> stringToDateList(String startTime, String endTime) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (StringUtils.isNotEmpty(startTime)) {
            start = TimeFormatterUtils.stringToDate(startTime);
        }

        if (StringUtils.isNotEmpty(endTime)) {
            end = TimeFormatterUtils.stringToDate(endTime);
            // 如果时分秒都为0，加上24*60*60-1秒，使得endTime为当天的23:59:59
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(localDateTimeToDate(end));
            if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0) {
                calendar.add(Calendar.SECOND, 24 * 60 * 60 - 1);
                end = dateToLocalDateTime(calendar.getTime());
            }
        }

        return Arrays.asList(start, end);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
