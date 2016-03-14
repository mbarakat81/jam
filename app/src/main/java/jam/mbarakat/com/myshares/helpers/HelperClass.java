package jam.mbarakat.com.myshares.helpers;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MBARAKAT on 2/8/2016.
 */
public class HelperClass {

    public static String getFormatedDateFromString(String deliverDate) {
        String[] dataSplitArray = deliverDate.split(" ");
        if(dataSplitArray.length == 1)
            return deliverDate;
        if(dataSplitArray[1].endsWith(",")){
            dataSplitArray[1] = dataSplitArray[1].substring(0,dataSplitArray[1].length()-1);
        }
        String date = dataSplitArray[1] + " " + dataSplitArray[0] + " " + dataSplitArray[2];
        return date;
    }

    public static Date getDate(String day){
        String[] arrDay = day.split("/");
        int _day, month, year;
        _day = Integer.parseInt(arrDay[0]);
        month = Integer.parseInt(arrDay[1])-1;
        year =  Integer.parseInt(arrDay[2])-1900;
        return new Date(year,month,_day);
    }

    public static Date getDateFromString(String date) throws ParseException {
        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = curFormater.parse(date);
        return dateObj;
    }
    public static Date getCurrentDatePlusMonth(int month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, month);
        Date newDate = calendar.getTime();
        return newDate;
    }

    public static Date getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date newDate = calendar.getTime();
        return newDate;
    }

    public enum Period {
        WEEKLY {
            public String  toNumber() {
                return "WEEKLY";
            }
        },

        MONTHLY {
            public String toNumber() {
                return "MOUNTHLY";
            }
        }
    }


    public static String getStringFormatDate(Date date, Context context){
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        ((SimpleDateFormat) dateFormat).applyPattern("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static Date getFormatDate(String _date){
        String dateOfBirth = _date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = sdf.parse(dateOfBirth);
        } catch (ParseException e) {
            // handle exception here !
        }
        return date;
    }

    public static String getStringDateFromString(String _date, Context context){
        String dateOfBirth = _date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = sdf.parse(dateOfBirth);
        } catch (ParseException e) {
            // handle exception here !
        }
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return dateFormat.format(date);
    }

    public static String setDate(String date, int days)  {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String[] arr = date.split("/");
        int day,year,month;
        day = Integer.parseInt(arr[0]);
        month = Integer.parseInt(arr[1]);
        year = Integer.parseInt(arr[2]);
        Calendar c = Calendar.getInstance();
        c.set(year,month-1,day);
        c.add(Calendar.DAY_OF_YEAR, days);

        return sdf.format(c.getTime());
    }

}
