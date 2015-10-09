package com.example.android.moviezone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener, GridMoviesFragment.CallBack,FavouriteFragment.CallBackFavourite {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    int tabPosition;
    String ARG_SECTION_NUMBER="sectionNumber";
    private static final String DETAILFRAGMENT_TAG = "DFTAG" ;
    boolean mTwoPane;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private String sortedBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //////////////////////////////////

        // force show actionbar 'overflow' button on devices with hardware menu button
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ///////////////////////////////////
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.movie_detail, new DetailFragment(), DETAILFRAGMENT_TAG)
//                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        tabPosition=0;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortedBy=prefs.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_most_popular));
        // Set up the action bar.
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.parseColor("#7D387C"));
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
//        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
//            }
//        });

        // For each of the sections in the app, add a tab to the action bar.
    //    for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
//            actionBar.addTab(
//                    actionBar.newTab()
//                            .setText(mSectionsPagerAdapter.getPageTitle(i))
//                            .setTabListener(this));
  //      }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sort=prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_most_popular));
        if(sort!=null&&!(sort.equals(sortedBy))){
//            if(sort.equals(getString(R.string.pref_sort_most_popular))){
//               // getSupportActionBar().getTabAt(0).setText(getString(R.string.pref_sort_label_most_popular).toUpperCase(Locale.getDefault()));
//            }
//            else{
//              //  getSupportActionBar().getTabAt(0).setText(getString(R.string.pref_sort_label_highest_rated).toUpperCase(Locale.getDefault()));
//            }
             mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
            sortedBy=sort;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_refresh) {
            mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mSectionsPagerAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        tabPosition=tab.getPosition();

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onItemSelected(String backdrop_path, int id,String original_title,String poster_path
            ,String overview,String release_date, double vote_average ){
        if (mTwoPane) {
            DetailFragment new_fragment=new DetailFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(new_fragment.ID, id);
            arguments.putString(new_fragment.BACK_GROUND, backdrop_path);
            arguments.putString(new_fragment.TITLE,original_title);
            arguments.putString(new_fragment.IMAGE,poster_path);
            arguments.putString(new_fragment.OVERVIEW, overview);
            arguments.putString(new_fragment.DATE, release_date);
            arguments.putDouble(new_fragment.RATE, vote_average);
            arguments.putBoolean("twoPane", mTwoPane);
            new_fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail, new_fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("backdrop_path", backdrop_path);
            intent.putExtra("id", id);
            intent.putExtra("original_title", original_title);
            intent.putExtra("poster_path", poster_path);
            intent.putExtra("overview", overview);
            intent.putExtra("release_date",release_date);
            intent.putExtra("vote_average",vote_average);
            intent.putExtra("twoPane",mTwoPane);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelectedFavourite(String backdrop_path, int id, String original_title, String poster_path, String overview, String release_date, double vote_average) {
        if (mTwoPane) {
            DetailFragment new_fragment=new DetailFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(new_fragment.ID, id);
            arguments.putString(new_fragment.BACK_GROUND, backdrop_path);
            arguments.putString(new_fragment.TITLE,original_title);
            arguments.putString(new_fragment.IMAGE,poster_path);
            arguments.putString(new_fragment.OVERVIEW, overview);
            arguments.putString(new_fragment.DATE, release_date);
            arguments.putDouble(new_fragment.RATE, vote_average);
            arguments.putBoolean("twoPane", mTwoPane);
            new_fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail, new_fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("backdrop_path", backdrop_path);
            intent.putExtra("id", id);
            intent.putExtra("original_title", original_title);
            intent.putExtra("poster_path", poster_path);
            intent.putExtra("overview", overview);
            intent.putExtra("release_date",release_date);
            intent.putExtra("vote_average",vote_average);
            intent.putExtra("twoPane",mTwoPane);
            startActivity(intent);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
             Fragment fragment = null;
            if(position==0){
                fragment=new GridMoviesFragment();
                Bundle args = new Bundle();
                args.putBoolean("twoPane", mTwoPane);
                fragment.setArguments(args);

            }else{
                fragment=new FavouriteFragment();
                Bundle args = new Bundle();
                args.putBoolean("twoPane", mTwoPane);
                fragment.setArguments(args);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String sort=prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_most_popular));
                    if(sort.equals(getString(R.string.pref_sort_most_popular))){
                        return getString(R.string.pref_sort_label_most_popular).toUpperCase(l);
                    }else{
                        return getString(R.string.pref_sort_label_highest_rated).toUpperCase(l);
                    }
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }

    }


}
