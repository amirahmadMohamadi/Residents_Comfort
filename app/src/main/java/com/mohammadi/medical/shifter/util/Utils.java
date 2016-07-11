package com.mohammadi.medical.shifter.util;

import android.os.Environment;

import java.util.List;

/**
 * Created by amirahmad on 16/6/5 AD.
 */
public class Utils
{
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            return true;
        }
        return false;
    }

    public static String listToString(List<?> list)
    {
        if (list == null)
            return "";
        return list.toString().substring(1, list.toString().length() - 1);
    }

}
