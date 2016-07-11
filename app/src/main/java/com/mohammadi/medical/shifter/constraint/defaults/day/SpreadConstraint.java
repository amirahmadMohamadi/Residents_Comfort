/**
 *
 */
package com.mohammadi.medical.shifter.constraint.defaults.day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

import com.mohammadi.medical.shifter.constraint.AbstractConstraint;
import com.mohammadi.medical.shifter.constraint.ConstraintImportance;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;
import com.mohammadi.medical.shifter.schedule.VariablesEntity;

/**
 * @author Mohammadi
 *         <p/>
 *         All residents must have equal variety in sites
 */
public class SpreadConstraint extends AbstractConstraint
{

    Map<Resident, List<Site>> exceptions;

    public SpreadConstraint()
    {
        setImportance(ConstraintImportance.Important);
        exceptions = new HashMap<>();
    }

    public void addException(Resident resident, List<Site> sites)
    {
        if (exceptions.get(resident) == null)
            exceptions.put(resident, new ArrayList<Site>());

        exceptions.get(resident).addAll(sites);
    }

    @Override
    public void postConstraint(Solver solver, AbstractScheduleRequest request, VariablesEntity variables, boolean isKnightShitConstraint)
    {
        int minimumSite = (request.getNumberOfDays() - request.getHolidays().size()) / ((ScheduleRequest) request).getSites().size();
        for (Resident resident : variables.getResidentDateMap().keySet())
        {
            List<Site> list = exceptions.get(resident);

            int[] values = new int[((ScheduleRequest) request).getSites().size()];
            IntVar[] occurances = new IntVar[((ScheduleRequest) request).getSites().size()];

            int i = 0;
            for (int site : ((ScheduleRequest) request).getSiteIds())
            {
                values[i] = site;
                if (list != null && list.contains(((ScheduleRequest) request).getSite(site)))
                    occurances[i] = VariableFactory.fixed(0, solver);
                else
                    occurances[i] = VariableFactory.bounded("criteria " + resident.getName(), minimumSite - 1, minimumSite + 2, solver);
                i++;
            }
            solver.post(IntConstraintFactory.global_cardinality(variables.getResidentDateMap().get(resident).values().toArray(new IntVar[0]), values, occurances, true));
        }

    }

    public boolean isEditable()
    {
        return false;
    }

    public boolean isDeletable()
    {
        return false;
    }

    public String toString()
    {
        return "sites must be uniformly spread among residents";
    }
}
