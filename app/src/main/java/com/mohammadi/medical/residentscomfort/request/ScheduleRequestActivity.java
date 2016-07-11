package com.mohammadi.medical.residentscomfort.request;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.mohammadi.medical.residentscomfort.Constants;
import com.mohammadi.medical.residentscomfort.MainActivity;
import com.mohammadi.medical.residentscomfort.R;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleRequestActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
                                                                          SiteFragment.OnFragmentInteractionListener,
                                                                          ResidentFragment.OnFragmentInteractionListener,
                                                                          ConstraintFragment.OnFragmentInteractionListener,
                                                                          NightSettingsFragment.OnFragmentInteractionListener
{

    AbstractScheduleRequest request;

    private Toolbar   toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FloatingActionButton requestFab;
    Map<String, Integer> fabColorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_request);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            setRequest((AbstractScheduleRequest) extras.getParcelable(Constants.REQUEST_KEY));
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        initFabs();

//        animateFab("Residents");
    }

    private void setupViewPager(final ViewPager viewPager)
    {
        fabColorMap = new HashMap<>();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ResidentFragment.newInstance(request), getString(R.string.residents));
        fabColorMap.put(getString(R.string.residents), android.R.color.holo_green_light);
        if (getRequest() instanceof ScheduleRequest)
        {
            adapter.addFragment(SiteFragment.newInstance((ScheduleRequest) request), getString(R.string.sites));
            fabColorMap.put(getString(R.string.sites), android.R.color.holo_blue_light);
        }
        adapter.addFragment(ConstraintFragment.newInstance(request, getRequest() instanceof ScheduleRequest ? true : false), getString(R.string.constraintTabName));
        fabColorMap.put(getString(R.string.constraintTabName), android.R.color.holo_red_light);
        adapter.addFragment(SettingsFragment.newInstance(request), getString(R.string.settingTabName));
        fabColorMap.put(getString(R.string.settingTabName), android.R.color.holo_orange_light);
        if (getRequest() instanceof ScheduleRequest && ((ScheduleRequest) getRequest()).getNightShiftScheduleRequest() != null)
        {
            adapter.addFragment(NightSettingsFragment.newInstance((ScheduleRequest) request), getString(R.string.nightSettingTabName));
            fabColorMap.put(getString(R.string.nightSettingTabName), android.R.color.holo_orange_light);
            adapter.addFragment(ConstraintFragment.newInstance(request, false), getString(R.string.nightConstraintTabName));
            fabColorMap.put(getString(R.string.nightConstraintTabName), android.R.color.holo_red_light);

        }

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
                    ((IFabFragment) fragment).configFab(requestFab);
                    animateFab((viewPager.getAdapter()).getPageTitle(position).toString());
                }
                else
                    requestFab.hide();
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelable("request", getRequest());
        savedInstanceState.putInt("tab", viewPager.getCurrentItem());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        setRequest((AbstractScheduleRequest) savedInstanceState.getParcelable("request"));
        viewPager.setCurrentItem(savedInstanceState.getInt("tab"));
    }

    private void initFabs()
    {
        requestFab = (FloatingActionButton) findViewById(R.id.request_fab);
        requestFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requestFab.getContext(), fabColorMap.get(getString(R.string.residents)))));
        ((IFabFragment) ((ViewPagerAdapter) viewPager.getAdapter()).getItem(0)).configFab(requestFab);

        FloatingActionButton okFab = (FloatingActionButton) findViewById(R.id.okFab);
        assert okFab != null;
        okFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ScheduleRequestActivity.this, MainActivity.class);
                intent.putExtra(Constants.REQUEST_KEY, (Parcelable) getRequest());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public AbstractScheduleRequest getRequest()
    {
        return request;
    }

    public void setRequest(AbstractScheduleRequest request)
    {
        this.request = request;
    }

    protected void animateFab(final String title)
    {
        if (requestFab.isShown() == false)
            requestFab.show();
        requestFab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                // Change FAB color and icon
                requestFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requestFab.getContext(), fabColorMap.get(title))));
//                requestFab.setImageResource(R.drawable.ic_add_white_18dp);

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                requestFab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        requestFab.startAnimation(shrink);
    }
}
