package com.mohammadi.medical.residentscomfort.request;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.shifter.constraint.AbstractConstraint;
import com.mohammadi.medical.shifter.constraint.Constraints;
import com.mohammadi.medical.shifter.constraint.FixedDays;
import com.mohammadi.medical.shifter.constraint.FixedShift;
import com.mohammadi.medical.shifter.constraint.OffDays;
import com.mohammadi.medical.shifter.constraint.PersonalConstraint;
import com.mohammadi.medical.shifter.constraint.defaults.day.SpreadException;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Site;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import ir.huri.jcal.JalaliCalendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateConstraintFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateConstraintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateConstraintFragment extends AppCompatDialogFragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RESIDENT_LIST = "residentList";
    private static final String SITE_LIST     = "siteList";
    private static final String CONSTRAINT    = "constraint";
    private static final String DAY           = "day";

    // TODO: Rename and change types of parameters
    private List<Resident>     residents;
    private List<Site>         sites;
    private PersonalConstraint constraint;
    private boolean            isDay;

    private OnFragmentInteractionListener mListener;

    private Spinner   constraintsSpinner;
    private Spinner   residentsSpinner;
    private Spinner   sitesSpinner;
    private ListView  siteSelectionList;
    private CheckBox  offCheckBox;
    private Button    endDateButton;
    private Button    startDateButton;
    private TextView  startDateLabel;
    private TextView  endDateLabel;
    private LocalDate startDate;
    private LocalDate endDate;

    public CreateConstraintFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateConstraintFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateConstraintFragment newInstance(List<Resident> param1, List<Site> param2, AbstractConstraint constraint, boolean isDay)
    {
        CreateConstraintFragment fragment = new CreateConstraintFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESIDENT_LIST, (Serializable) param1);
        args.putSerializable(SITE_LIST, (Serializable) param2);
        args.putSerializable(CONSTRAINT, constraint);
        args.putBoolean(DAY, isDay);
        fragment.setArguments(args);
        return fragment;
    }

    public List<Site> getSites()
    {
        return sites;
    }

    public List<Resident> getResidents()
    {
        return residents;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            residents = (List<Resident>) getArguments().getSerializable(RESIDENT_LIST);
            sites = (List<Site>) getArguments().getSerializable(SITE_LIST);
            constraint = (PersonalConstraint) getArguments().getSerializable(CONSTRAINT);
            isDay = getArguments().getBoolean(DAY);
        }
    }

    public LocalDate getEndDate()
    {
        return endDate;
    }

    public void setEndDate(LocalDate endDate)
    {
        this.endDate = endDate;
    }

    public LocalDate getStartDate()
    {
        return startDate;
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
    }

    public PersonalConstraint getConstraint()
    {
        return constraint;
    }

    private void initComponents(View view)
    {
        final TextView siteLabel = (TextView) view.findViewById(R.id.textView4);
        final TextView residentLabel = (TextView) view.findViewById(R.id.residentLabel);
        residentsSpinner = (Spinner) view.findViewById(R.id.residentSpinner);
        sitesSpinner = (Spinner) view.findViewById(R.id.siteSpinner);
        constraintsSpinner = (Spinner) view.findViewById(R.id.constraintSpinner);
        constraintsSpinner.setAdapter(new ArrayAdapter<>(getParentFragment().getActivity(), android.R.layout.simple_spinner_dropdown_item, isDay ? Constraints.getDaySupportedConstraints() : Constraints.getNightSupportedConstraints()));
        constraintsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                switch ((Constraints) adapterView.getSelectedItem())
                {
                    case FixedShift:
                    {
                        if (isDay)
                        {
                            sitesSpinner.setVisibility(View.GONE);
                            siteSelectionList.setVisibility(View.VISIBLE);
                            siteLabel.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            sitesSpinner.setVisibility(View.GONE);
                            siteSelectionList.setVisibility(View.GONE);
                            siteLabel.setVisibility(View.GONE);
                        }
                        startDateLabel.setText(getString(R.string.date));
                        endDateLabel.setVisibility(View.GONE);
                        endDateButton.setVisibility(View.GONE);
                        offCheckBox.setVisibility(View.VISIBLE);
                        break;
                    }
                    case FixedDays:
                    {
                        if (isDay)
                        {
                            sitesSpinner.setVisibility(View.VISIBLE);
                            siteSelectionList.setVisibility(View.GONE);
                            siteLabel.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            sitesSpinner.setVisibility(View.GONE);
                            siteSelectionList.setVisibility(View.GONE);
                            siteLabel.setVisibility(View.GONE);
                        }
                        startDateLabel.setText(getString(R.string.createDialogStartDateText));
                        endDateLabel.setVisibility(View.VISIBLE);
                        endDateButton.setVisibility(View.VISIBLE);
                        offCheckBox.setVisibility(View.GONE);

                        break;

                    }
                    case OffDays:
                    {
                        if (isDay)
                        {
                            sitesSpinner.setVisibility(View.GONE);
                            siteSelectionList.setVisibility(View.GONE);
                            siteLabel.setVisibility(View.GONE);
                        }
                        else
                        {
                            sitesSpinner.setVisibility(View.GONE);
                            siteSelectionList.setVisibility(View.GONE);
                            siteLabel.setVisibility(View.GONE);
                        }
                        startDateLabel.setText(getString(R.string.createDialogStartDateText));
                        endDateLabel.setVisibility(View.VISIBLE);
                        endDateButton.setVisibility(View.VISIBLE);
                        offCheckBox.setVisibility(View.GONE);

                        break;
                    }
                    case Spread:
                    {
                        if (isDay)
                        {
                            sitesSpinner.setVisibility(View.GONE);
                            siteSelectionList.setVisibility(View.VISIBLE);
                            siteLabel.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            sitesSpinner.setVisibility(View.GONE);
                            siteSelectionList.setVisibility(View.GONE);
                            siteLabel.setVisibility(View.GONE);
                        }
                        startDateLabel.setVisibility(View.GONE);
                        startDateButton.setVisibility(View.GONE);
                        endDateLabel.setVisibility(View.GONE);
                        endDateButton.setVisibility(View.GONE);
                        offCheckBox.setVisibility(View.GONE);

                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        siteSelectionList = (ListView) view.findViewById(R.id.siteSelectionList);
        siteSelectionList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        siteSelectionList.setAdapter(new ArrayAdapter<>(getParentFragment().getActivity(), android.R.layout.simple_list_item_multiple_choice, getSites()));
        offCheckBox = (CheckBox) view.findViewById(R.id.offCheckBox);

        residentsSpinner.setAdapter(new ArrayAdapter<>(getParentFragment().getActivity(), android.R.layout.simple_spinner_dropdown_item, getResidents()));
        sitesSpinner.setAdapter(new ArrayAdapter<>(getParentFragment().getActivity(), android.R.layout.simple_spinner_dropdown_item, getSites()));

        startDateButton = (Button) view.findViewById(R.id.startDateButton);
        startDateButton.setText(new JalaliCalendar(new GregorianCalendar()).toString());
        setStartDate(new JalaliCalendar(new GregorianCalendar()).toLocalDate());
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
                                JalaliCalendar jalaliCalendar = new JalaliCalendar(year, monthOfYear + 1, dayOfMonth);
                                setStartDate(jalaliCalendar.toLocalDate());
                                startDateButton.setText(jalaliCalendar.toString());
                            }
                        },
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay()
                );
                dpd.setThemeDark(false);
                dpd.show(getParentFragment().getActivity().getFragmentManager(), "DatePickerDialog");

            }
        });
        endDateButton = (Button) view.findViewById(R.id.endDateButton);
        endDateButton.setText(new JalaliCalendar(new GregorianCalendar()).toString());
        setEndDate(new JalaliCalendar(new GregorianCalendar()).toLocalDate());
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
                                JalaliCalendar jalaliCalendar = new JalaliCalendar(year, monthOfYear + 1, dayOfMonth);
                                setEndDate(jalaliCalendar.toLocalDate());
                                endDateButton.setText(jalaliCalendar.toString());
                            }
                        },
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay()
                );
                dpd.setThemeDark(false);
                dpd.show(getParentFragment().getActivity().getFragmentManager(), "DatePickerDialog");

            }
        });
        startDateLabel = (TextView) view.findViewById(R.id.startDateLabel);
        endDateLabel = (TextView) view.findViewById(R.id.endDateLabel);

    }

    private void initRuntimeComponent(View view)
    {

        constraintsSpinner.setSelection(constraint.getType().ordinal());
        switch (constraint.getType())
        {

            case FixedShift:
                residentsSpinner.setSelection(residents.indexOf(constraint.getResident()));
                for (Site site : ((FixedShift) constraint).getSites())
                    siteSelectionList.setItemChecked(sites.indexOf(site), true);
                setStartDate(((FixedShift) constraint).getDate());
                startDateButton.setText(new JalaliCalendar(getStartDate()).toString());
                break;
            case FixedDays:
                residentsSpinner.setSelection(residents.indexOf(constraint.getResident()));
                sitesSpinner.setSelection(sites.indexOf(((FixedDays) constraint).getSite()));
                setStartDate(((FixedDays) constraint).getStart());
                startDateButton.setText(new JalaliCalendar(getStartDate()).toString());
                setEndDate(((FixedDays) constraint).getEnd());
                endDateButton.setText(new JalaliCalendar(getEndDate()).toString());
                break;
            case OffDays:
                residentsSpinner.setSelection(residents.indexOf(constraint.getResident()));
                setStartDate(((OffDays) constraint).getStart());
                startDateButton.setText(new JalaliCalendar(getStartDate()).toString());
                setEndDate(((OffDays) constraint).getEnd());
                endDateButton.setText(new JalaliCalendar(getEndDate()).toString());
                break;
            case Spread:
                residentsSpinner.setSelection(residents.indexOf(constraint.getResident()));
                for (Site site : ((SpreadException) constraint).getSites())
                    siteSelectionList.setItemChecked(sites.indexOf(site), true);
                break;
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = getParentFragment().getActivity().getLayoutInflater().inflate(R.layout.fragment_create_constraint, null);
        initComponents(view);
        if (constraint != null)
            initRuntimeComponent(view);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getParentFragment().getActivity());
        alertDialogBuilder.setView(view);

        // setup a dialog window
        alertDialogBuilder.setTitle(getString(R.string.create_constraint));
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        AbstractConstraint abstractConstraint = createConstraint();
                        if (constraint != null)
                            abstractConstraint.setId(constraint.getId());
                        if (mListener != null)
                        {
                            if (getConstraint() != null)
                                mListener.editConstraint(abstractConstraint);
                            else
                                mListener.createConstraint(abstractConstraint);
                        }

                    }
                })
                .setNegativeButton(
                        getActivity().getString(R.string.cancel),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        }
                );

        // create an alert dialog
        return alertDialogBuilder.create();

    }

    private AbstractConstraint createConstraint()
    {
        AbstractConstraint constraint = null;

        switch ((Constraints) constraintsSpinner.getSelectedItem())
        {
            case FixedShift:
                List<Site> sites = new ArrayList<>();

                SparseBooleanArray positions = siteSelectionList.getCheckedItemPositions();
                for (int i = 0; i < positions.size(); i++)
                {
                    int key = positions.keyAt(i);
                    Object obj = positions.get(key);
                    if (obj.equals(Boolean.TRUE))
                        sites.add(getSites().get(key));
                }
                constraint = new FixedShift(getStartDate(), (Resident) residentsSpinner.getSelectedItem(), sites, offCheckBox.isChecked());
                break;
            case FixedDays:
                constraint = new FixedDays(getStartDate(), getEndDate(), (Resident) residentsSpinner.getSelectedItem(), (Site) sitesSpinner.getSelectedItem());
                break;
            case OffDays:
                constraint = new OffDays(getStartDate(), getEndDate(), (Resident) residentsSpinner.getSelectedItem());
                break;
            case Spread:
                sites = new ArrayList<>();

                positions = siteSelectionList.getCheckedItemPositions();
                for (int i = 0; i < positions.size(); i++)
                {
                    int key = positions.keyAt(i);
                    Object obj = positions.get(key);
                    if (obj.equals(Boolean.TRUE))
                        sites.add(getSites().get(key));
                }
                constraint = new SpreadException((Resident) residentsSpinner.getSelectedItem(), sites);
                break;
        }

        return constraint;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) getParentFragment();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        void createConstraint(AbstractConstraint constraint);

        void editConstraint(AbstractConstraint constraint);
    }

}
