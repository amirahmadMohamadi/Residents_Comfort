package com.mohammadi.medical.residentscomfort;

import com.mohammadi.medical.shifter.constraint.FixedDays;
import com.mohammadi.medical.shifter.constraint.FixedShift;
import com.mohammadi.medical.shifter.constraint.OffDays;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Schedule;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;
import com.mohammadi.medical.shifter.schedule.Scheduler;
import com.mohammadi.medical.shifter.schedule.SchedulingFailedException;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import ir.huri.jcal.JalaliCalendar;

/**
 * Created by amirahmad on 16/6/14 AD.
 */
public class SchedulerTest
{

    @Test
    public void scheduleTest() throws SchedulingFailedException
    {
        ScheduleRequest request = addTestData();

        Scheduler scheduler = new Scheduler();
        scheduler.getRequests().add(request);

        Schedule schedule = scheduler.schedule(request);

        System.out.println(schedule);
    }

    private ScheduleRequest addTestData()
    {
        ScheduleRequest request = new ScheduleRequest("تیر");
//        request.createResident("خیری دوست");
        Resident borji = request.createResident("برجی");
        Resident gholamhoseini = request.createResident("غلامحسینی");
        Resident mohaghegh = request.createResident("محقق");
        Resident eslami = request.createResident("اسلامی");
        Resident akhoundi = request.createResident("آخوندی");
        Resident paraham = request.createResident("پراهام");
        Resident hajihashemi = request.createResident("حاجی هاشمی");
        Resident azizi = request.createResident("عزیزی");

        request.createSite("سونو ۱");
        request.createSite("سونو ۲");
        request.createSite("سونو ۳");
        Site sonoEx = request.createSite("سونو اضافه");
        request.createSite("شرح حال");
        Site graphy = request.createSite("گرافی");
        Site ct = request.createSite("ct");
        Site scopy = request.createSite("فلوروسکوپی");

        request.setStartDate(new JalaliCalendar(1395, 4, 1).toLocalDate());
        request.setEndDate(new JalaliCalendar(1395, 4, 31).toLocalDate());

        request.addNightShiftScheduleRequest();
        request.fillNightShiftScheduleRequest();
        request.getNightShiftScheduleRequest().removeResident(hajihashemi);

        // Constraints
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 25).toLocalDate(), new JalaliCalendar(1395, 4, 26).toLocalDate(), akhoundi, sonoEx));

        request.addConstraint(new FixedShift(new JalaliCalendar(1395, 4, 19).toLocalDate(), borji, Arrays.asList(sonoEx), false));
        request.addConstraint(new FixedShift(new JalaliCalendar(1395, 4, 29).toLocalDate(), borji, Arrays.asList(sonoEx), false));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 15).toLocalDate(), new JalaliCalendar(1395, 4, 16).toLocalDate(), azizi, sonoEx));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 17).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), azizi, scopy));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 30).toLocalDate(), new JalaliCalendar(1395, 4, 31).toLocalDate(), azizi, sonoEx));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 15).toLocalDate(), new JalaliCalendar(1395, 4, 16).toLocalDate(), gholamhoseini, scopy));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 17).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), gholamhoseini, sonoEx));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 4).toLocalDate(), new JalaliCalendar(1395, 4, 5).toLocalDate(), mohaghegh, sonoEx));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 6).toLocalDate(), new JalaliCalendar(1395, 4, 7).toLocalDate(), mohaghegh, scopy));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 4).toLocalDate(), new JalaliCalendar(1395, 4, 5).toLocalDate(), eslami, scopy));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 6).toLocalDate(), new JalaliCalendar(1395, 4, 7).toLocalDate(), eslami, sonoEx));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 16).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), paraham, graphy));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 30).toLocalDate(), new JalaliCalendar(1395, 4, 31).toLocalDate(), paraham, scopy));

        // Night Constraints
        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 25).toLocalDate(), new JalaliCalendar(1395, 4, 26).toLocalDate(), akhoundi));

        request.getNightShiftScheduleRequest().addConstraint(new FixedShift(new JalaliCalendar(1395, 4, 19).toLocalDate(), borji, new ArrayList<Site>(), true));
        request.getNightShiftScheduleRequest().addConstraint(new FixedShift(new JalaliCalendar(1395, 4, 29).toLocalDate(), borji, new ArrayList<Site>(), true));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 15).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), azizi));
        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 30).toLocalDate(), new JalaliCalendar(1395, 4, 31).toLocalDate(), azizi));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 15).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), gholamhoseini));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 4).toLocalDate(), new JalaliCalendar(1395, 4, 7).toLocalDate(), mohaghegh));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 4).toLocalDate(), new JalaliCalendar(1395, 4, 7).toLocalDate(), eslami));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 16).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), paraham));
        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 30).toLocalDate(), new JalaliCalendar(1395, 4, 31).toLocalDate(), paraham));

        return request;
    }

}
