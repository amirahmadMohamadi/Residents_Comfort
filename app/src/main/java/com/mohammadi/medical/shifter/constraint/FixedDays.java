package com.mohammadi.medical.shifter.constraint;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.variables.IntVar;
import org.joda.time.LocalDate;

import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.VariablesEntity;

import ir.huri.jcal.JalaliCalendar;

public class FixedDays extends PersonalConstraint
{

    private Site site;
    private LocalDate start;
    private LocalDate end;

    public FixedDays(LocalDate start, LocalDate end, Resident resident, Site site)
    {
        super();
        setSite(site);
        setResident(resident);
        this.start = start.minusDays(1);
        this.end = end.plusDays(1);
    }

    public FixedDays(LocalDate start, LocalDate end, Resident resident, Site site, ConstraintImportance importance)
    {
        super();
        setSite(site);
        setResident(resident);
        this.start = start.minusDays(1);
        this.end = end.plusDays(1);
        setImportance(importance);
    }

    public Constraints getType()
    {
        return Constraints.FixedDays;
    }

    public Site getSite()
    {
        return site;
    }

    public void setSite(Site site)
    {
        this.site = site;
    }

    public LocalDate getStart()
    {
        return start;
    }

    public void setStart(LocalDate start)
    {
        this.start = start;
    }

    public LocalDate getEnd()
    {
        return end;
    }

    public void setEnd(LocalDate end)
    {
        this.end = end;
    }

    @Override
    public void postConstraint(Solver solver, AbstractScheduleRequest request, VariablesEntity variables, boolean isKnightShitConstraint)
    {

        if (isKnightShitConstraint)
            throw new UnsupportedOperationException();
        for (IntVar boolVar : solver.retrieveIntVars())
        {
            int index1 = boolVar.getName().indexOf(".", 0);
            if (index1 == -1)
                continue;
            int index2 = boolVar.getName().indexOf(".", index1 + 1);

            int dayId = Integer.parseInt(boolVar.getName().substring(index1 + 1, index2));
            int residentId = Integer.parseInt(boolVar.getName().substring(index2 + 1));

            if (request.getHolidays().contains(request.getStartDate().plusDays(dayId)))
                continue;

            if (request.getStartDate().plusDays(dayId).isAfter(getStart())
                    && request.getStartDate().plusDays(dayId).isBefore(getEnd())
                    && getResident().getId() == residentId)
            {
                solver.post(IntConstraintFactory.arithm(boolVar, "=", site.getId()));
            }
        }
    }

    @Override
    public FixedDays clone()
    {
        FixedDays clone = (FixedDays) super.clone();
        if (getStart() != null)
            clone.setStart(LocalDate.fromDateFields(start.toDate()));
        if (getEnd() != null)
            clone.setEnd(LocalDate.fromDateFields(end.toDate()));

        if (site != null)
            clone.site = site.clone();
        return clone;
    }

    public String toString()
    {
        return getResident().toString() + " should be on " + getSite() + " from " + new JalaliCalendar(getStart()).toString() +
                " to " + new JalaliCalendar(getEnd()).toString();
    }


}
