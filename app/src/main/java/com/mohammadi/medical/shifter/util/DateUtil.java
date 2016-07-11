package com.mohammadi.medical.shifter.util;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ir.huri.jcal.JalaliCalendar;

/**
 * Created by amirahmad on 16/6/3 AD.
 */
public class DateUtil
{
    public static final List<JalaliCalendar>    shamsiHolidays  = new ArrayList<>();
    public static final List<UmmalquraCalendar> ghamariHolidays = new ArrayList<>();

    static
    {
        shamsiHolidays.add(new JalaliCalendar(1, 1, 1));
        shamsiHolidays.add(new JalaliCalendar(1, 1, 2));
        shamsiHolidays.add(new JalaliCalendar(1, 1, 3));
        shamsiHolidays.add(new JalaliCalendar(1, 1, 4));
        shamsiHolidays.add(new JalaliCalendar(1, 1, 12));
        shamsiHolidays.add(new JalaliCalendar(1, 1, 13));
        shamsiHolidays.add(new JalaliCalendar(1, 3, 14));
        shamsiHolidays.add(new JalaliCalendar(1, 3, 15));
        shamsiHolidays.add(new JalaliCalendar(1, 11, 22));
        shamsiHolidays.add(new JalaliCalendar(1, 12, 29));
    }

    static
    {
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.MUHARRAM, 9));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.MUHARRAM, 10));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.SAFAR, 20));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.SAFAR, 28));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.SAFAR, 29));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.RABI_AWWAL, 17));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.JUMADA_THANI, 3));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.RAJAB, 13));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.RAJAB, 27));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.SHAABAN, 15));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.RAMADHAN, 21));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.SHAWWAL, 1));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.SHAWWAL, 2));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.SHAWWAL, 25));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.THUL_HIJJAH, 10));
        ghamariHolidays.add(new UmmalquraCalendar(1430, UmmalquraCalendar.THUL_HIJJAH, 18));
    }

    public static List<LocalDate> getHolidaysBetween(LocalDate start, LocalDate end)
    {
        List<LocalDate> holidays = new ArrayList<>();

        int year = new JalaliCalendar(start).getYear();
        int ghamariYear = new UmmalquraCalendar().get(Calendar.YEAR);

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1))
        {
            if (date.getDayOfWeek() == DateTimeConstants.FRIDAY)
            {
                holidays.add(date);
            }
            else
            {
                for (JalaliCalendar shamsiHoliday : shamsiHolidays)
                {
                    if (new JalaliCalendar(year, shamsiHoliday.getMonth(), shamsiHoliday.getDay()).toLocalDate().equals(date))
                    {
                        holidays.add(date);
                        break;
                    }
                }

                if (holidays.contains(date) == false)
                    for (UmmalquraCalendar ghmariHoliday : ghamariHolidays)
                    {
                        UmmalquraCalendar ummalquraCalendar = new UmmalquraCalendar(ghamariYear, ghmariHoliday.get(Calendar.MONTH), ghmariHoliday.get(Calendar.DAY_OF_MONTH));
                        GregorianCalendar cal = new GregorianCalendar();
                        cal.setTime(ummalquraCalendar.getTime());
                        if (new JalaliCalendar(cal).toLocalDate().equals(date))
                        {
                            holidays.add(date);
                            break;
                        }
                    }

            }
        }

        return holidays;
    }

}
