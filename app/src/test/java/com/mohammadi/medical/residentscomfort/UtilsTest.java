package com.mohammadi.medical.residentscomfort;

import com.mohammadi.medical.shifter.util.DateUtil;
import com.mohammadi.medical.shifter.util.Utils;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.List;

import ir.huri.jcal.JalaliCalendar;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UtilsTest
{

    @Test
    public void holidayTest() throws Exception
    {
        List<LocalDate> holidaysBetween = DateUtil.getHolidaysBetween(new JalaliCalendar(1395, 3, 1).toLocalDate(), new JalaliCalendar(1395, 3, 31).toLocalDate());

        System.out.println(Utils.listToString(holidaysBetween));
    }
}