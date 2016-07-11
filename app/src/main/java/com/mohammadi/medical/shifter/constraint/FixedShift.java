package com.mohammadi.medical.shifter.constraint;

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.joda.time.LocalDate;

import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.VariablesEntity;
import com.mohammadi.medical.shifter.util.Utils;

import ir.huri.jcal.JalaliCalendar;

public class FixedShift extends PersonalConstraint
{
    private List<Site> sites;
    private LocalDate date;
    private boolean isDayOff;

    public FixedShift(LocalDate date, Resident resident, List<Site> sites, boolean isDayOff)
    {
        super();
        setDate(date);
        setResident(resident);
        this.sites = sites;
        this.isDayOff = isDayOff;
    }

    public FixedShift(LocalDate date, Resident resident, List<Site> sites, boolean isDayOff, ConstraintImportance importance)
    {
        super();
        setDate(date);
        setResident(resident);
        this.sites = sites;
        this.isDayOff = isDayOff;
        setImportance(importance);
    }

    public Constraints getType()
    {
        return Constraints.FixedShift;
    }

    // TODO
    @Override
    public void postConstraint(Solver solver, AbstractScheduleRequest request, VariablesEntity variables, boolean isKnightShitConstraint)
    {

        if (isKnightShitConstraint)
            for (BoolVar boolVar : solver.retrieveBoolVars())
            {
                int index1 = boolVar.getName().indexOf(".", 0);
                if (index1 == -1)
                    continue;
                int index2 = boolVar.getName().indexOf(".", index1 + 1);

                int dayId = Integer.parseInt(boolVar.getName().substring(index1 + 1, index2));
                int residentId = Integer.parseInt(boolVar.getName().substring(index2 + 1));

                if (request.getStartDate().plusDays(dayId).equals(getDate()))
                {
                    if (isDayOff() == false)
                    {
                        if (getResident().getId() == residentId)
                            solver.post(IntConstraintFactory.arithm(boolVar, "=", VariableFactory.fixed(1, solver)));
                        else
                            solver.post(IntConstraintFactory.arithm(boolVar, "=", VariableFactory.fixed(0, solver)));

                    }
                    else if (getResident().getId() == residentId)
                        solver.post(IntConstraintFactory.arithm(boolVar, "=", VariableFactory.fixed(0, solver)));
                }
            }
        else
        {
            int[] siteIds = new int[sites.size()];
            for (int i = 0; i < sites.size(); i++)
            {
                siteIds[i] = sites.get(i).getId();
            }

            for (IntVar boolVar : solver.retrieveIntVars())
            {
                int index1 = boolVar.getName().indexOf(".", 0);
                if (index1 == -1)
                    continue;
                int index2 = boolVar.getName().indexOf(".", index1 + 1);

                int dayId = Integer.parseInt(boolVar.getName().substring(index1 + 1, index2));
                int residentId = Integer.parseInt(boolVar.getName().substring(index2 + 1));

                if (request.getStartDate().plusDays(dayId).equals(getDate()) && getResident().getId() == residentId)
                {
                    if (isDayOff() == false)
                        solver.post(IntConstraintFactory.member(boolVar, siteIds));
                    else
                        solver.post(IntConstraintFactory.not_member(boolVar, siteIds));
                }
            }
        }
    }

    public List<Site> getSite()
    {
        return sites;
    }

    public void setSites(List<Site> sites)
    {
        this.sites = sites;
    }

    public boolean isDayOff()
    {
        return isDayOff;
    }

    public void setDayOff(boolean isDayOff)
    {
        this.isDayOff = isDayOff;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    @Override
    public FixedShift clone()
    {
        FixedShift clone = (FixedShift) super.clone();
        if (getDate() != null)
            clone.setDate(LocalDate.fromDateFields(date.toDate()));

        if (sites != null)
        {
            clone.sites = new ArrayList<>();
            for (Site site : sites)
            {
                clone.sites.add(site.clone());
            }
        }
        return clone;
    }

    public String toString()
    {
        return getResident().toString() + " should " + (isDayOff ? "not " : "") + "be in " + Utils.listToString(getSite()) + " in " + new JalaliCalendar(getDate()).toString();
    }

}
