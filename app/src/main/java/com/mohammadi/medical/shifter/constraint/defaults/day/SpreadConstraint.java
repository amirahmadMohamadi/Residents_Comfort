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
    public void postConstraint(Solver solver, AbstractScheduleRequest request, VariablesEntity variables,
                               boolean isKnightShitConstraint)
    {
        int minimumSite = (request.getNumberOfDays() - request.getHolidays().size())
                / ((ScheduleRequest) request).getSites().size();

        Map<Resident, IntVar[]> map = new HashMap<>();
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
                    occurances[i] = VariableFactory.bounded("criteria " + site + resident.getName(), minimumSite - 2,
                            minimumSite + 3, solver);

                solver.post(IntConstraintFactory.count( site,
                        variables.getResidentDateMap().get(resident).values().toArray(new IntVar[0]), occurances[i]));

                i++;
            }
            map.put(resident, occurances);
//			solver.post(IntConstraintFactory.global_cardinality(
//					variables.getResidentDateMap().get(resident).values().toArray(new IntVar[0]), values, occurances,
//					true));
        }

        Map<Integer, List<IntVar>> map2 = new HashMap<>();

        for (int j = 0; j < ((ScheduleRequest) request).getSiteIds().size(); j++)
        {
            Integer integer = ((ScheduleRequest) request).getSiteIds().get(j);
            if (map2.get(integer) == null)
                map2.put(integer, new ArrayList<IntVar>());
            for (Map.Entry<Resident, IntVar[]> entry : map.entrySet())
            {
                map2.get(integer).add(entry.getValue()[j]);
            }
        }

        IntVar[] max = VariableFactory.integerArray("max", ((ScheduleRequest) request).getSites().size(), 0, 7, solver);
        IntVar[] min = VariableFactory.integerArray("min", ((ScheduleRequest) request).getSites().size(), 0, 7, solver);
        IntVar[] diff = VariableFactory.integerArray("diff", ((ScheduleRequest) request).getSites().size(), 0, 7,
                solver);

        for (int j = 0; j < ((ScheduleRequest) request).getSiteIds().size(); j++)
        {
            Integer siteId = ((ScheduleRequest) request).getSiteIds().get(j);
            solver.post(IntConstraintFactory.maximum(max[j], map2.get(siteId).toArray(new IntVar[0])));
            solver.post(IntConstraintFactory.minimum(min[j], map2.get(siteId).toArray(new IntVar[0])));
            solver.post(IntConstraintFactory.distance(max[j], min[j], "=", diff[j]));

        }

        IntVar integer = VariableFactory.integer("objective", 0, 70, solver);
        solver.post(IntConstraintFactory.sum(diff, integer));
        solver.setObjectives(integer);

        // solver.setObjectives(diff);

    }

    public boolean isEditable()
    {
        return true;
    }

    public boolean isDeletable()
    {
        return false;
    }

    public String toString()
    {
//        return "sites must be uniformly spread among residents";
        return "سایت ها باید متوازن بین رزیدنت ها تقسیم شود.";
    }
}
