package com.mohammadi.medical.shifter.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.explanations.ExplanationFactory;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.search.solution.Solution;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.Random;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.util.ESat;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;

import com.mohammadi.medical.shifter.constraint.AbstractConstraint;
import com.mohammadi.medical.shifter.constraint.ConstraintUtil;
import com.mohammadi.medical.shifter.constraint.FixedDays;
import com.mohammadi.medical.shifter.constraint.defaults.day.SpreadConstraint;
import com.mohammadi.medical.shifter.constraint.defaults.day.SpreadException;
import com.mohammadi.medical.shifter.entities.DaySchedule;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Schedule;

public class Scheduler implements Serializable
{
    Map<AbstractScheduleRequest, Schedule> map;
    List<AbstractScheduleRequest>          requests;

    public Scheduler()
    {
        map = new HashMap<>();
        requests = new ArrayList<>();
    }

    public NightShiftScheduleRequest createNightShiftScheduleRequest(String name)
    {
        NightShiftScheduleRequest request = new NightShiftScheduleRequest(name);
        if (requests.isEmpty())
            request.setId(0);
        else
            request.setId(requests.get(requests.size() - 1).getId() + 1);

        requests.add(request);
        return request;
    }

    public List<AbstractScheduleRequest> getRequests()
    {
        return requests;
    }

    public Map<AbstractScheduleRequest, Schedule> getMap()
    {
        return map;
    }

    public ScheduleRequest createScheduleRequest(String name)
    {
        ScheduleRequest request = new ScheduleRequest(name);
        if (requests.isEmpty())
            request.setId(0);
        else
            request.setId(requests.get(requests.size() - 1).getId() + 1);

        requests.add(request);
        return request;
    }

    public void removeRequest(ScheduleRequest request)
    {
        requests.remove(request);
        map.remove(request);
    }

    public Map<LocalDate, List<Resident>> mainScheduleNightShifts(NightShiftScheduleRequest request) throws SchedulingFailedException
    {

        List<List<AbstractConstraint>> sortedOptionalConstraints = ConstraintUtil
                .getSortedOptionalConstraints(request.getConstraints());

        NightScheduleResult result = new NightScheduleResult();

        while (result.isSolved() == false && sortedOptionalConstraints.isEmpty() == false)
        {
            NightShiftScheduleRequest clone = request.clone();
            clone.getConstraints().removeAll(sortedOptionalConstraints.get(0));
            sortedOptionalConstraints.remove(0);
            result = scheduleNightShifts(clone);
            System.out.println(result.isSolved());
        }

        return result.getShifts();

    }

