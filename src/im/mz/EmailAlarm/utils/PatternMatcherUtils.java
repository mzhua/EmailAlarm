package im.mz.EmailAlarm.utils;


import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HUA on 2014/11/16.
 */
public class PatternMatcherUtils {

    public static Pattern pLocation = Pattern.compile("(在|于|去|到|地\\s*址|地\\s*点){1}.{0,10}\\d*(会\\s*议\\s*室?|房\\s*间|召\\s*开|举\\s*行|进\\s*行|开\\s*展|室|开\\s*会|见\\s*面|楼|座|层|号|大\\s*厦)+?");

    //年
    public static Pattern pYear = Pattern.compile("(\\d{2,4}\\s*年)|(今年)|(明年)");

    //月
    public static Pattern pMonth = Pattern.compile("(本\\s*月|这\\s*个\\s*月|当\\s*月)|(下\\s*个?月)|(下\\s*下\\s*个?月)|((1|2|3|4|5|6|7|8|9|10|11|12|一|二|三|四|五|六|七|八|九|十|十\\s*一|十\\s*二|)\\s*月)");

    //日
    public static Pattern pDay = Pattern.compile("(今\\s*天|今\\s*日|本\\s*日)|(明\\s*天|明\\s*日)|(后\\s*天|后\\s*日)|(大\\s*后\\s*天)|((\\d{1,2})\\s*(日|号))|(月\\s*(\\d{1,2})\\s*(日|号)?)");

    public static Pattern pDay_week = Pattern.compile("(本|这|下|下\\s*下)?个?\\s*(周|星\\s*期|礼\\s*拜)\\s*(一|1|二|2|三|3|四|4|五|5|六|6|七|7|日|天)");

    //时,分
    public static Pattern pTime_ap = Pattern.compile("(上\\s*午|早\\s*上|早\\s*晨|凌\\s*晨|清\\s*晨|AM|am)|(下\\s*午|今\\s*晚|晚\\s*上|傍\\s*晚|pm|PM)");

    //即使数字和汉字之间有空格也要识别
    public static Pattern pTime = Pattern.compile("((\\d{1,2})\\s*[点时]\\s*(一|二|三|四|1|2|3|4)\\s*刻)|((\\d{1,2})\\s*[点时]\\s*(\\d{1,2})\\s*分)|((\\d{1,2})\\s*点\\s*(半)?)|((\\d{1,2})\\s*(:|：)\\s*(\\d{1,2}))");

    /**
     * 检测在复制的内容中是否有地址和时间
     *
     * @param clipData
     * @return
     */
    public static boolean isMatched(String clipData) {

        Matcher matcher;

        matcher = pDay_week.matcher(clipData);
        if (!matcher.find()) {
            matcher = pDay.matcher(clipData);
            if (!matcher.find()) {
                matcher = pTime.matcher(clipData);
                if (!matcher.find()) {
                    matcher = pMonth.matcher(clipData);
                    if (!matcher.find()) {
                        return false;
                    }
                }
            }
        }

        matcher = pLocation.matcher(clipData);
        if (matcher.find()) {
            return true;
        }

        return false;
    }

