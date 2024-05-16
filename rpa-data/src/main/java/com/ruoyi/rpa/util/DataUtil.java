package com.ruoyi.rpa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
    public static String formatToStandardDateTime(String inputFormat, String inputDate) {
        // 定义输入的日期格式，可以根据实际情况进行调整
        SimpleDateFormat inputFormatString = new SimpleDateFormat(inputFormat);

        // 定义输出的日期格式
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            // 将输入的日期字符串解析为Date对象
            Date date = inputFormatString.parse(inputDate);

            // 将Date对象格式化为指定的输出格式
            return outputFormat.format(date);
        } catch (ParseException e) {
            // 如果日期字符串无法解析，可以根据实际情况进行异常处理
            e.printStackTrace();
            return "-";
        }
    }
}
