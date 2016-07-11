package com.mohammadi.medical.residentscomfort.request;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;

import java.io.Serializable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResidentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResidentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidentFragment extends Fragment implements IFabFragment
{
    private static final String ARG_REQUEST = "request";

    private AbstractScheduleRequest request;

    ListView residentsList;

    private OnFragmentInteractionListener mListener;

    public ResidentFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ResidentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResidentFragment newInstance(AbstractScheduleRequest param1)
    {
        ResidentFragment fragment = new ResidentFragment();
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
        View view = inflater.inflate(R.layout.fragment_resident, container, false);

        residentsList = (ListView) view.findViewById(R.id.residentsList);
        residentsList.setAdapter(new ResidentArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1));
        registerForContextMenu(residentsList);

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
        if (v.getId() == residentsList.getId())
            getActivity().getMenuInflater().inflate(R.menu.resident_context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        if (item.getItemId() == R.id.resident_delete)
        {
            request.getResidents().remove(position);
            ((ResidentArrayAdapter) residentsList.getAdapter()).notifyDataSetChanged();
        }
        if (item.getItemId() == R.id.resident_edit)
        {
            showDialog(position);
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

                showDialog(-1);
            }
        });

    }

    protected void showDialog(final int position)
    {

        // get prompts.xml view
        final View promptView = getActivity().getLayoutInflater().inflate(R.layout.name_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        if (position != -1)
            editText.setText(request.getResidents().get(position).getName());

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                        if (position == -1)
                        {
                            request.createResident(editText.getText().toString());
                            Snackbar.make(promptView, "Resident created.", Snackbar.LENGTH_LONG).show();

                        }
                        else
                        {
                            request.getResidents().get(position).setName(editText.getText().toString());
                            ((ResidentArrayAdapter) residentsList.getAdapter()).notifyDataSetChanged();
                            Snackbar.make(promptView, "Resident edited.", Snackbar.LENGTH_LONG).show();

                        }
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

    private class ResidentArrayAdapter extends ArrayAdapter<Resident>
    {

        public ResidentArrayAdapter(Context context, int textViewResourceId)
        {
            super(context, textViewResourceId);
        }

        @Override
        public long getItemId(int position)
        {
            return request.getResidents().get(position).getId();
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public int getCount()
        {
            return request.getResidents().size();
        }

        @Override
        public Resident getItem(int position)
        {
            return request.getResidents().get(position);
        }
    }
}
