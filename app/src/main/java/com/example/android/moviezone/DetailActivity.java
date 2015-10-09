package com.example.android.moviezone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class DetailActivity extends ActionBarActivity{

    private Fragment detail;
    private static final String DETAIL = "detail" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent=getIntent();
         String backdrop_path=intent.getStringExtra("backdrop_path");
         int id=intent.getIntExtra("id", 0);
         String original_title=intent.getStringExtra("original_title");
         String poster_path=intent.getStringExtra("poster_path");
         String overview=intent.getStringExtra("overview");
         String release_date=intent.getStringExtra("release_date");
         double vote_average=intent.getDoubleExtra("vote_average", 0.0);

        if (savedInstanceState == null) {
            detail=new DetailFragment();
            DetailFragment fragment = new DetailFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(fragment.ID, id);
            arguments.putString(fragment.BACK_GROUND, backdrop_path);
            arguments.putString(fragment.TITLE,original_title);
            arguments.putString(fragment.IMAGE,poster_path);
            arguments.putString(fragment.OVERVIEW, overview);
            arguments.putString(fragment.DATE, release_date);
            arguments.putDouble(fragment.RATE, vote_average);
            arguments.putBoolean("twoPane", intent.getBooleanExtra("twoPane",false));
            detail.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container ,detail)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, DETAIL, detail);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null) {
            detail = getSupportFragmentManager().getFragment(savedInstanceState, DETAIL);
        }
    }
}
