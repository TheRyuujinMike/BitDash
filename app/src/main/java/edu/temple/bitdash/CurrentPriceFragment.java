package edu.temple.bitdash;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.R.attr.button;

public class CurrentPriceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CurrentPriceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentPriceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentPriceFragment newInstance(String param1, String param2) {
        CurrentPriceFragment fragment = new CurrentPriceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_price, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT &&
                getActivity().getResources().getConfiguration().screenLayout != Configuration.SCREENLAYOUT_SIZE_LARGE) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        new CurrentPriceTask().execute("");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class CurrentPriceTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {

            return coinDesk();

        }

        @Override
        protected void onPostExecute(JSONObject result) {

            TextView currentPrice = (TextView) getView().findViewById(R.id.current_price);
            TextView accordingTo = (TextView) getView().findViewById(R.id.according_to);

            try {

                Double price = Double.parseDouble(result.getJSONObject("bpi").getJSONObject("USD").getString("rate"));

                String lastUpdated = getResources().getString(R.string.last_updated);

                currentPrice.setText("$" + price.toString());

                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String formattedDate = df.format(c.getTime());

                accordingTo.setText(lastUpdated + ": " + formattedDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private JSONObject coinDesk() {

        HttpURLConnection connection = null;

        URL url = null;

        try {
            url = new URL("http://api.coindesk.com/v1/bpi/currentprice.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder();

        String line;

        try {
            while ((line = reader.readLine()) != null) {

                builder.append(line + "\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject json = null;

        try {
            json = new JSONObject(builder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.currentprice_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.priceRefresh:

                new CurrentPriceTask().execute("");

                break;

            case android.R.id.home:

                FunctionListFragment functionListFragment = new FunctionListFragment();

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.list_space, functionListFragment)
                        .commit();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