    public NightScheduleResult scheduleNightShifts(NightShiftScheduleRequest request) throws SchedulingFailedException
    {
        Solver solver = new Solver(request.getName());

        List<IntVar> allVars = new ArrayList<>();

        Map<Integer, Map<Resident, BoolVar>> map = new HashMap<>();
        Map<Resident, Map<Integer, BoolVar>> map2 = new HashMap<>();

        int numberOfDays = request.getNumberOfDays();
        int numberOfWeeks = Weeks.weeksBetween(request.getStartDate(), request.getEndDate()).getWeeks();
        for (int day = 0; day < numberOfDays; day++)
        {
            map.put(day, new HashMap<Resident, BoolVar>());
            for (Resident resident : request.getResidents())
            {
                BoolVar boolVar = VariableFactory.bool("X." + day + "." + resident.getId(), solver);
                allVars.add(boolVar);
                map.get(day).put(resident, boolVar);

                if (map2.get(resident) == null)
                    map2.put(resident, new HashMap<Integer, BoolVar>());
                map2.get(resident).put(day, boolVar);
            }
        }

        // Constraints
        for (AbstractConstraint constraint : request.getConstraints())
        {
            constraint.postConstraint(solver, request, null, true);
        }

        // One resident for each night;
        for (Integer integer : map.keySet())
        {
            solver.post(IntConstraintFactory.sum(map.get(integer).values().toArray(new BoolVar[0]), "=",
                    VariableFactory.fixed(request.getNumberOfShiftsPerNight(), solver)
            ));
        }

        // No consecutive night shifts
        int noShiftPeriod = 4;
        int[] domain = new int[noShiftPeriod - 1];
        for (int i = 0; i < domain.length; i++)
        {
            domain[i] = (1 << (i + 1)) - 1;
        }
        IntVar objective = VariableFactory.enumerated("objective", domain, solver);
        for (int day = 0; day < numberOfDays - noShiftPeriod + 1; day++)
        {
            for (int j = 0; j < request.getResidents().size(); j++)
            {
                Resident resident = request.getResidents().get(j);

                List<BoolVar> vars = new ArrayList<>();
                for (int i = 0; i < noShiftPeriod; i++)
                {
                    vars.add(map.get(day + i).get(resident));
                }
                BoolVar[] array = VariableFactory.boolArray("array", noShiftPeriod, solver);
                solver.post(IntConstraintFactory.bit_channeling(array, objective));

                List<IntVar> productVars = new ArrayList<>();
                int t = 0;
                for (BoolVar boolVar : vars)
                {
                    IntVar intVar = VariableFactory.bool("product " + t, solver);
                    solver.post(IntConstraintFactory.times(boolVar, array[t], intVar));
                    productVars.add(intVar);
                    t++;
                }

                solver.post(IntConstraintFactory.sum(productVars.toArray(new BoolVar[0]), "<=", VariableFactory.fixed(1, solver)));
                // solver.post(IntConstraintFactory.sum(vars.toArray(new BoolVar[0]), "<=", VariableFactory.fixed(1, solver)));
            }
        }

        List<Integer> saturdays = new ArrayList<>();
        for (int day = 0; day < numberOfDays; day++)
        {
            if (request.getStartDate().plusDays(day).getDayOfWeek() == DateTimeConstants.SATURDAY)
            {
                saturdays.add(day);
            }
        }

        for (int day : saturdays)
        {
            for (int j = 0; j < request.getResidents().size(); j++)
            {
                Resident resident = request.getResidents().get(j);

                List<IntVar> weekVars = new ArrayList<>();
                for (int i = 0; i < 7; i++)
                {
                    if (map2.get(resident).get(request.getStartDate().plusDays(day + i)) != null)
                        weekVars.add(map2.get(resident).get(request.getStartDate().plusDays(day + i)));
                }

                solver.post(IntConstraintFactory.sum(weekVars.toArray(new BoolVar[0]), "<=",
                        VariableFactory.fixed(2, solver)
                ));
            }
        }

        // All residents must have equal variety in sites
        int minimumSite = (numberOfDays) / request.getResidents().size();
        for (Resident resident : map2.keySet())
        {
            solver.post(IntConstraintFactory.sum(map2.get(resident).values().toArray(new BoolVar[0]), "=",
                    VariableFactory.bounded("spread", minimumSite, minimumSite + 1, solver)
            ));
        }

        // All residents must have equal variety in holiday sites
        int maximumHolidays = (numberOfWeeks) / request.getResidents().size();
        for (Resident resident : map2.keySet())
        {
            List<BoolVar> preHolidays = new ArrayList<>();
            List<BoolVar> holidays = new ArrayList<>();
            for (Entry<Integer, BoolVar> entry : map2.get(resident).entrySet())
            {
                if (request.getHolidays().contains(request.getStartDate().plusDays(entry.getKey())))
                {
                    holidays.add(entry.getValue());
                    if (request.getStartDate().plusDays(entry.getKey() - 1)
                            .isAfter(request.getStartDate().minusDays(1)))
                        preHolidays.add(map2.get(resident).get(entry.getKey() - 1));
                }
            }
            solver.post(IntConstraintFactory.sum(holidays.toArray(new BoolVar[0]), "<=",
                    VariableFactory.fixed(maximumHolidays + 1, solver)
            ));
            solver.post(IntConstraintFactory.sum(preHolidays.toArray(new BoolVar[0]), "<=",
                    VariableFactory.fixed(maximumHolidays + 1, solver)
            ));
        }

        allVars.add(objective);
        solver.set(
                IntStrategyFactory.custom(new Random<IntVar>(126784L), new IntDomainMin(), allVars.toArray(new IntVar[0])),
                IntStrategyFactory.objective_top_bottom(objective)
        );
        // Chatterbox.showContradiction(solver);

        request.getSettings().applySettings(solver);
        // ExplanationFactory.DBT.plugin(solver, true, false);
        // LNSFactory.rlns(solver, allVars.toArray(new IntVar[0]), 30, 20140909L, new FailCounter(solver, 100));
        solver.findOptimalSolution(ResolutionPolicy.MAXIMIZE, objective);
        boolean isSolved = solver.isSatisfied() == ESat.TRUE;
        // boolean isSolved = solver.findSolution();

        if (isSolved == false)
            throw new SchedulingFailedException();

        NightScheduleResult scheduleResult = new NightScheduleResult();

        Map<LocalDate, List<Resident>> result = new TreeMap<>();
        for (Entry<Integer, Map<Resident, BoolVar>> entry : map.entrySet())
        {
            for (Entry<Resident, BoolVar> entry2 : entry.getValue().entrySet())
            {
                if (entry2.getValue().getValue() == 0)
                    continue;
                if (result.get(request.getStartDate().plusDays(entry.getKey())) == null)
                    result.put(request.getStartDate().plusDays(entry.getKey()), new ArrayList<Resident>());
                result.get(request.getStartDate().plusDays(entry.getKey())).add(entry2.getKey());
            }
        }

        scheduleResult.setShifts(result);
        scheduleResult.setSolved(true);

        System.out.println("objective = " + objective.getValue());
        return scheduleResult;
    }

