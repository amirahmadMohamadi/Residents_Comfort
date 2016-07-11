package com.mohammadi.medical.residentscomfort.request;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.shifter.constraint.AbstractConstraint;
import com.mohammadi.medical.shifter.constraint.PersonalConstraint;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.NightShiftScheduleRequest;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConstraintFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConstraintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConstraintFragment extends Fragment implements IFabFragment, CreateConstraintFragment.OnFragmentInteractionListener
{
    private static final String ARG_REQUEST = "request";
    private static final String ARG_DAY     = "day";

    private AbstractScheduleRequest request;
    private boolean                 isDay;

    ListView constraintsList;

    private OnFragmentInteractionListener mListener;

    public ConstraintFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResidentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConstraintFragment newInstance(AbstractScheduleRequest param1, boolean param2)
    {
        ConstraintFragment fragment = new ConstraintFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REQUEST, (Serializable) param1);
        args.putBoolean(ARG_DAY, param2);
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
            isDay = getArguments().getBoolean(ARG_DAY);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_constraint, container, false);

        constraintsList = (ListView) view.findViewById(R.id.constraintsList);
        constraintsList.setAdapter(new ConstraintArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1));
        registerForContextMenu(constraintsList);

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
        if (v.getId() == constraintsList.getId())
        {
            getActivity().getMenuInflater().inflate(R.menu.constraint_context_menu, menu);
            if (request.getConstraints().get(((AdapterView.AdapterContextMenuInfo) menuInfo).position).isEditable() == false)
            {
                menu.removeItem(R.id.constraint_edit);
            }

            if (request.getConstraints().get(((AdapterView.AdapterContextMenuInfo) menuInfo).position).isDeletable() == false)
            {
                menu.removeItem(R.id.constraint_delete);
            }
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        if (item.getItemId() == R.id.constraint_delete)
        {
            getCorrectList().remove(position);
            ((ConstraintArrayAdapter) constraintsList.getAdapter()).notifyDataSetChanged();
        }
        if (item.getItemId() == R.id.constraint_edit)
        {
            showConstraintDialog((PersonalConstraint) request.getConstraints().get(position));
        }

        return super.onContextItemSelected(item);
    }

    private List<AbstractConstraint> getCorrectList()
    {
        if (isDay || request instanceof NightShiftScheduleRequest)
            return request.getConstraints();
        else
            return ((ScheduleRequest) request).getNightShiftScheduleRequest().getConstraints();
    }

    @Override
    public void configFab(FloatingActionButton requestFab)
    {
        requestFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                showConstraintDialog(null);
            }
        });

    }

    private void showConstraintDialog(PersonalConstraint constraint)
    {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
        {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        CreateConstraintFragment fragment = CreateConstraintFragment.newInstance(request.getResidents(), request instanceof ScheduleRequest ? ((ScheduleRequest) request).getSites() : new ArrayList<Site>(), constraint, isDay);
        fragment.show(ft, "dialog");

    }

    @Override
    public void createConstraint(AbstractConstraint constraint)
    {
        if (getCorrectList().isEmpty())
            constraint.setId(0);
        else
            constraint.setId(getCorrectList().get(getCorrectList().size() - 1).getId() + 1);
        getCorrectList().add(constraint);
        ((ConstraintArrayAdapter) constraintsList.getAdapter()).notifyDataSetChanged();
        Snackbar.make(getView(), "Constraint created.", Snackbar.LENGTH_LONG).show();


    }

    @Override
    public void editConstraint(AbstractConstraint constraint)
    {
        int i = 0;
        for (i = 0; i < getCorrectList().size(); i++)
        {
            if (getCorrectList().get(i).getId() == constraint.getId())
                break;
        }
        getCorrectList().set(i, constraint);
        ((ConstraintArrayAdapter) constraintsList.getAdapter()).notifyDataSetChanged();
        Snackbar.make(getView(), "Constraint edited.", Snackbar.LENGTH_LONG).show();

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

    private class ConstraintArrayAdapter extends ArrayAdapter<AbstractConstraint>
    {

        public ConstraintArrayAdapter(Context context, int textViewResourceId)
        {
            super(context, textViewResourceId);
        }

        @Override
        public long getItemId(int position)
        {
            return getCorrectList().get(position).getId();
        }


        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public int getCount()
        {
            return getCorrectList().size();
        }

        @Override
        public AbstractConstraint getItem(int position)
        {
            return getCorrectList().get(position);
        }

    }


}
