package com.mohammadi.medical.shifter.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.LocalDate;

import com.mohammadi.medical.shifter.constraint.PersonalConstraint;
import com.mohammadi.medical.shifter.entities.Resident;

public class NightScheduleResult
{
    Map<LocalDate, List<Resident>> shifts;
    boolean                        solved;
    List<PersonalConstraint>       violatedConstraints;

    public NightScheduleResult()
    {
        shifts = new TreeMap<>();
        solved = false;
        violatedConstraints = new ArrayList<>();
    }

    public Map<LocalDate, List<Resident>> getShifts()
    {
        return shifts;
    }

    public void setShifts(Map<LocalDate, List<Resident>> shifts)
    {
        this.shifts = shifts;
    }

    public boolean isSolved()
    {
        return solved;
    }

    public void setSolved(boolean solved)
    {
        this.solved = solved;
    }
}
