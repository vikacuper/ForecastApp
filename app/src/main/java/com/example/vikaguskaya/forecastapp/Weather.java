package com.example.vikaguskaya.forecastapp;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
public class Weather {
    public final String date;
    public String lowTemp;
    public String hiTemp;
    public String tempFormat;
    public final String humidity;
    public final String description;
    public final String iconName;
    public String grad = "\u00B0C";
    public Weather(long timeStamp, double lowTemp, double hiTemp, double humidity,
                   String description, String iconName, String locationID)
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        this.date = convertTimeStampToDate(timeStamp, locationID); // TODO Проверить как передается и превращается время.
        this.tempFormat = tempFormat; //TODO Добавить переключение.
        this.description = description;
        this.hiTemp = numberFormat.format(hiTemp)+grad;
        this.lowTemp = numberFormat.format(lowTemp)+grad;
        this.humidity = numberFormat.getPercentInstance().format(humidity/100.);
        this.iconName = iconName+".png";

    }

    private static String convertTimeStampToDate(long timeStamp, String locationID)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp*1000);
        TimeZone timeZone = TimeZone.getTimeZone(locationID);
        calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:00");
        return  dateFormatter.format(calendar.getTime());
    }
}


