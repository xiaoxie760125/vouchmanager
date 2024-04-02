package com.sqds.comutil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class ToolUtil {
    /**
     * LocalDatetime转换为Date
     * @param date
     * @return
     */
    public static Date changeformatedatetodate(LocalDateTime date)
    {
        return  Date.from(date.toInstant(ZoneOffset.of("+8")));

    }

    /**
     * 将日期转换为LocalDateTime
     * @param date
     * @return
     */
   public  static   LocalDateTime changeformatedatetolocaldate(Date date)
    {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    public static boolean  strisNull(String str)
    {
        return  str==null||str.isEmpty();
    }
}
