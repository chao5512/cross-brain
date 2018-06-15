package com.dataset.management.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ClassName DateUtils
 * @Description 日期常用方法
 * @Auther: 王培文
 * @Date: 2018/5/11 
 * @Version 1.0
 **/
public class DateUtils {
    /**
     * 常用变量
     */
    public static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_HMS = "HH:mm:ss";
    public static final String DATE_FORMAT_HM = "HH:mm";
    public static final String DATE_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_TIME_YMDHMS = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_TIME_YMD = "yyyyMMdd";
    public static final long ONE_DAY_MILLS = 3600000 * 24;
    public static final int WEEK_DAYS = 7;
    private static final int dateLength = DATE_FORMAT_YMDHM.length();

    /**
     * 功能描述: 日期转化为特定字符串
     *
     * @param: [time, format]
     * @return: java.lang.String
     * @auther: 王培文
     * @date: 2018/5/11 10:57
     */
    public static String formatDateToString(Date time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    /**
     * 功能描述: 字符串转换为制定格式日期
     * (注意：当你输入的日期是2014-12-21 12:12，format对应的应为yyyy-MM-dd HH:mm否则异常抛出)
     * @param: [date, format]
     * @return: java.util.Date
     * @auther: 王培文
     * @date: 2018/5/11 11:01
     */
    public static Date formatStringToDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.toString());
        }
    }
    /*
     * 功能描述: 判断一个日期是否属于两个时段内
     *
     * @param: [time, timeRange]
     * @return: boolean
     * @auther: 王培文
     * @date: 2018/5/11 11:02
     */
    public static boolean isTimeInRange(Date time, Date[] timeRange) {
        return (!time.before(timeRange[0]) && !time.after(timeRange[1]));
    }

    /**
     * 功能描述: 从完整的时间截取精确到分的时间
     *
     * @param: [fullDateStr]
     * @return: java.lang.String
     * @auther: 王培文
     * @date: 2018/5/11 11:07
     */
    public static String getDateToMinute(String fullDateStr) {
        return fullDateStr == null ? null
                : (fullDateStr.length() >= dateLength ? fullDateStr.substring(
                0, dateLength) : fullDateStr);
    }

    /**
     * 功能描述:
     * 返回指定年度的所有周。List中包含的是String[2]对象 string[0]本周的开始日期,string[1]是本周的结束日期。
     * 日期的格式为YYYY-MM-DD 每年的第一个周，必须包含星期一且是完整的七天。
     * 例如：2009年的第一个周开始日期为2009-01-05，结束日期为2009-01-11。 星期一在哪一年，那么包含这个星期的周就是哪一年的周。
     * 例如：2008-12-29是星期一，2009-01-04是星期日，哪么这个周就是2008年度的最后一个周。
     *
     * @param: [year]
     * @return: java.util.List<java.lang.String[]>
     * @auther: 王培文
     * @date: 2018/5/11 11:21
     */
    public static List<String[]> getWeeksByYear(final int year) {
        int weeks = getWeekNumOfYear(year);
        List<String[]> result = new ArrayList<String[]>(weeks);
        int start = 1;
        int end = 7;
        for (int i = 1; i <= weeks; i++) {
            String[] tempWeek = new String[2];
            tempWeek[0] = getDateForDayOfWeek(year, i, start);
            tempWeek[1] = getDateForDayOfWeek(year, i, end);
            result.add(tempWeek);
        }
        return result;
    }
    
    /**
     * 功能描述: 计算指定年、周的上一年、周
     *
     * @param: [year, week]
     * @return: int[]
     * @auther: 王培文
     * @date: 2018/5/11 11:23
     */
    public static int[] getLastYearWeek(int year, int week) {
        if (week <= 0) {
            throw new IllegalArgumentException("周序号不能小于1！！");
        }
        int[] result = { week, year };
        if (week == 1) {
            // 上一年
            result[1] -= 1;
            // 最后一周
            result[0] = getWeekNumOfYear(result[1]);
        } else {
            result[0] -= 1;
        }
        return result;
    }

    /**
     * 功能描述: 下一个[周，年]
     *
     * @param: [year, week]
     * @return: int[]
     * @auther: 王培文
     * @date: 2018/5/11 11:25
     */
    public static int[] getNextYearWeek(int year, int week) {
        if (week <= 0) {
            throw new IllegalArgumentException("周序号不能小于1！！");
        }
        int[] result = { week, year };
        int weeks = getWeekNumOfYear(year);
        if (week == weeks) {
            // 下一年
            result[1] += 1;
            // 第一周
            result[0] = 1;
        } else {
            result[0] += 1;
        }
        return result;
    }
    /**
     * 功能描述: 计算指定年度共有多少个周。(从周一开始)
     *
     * @param: [year]
     * @return: int
     * @auther: 王培文
     * @date: 2018/5/11 11:09
     */
    public static int getWeekNumOfYear(final int year) {
        return getWeekNumOfYear(year, Calendar.MONDAY);
    }
    
    /**
     * 功能描述: 计算指定年度共有多少个周。
     *
     * @param: [year, firstDayOfWeek]
     * @return: int
     * @auther: 王培文
     * @date: 2018/5/11 11:11
     */
    public static int getWeekNumOfYear(final int year, int firstDayOfWeek) {
        // 每年至少有52个周 ，最多有53个周。
        int minWeeks = 52;
        int maxWeeks = 53;
        int result = minWeeks;
        int sIndex = 4;
        String date = getDateForDayOfWeek(year, maxWeeks, firstDayOfWeek);
        // 判断年度是否相符，如果相符说明有53个周。
        if (date.substring(0, sIndex).equals(year)) {
            result = maxWeeks;
        }
        return result;
    }

    public static int getWeeksOfWeekYear(final int year) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(WEEK_DAYS);
        cal.set(Calendar.YEAR, year);
        return cal.getWeeksInWeekYear();
    }

    /**
     * 功能描述: 获取指定年份的第几周的第几天对应的日期yyyy-MM-dd(从周一开始)
     *
     * @param: [year, weekOfYear, dayOfWeek]
     * @return: java.lang.String
     * @auther: 王培文
     * @date: 2018/5/11 11:14
     */
    public static String getDateForDayOfWeek(int year, int weekOfYear,
                                             int dayOfWeek) {
        return getDateForDayOfWeek(year, weekOfYear, dayOfWeek, Calendar.MONDAY);
    }

    /**
     * 功能描述: 获取指定年份的第几周的第几天对应的日期yyyy-MM-dd，指定周几算一周的第一天（firstDayOfWeek）
     *
     * @param: [year, weekOfYear, dayOfWeek, firstDayOfWeek]
     * @return: java.lang.String
     * @auther: 王培文
     * @date: 2018/5/11 11:17
     */
    public static String getDateForDayOfWeek(int year, int weekOfYear,
                                             int dayOfWeek, int firstDayOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(firstDayOfWeek);
        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        cal.setMinimalDaysInFirstWeek(WEEK_DAYS);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        return formatDateToString(cal.getTime(), DATE_FORMAT_YMD);
    }
    
    /**
     * 功能描述: 获取指定日期星期几
     *
     * @param: [datetime]
     * @return: int
     * @auther: 王培文
     * @date: 2018/5/11 11:29
     */
    public static int getWeekOfDate(String datetime) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(WEEK_DAYS);
        Date date = formatStringToDate(datetime, DATE_FORMAT_YMD);
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 功能描述: 计算某年某周内的所有日期(从周一开始 为每周的第一天)
     *
     * @param: [yearNum, weekNum]
     * @return: java.util.List<java.lang.String>
     * @auther: 王培文
     * @date: 2018/5/11 11:30
     */
    public static List<String> getWeekDays(int yearNum, int weekNum) {
        return getWeekDays(yearNum, weekNum, Calendar.MONDAY);
    }

    /**
     * 功能描述: 计算某年某周内的所有日期(七天)
     *
     * @param: [year, weekOfYear, firstDayOfWeek]
     * @return: java.util.List<java.lang.String>
     * @auther: 王培文
     * @date: 2018/5/11 11:30
     */
    public static List<String> getWeekDays(int year, int weekOfYear,
                                           int firstDayOfWeek) {
        List<String> dates = new ArrayList<String>();
        int dayOfWeek = firstDayOfWeek;
        for (int i = 0; i < WEEK_DAYS; i++) {
            dates.add(getDateForDayOfWeek(year, weekOfYear, dayOfWeek++,
                    firstDayOfWeek));
        }
        return dates;
    }


    /**
     * 功能描述:
     * @param queryDate  传入的时间
     * @param weekOffset  -1:上周 0:本周 1:下周
     * @param firstDayOfWeek  每周以第几天为首日
     * @return: int[]
     * @auther: 王培文
     * @date: 2018/5/11 11:47
     */
    public static int[] getWeekAndYear(String queryDate, int weekOffset,
                                       int firstDayOfWeek) throws ParseException {

        Date date = formatStringToDate(queryDate, DATE_FORMAT_YMD);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(firstDayOfWeek);
        calendar.setMinimalDaysInFirstWeek(WEEK_DAYS);
        int year = calendar.getWeekYear();
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int[] result = { week, year };
        switch (weekOffset) {
            case 1:
                result = getNextYearWeek(year, week);
                break;
            case -1:
                result = getLastYearWeek(year, week);
                break;
            default:
                break;
        }
        return result;
    }


    /**
     * 功能描述: 计算个两日期的天数
     * @param startDate
     * @param endDate
     * @return: int
     * @auther: 王培文
     * @date: 2018/5/11 11:53
     */
    public static int getDaysBetween(String startDate, String endDate)
            throws ParseException {
        int dayGap = 0;
        if (startDate != null && startDate.length() > 0 && endDate != null
                && endDate.length() > 0) {
            Date end = formatStringToDate(endDate, DATE_FORMAT_YMD);
            Date start = formatStringToDate(startDate, DATE_FORMAT_YMD);
            dayGap = getDaysBetween(start, end);
        }
        return dayGap;
    }

    private static int getDaysBetween(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / ONE_DAY_MILLS);
    }
    /**
     * 功能描述:计算两个日期之间的天数差
     * @param startDate
     * @param endDate
     * @return: int
     * @auther: 王培文
     * @date: 2018/5/11 12:56
     */

    public static int getDaysGapOfDates(Date startDate, Date endDate) {
        int date = 0;
        if (startDate != null && endDate != null) {
            date = getDaysBetween(startDate, endDate);
        }
        return date;
    }

    /**
     * 功能描述:计算两个日期之间的年份差距
     * @param firstDate
     * @param secondDate
     * @return: int
     * @auther: 王培文
     * @date: 2018/5/11 12:57
     */
    public static int getYearGapOfDates(Date firstDate, Date secondDate) {
        if (firstDate == null || secondDate == null) {
            return 0;
        }
        Calendar helpCalendar = Calendar.getInstance();
        helpCalendar.setTime(firstDate);
        int firstYear = helpCalendar.get(Calendar.YEAR);
        helpCalendar.setTime(secondDate);
        int secondYear = helpCalendar.get(Calendar.YEAR);
        return secondYear - firstYear;
    }
    
    /**
     * 功能描述:计算两个日期之间的月份差距
     * @param firstDate
     * @param secondDate
     * @return: int
     * @auther: 王培文
     * @date: 2018/5/11 12:57
     */
    public static int getMonthGapOfDates(Date firstDate, Date secondDate) {
        if (firstDate == null || secondDate == null) {
            return 0;
        }

        return (int) ((secondDate.getTime() - firstDate.getTime())
                / ONE_DAY_MILLS / 30);
    }
    
    /**
     * 功能描述:计算是否包含当前日期
     * @param dates
     * @return: boolean
     * @auther: 王培文
     * @date: 2018/5/11 12:58
     */
    public static boolean isContainCurrent(List<String> dates) {
        boolean flag = false;
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_YMD);
        Date date = new Date();
        String dateStr = fmt.format(date);
        for (int i = 0; i < dates.size(); i++) {
            if (dateStr.equals(dates.get(i))) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 功能描述:从date开始计算time天后的日期
     * @param startDate
     * @param time
     * @return: java.lang.String
     * @auther: 王培文
     * @date: 2018/5/11 12:59
     */
    public static String getCalculateDateToString(String startDate, int time)
            throws ParseException {
        String resultDate = null;
        if (startDate != null && startDate.length() > 0) {
            Date date = formatStringToDate(startDate, DATE_FORMAT_YMD);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.DAY_OF_YEAR, time);
            date = c.getTime();
            resultDate = formatDateToString(date, DATE_FORMAT_YMD);
        }
        return resultDate;
    }
    
    /**
     * 功能描述:获取从某日期开始计算，指定的日期所在该年的第几周
     * @param date
     * @param admitDate
     * @return: int[]
     * @auther: 王培文
     * @date: 2018/5/11 13:01
     */
    public static int[] getYearAndWeeks(String date, String admitDate)
            throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(formatStringToDate(admitDate, DATE_FORMAT_YMD));
        int time = c.get(Calendar.DAY_OF_WEEK);
        return getWeekAndYear(date, 0, time);
    }

    /**
     * 功能描述:获取指定日期refDate，前或后一周的所有日期
     * @param refDate 参考日期
     * @param weekOffset 1:上周 0:本周 1:下周
     * @param startDate  哪天算一周的第一天
     * @return: java.util.List<java.lang.String>
     * @auther: 王培文
     * @date: 2018/5/11 13:02
     */
    public static List<String> getWeekDaysAroundDate(String refDate,
                                                     int weekOffset, String startDate) throws ParseException {
        // 以startDate为一周的第一天
        Calendar c = Calendar.getInstance();
        c.setTime(formatStringToDate(startDate, DATE_FORMAT_YMD));
        int firstDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        // 获取相应周
        int[] weekAndYear = getWeekAndYear(refDate, weekOffset, firstDayOfWeek);
        // 获取相应周的所有日期
        return getWeekDays(weekAndYear[1], weekAndYear[0], firstDayOfWeek);
    }

    /**
     * 功能描述:根据时间点获取时间区间
     * @param hours
     * @return: java.util.List<java.lang.String[]>
     * @auther: 王培文
     * @date: 2018/5/11 13:04
     */
    public static List<String[]> getTimePointsByHour(int[] hours) {
        List<String[]> hourPoints = new ArrayList<String[]>();
        String sbStart = ":00:00";
        String sbEnd = ":59:59";
        for (int i = 0; i < hours.length; i++) {
            String[] times = new String[2];
            times[0] = hours[i] + sbStart;
            times[1] = (hours[(i + 1 + hours.length) % hours.length] - 1)
                    + sbEnd;
            hourPoints.add(times);
        }
        return hourPoints;
    }

    /**
     * 功能描述:根据指定的日期，增加或者减少天数
     * @param date
     * @param amount
     * @return: java.util.Date
     * @auther: 王培文
     * @date: 2018/5/11 13:06
     */
    public static Date addDays(Date date, int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 功能描述:根据指定的日期，类型，增加或减少数量
     * @param date
     * @param calendarField
     * @param amount
     * @return: java.util.Date
     * @auther: 王培文
     * @date: 2018/5/11 13:06
     */
    public static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 功能描述:
     * @param 
     * @return: 获取当前日期的最大日期 时间2014-12-21 23:59:59
     * @auther: 王培文
     * @date: 2018/5/11 13:06
     */
    public static Date getCurDateWithMaxTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * 功能描述:获取当前日期的最小日期时间 2014-12-21 00:00:00
     * @param
     * @return: java.util.Date
     * @auther: 王培文
     * @date: 2018/5/11 13:07
     */
    public static Date getCurDateWithMinTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }


}