    /**
     * 解析字符串中的事件元素
     *
     * @param clipData 需要解析的字符串
     * @return 返回解析好的时间
     */
    public static long analyzeData(String clipData) {
        Calendar calendar = Calendar.getInstance();

        String yearStr = getYear(clipData);
        int year;
        int month = getMonth(clipData);
        int day = getDay(clipData);

        int[] time = getTime(clipData);
        int hour = time[0];
        int minute = time[1];


        //年
        if (StringUtils.isBlank(yearStr)) {
            year = calendar.get(Calendar.YEAR);
        } else {
            year = Integer.parseInt(yearStr);
        }
        //月
        if (month == 0) {
            month = calendar.get(Calendar.MONTH) + 1;
        }
        //日
        if (day > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {//比本月的最大日期还大，则说明是下个月的日期
            month = month + 1;
            day = day - calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else if (day <= 0) {
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        //月，年
        if (month > 12) {
            year = year + 1;
            month = month - 12;
        }


        Date mDate = MyDateUtils.formatToDate(year, month, day, hour, minute);
        return mDate.getTime();
    }

    /**
     * 解析年份
     *
     * @param clipData
     * @return
     */
    public static String getYear(String clipData) {
        String year = "";
        Calendar calendar = Calendar.getInstance();
        //年
        Matcher yMatcher = pYear.matcher(clipData);
        if (yMatcher.find()) {
            int matchGroup = getMatchGroup(yMatcher);

            String cYear = String.valueOf(calendar.get(Calendar.YEAR));
            switch (matchGroup) {
                case 1:
                    Pattern yearNumber = Pattern.compile("\\d{2,4}");
                    String t = yMatcher.group(1);
                    Matcher nMatcher = yearNumber.matcher(t);
                    if (nMatcher.find()) {
                        year = nMatcher.group();
                        if (!StringUtils.isBlank(year)) {
                            if (year.length() < 4) {
                                String preYear = cYear.substring(0, cYear.length() - year.length());
                                year = preYear + year; //拼接成2014这样的格式
                            }
                        }

                    }

                    break;
                case 2:
                    year = cYear;
                    break;
                case 3:
                    year = String.valueOf(calendar.get(Calendar.YEAR) + 1);
                    break;
            }
        }
        return year;
    }

    /**
     * 解析月份
     *
     * @param clipData
     * @return 返回月份，如果返回值超过12，则说明检测到的是下个月或者下下个月(当前月为12月的情况)，此时年份要加1
     */
    public static int getMonth(String clipData) {
        int month = 0;
        Calendar calendar = Calendar.getInstance();
        Matcher monMatcher = pMonth.matcher(clipData);
        if (monMatcher.find()) {
            int matchGroup = getMatchGroup(monMatcher);
            switch (matchGroup) {
                case 1:
                    month = calendar.get(Calendar.MONTH) + 1;
                    break;
                case 2:
                    month = calendar.get(Calendar.MONTH) + 2;
                    break;
                case 3:
                    month = calendar.get(Calendar.MONTH) + 3;
                    break;
                case 4:
                    String monthNum = monMatcher.group(5); //如果匹配组为4，那么数字就在组5里面
                    month = StringUtils.convertToNumber(monthNum);
                    break;

            }
        }

        return month;
    }

    /**
     * 解析日
     *
     * @param clipData
     * @return 由于有下周等条件，所以最后的日期可能会超过本月的最大日期，在需要使用时判断，如果超过本月最大日期，则表示该日期是下个月的并用这个日期减去本月的天数即为下个月的日期
     */
    public static int getDay(String clipData) {
        int day = 0;
        Calendar calendar = Calendar.getInstance();
        Matcher dMatcher;
        dMatcher = pDay.matcher(clipData);
        if (dMatcher.find()) { //pDay
            int matchGroup = getMatchGroup(dMatcher);

            switch (matchGroup) {
                case 1:
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    break;
                case 2:
                    day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                    break;
                case 3:
                    day = calendar.get(Calendar.DAY_OF_MONTH) + 2;
                    break;
                case 4:
                    day = calendar.get(Calendar.DAY_OF_MONTH) + 3;
                    break;
                case 5:
                    day = Integer.parseInt(dMatcher.group(6));
                    break;
                case 8:
                    day = Integer.parseInt(dMatcher.group(9));
                    break;
            }
        } else {
            //星期
            dMatcher = pDay_week.matcher(clipData);
            if (dMatcher.find()) {
                int cDay = calendar.get(Calendar.DAY_OF_MONTH);//今天的日期
                int cWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;//星期,注意，默认sunday是1
                cWeek = cWeek <= 0 ? 7 : cWeek;

                int nWeek = StringUtils.convertToNumber(dMatcher.group(3));//解析出来的星期
                day = cDay + (nWeek - cWeek); //本周对应星期的日期

                if ("下".equals(dMatcher.group(1))) {
                    day += 7;
                } else if ("下下".equals(dMatcher.group(1))) {
                    day += 14;
                }
            }
        }

        return day;
    }

    /**
     * 解析时间
     *
     * @param clipData
     * @return 最终的结果为24小时制的小时
     */
    public static int[] getTime(String clipData) {
        int[] time = new int[2];
        int hour = 9;
        int minute = 0;

        Matcher tMatcher = pTime.matcher(clipData);
        if (tMatcher.find()) {
            int matchGroup = getMatchGroup(tMatcher);
            switch (matchGroup) {
                case 1://刻
                    hour = Integer.parseInt(tMatcher.group(2));

                    minute = StringUtils.convertToNumber(tMatcher.group(3)) * 15;
                    break;
                case 4://分
                    hour = Integer.parseInt(tMatcher.group(5));

                    minute = Integer.parseInt(tMatcher.group(6));
                    break;
                case 7://整点
                    hour = Integer.parseInt(tMatcher.group(8));
                    if (StringUtils.isBlank(tMatcher.group(9))) {//半
                        minute = 0;
                    } else {
                        minute = 30;
                    }
                    break;
                case 10://  :
                    hour = Integer.parseInt(tMatcher.group(11));
                    minute = Integer.parseInt(tMatcher.group(13));
                    break;
            }


        }
        //上午或下午
        tMatcher = pTime_ap.matcher(clipData);
        if (tMatcher.find()) {
            int matchGroup = getMatchGroup(tMatcher);
            switch (matchGroup) {
                case 1:

                    break;
                case 2:
                    if(hour < 12){
                        hour = hour + 12;
                    }

                    break;
            }
        }
        if (hour < 0 || hour > 24) {
            hour = 0;
        }
        if (minute < 0 || minute > 60) {
            minute = 0;
        }

        time[0] = hour;
        time[1] = minute;
        return time;
    }

    /**
     * 获取匹配的组
     *
     * @param matcher
     * @return
     */
    public static int getMatchGroup(Matcher matcher) {
        int matchGroup = 0;
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (!StringUtils.isBlank(matcher.group(i))) {
                matchGroup = i;
                break;
            }
        }
        return matchGroup;
    }
}
