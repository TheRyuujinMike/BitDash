package edu.temple.bitdash;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements FunctionListFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Drawable menuButton = getResources().getDrawable(R.drawable.ic_menu_black_24dp, null);
        getSupportActionBar().setHomeAsUpIndicator(menuButton);


        CurrentPriceFragment currentPriceFragment = new CurrentPriceFragment();


        int orientation=this.getResources().getConfiguration().orientation;
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if(orientation== Configuration.ORIENTATION_PORTRAIT && screenSize != Configuration.SCREENLAYOUT_SIZE_LARGE){

            getSupportActionBar().setDisplayShowTitleEnabled(false);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.list_space, currentPriceFragment)
                    .commit();

        }
        else {

            FunctionListFragment functionListFragment = new FunctionListFragment();

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.left_fragment, functionListFragment)
                    .replace(R.id.right_fragment,currentPriceFragment)
                    .commit();

        }

        Intent serviceIntent = new Intent(this, ChartUpdateService.class);

        this.startService(serviceIntent);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
