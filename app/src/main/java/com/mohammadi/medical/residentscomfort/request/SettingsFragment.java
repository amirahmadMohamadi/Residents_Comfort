package com.mohammadi.medical.residentscomfort.request;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;
import com.mohammadi.medical.shifter.util.DateUtil;

import ir.huri.jcal.JalaliCalendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements IFabFragment
{
    private static final String ARG_REQUEST = "request";

    private AbstractScheduleRequest request;

    private ListView holidaysList;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment()
    {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(AbstractScheduleRequest request)
    {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REQUEST, request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            request = (AbstractScheduleRequest) getArguments().getSerializable(ARG_REQUEST);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.request_settings, container, false);

        TextView nameTextEdit = (TextView) view.findViewById(R.id.name_text);
        nameTextEdit.setText(request.getName());
        nameTextEdit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                request.setName(editable.toString());
            }
        });

        final Button startDateButton = (Button) view.findViewById(R.id.start_date_button);
        final Button endDateButton = (Button) view.findViewById(R.id.end_date_button);
        startDateButton.setText(new JalaliCalendar(request.getStartDate()).toString());
        endDateButton.setText(new JalaliCalendar(request.getEndDate()).toString());
        startDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PersianCalendar now = new PersianCalendar();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                            {
                                JalaliCalendar startDate = new JalaliCalendar(year, monthOfYear + 1, dayOfMonth);
                                request.setStartDate(startDate.toLocalDate());
                                startDateButton.setText(startDate.toString());
                                request.setHolidays(DateUtil.getHolidaysBetween(request.getStartDate(), request.getEndDate()));
                                if (request instanceof ScheduleRequest && ((ScheduleRequest) request).getNightShiftScheduleRequest() != null)
                                {
                                    ((ScheduleRequest) request).getNightShiftScheduleRequest().setStartDate(startDate.toLocalDate());
                                    ((ScheduleRequest) request).getNightShiftScheduleRequest().setHolidays(request.getHolidays());
                                }
                                ((HolidayArrayAdapter) holidaysList.getAdapter()).notifyDataSetChanged();
                            }
                        },
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay()
                );
                dpd.setThemeDark(false);
                dpd.show(getActivity().getFragmentManager(), "DatePickerDialog");

            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PersianCalendar now = new PersianCalendar();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                            {
                                JalaliCalendar endDate = new JalaliCalendar(year, monthOfYear + 1, dayOfMonth);
                                request.setEndDate(endDate.toLocalDate());
                                endDateButton.setText(endDate.toString());
                                request.setHolidays(DateUtil.getHolidaysBetween(request.getStartDate(), request.getEndDate()));
                                if (request instanceof ScheduleRequest && ((ScheduleRequest) request).getNightShiftScheduleRequest() != null)
                                {
                                    ((ScheduleRequest) request).getNightShiftScheduleRequest().setEndDate(endDate.toLocalDate());
                                    ((ScheduleRequest) request).getNightShiftScheduleRequest().setHolidays(request.getHolidays());
                                }

                                ((HolidayArrayAdapter) holidaysList.getAdapter()).notifyDataSetChanged();
                            }
                        },
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay()
                );
                dpd.setThemeDark(false);
                dpd.show(getActivity().getFragmentManager(), "DatePickerDialog");

            }
        });

        holidaysList = (ListView) view.findViewById(R.id.holidays_list);
        holidaysList.setAdapter(new HolidayArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1));
        registerForContextMenu(holidaysList);

        return view;
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == holidaysList.getId())
            getActivity().getMenuInflater().inflate(R.menu.holiday_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        if (item.getItemId() == R.id.holiday_delete)
        {
            request.getHolidays().remove(position);
//            if (request instanceof ScheduleRequest && ((ScheduleRequest) request).getNightShiftScheduleRequest()!= null)
//            {
//                ((ScheduleRequest) request).getNightShiftScheduleRequest().getHolidays().remove(position);
//            }
            ((HolidayArrayAdapter) holidaysList.getAdapter()).notifyDataSetChanged();
        }

        return super.onContextItemSelected(item);

    }

    @Override
    public void configFab(FloatingActionButton requestFab)
    {
        requestFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                PersianCalendar now = new PersianCalendar();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                            {
                                JalaliCalendar holiday = new JalaliCalendar(year, monthOfYear + 1, dayOfMonth);
                                request.getHolidays().add(holiday.toLocalDate());
//                                if (request instanceof ScheduleRequest && ((ScheduleRequest) request).getNightShiftScheduleRequest()!= null)
//                                {
//                                    ((ScheduleRequest) request).getNightShiftScheduleRequest().getHolidays().add(holiday.toLocalDate());
//                                }
                                ((HolidayArrayAdapter) holidaysList.getAdapter()).notifyDataSetChanged();
                                Snackbar.make(getView(), "Holiday created.", Snackbar.LENGTH_LONG).show();

                            }
                        },
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay()
                );
                dpd.setThemeDark(false);
                dpd.show(getActivity().getFragmentManager(), "DatePickerDialog");
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

    private class HolidayArrayAdapter extends ArrayAdapter<JalaliCalendar>
    {

        public HolidayArrayAdapter(Context context, int textViewResourceId)
        {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount()
        {
            return request.getHolidays().size();
        }

        @Override
        public JalaliCalendar getItem(int position)
        {
            return new JalaliCalendar(request.getHolidays().get(position));
        }


    }

}
