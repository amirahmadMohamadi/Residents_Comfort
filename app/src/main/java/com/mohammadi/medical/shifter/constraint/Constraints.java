package com.mohammadi.medical.shifter.constraint;

import android.content.res.Resources;

import com.mohammadi.medical.residentscomfort.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amirahmad on 16/7/7 AD.
 */
public enum Constraints
{
    FixedShift("محدودیت روزانه", true, true),
    FixedDays("محدودیت دوره ای", true, true),
    OffDays("مرخصی", true, false),
    Spread("محدودیت کلی", true, false);

    private final String  name;
    private       boolean isDaySupported;
    private       boolean isNightSupported;

    private Constraints(String name, boolean isDaySupported, boolean isNightSupported)
    {
        this.name = name;
        this.isDaySupported = isDaySupported;
        this.isNightSupported = isNightSupported;
    }

    public boolean isDaySupported()
    {
        return isDaySupported;
    }

    public boolean isNightSupported()
    {
        return isNightSupported;
    }

    public static List<Constraints> getDaySupportedConstraints()
    {
        List<Constraints> list = new ArrayList<>();
        for (Constraints constraints : Constraints.values())
        {
            if (constraints.isDaySupported())
                list.add(constraints);
        }

        return list;
    }

    public static List<Constraints> getNightSupportedConstraints()
    {
        List<Constraints> list = new ArrayList<>();
        for (Constraints constraints : Constraints.values())
        {
            if (constraints.isNightSupported())
                list.add(constraints);
        }

        return list;
    }

    @Override
    public String toString()
    {
        return name;
    }

}
