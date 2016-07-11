package com.mohammadi.medical.shifter.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import com.mohammadi.medical.shifter.entities.Site;

import java.io.Serializable;

/**
 * Created by amirahmad on 16/6/15 AD.
 */
public class NightShiftScheduleRequest extends AbstractScheduleRequest
{

    int  numberOfShiftsPerNight;

    Site onNightShiftSite;
    Site onPostNightShiftSite;

    public NightShiftScheduleRequest(String name)
    {
        super(name);
        setNumberOfShiftsPerNight(1);
    }

    public NightShiftScheduleRequest(Parcel in)
    {
        super(in);
        setNumberOfShiftsPerNight(in.readInt());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        super.writeToParcel(parcel, i);
        parcel.writeInt(numberOfShiftsPerNight);
    }

    public static final Parcelable.Creator<NightShiftScheduleRequest> CREATOR = new Parcelable.Creator<NightShiftScheduleRequest>()
    {
        @Override
        public NightShiftScheduleRequest createFromParcel(Parcel in)
        {
            return new NightShiftScheduleRequest(in);
        }

        @Override
        public NightShiftScheduleRequest[] newArray(int size)
        {
            return new NightShiftScheduleRequest[size];
        }
    };

    public int getNumberOfShiftsPerNight()
    {
        return numberOfShiftsPerNight;
    }

    public void setNumberOfShiftsPerNight(int numberOfShiftsPerNight)
    {
        this.numberOfShiftsPerNight = numberOfShiftsPerNight;
    }

    public Site getOnNightShiftSite()
    {
        return onNightShiftSite;
    }

    public void setOnNightShiftSite(Site onNightShiftSite)
    {
        this.onNightShiftSite = onNightShiftSite;
    }

    public Site getOnPostNightShiftSite()
    {
        return onPostNightShiftSite;
    }

    public void setOnPostNightShiftSite(Site onPostNightShiftSite)
    {
        this.onPostNightShiftSite = onPostNightShiftSite;
    }

    @Override
    public NightShiftScheduleRequest clone()
    {
        return (NightShiftScheduleRequest) super.clone();
    }

}
