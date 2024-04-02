package com.sqds.comutil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class ToolUtil {
    /**
     * LocalDatetimeת��ΪDate
     * @param date
     * @return
     */
    public static Date changeformatedatetodate(LocalDateTime date)
    {
        return  Date.from(date.toInstant(ZoneOffset.of("+8")));

    }

    /**
     * ������ת��ΪLocalDateTime
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
