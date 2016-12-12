package edu.temple.bitdash;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddressBalanceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<String> addressList;
    private Button addDeleteButton;
    private TextView currentAddressText;
    private TextView balanceText;
    private TextView totalReceivedText;
    private Spinner addressSpinner;

    public AddressBalanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressBalanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressBalanceFragment newInstance(String param1, String param2) {
        AddressBalanceFragment fragment = new AddressBalanceFragment();
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
        return inflater.inflate(R.layout.fragment_address_balance, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceBundle) {

        retrieveArrayList();

        if (addressList != null) {

            setSpinner();

        }

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT &&
                getActivity().getResources().getConfiguration().screenLayout != Configuration.SCREENLAYOUT_SIZE_LARGE) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        addDeleteButton = (Button) getActivity().findViewById(R.id.address_add_delete_button);

        addDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean spinnerAlreadySet = true;

                if (addDeleteButton.getText().toString().matches(getString(R.string.address_save_button))) {

                    if (addressList == null) {

                        addressList = new ArrayList<>();
                        spinnerAlreadySet = false;

                    }

                    addressList.add(currentAddressText.getText().toString());
                    saveArrayList();

                    if (!spinnerAlreadySet) {
                        setSpinner();
                    }

                    addressSpinner.setSelection(addressList.size() - 1);

                    Toast.makeText(getContext(), R.string.address_saved_toast, Toast.LENGTH_SHORT).show();
                    addDeleteButton.setText(getString(R.string.address_delete_button));

                }
                else if (addDeleteButton.getText().toString().matches(getString(R.string.address_delete_button))) {

                    addressSpinner.setSelection(0);

                    addressList.remove(currentAddressText.getText().toString());
                    saveArrayList();

                    Toast.makeText(getContext(),R.string.address_deleted_toast, Toast.LENGTH_SHORT).show();
                    addDeleteButton.setText(getString(R.string.address_save_button));


                }
                else {

                    //Do nothing

                }

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.addressbalance_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search_address:

                AlertDialog.Builder getAddress = new AlertDialog.Builder(getContext());

                final EditText editText = new EditText(getContext());
                getAddress.setMessage(R.string.address_search_prompt);
                getAddress.setTitle(R.string.address_search_title);

                getAddress.setView(editText);

                getAddress.setPositiveButton(R.string.address_search_okay, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String editTextValue = editText.getText().toString();

                        new AddressSearchTask().execute(editTextValue);

                    }

                });

                getAddress.setNegativeButton(R.string.address_search_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                    }

                });

                getAddress.show();

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

    private class AddressSearchTask extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... params) {

            return bitAddress(params[0]);

        }

        @Override
        protected void onPostExecute(JSONObject result) {

            String checkSuccess = null;

            try {
                checkSuccess = result.getJSONObject("data").get("is_unknown").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (checkSuccess.matches("false")) {

                setAddressData(result);

            }
            else {

                AlertDialog.Builder retryAddress = new AlertDialog.Builder(getContext());

                final EditText retryText = new EditText(getContext());
                try {
                    retryText.setText(result.getJSONObject("data").get("address").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                retryAddress.setMessage(R.string.address_retry_prompt);
                retryAddress.setTitle(R.string.address_retry_title);

                retryAddress.setView(retryText);

                retryAddress.setPositiveButton(R.string.address_search_okay, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String editTextValue = retryText.getText().toString();

                        new AddressSearchTask().execute(editTextValue);

                    }

                });

                retryAddress.setNegativeButton(R.string.address_search_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                    }

                });

                retryAddress.show();

            }

        }
    }

    private JSONObject bitAddress(String address) {

        HttpURLConnection connection = null;

        URL url = null;

        try {
            url = new URL("http://btc.blockr.io/api/v1/address/info/" + address);
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

    private void setAddressData (JSONObject result) {

        String acceptedAddress = null;
        String balance = null;
        String totalReceived = null;

        try {
            acceptedAddress = result.getJSONObject("data").get("address").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            balance = "$" + result.getJSONObject("data").get("balance").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            totalReceived = "$" + result.getJSONObject("data").get("totalreceived").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        currentAddressText = (TextView) getActivity().findViewById(R.id.current_address_text);
        balanceText = (TextView) getActivity().findViewById(R.id.balance_text);
        totalReceivedText = (TextView) getActivity().findViewById(R.id.total_received_text);

        currentAddressText.setText(acceptedAddress);
        balanceText.setText(balance);
        totalReceivedText.setText(totalReceived);

        addDeleteButton = (Button) getActivity().findViewById(R.id.address_add_delete_button);

        if (acceptedAddress != null) {

            boolean isSaved = false;

            if (addressList != null) {

                if (addressList.contains(acceptedAddress)) {
                    isSaved = true;
                }
            }

            if (isSaved) {

                addDeleteButton.setText(R.string.address_delete_button);

            }
            else {

                addDeleteButton.setText(R.string.address_save_button);

            }

        }

    }

    private void setSpinner() {

        if (addressList != null) {

            ArrayAdapter spinnerAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_dropdown_item, addressList);

            addressSpinner = (Spinner) getActivity().findViewById(R.id.address_spinner);
            addressSpinner.setAdapter(spinnerAdapter);

            addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String selectedAddress;

                    if (addressList != null) {

                        selectedAddress = addressSpinner.getItemAtPosition(position).toString();

                        new AddressSearchTask().execute(selectedAddress);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }

    private void saveArrayList() {

        Set<String> set = new HashSet<>();
        set.addAll(addressList);

        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putStringSet("saved_addresses", set);
        ed.commit();

    }

    private void retrieveArrayList() {

        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);

        Set<String> set = sp.getStringSet("saved_addresses", null);

        if (set != null) {
            addressList = new ArrayList<>();
            addressList.addAll(set);
        }

    }

}
