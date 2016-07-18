package com.mohammadi.medical.shifter.entities;

import com.mohammadi.medical.shifter.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joda.time.LocalDate;

import ir.huri.jcal.JalaliCalendar;

public class Schedule implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -4355610000968209464L;
    private List<Site>                     sites;
    private Map<LocalDate, DaySchedule>    map;
    private Map<LocalDate, List<Resident>> nightShifts;
    private String                         name;
    private List<Resident>                 residents;

    public Schedule()
    {
        map = new TreeMap<>();
        nightShifts = new TreeMap<>();
        sites = new ArrayList<>();
    }

    public Map<LocalDate, DaySchedule> getMap()
    {
        return map;
    }

    public void setMap(Map<LocalDate, DaySchedule> map)
    {
        this.map = map;
    }

    public List<Resident> getResidents()
    {
        return residents;
    }

    public void setResidents(List<Resident> residents)
    {
        this.residents = residents;
    }

    public List<Site> getSites()
    {
        return sites;
    }

    public void setSites(List<Site> sites)
    {
        this.sites = sites;
    }

    public Map<LocalDate, List<Resident>> getNightShifts()
    {
        return nightShifts;
    }

    public void setNightShift(Map<LocalDate, List<Resident>> nightShifts)
    {
        this.nightShifts = nightShifts;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Map<Resident, Map<Site, Integer>> getNumberMap()
    {
        Map<Resident, Map<Site, Integer>> numberMap = new TreeMap<>();

        for (Entry<LocalDate, DaySchedule> entry3 : getMap().entrySet())
        {
            for (Entry<Site, List<Resident>> entry : entry3.getValue().getMap().entrySet())
            {
                for (Resident resident : entry.getValue())
                {
                    if (numberMap.get(resident) == null)
                        numberMap.put(resident, new TreeMap<Site, Integer>());
                }
                for (Resident resident : entry.getValue())
                {
                    Integer integer = numberMap.get(resident).get(entry.getKey());
                    if (integer == null)
                        integer = 0;
                    numberMap.get(resident).put(entry.getKey(), integer + 1);
                }
            }
        }

        for (Site site : getSites())
        {
            for (Entry<Resident, Map<Site, Integer>> entry : numberMap.entrySet())
            {
                if (entry.getValue().get(site) == null)
                    entry.getValue().put(site, 0);
            }
        }
        return numberMap;
    }

    @SuppressWarnings("unchecked")
    public String getNumberMapString()
    {
        Map<Resident, Map<Site, Integer>> numberMap = getNumberMap();
        if (numberMap.isEmpty())
            return null;

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-15s", "بخش"));
        sb.append("\t");
        for (Site site : getSites())
        {
            sb.append(String.format("%-10s", site));
            sb.append("\t");

        }

        sb.append(String.format("%n"));
        for (Entry<Resident, Map<Site, Integer>> entry : numberMap.entrySet())
        {
            sb.append(String.format("%-15s", entry.getKey()));
            sb.append("\t");
            for (Site site : getSites())
            {
                sb.append(String.format("%-10d", entry.getValue().get(site)));
                sb.append("\t");
            }
            sb.append(String.format("%n"));
        }

        return sb.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("کشیک ها");
        sb.append("\n");
        for (Entry<LocalDate, List<Resident>> entry : getNightShifts().entrySet())
        {
            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal.setTime(entry.getKey().toDateTimeAtStartOfDay().toDate());
            JalaliCalendar jcal = new JalaliCalendar(tempCal);

            sb.append(jcal + ":" + jcal.getDayOfWeekString() + ":" + Utils.listToString(entry.getValue()) + "\n");
        }
        sb.append("-------------------------------\n");
        Map<Resident, Integer> numberMap = new HashMap<>();
        for (List<Resident> residents : getNightShifts().values())
        {
            for (Resident resident : residents)
            {
                numberMap.put(resident, 0);
            }

        }
        for (Entry<LocalDate, List<Resident>> entry2 : getNightShifts().entrySet())
        {
            for (Resident resident : entry2.getValue())
            {
                numberMap.put(resident, numberMap.get(entry2.getValue()) + 1);
            }

        }

        for (Entry<Resident, Integer> entry3 : numberMap.entrySet())
        {
            sb.append(entry3.getKey() + " : " + entry3.getValue() + "\n");
        }

        sb.append("-------------------------------\n");
        sb.append("شیفت ها");
        sb.append("\n");
        for (Entry<LocalDate, DaySchedule> entry : map.entrySet())
        {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(entry.getKey().toDateTimeAtStartOfDay().toDate());
            JalaliCalendar jcal = new JalaliCalendar(cal);

            sb.append(jcal + "-" + jcal.getDayOfWeekString());
            sb.append(entry.getValue().toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public void replace(LocalDate fromDate, Resident from, Site fromSite, LocalDate toDate, Resident to, Site toSite)
    {
        List<Resident> toResidents = getMap().get(fromDate).getMap().get(fromSite);
        toResidents.remove(from);
        toResidents.add(to);

        toResidents = getMap().get(toDate).getMap().get(toSite);
        toResidents.remove(to);
        toResidents.add(from);
    }

}