    public Schedule schedule(final ScheduleRequest request) throws SchedulingFailedException
    {
        final Solver solver = new Solver(request.getName());

        final VariablesEntity variables = createVariables(request, solver);

        // Constraints
        SpreadConstraint spread = null;
        for (AbstractConstraint constraint : request.getConstraints())
        {
            if (constraint instanceof SpreadConstraint)
            {
                spread = (SpreadConstraint) constraint;
                break;
            }
        }

        if (spread != null)
            for (AbstractConstraint constraint : request.getConstraints())
            {
                if (constraint instanceof SpreadException)
                {
                    spread.addException(((SpreadException) constraint).getResident(), ((SpreadException) constraint).getSites());
                }
            }

        for (AbstractConstraint constraint : request.getConstraints())
        {
            constraint.postConstraint(solver, request, variables, false);
        }

        // All sites must be occupied every day by at least one resident
        for (Integer integer : variables.getDateResidentMap().keySet())
        {
            solver.post(IntConstraintFactory.atleast_nvalues(variables.getDateResidentMap().get(integer).values().toArray(new IntVar[0]),
                    VariableFactory.fixed(request.getSites().size(), solver), true
            ));
//            for (Site site: request.getSites())
//            {
//                solver.post(IntConstraintFactory.count(site.getId(), variables.getDateResidentMap().get(integer).values().toArray(new IntVar[0]), VariableFactory.fixed(site.getCapacity(), solver)));
//            }

        }

        List<LocalDate> days = new ArrayList<>();
        for (int i = 0; i < request.getNumberOfDays(); i++)
        {
            if (request.getHolidays().contains(request.getStartDate().plusDays(i)) == false)
                days.add(request.getStartDate().plusDays(i));
        }

        for (Resident resident : variables.getResidentDateMap().keySet())
        {
            for (int i = 0; i < days.size() - 1; i = i + 2)
            {
                LocalDate day = days.get(i);
                Days day1 = Days.daysBetween(request.getStartDate(), day);
                Days day2 = Days.daysBetween(request.getStartDate(), days.get(i + 1));
                if (request.hasConstraint(resident, day) == false)
                {
                    IntConstraintFactory.arithm(variables.getResidentDateMap().get(resident).get(day1.getDays()), "!=",
                            variables.getResidentDateMap().get(resident).get(day2.getDays())
                    );
                }
            }
        }

        // Solve
        solver.set(IntStrategyFactory.custom(new Random<IntVar>(3564564L), new IntDomainMin(), variables.getAllVars().toArray(new IntVar[0])));
        solver.makeCompleteSearch(true);

        // try
        // {
        // solver.propagate();
        // }
        // catch (ContradictionException e)
        // {
        // throw new SchedulingFailedException(e);
        // }
        //
        // solver.getEngine().flush();
        request.getSettings().applySettings(solver);
        // Chatterbox.showContradiction(solver);
        solver.plugMonitor(new IMonitorSolution()
        {

            /**
             *
             */
            private static final long serialVersionUID = -4406771340152130183L;

            @Override
            public void onSolution()
            {
                Solution solution = new Solution();
                solution.record(solver);
                System.out.println(solution.toString(solver));

                Schedule schedule = convertToSchedule(variables.getDateResidentMap(), request);
                // System.out.println(schedule);
                System.out.println(schedule.getNumberMapString());
            }
        });
        ExplanationFactory.DBT.plugin(solver, false, false);
        solver.findOptimalSolution(ResolutionPolicy.MINIMIZE);
        // System.out.println("solution is found = " +
        // solver.findAllSolutions());

        Chatterbox.printStatistics(solver);
        //
        System.out.println(solver.isFeasible());
        System.out.println(solver.isSatisfied());

        if (solver.isSatisfied() == ESat.FALSE)
            throw new SchedulingFailedException();

        Schedule schedule = convertToSchedule(variables.getDateResidentMap(), request);

        System.out.println(schedule.getNumberMapString());
        map.put(request, schedule);

        return schedule;
    }

