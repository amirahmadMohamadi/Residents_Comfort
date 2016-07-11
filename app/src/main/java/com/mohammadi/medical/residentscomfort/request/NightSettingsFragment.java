package com.mohammadi.medical.residentscomfort.request;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NightSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NightSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NightSettingsFragment extends Fragment
{
    private static final String ARG_REQUEST = "request";

    private ScheduleRequest request;

    ListView residentsSelectList;

    private OnFragmentInteractionListener mListener;

    public NightSettingsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment NightSettingsFragment.
     */
    public static NightSettingsFragment newInstance(ScheduleRequest param1)
    {
        NightSettingsFragment fragment = new NightSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REQUEST, (Serializable) param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            request = (ScheduleRequest) getArguments().getSerializable(ARG_REQUEST);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_night_settings, container, false);

        residentsSelectList = (ListView) view.findViewById(R.id.residentSelectList);
        residentsSelectList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        residentsSelectList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, request.getResidents()));

        for (Resident resident : request.getNightShiftScheduleRequest().getResidents())
        {
            residentsSelectList.setItemChecked(request.getResidents().indexOf(resident), true);
        }

        residentsSelectList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (residentsSelectList.isItemChecked(position))
                    request.getNightShiftScheduleRequest().getResidents().add(request.getResidents().get(position));
                else
                    request.getNightShiftScheduleRequest().getResidents().remove(request.getResidents().get(position));
            }
        });

        TextView numberText = (TextView) view.findViewById(R.id.residentNumberText);
        numberText.setText(String.valueOf(request.getNightShiftScheduleRequest().getNumberOfShiftsPerNight()));
        numberText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                try
                {
                    request.getNightShiftScheduleRequest().setNumberOfShiftsPerNight(Integer.parseInt(s.toString()));
                }
                catch (NumberFormatException e)
                {

                }
            }
        });

        final Spinner siteSpinner1 = (Spinner) view.findViewById(R.id.spinner);
        siteSpinner1.setAdapter(new ArrayAdapter<Site>(getActivity(), android.R.layout.simple_spinner_dropdown_item, request.getSites()));
        if (request.getNightShiftScheduleRequest().getOnNightShiftSite() != null)
            siteSpinner1.setSelection(request.getSites().indexOf(request.getNightShiftScheduleRequest().getOnNightShiftSite()));

        final Spinner siteSpinner2 = (Spinner) view.findViewById(R.id.spinner2);
        siteSpinner2.setAdapter(new ArrayAdapter<Site>(getActivity(), android.R.layout.simple_spinner_dropdown_item, request.getSites()));
        if (request.getNightShiftScheduleRequest().getOnPostNightShiftSite() != null)
            siteSpinner2.setSelection(request.getSites().indexOf(request.getNightShiftScheduleRequest().getOnPostNightShiftSite()));

        final Switch nightShiftSwitch = (Switch) view.findViewById(R.id.nighShiftSwitch);
        nightShiftSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked == false)
                {
                    request.getNightShiftScheduleRequest().setOnNightShiftSite(null);
                    siteSpinner1.setVisibility(View.GONE);
                }
                else
                {
                    siteSpinner1.setVisibility(View.VISIBLE);
                    request.getNightShiftScheduleRequest().setOnNightShiftSite((Site) siteSpinner1.getSelectedItem());
                }
            }
        });
        siteSpinner1.setVisibility(View.GONE);
        nightShiftSwitch.setChecked(request.getNightShiftScheduleRequest().getOnNightShiftSite() != null);
        final Switch postNightShiftSwitch = (Switch) view.findViewById(R.id.posstNightShiftSwitch);

        postNightShiftSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked == false)
                {
                    siteSpinner2.setVisibility(View.GONE);
                    request.getNightShiftScheduleRequest().setOnPostNightShiftSite(null);
                }
                else
                {
                    siteSpinner2.setVisibility(View.VISIBLE);
                    request.getNightShiftScheduleRequest().setOnPostNightShiftSite((Site) siteSpinner2.getSelectedItem());
                }
            }
        });
        postNightShiftSwitch.setChecked(request.getNightShiftScheduleRequest().getOnPostNightShiftSite() != null);
        siteSpinner2.setVisibility(View.GONE);

        siteSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (nightShiftSwitch.isChecked())
                    request.getNightShiftScheduleRequest().setOnNightShiftSite((Site) siteSpinner1.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        siteSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (postNightShiftSwitch.isChecked())
                    request.getNightShiftScheduleRequest().setOnPostNightShiftSite((Site) siteSpinner2.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

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
