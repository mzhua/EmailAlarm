package im.mz.EmailAlarm.utils;

/**
 * Created by HUA on 2014/11/13.
 */
public class StringUtils {
    /**
     * 是否为空，包括null和""的情况
     * @param src
     * @return
     */
    public static boolean isBlank(String src){
        if(src == null){
            return true;
        }else{
            if("".equals(src.trim())){
                return true;
            }else{
                return false;
            }

        }
    }

    /**
     * 将字符串数字转化为数值的数字
     * @param num
     * @return
     */
    public static int convertToNumber(String num){
        if(!isBlank(num)){
            if("一".equals(num) || "1".equals(num)){
                return 1;
            }else if("二".equals(num) || "2".equals(num)){
                return 2;
            }else if("三".equals(num) || "3".equals(num)){
                return 3;
            }else if("四".equals(num) || "4".equals(num)){
                return 4;
            }else if("五".equals(num) || "5".equals(num)){
                return 5;
            }else if("六".equals(num) || "6".equals(num)){
                return 6;
            }else if("七".equals(num) || "7".equals(num) || "日".equals(num) || "天".equals(num)){
                return 7;
            }else if("八".equals(num) || "8".equals(num)){
                return 8;
            }else if("九".equals(num) || "9".equals(num)){
                return 9;
            }else if("十".equals(num) || "10".equals(num)){
                return 10;
            }else if("十一".equals(num) || "11".equals(num)){
                return 11;
            }else if("十二".equals(num) || "12".equals(num)){
                return 12;
            }
        }
        return 0;
    }
}
