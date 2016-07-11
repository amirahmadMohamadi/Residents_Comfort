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
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.VariablesEntity;

import ir.huri.jcal.JalaliCalendar;

public class OffDays extends PersonalConstraint
{

    private LocalDate start;
    private LocalDate end;

    public OffDays(LocalDate start, LocalDate end, Resident resident)
    {
        super();
        setResident(resident);
        this.start = start.minusDays(1);
        this.end = end.plusDays(1);
    }

    public OffDays(LocalDate start, LocalDate end, Resident resident, ConstraintImportance importance)
    {
        super();
        setResident(resident);
        this.start = start.minusDays(1);
        this.end = end.plusDays(1);
        setImportance(importance);
    }

    public OffDays(LocalDate date, Resident resident)
    {
        super();
        setResident(resident);
        this.start = date.minusDays(1);
        this.end = date.plusDays(1);
    }

    public OffDays(LocalDate date, Resident resident, ConstraintImportance importance)
    {
        super();
        setResident(resident);
        this.start = date.minusDays(1);
        this.end = date.plusDays(1);
        setImportance(importance);
    }

    public Constraints getType()
    {
        return Constraints.OffDays;
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
            for (BoolVar boolVar : solver.retrieveBoolVars())
            {
                int index1 = boolVar.getName().indexOf(".", 0);
                if (index1 == -1)
                    continue;
                int index2 = boolVar.getName().indexOf(".", index1 + 1);

                int dayId = Integer.parseInt(boolVar.getName().substring(index1 + 1, index2));
                int residentId = Integer.parseInt(boolVar.getName().substring(index2 + 1));

                if (request.getStartDate().plusDays(dayId).isAfter(getStart())
                        && request.getStartDate().plusDays(dayId).isBefore(getEnd())
                        && getResident().getId() == residentId)
                    solver.post(IntConstraintFactory.arithm(boolVar, "=", VariableFactory.fixed(0, solver)));
            }
        else
        {
            List<IntVar> deletedVars = new ArrayList<>();

            for (IntVar boolVar : solver.retrieveIntVars())
            {
                int index1 = boolVar.getName().indexOf(".", 0);
                if (index1 == -1)
                    continue;
                int index2 = boolVar.getName().indexOf(".", index1 + 1);

                int dayId = Integer.parseInt(boolVar.getName().substring(index1 + 1, index2));
                int residentId = Integer.parseInt(boolVar.getName().substring(index2 + 1));

                if (request.getStartDate().plusDays(dayId).isAfter(getStart())
                        && request.getStartDate().plusDays(dayId).isBefore(getEnd())
                        && getResident().getId() == residentId)
                {
                    deletedVars.add(boolVar);
                }
            }
            for (IntVar intVar : deletedVars)
            {
                solver.unassociates(intVar);
            }
        }

    }

    @Override
    public OffDays clone()
    {
        OffDays clone = (OffDays) super.clone();
        if (getStart() != null)
            clone.setStart(LocalDate.fromDateFields(start.toDate()));
        if (getEnd() != null)
            clone.setEnd(LocalDate.fromDateFields(end.toDate()));
        return clone;
    }

    public String toString()
    {
        return getResident().toString() + " should be off from " + new JalaliCalendar(getStart()).toString() + " to " + new JalaliCalendar(getEnd()).toString();
    }
}