    /**
     * @return
     */
    private static VariablesEntity createVariables(ScheduleRequest request, Solver solver)
    {
        int numberOfDays = request.getNumberOfDays();

        List<IntVar> allVars = new ArrayList<>();
        Map<Resident, Map<Integer, IntVar>> tempMap = new HashMap<>();
        Map<Integer, Map<Resident, IntVar>> tempMap2 = new HashMap<>();
        for (int day = 0; day < numberOfDays; day++)
        {
            if (request.getHolidays().contains(request.getStartDate().plusDays(day)))
                continue;
            tempMap2.put(day, new HashMap<Resident, IntVar>());
            for (Resident resident : request.getResidents())
            {
                if (tempMap.get(resident) == null)
                    tempMap.put(resident, new HashMap<Integer, IntVar>());

                IntVar boolVar = VariableFactory.enumerated("X." + day + "." + resident.getId(), request.getSiteIdsArray(), solver);
                allVars.add(boolVar);
                tempMap2.get(day).put(resident, boolVar);
                tempMap.get(resident).put(day, boolVar);
            }
        }

        return new VariablesEntity(allVars, tempMap, tempMap2);
    }

    /**
     * @param tempMap
     * @return
     */
    private static Schedule convertToSchedule(Map<Integer, Map<Resident, IntVar>> tempMap, ScheduleRequest request)
    {
        Schedule schedule = new Schedule();

        for (int day = 0; request.getStartDate().plusDays(day).isBefore(request.getEndDate().plusDays(1)); day++)
        {
            LocalDate date = request.getStartDate().plusDays(day);
            if (schedule.getMap().get(date) == null)
            {
                DaySchedule daySchedule = new DaySchedule();
                schedule.getMap().put(date, daySchedule);
            }
            if (tempMap.get(day) != null)
                for (Entry<Resident, IntVar> entry : tempMap.get(day).entrySet())
                {
                    if (schedule.getMap().get(date).getMap().get(request.getSite(entry.getValue().getValue())) == null)
                    {
                        schedule.getMap().get(date).getMap().put(request.getSite(entry.getValue().getValue()), new ArrayList<Resident>());
                    }
                    schedule.getMap().get(date).getMap().get(request.getSite(entry.getValue().getValue())).add(entry.getKey());
                }
        }

        schedule.setSites(request.getSites());
        schedule.setName(request.getName());
        return schedule;
    }

}
