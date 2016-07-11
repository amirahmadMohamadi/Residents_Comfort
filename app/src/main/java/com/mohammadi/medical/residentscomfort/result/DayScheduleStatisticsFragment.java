package com.mohammadi.medical.residentscomfort.result;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Schedule;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.util.Utils;

import org.joda.time.LocalDate;

import java.util.Map;
import java.util.Set;

import ir.huri.jcal.JalaliCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayScheduleStatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayScheduleStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayScheduleStatisticsFragment extends Fragment
{
    private static final String ARG_SCHEDULE = "schedule";

    private Schedule schedule;

    private OnFragmentInteractionListener mListener;

    public DayScheduleStatisticsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DayScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DayScheduleStatisticsFragment newInstance(Schedule param1)
    {
        DayScheduleStatisticsFragment fragment = new DayScheduleStatisticsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCHEDULE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            schedule = (Schedule) getArguments().getSerializable(ARG_SCHEDULE);
        }
    }

    public Schedule getSchedule()
    {
        return schedule;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day_schedule_statistics, container, false);
        fillStatisticsTable(view);

        return view;
    }

    private void fillStatisticsTable(View view)
    {
        if (getSchedule().getSites().isEmpty())
            return;

        TableLayout table = (TableLayout) view.findViewById(R.id.statisticsTable);

        TableRow row = new TableRow(getActivity());
        TextView text = new TextView(getActivity());
        text.setText("    ");
        row.addView(text);
        for (Site site : getSchedule().getSites())
        {
            text = new TextView(getActivity());
            text.setText(site.toString());
            row.addView(text);
        }
        table.addView(row);

        for (Map.Entry<Resident, Map<Site, Integer>> entry : getSchedule().getNumberMap().entrySet())
        {
            row = new TableRow(getActivity());

            text = new TextView(getActivity());
            text.setText(entry.getKey().toString());
            row.addView(text);

            for (Site site : getSchedule().getSites())
            {
                text = new TextView(getActivity());
                Integer integer = entry.getValue().get(site);
                text.setText(integer == null ? "0" : integer.toString());
                row.addView(text);
            }

            table.addView(row);
        }

    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
    }
}
