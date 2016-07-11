package com.mohammadi.medical.residentscomfort.result;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;
import com.mohammadi.medical.residentscomfort.Constants;
import com.mohammadi.medical.residentscomfort.MainActivity;
import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.residentscomfort.request.IFabFragment;
import com.mohammadi.medical.shifter.entities.Schedule;
import com.mohammadi.medical.shifter.util.PdfUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScheduleResultActivity extends AppCompatActivity implements DayScheduleFragment.OnFragmentInteractionListener, DayScheduleStatisticsFragment.OnFragmentInteractionListener, NightScheduleFragment.OnFragmentInteractionListener
{

    private Schedule schedule;

    private Toolbar   toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton resultFab;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_result);
        toolbar = (Toolbar) findViewById(R.id.resultToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            setSchedule((Schedule) extras.getSerializable(Constants.SCHEDULE_KEY));
        }

        initFabs();

        viewPager = (ViewPager) findViewById(R.id.resultViewpager);
        tabLayout = (TabLayout) findViewById(R.id.resultTabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(final ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (getSchedule().getMap().isEmpty() == false)
        {
            adapter.addFragment(DayScheduleFragment.newInstance(getSchedule()), getString(R.string.dayScheduleTabName));
            adapter.addFragment(DayScheduleStatisticsFragment.newInstance(getSchedule()), getString(R.string.dayScheduleStatTabName));
        }
        if (getSchedule().getNightShifts().isEmpty() == false)
            adapter.addFragment(NightScheduleFragment.newInstance(getSchedule()), getString(R.string.nightScheduleTabName));

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                Fragment fragment = ((ViewPagerAdapter) viewPager.getAdapter()).getItem(position);
                if (fragment instanceof IFabFragment)
                {
                    ((IFabFragment) fragment).configFab(resultFab);
                    resultFab.show();
                }
                else
                    resultFab.hide();
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList      = new ArrayList<>();
        private final List<String>   mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }

    private void initFabs()
    {
        resultFab= (FloatingActionButton) findViewById(R.id.pdfButton);

        FloatingActionButton okFab = (FloatingActionButton) findViewById(R.id.okFab);
        assert okFab != null;
        okFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ScheduleResultActivity.this, MainActivity.class);
                intent.putExtra(Constants.SCHEDULE_KEY, getSchedule());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putSerializable("schedule", getSchedule());
        savedInstanceState.putInt("tab", viewPager.getCurrentItem());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        setSchedule((Schedule) savedInstanceState.getSerializable("schedule"));
        viewPager.setCurrentItem(savedInstanceState.getInt("tab"));
    }

    public Schedule getSchedule()
    {
        return schedule;
    }

    public void setSchedule(Schedule schedule)
    {
        this.schedule = schedule;
    }

}
