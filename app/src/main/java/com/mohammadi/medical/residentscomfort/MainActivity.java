package com.mohammadi.medical.residentscomfort;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.konifar.fab_transformation.FabTransformation;
import com.mohammadi.medical.residentscomfort.request.ScheduleRequestActivity;
import com.mohammadi.medical.residentscomfort.result.ScheduleResultActivity;
import com.mohammadi.medical.shifter.constraint.FixedDays;
import com.mohammadi.medical.shifter.constraint.FixedShift;
import com.mohammadi.medical.shifter.constraint.OffDays;
import com.mohammadi.medical.shifter.constraint.defaults.day.SpreadConstraint;
import com.mohammadi.medical.shifter.constraint.defaults.day.SpreadException;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Schedule;
import com.mohammadi.medical.shifter.entities.Site;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.NightShiftScheduleRequest;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;
import com.mohammadi.medical.shifter.schedule.Scheduler;
import com.mohammadi.medical.shifter.schedule.SchedulingFailedException;

import org.joda.time.LocalDate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.huri.jcal.JalaliCalendar;

public class MainActivity extends AppCompatActivity
{

    Scheduler scheduler;

    ListView listView;
    private FloatingActionButton fab;
    private Toolbar              toolbarFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadScheduler();

        initMenu();
        initRequestsList();

    }

    @Override
    protected void onStop()
    {
        super.onStop();

        saveScheduler();

    }

    private void loadScheduler()
    {
        try
        {
            FileInputStream fis = openFileInput("data");
            ObjectInputStream ois = new ObjectInputStream(fis);
            scheduler = (Scheduler) ois.readObject();

        }
        catch (IOException | ClassNotFoundException e)
        {
            scheduler = new Scheduler();

            addTestData();
        }
    }

    private void saveScheduler()
    {
        try
        {
            FileOutputStream fos = openFileOutput("data", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(scheduler);
        }
        catch (IOException e)
        {

        }
    }

    private void addTestData()
    {
        ScheduleRequest request = new ScheduleRequest("تیر");
//        request.createResident("خیری دوست");
        Resident borji = request.createResident("برجی");
        Resident gholamhoseini = request.createResident("غلامحسینی");
        Resident mohaghegh = request.createResident("محقق");
        Resident eslami = request.createResident("اسلامی");
        Resident akhoundi = request.createResident("آخوندی");
        Resident paraham = request.createResident("پراهام");
        Resident hajihashemi = request.createResident("حاجی هاشمی");
        Resident azizi = request.createResident("عزیزی");

        request.createSite("سونو ۱");
        request.createSite("سونو ۲");
        request.createSite("سونو ۳");
        Site sonoEx = request.createSite("سونو اضافه");
        request.createSite("شرح حال");
        Site graphy = request.createSite("گرافی");
        Site ct = request.createSite("ct");
        Site scopy = request.createSite("فلوروسکوپی");

        request.setStartDate(new JalaliCalendar(1395, 4, 1).toLocalDate());
        request.setEndDate(new JalaliCalendar(1395, 4, 31).toLocalDate());

        request.addNightShiftScheduleRequest();
        request.fillNightShiftScheduleRequest();
        request.getNightShiftScheduleRequest().removeResident(hajihashemi);

        // Constraints
        request.addConstraint(new SpreadException(hajihashemi, Arrays.asList(scopy)));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 25).toLocalDate(), new JalaliCalendar(1395, 4, 26).toLocalDate(), akhoundi, sonoEx));

        request.addConstraint(new FixedShift(new JalaliCalendar(1395, 4, 19).toLocalDate(), borji, Arrays.asList(sonoEx), false));
        request.addConstraint(new FixedShift(new JalaliCalendar(1395, 4, 29).toLocalDate(), borji, Arrays.asList(sonoEx), false));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 15).toLocalDate(), new JalaliCalendar(1395, 4, 16).toLocalDate(), azizi, sonoEx));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 17).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), azizi, scopy));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 30).toLocalDate(), new JalaliCalendar(1395, 4, 31).toLocalDate(), azizi, sonoEx));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 15).toLocalDate(), new JalaliCalendar(1395, 4, 16).toLocalDate(), gholamhoseini, scopy));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 17).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), gholamhoseini, sonoEx));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 4).toLocalDate(), new JalaliCalendar(1395, 4, 5).toLocalDate(), mohaghegh, sonoEx));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 6).toLocalDate(), new JalaliCalendar(1395, 4, 7).toLocalDate(), mohaghegh, scopy));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 4).toLocalDate(), new JalaliCalendar(1395, 4, 5).toLocalDate(), eslami, scopy));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 6).toLocalDate(), new JalaliCalendar(1395, 4, 7).toLocalDate(), eslami, sonoEx));

        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 16).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), paraham, graphy));
        request.addConstraint(new FixedDays(new JalaliCalendar(1395, 4, 30).toLocalDate(), new JalaliCalendar(1395, 4, 31).toLocalDate(), paraham, scopy));

        // Night Constraints
        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 25).toLocalDate(), new JalaliCalendar(1395, 4, 26).toLocalDate(), akhoundi));

        request.getNightShiftScheduleRequest().addConstraint(new FixedShift(new JalaliCalendar(1395, 4, 19).toLocalDate(), borji, new ArrayList<Site>(), true));
        request.getNightShiftScheduleRequest().addConstraint(new FixedShift(new JalaliCalendar(1395, 4, 29).toLocalDate(), borji, new ArrayList<Site>(), true));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 15).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), azizi));
        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 30).toLocalDate(), new JalaliCalendar(1395, 4, 31).toLocalDate(), azizi));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 15).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), gholamhoseini));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 4).toLocalDate(), new JalaliCalendar(1395, 4, 7).toLocalDate(), mohaghegh));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 4).toLocalDate(), new JalaliCalendar(1395, 4, 7).toLocalDate(), eslami));

        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 16).toLocalDate(), new JalaliCalendar(1395, 4, 18).toLocalDate(), paraham));
        request.getNightShiftScheduleRequest().addConstraint(new OffDays(new JalaliCalendar(1395, 4, 30).toLocalDate(), new JalaliCalendar(1395, 4, 31).toLocalDate(), paraham));

        scheduler.getRequests().add(request);
    }

    private void initRequestsList()
    {
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ScheduleArrayAdapter(this, android.R.layout.simple_list_item_1));
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                AbstractScheduleRequest abstractScheduleRequest = scheduler.getRequests().get(i);
                schedule(abstractScheduleRequest);
            }
        });
    }

    private void schedule(final AbstractScheduleRequest abstractScheduleRequest)
    {
        new AsyncTask<AbstractScheduleRequest, String, Schedule>()
        {

            private ProgressDialog dialog;

            @Override
            protected void onPreExecute()
            {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Scheduling ...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            protected Schedule doInBackground(AbstractScheduleRequest... params)
            {
                if (scheduler.getMap().get(abstractScheduleRequest) != null)
                    return scheduler.getMap().get(abstractScheduleRequest);

                Schedule schedule = null;
                try
                {
                    if (abstractScheduleRequest instanceof ScheduleRequest)
                    {
                        Map<LocalDate, List<Resident>> map = new HashMap<LocalDate, List<Resident>>();
                        if (((ScheduleRequest) abstractScheduleRequest).getNightShiftScheduleRequest() != null &&
                                ((ScheduleRequest) abstractScheduleRequest).getNightShiftScheduleRequest().getResidents().isEmpty() == false)
                        {
                            publishProgress("Night");
                            map = scheduler.mainScheduleNightShifts(((ScheduleRequest) abstractScheduleRequest).getNightShiftScheduleRequest());
                        }
                        if (map != null)
                            addPostNightShiftConstraints((ScheduleRequest) abstractScheduleRequest, map);
                        publishProgress("Day");
                        schedule = scheduler.schedule((ScheduleRequest) abstractScheduleRequest);
                        schedule.setNightShift(map);
                    }
                    else if (abstractScheduleRequest instanceof NightShiftScheduleRequest)
                    {
                        Map<LocalDate, List<Resident>> localDateResidentMap = scheduler.mainScheduleNightShifts((NightShiftScheduleRequest) abstractScheduleRequest);
                        schedule = new Schedule();
                        schedule.setName(abstractScheduleRequest.getName());
                        schedule.setNightShift(localDateResidentMap);
                        scheduler.getMap().put(abstractScheduleRequest, schedule);
                    }
                    schedule.setResidents(abstractScheduleRequest.getResidents());
                }
                catch (SchedulingFailedException e)
                {
                    String message = "Scheduling failed!";
                    if (e.getCause() != null && e.getCause().getMessage() != null)
                        message = e.getCause().getMessage();
                    publishProgress("Exception:" + message);
                }
                return schedule;

            }

            @Override
            protected void onProgressUpdate(String... values)
            {
                super.onProgressUpdate(values);
                if (values[0].contains("Exception"))
                    Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_LONG).show();
                else
                    dialog.setMessage("Scheduling " + values[0] + " ...");
            }

            private void addPostNightShiftConstraints(ScheduleRequest abstractScheduleRequest, Map<LocalDate, List<Resident>> map)
            {
                for (Map.Entry<LocalDate, List<Resident>> entry : map.entrySet())
                {
                    for (Resident resident : entry.getValue())
                    {
                        if (abstractScheduleRequest.getNightShiftScheduleRequest().getOnNightShiftSite() != null)
                            abstractScheduleRequest.addConstraint(new FixedShift(entry.getKey(), resident, Arrays.asList(abstractScheduleRequest.getNightShiftScheduleRequest().getOnNightShiftSite()), false));
                        if (abstractScheduleRequest.getNightShiftScheduleRequest().getOnPostNightShiftSite() != null)
                            abstractScheduleRequest.addConstraint(new FixedShift(entry.getKey().plusDays(1), resident, Arrays.asList(abstractScheduleRequest.getNightShiftScheduleRequest().getOnPostNightShiftSite()), false));

                    }

                }

            }

            @Override
            protected void onPostExecute(Schedule schedule)
            {
                super.onPostExecute(schedule);
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }

                if (schedule == null)
                    return;
                Intent intent = new Intent(MainActivity.this, ScheduleResultActivity.class);
                intent.putExtra(Constants.SCHEDULE_KEY, schedule);
                startActivityForResult(intent, Constants.RESULT_SAVE_CODE);

            }
        }.execute();


    }

    private void initMenu()
    {
        toolbarFooter = (Toolbar) findViewById(R.id.toolbar_footer);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                FabTransformation.with(fab).transformTo(toolbarFooter);
            }
        });

        Button dayButton = (Button) findViewById(R.id.dayButton);
        dayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showInputDialog("day");
                FabTransformation.with(fab).transformFrom(toolbarFooter);

            }
        });

        dayButton = (Button) findViewById(R.id.nightButton);
        dayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showInputDialog("night");
                FabTransformation.with(fab).transformFrom(toolbarFooter);

            }
        });

        dayButton = (Button) findViewById(R.id.dayNightButton);
        dayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showInputDialog("dayNight");
                FabTransformation.with(fab).transformFrom(toolbarFooter);

            }
        });

        ImageButton closeButton = (ImageButton) findViewById(R.id.closeMenuButton);
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FabTransformation.with(fab).transformFrom(toolbarFooter);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if (fab.getVisibility() != View.VISIBLE)
        {
            FabTransformation.with(fab).transformFrom(toolbarFooter);
            return;
        }
        super.onBackPressed();
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState)
//    {
//        savedInstanceState.putSerializable("requests", (Serializable) scheduler.getMap());
//        savedInstanceState.putSerializable("requestsList", (Serializable) scheduler.getRequests());
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    //onRestoreInstanceState
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState)
//    {
//        super.onRestoreInstanceState(savedInstanceState);
//        scheduler.getMap().putAll((Map<? extends AbstractScheduleRequest, ? extends Schedule>) savedInstanceState.getSerializable("requests"));
//        scheduler.getRequests().addAll(scheduler.getMap().keySet());
//        List<AbstractScheduleRequest> savedRequests = (List<AbstractScheduleRequest>) savedInstanceState.getSerializable("requestsList");
//        for (AbstractScheduleRequest request : scheduler.getRequests())
//        {
//            AbstractScheduleRequest deletedRequest = null;
//            for (AbstractScheduleRequest currentRequest : savedRequests)
//            {
//                if (currentRequest.getId() == request.getId())
//                {
//                    deletedRequest = currentRequest;
//                    break;
//                }
//            }
//            savedRequests.remove(deletedRequest);
//        }
//        scheduler.getRequests().addAll(savedRequests);
//
//        ((ScheduleArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == listView.getId())
            getMenuInflater().inflate(R.menu.request_context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        if (item.getItemId() == R.id.request_delete)
        {
            scheduler.getRequests().remove(position);
            ((ScheduleArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
        if (item.getItemId() == R.id.request_edit)
        {
            Intent intent = new Intent(getApplicationContext(), ScheduleRequestActivity.class);
            intent.putExtra(Constants.REQUEST_KEY, (Parcelable) scheduler.getRequests().get(position));
            startActivityForResult(intent, Constants.REQUEST_SAVE_CODE);

        }

        return super.onContextItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Constants.REQUEST_SAVE_CODE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            if (extras != null)
            {
                AbstractScheduleRequest request = extras.getParcelable(Constants.REQUEST_KEY);
                int i = 0;
                for (i = 0; i < scheduler.getRequests().size(); i++)
                {
                    if (scheduler.getRequests().get(i).getId() == request.getId())
                        break;
                }
                scheduler.getRequests().set(i, request);
                scheduler.getMap().remove(request);
                ((ScheduleArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        }

        else if (requestCode == Constants.RESULT_SAVE_CODE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            if (extras != null)
            {
                Schedule schedule = (Schedule) extras.getSerializable(Constants.SCHEDULE_KEY);

                AbstractScheduleRequest request = null;
                for (Map.Entry<AbstractScheduleRequest, Schedule> entry : scheduler.getMap().entrySet())
                {
                    if (entry.getValue().getName().equals(schedule.getName()))
                    {
                        request = entry.getKey();
                        break;
                    }
                }
                if (request != null)
                {
                    scheduler.getMap().put(request, schedule);
                    ((ScheduleArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                }


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showInputDialog(final String command)
    {

        // get prompts.xml view
        final View promptView = getLayoutInflater().inflate(R.layout.name_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        switch (command)
                        {
                            case "day":
                                scheduler.createScheduleRequest(editText.getText().toString());
                                Snackbar.make(promptView, "Schedule Request created", Snackbar.LENGTH_SHORT).show();
                                break;
                            case "night":
                                scheduler.createNightShiftScheduleRequest(editText.getText().toString());
                                Snackbar.make(promptView, "Night Shift Schedule Request created", Snackbar.LENGTH_SHORT).show();
                                break;
                            case "dayNight":
                            {
                                ScheduleRequest scheduleRequest = scheduler.createScheduleRequest(editText.getText().toString());
                                scheduleRequest.addNightShiftScheduleRequest();
                                Snackbar.make(promptView, "Day and Night Shift Schedule Request created", Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        ((ScheduleArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }

                })
                .setNegativeButton(
                        getString(R.string.cancel),
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

    private class ScheduleArrayAdapter extends ArrayAdapter<AbstractScheduleRequest>
    {

        public ScheduleArrayAdapter(Context context, int textViewResourceId)
        {
            super(context, textViewResourceId);
        }

        @Override
        public long getItemId(int position)
        {
            return scheduler.getRequests().get(position).getId();
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public int getCount()
        {
            return scheduler.getRequests().size();
        }

        @Override
        public AbstractScheduleRequest getItem(int position)
        {
            return scheduler.getRequests().get(position);
        }
    }

}
