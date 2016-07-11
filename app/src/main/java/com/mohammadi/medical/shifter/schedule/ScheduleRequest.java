package com.mohammadi.medical.shifter.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mohammadi.medical.shifter.constraint.AbstractConstraint;
import com.mohammadi.medical.shifter.constraint.defaults.day.SpreadConstraint;
import com.mohammadi.medical.shifter.entities.Site;

public class ScheduleRequest extends AbstractScheduleRequest
{
    private List<Site>              sites;
    private NightShiftScheduleRequest nightShiftScheduleRequest;

    public ScheduleRequest(String name)
    {
        super(name);
        sites = new ArrayList<>();
        nightShiftScheduleRequest = null;

        addDefaultConstraint();
    }

    protected ScheduleRequest(Parcel in)
    {
        super(in);
        setSites((List<Site>) in.readSerializable());
        setNightShiftScheduleRequest((NightShiftScheduleRequest) in.readParcelable(AbstractScheduleRequest.class.getClassLoader()));
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        super.writeToParcel(parcel, i);
        parcel.writeSerializable((Serializable) sites);
        parcel.writeParcelable(nightShiftScheduleRequest, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
    }

    public static final Creator<ScheduleRequest> CREATOR = new Creator<ScheduleRequest>()
    {
        @Override
        public ScheduleRequest createFromParcel(Parcel in)
        {
            return new ScheduleRequest(in);
        }

        @Override
        public ScheduleRequest[] newArray(int size)
        {
            return new ScheduleRequest[size];
        }
    };

    public void addNightShiftScheduleRequest()
    {
        setNightShiftScheduleRequest(new NightShiftScheduleRequest(getName() + " night"));
    }

    private void addDefaultConstraint()
    {
        AbstractConstraint spread = new SpreadConstraint();
        spread.setId(0);
        addConstraint(spread);
    }

    public Site createSite(String name, int capacity)
    {
        Site site = new Site(name, capacity);
        if (sites.isEmpty())
            site.setId(0);
        else
            site.setId(sites.get(sites.size() - 1).getId() + 1);
        sites.add(site);
        return site;
    }

    public Site createSite(String name)
    {
        Site site = new Site(name);
        if (sites.isEmpty())
            site.setId(0);
        else
            site.setId(sites.get(sites.size() - 1).getId() + 1);
        sites.add(site);
        return site;
    }

    public void removeSite(Site site)
    {
        sites.remove(site);
    }

    public List<Site> getSites()
    {
        return sites;
    }

    public Site getSite(int siteId)
    {
        for (Site site : sites) {
            if (site.getId() == siteId)
                return site;
        }
        return null;
    }

    public List<Integer> getSiteIds()
    {
        List<Integer> list = new ArrayList<>();

        for (Site site : sites) {
            list.add(site.getId());
        }

        return list;
    }

    public int[] getSiteIdsArray()
    {
        int[] list = new int[sites.size()];

        for (int i = 0; i < sites.size(); i++) {
            Site site = sites.get(i);
            list[i] = site.getId();
        }

        return list;
    }

    public void setSites(List<Site> sites)
    {
        this.sites = sites;
    }

    public NightShiftScheduleRequest getNightShiftScheduleRequest()
    {
        return nightShiftScheduleRequest;
    }

    public void setNightShiftScheduleRequest(NightShiftScheduleRequest nightShiftScheduleRequest)
    {
        this.nightShiftScheduleRequest = nightShiftScheduleRequest;
    }

    public void fillNightShiftScheduleRequest()
    {
        getNightShiftScheduleRequest().setPeriod(getStartDate(), getEndDate());
        getNightShiftScheduleRequest().getHolidays().addAll(getHolidays());
        getNightShiftScheduleRequest().getResidents().addAll(getResidents());
        getNightShiftScheduleRequest().setSettings(getSettings().clone());
    }

    @Override
    public ScheduleRequest clone()
    {
        ScheduleRequest request = (ScheduleRequest) super.clone();

        request.sites = new ArrayList<>();
        for (Site site : sites) {
            request.sites.add(site.clone());
        }

        return request;
    }

}
