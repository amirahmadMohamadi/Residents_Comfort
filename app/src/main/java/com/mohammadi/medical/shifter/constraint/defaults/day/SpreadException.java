package com.mohammadi.medical.shifter.constraint.defaults.day;

import com.mohammadi.medical.shifter.constraint.AbstractConstraint;
import com.mohammadi.medical.shifter.constraint.Constraints;
import com.mohammadi.medical.shifter.constraint.PersonalConstraint;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.VariablesEntity;
import com.mohammadi.medical.shifter.util.Utils;

import org.chocosolver.solver.Solver;

import java.util.List;

/**
 * Created by amirahmad on 16/7/18 AD.
 */
public class SpreadException extends PersonalConstraint
{
    private List<Site> sites;

    public SpreadException(Resident resident, List<Site> sites)
    {
        super();
        setResident(resident);
        this.sites = sites;
    }

    public List<Site> getSites()
    {
        return this.sites;
    }

    @Override
    public Constraints getType()
    {
        return Constraints.Spread;
    }

    @Override
    public void postConstraint(Solver solver, AbstractScheduleRequest request, VariablesEntity variables, boolean isKnightShitConstraint)
    {

    }

    public String toString()
    {
        return getResident() + " در سایت های " + Utils.listToString(getSites()) + " نباشد.";
    }

}
