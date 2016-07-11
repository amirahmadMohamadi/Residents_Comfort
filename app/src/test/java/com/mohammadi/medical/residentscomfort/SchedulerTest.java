package com.mohammadi.medical.residentscomfort;

import com.mohammadi.medical.shifter.entities.Schedule;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;
import com.mohammadi.medical.shifter.schedule.Scheduler;
import com.mohammadi.medical.shifter.schedule.SchedulingFailedException;

import org.junit.Test;

import ir.huri.jcal.JalaliCalendar;

/**
 * Created by amirahmad on 16/6/14 AD.
 */
public class SchedulerTest
{

    @Test
    public void scheduleTest() throws SchedulingFailedException
    {
        ScheduleRequest request = new ScheduleRequest("خرداد");
        request.createResident("خیری دوست");
        request.createResident("برجی");
        request.createResident("غلامحسینی");
        request.createResident("محقق");
        request.createResident("اسلامی");
        request.createResident("آخوندی");

        request.createSite("سونو ۱");
        request.createSite("سونو ۲");
        request.createSite("سونو ۳");
//        request.createSite("شرح حال");
        request.createSite("گرافی");
        request.createSite("ct", 2);

        request.getHolidays().add(new JalaliCalendar(1395, 3, 14).toLocalDate());

        request.setStartDate(new JalaliCalendar(1395, 3, 1).toLocalDate());
        request.setEndDate(new JalaliCalendar(1395, 3, 31).toLocalDate());

        Scheduler scheduler = new Scheduler();
        scheduler.getRequests().add(request);

        Schedule schedule = scheduler.schedule(request);

        System.out.println(schedule);
    }
}
