package com.mohammadi.medical.residentscomfort.result;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;
import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.residentscomfort.request.IFabFragment;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Schedule;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;
import com.mohammadi.medical.shifter.util.PdfUtil;
import com.mohammadi.medical.shifter.util.Utils;

import org.joda.time.LocalDate;
import org.joda.time.ReadableDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ir.huri.jcal.JalaliCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayScheduleFragment extends Fragment implements IFabFragment
{
    private static final String ARG_SCHEDULE = "schedule";

    private Schedule schedule;
    Map<Button, Pair<LocalDate, Site>> buttonPairMap;

    private OnFragmentInteractionListener mListener;

    public DayScheduleFragment()
    {
        // Required empty public constructor
        buttonPairMap = new HashMap<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DayScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DayScheduleFragment newInstance(Schedule param1)
    {
        DayScheduleFragment fragment = new DayScheduleFragment();
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
        View view = inflater.inflate(R.layout.fragment_day_schedule, container, false);

        fillScheduleTable(view);

        return view;
    }

    private void fillScheduleTable(View view)
    {
        if (getSchedule().getSites().isEmpty())
            return;

        TableLayout table = (TableLayout) view.findViewById(R.id.scheduleTable);

        TableRow row = new TableRow(getActivity());
        TextView text = new TextView(getActivity());
        text.setText("    ");
        row.addView(text);
        for (Site site : getSchedule().getSites())
        {
            text = new TextView(getActivity());
            text.setText(site.toString());
            text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            row.addView(text);
        }
        table.addView(row);

        Set<LocalDate> localDates = getSchedule().getMap().keySet();
        if (localDates.isEmpty())
            return;

        LocalDate startDate = localDates.toArray(new LocalDate[0])[0];
        LocalDate endDate = localDates.toArray(new LocalDate[0])[localDates.size() - 1];


        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1))
        {
            row = new TableRow(getActivity());

            text = new TextView(getActivity());
            text.setText(new JalaliCalendar(date).toString());
            row.addView(text);

            if (getSchedule().getMap().get(date) != null)
                for (Site site : getSchedule().getSites())
                {
                    Button button = new Button(getActivity());
                    button.setBackgroundColor(ContextCompat.getColor(button.getContext(), android.R.color.transparent));
                    final LocalDate date2 = date;
                    final Site site2 = site;
                    final Button button2 = button;

                    button.setOnLongClickListener(new View.OnLongClickListener()
                    {

                        @Override
                        public boolean onLongClick(View v)
                        {
                            showEditDialog(date2, site2, button2);
                            return true;
                        }
                    });
                    buttonPairMap.put(button, new Pair<>(date, site));
                    button.setText(Utils.listToString(getSchedule().getMap().get(date).getMap().get(site)));
                    row.addView(button);
                }

            table.addView(row);
        }


    }

    private void showEditDialog(final LocalDate date, final Site site, final Button button)
    {
        final View promptView = getActivity().getLayoutInflater().inflate(R.layout.resident_select, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final ListView listView = (ListView) promptView.findViewById(R.id.listView2);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, getSchedule().getResidents()));
        for (Resident resident : getSchedule().getMap().get(date).getMap().get(site))
        {
            listView.setItemChecked(getSchedule().getResidents().indexOf(resident), true);
        }


        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        List<Resident> residents = new ArrayList<>();
                        SparseBooleanArray positions = listView.getCheckedItemPositions();
                        for (int i = 0; i < positions.size(); i++)
                        {
                            int key = positions.keyAt(i);
                            Object obj = positions.get(key);
                            if (obj.equals(Boolean.TRUE))
                                residents.add(getSchedule().getResidents().get(key));
                        }
                        getSchedule().getMap().get(date).getMap().put(site, residents);
                        button.setText(Utils.listToString(residents));
                    }

                })
                .setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        }
                );

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

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

    @Override
    public void configFab(FloatingActionButton resultFab)
    {
        resultFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    PdfUtil.writeSchedule(getSchedule());
                    Toast.makeText(getContext(), "saved in " + schedule.getName() + ".pdf", Toast.LENGTH_LONG).show();
                }
                catch (DocumentException | IOException e)
                {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
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
