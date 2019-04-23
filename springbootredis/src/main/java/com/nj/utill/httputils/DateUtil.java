package com.nj.utill.httputils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {
    /**
     * 获取设置的订单有效时间
     * @param hour
     * @return
     */
    public static Date getEffectiveDate(Integer hour)
    {
        Date date = new Date();
        if (hour > 0)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MINUTE, hour * 60);
            return c.getTime();
        }
        return date;

    }

    /**
     * 把字符串转成date
     * @param date
     * @param type
     * @return
     */
    public static Date stringToDate(String date, String type)
    {
        if (date != null && date.length() > 0 && type != null && type.length() > 0)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(type);
            try
            {
                return sdf.parse(date);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 获取传入时间的年份  1 年 2 月 3日
     * @param date
     * @return
     */
    public  static String getYearOrMonthOrDayByDate(Date date,int type){
        Calendar a=Calendar.getInstance();
        a.setTime(date);
        if(type == 1){
            return  a.get(Calendar.YEAR)+"";
        }else if(type == 2){
            return  a.get(Calendar.MONTH)+1+"";
        }else if(type == 3){
            return a.get(Calendar.DAY_OF_YEAR)+"";
        }
        return null;
    }
    /**
     * 把date转成string
     *
     * @param date
     * @param type
     * @return
     */
    public static String dateToString(Date date, String type)
    {
        if (date != null && type != null && type.length() > 0)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(type);
            return sdf.format(date);
        }
        return "";
    }

    public static String dateToStringLong()
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }




    /**
     * 获取两个时间差 分钟
     * @param sdate
     * @param edate
     * @return
     */
    public static Integer differenceM(Date sdate, Date edate){
        Calendar dateOne=Calendar.getInstance()
                ,dateTwo=Calendar.getInstance();
        dateOne.setTime(sdate);
        dateTwo.setTime(edate);
        long timeOne=dateOne.getTimeInMillis();
        long timeTwo=dateTwo.getTimeInMillis();
        long minute=(timeTwo-timeOne)/(1000*60);//转化minute
        return Integer.parseInt(String.valueOf(minute));
    }

    public static void main(String[] args)
    {
        System.out.println(dateToStringLong());

    }

    /**
     * 获取token的到期时间， 这里设置的是的有效时间是23小时
     * @return
     */
    public static String  getTokenDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeLose=System.currentTimeMillis()+23*60*60*1000;
        String loseDate=sdf.format(new Date(timeLose));
        return loseDate;
    }

    /**
     * 比较时间大小
     * @param date1
     * @param date2
     * @return
     */
    public static boolean after(Date date1,Date date2){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        return c2.after(c1);
    }


}
