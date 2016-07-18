/**
 *
 */
package com.mohammadi.medical.shifter.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.mohammadi.medical.shifter.constraint.AbstractConstraint;
import com.mohammadi.medical.shifter.constraint.FixedDays;
import com.mohammadi.medical.shifter.constraint.FixedShift;
import com.mohammadi.medical.shifter.constraint.PersonalConstraint;
import com.mohammadi.medical.shifter.entities.Resident;

import ir.huri.jcal.JalaliCalendar;

/**
 * @author Mohammadi
 */
public abstract class AbstractScheduleRequest implements Cloneable, Parcelable, Serializable
{
    private int                      id;
    private String                   name;
    private List<Resident>           residents;
    private Period                   days;
    private LocalDate                startDate;
    private LocalDate                endDate;
    private List<AbstractConstraint> constraints;
    private List<LocalDate>          holidays;
    private ScheduleSettings         settings;

    public AbstractScheduleRequest(String name)
    {
        this.name = name;
        residents = new ArrayList<>();
        startDate = new JalaliCalendar(new GregorianCalendar()).toLocalDate();
        endDate = new JalaliCalendar(new GregorianCalendar()).toLocalDate().plusDays(1);
        setPeriod(startDate, endDate);
        constraints = new ArrayList<>();
        holidays = new ArrayList<>();
        settings = new ScheduleSettings();

        addDefaultConstraint();
    }

    protected AbstractScheduleRequest(Parcel in)
    {
        id = in.readInt();
        name = in.readString();

        setResidents((List<Resident>) in.readSerializable());
        setConstraints((List<AbstractConstraint>) in.readSerializable());

        setStartDate((LocalDate) in.readSerializable());
        setEndDate((LocalDate) in.readSerializable());

        setHolidays((List<LocalDate>) in.readSerializable());
        setSettings((ScheduleSettings) in.readSerializable());

    }

    private void addDefaultConstraint()
    {
    }

    public Resident createResident(String name)
    {
        Resident resident = new Resident(name);
        if (residents.isEmpty())
            resident.setId(0);
        else
            resident.setId(residents.get(residents.size() - 1).getId() + 1);
        residents.add(resident);
        return resident;
    }

    public void removeResident(Resident resident)
    {
        residents.remove(resident);
    }

    public void addConstraint(AbstractConstraint constraint)
    {
        if (constraints.isEmpty())
            constraint.setId(0);
        else
            constraint.setId(constraints.get(constraints.size() - 1).getId() + 1);

        constraints.add(constraint);
    }

    public void removeConstraint(AbstractConstraint constraint)
    {
        residents.remove(constraint);
    }

    public void setPeriod(LocalDate start, LocalDate end)
    {
        startDate = start;
        endDate = end;
        days = Period.fieldDifference(start, end);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Resident> getResidents()
    {
        return residents;
    }

    public void setResidents(List<Resident> residents)
    {
        this.residents = residents;
    }

    public Period getDays()
    {
        return days;
    }

    public List<AbstractConstraint> getConstraints()
    {
        return constraints;
    }

    public void setConstraints(List<AbstractConstraint> constraints)
    {
        this.constraints = constraints;
    }

    public LocalDate getStartDate()
    {
        return startDate;
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
        if (startDate != null && endDate != null)
            days = Period.fieldDifference(getStartDate(), getEndDate());
    }

    public LocalDate getEndDate()
    {
        return endDate;
    }

    public void setEndDate(LocalDate endDate)
    {
        this.endDate = endDate;
        if (startDate != null && endDate != null)
            days = Period.fieldDifference(getStartDate(), getEndDate());
    }

    public int getNumberOfDays()
    {
        return Days.daysBetween(getStartDate(), getEndDate().plusDays(1)).getDays();
    }

    public List<LocalDate> getHolidays()
    {
        return holidays;
    }

    public void setHolidays(List<LocalDate> holidays)
    {
        this.holidays = holidays;
    }

    public ScheduleSettings getSettings()
    {
        return settings;
    }

    public void setSettings(ScheduleSettings settings)
    {
        this.settings = settings;
    }

    public List<AbstractConstraint> getConstraintsFor(Resident resident)
    {
        List<AbstractConstraint> temp = new ArrayList<>();

        for (AbstractConstraint constraint : constraints)
        {
            if (constraint instanceof PersonalConstraint == false)
                continue;
            if (((PersonalConstraint) constraint).getResident().getId() == resident.getId())
                temp.add(constraint);
        }

        return temp;
    }

    @Override
    public AbstractScheduleRequest clone()
    {
        try
        {
            AbstractScheduleRequest request = (AbstractScheduleRequest) super.clone();

            request.residents = new ArrayList<>();
            for (Resident resident : residents)
            {
                request.residents.add(resident.clone());
            }

            request.constraints = new ArrayList<>();
            for (AbstractConstraint constraint : constraints)
            {
                request.constraints.add(constraint.clone());
            }

            request.setPeriod(
                    new LocalDate(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth()),
                    new LocalDate(endDate.getYear(), endDate.getMonthOfYear(), endDate.getDayOfMonth())
            );

            request.holidays = new ArrayList<>();
            for (LocalDate localDate : holidays)
            {
                request.holidays.add(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));

            }

            if (getSettings() != null)
                request.settings = settings.clone();
            return request;
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean hasConstraint(Resident resident, LocalDate plusDays)
    {
        for (AbstractConstraint abstractConstraint : getConstraintsFor(resident))
        {
            if (abstractConstraint instanceof FixedDays)
                if (((FixedDays) abstractConstraint).getStart().isBefore(plusDays)
                        && ((FixedDays) abstractConstraint).getEnd().isAfter(plusDays))
                    return true;
                else if (abstractConstraint instanceof FixedShift)
                    if (((FixedShift) abstractConstraint).getDate().equals(plusDays))
                        return true;
        }
        return false;
    }

    public String toString()
    {
        return getName();
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeSerializable((Serializable) getResidents());
        parcel.writeSerializable((Serializable) getConstraints());
        parcel.writeSerializable(getStartDate());
        parcel.writeSerializable(getEndDate());
        parcel.writeSerializable((Serializable) getHolidays());
        parcel.writeSerializable(getSettings());

    }
}
