package edu.temple.bitdash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.R.attr.orientation;
import static android.R.attr.screenSize;

public class BlockInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlockInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlockInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlockInfoFragment newInstance(String param1, String param2) {
        BlockInfoFragment fragment = new BlockInfoFragment();
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
        return inflater.inflate(R.layout.fragment_block_info, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceBundle) {

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT &&
                getActivity().getResources().getConfiguration().screenLayout != Configuration.SCREENLAYOUT_SIZE_LARGE) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.blockinfo_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.blockInput:

                AlertDialog.Builder getAddress = new AlertDialog.Builder(getContext());

                final EditText editText = new EditText(getContext());
                getAddress.setMessage(R.string.block_search_prompt);
                getAddress.setTitle(R.string.block_search_title);

                getAddress.setView(editText);

                getAddress.setPositiveButton(R.string.block_search_okay, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String editTextValue = editText.getText().toString();

                        new BlockSearchTask().execute(editTextValue);

                    }

                });

                getAddress.setNegativeButton(R.string.block_search_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                    }

                });

                getAddress.show();

                break;

            case R.id.blockPrevious:

                TextView previousBlock = (TextView) getActivity().findViewById(R.id.previous_number_text);

                if (!previousBlock.getText().toString().matches("") && Integer.parseInt(previousBlock.getText().toString()) > 0) {

                    String previousNumber = previousBlock.getText().toString();

                    new BlockSearchTask().execute(previousNumber);

                }

                break;

            case R.id.blockNext:

                TextView nextBlock = (TextView) getActivity().findViewById(R.id.next_number_text);

                if (!nextBlock.getText().toString().matches("")) {

                    String nextNumber = nextBlock.getText().toString();

                    new BlockSearchTask().execute(nextNumber);

                }

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

    private class BlockSearchTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {

            return blockAddress(params[0]);

        }

        @Override
        protected void onPostExecute(JSONObject result) {

            String checkSuccess = null;

            try {
                checkSuccess = result.get("status").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (checkSuccess.matches("success")) {

                setBlockData(result);

            }
            else {

                AlertDialog.Builder retryAddress = new AlertDialog.Builder(getContext());

                final EditText retryText = new EditText(getContext());
                try {
                    retryText.setText(result.getJSONObject("data").get("address").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                retryAddress.setMessage(R.string.block_retry_prompt);
                retryAddress.setTitle(R.string.block_retry_title);

                retryAddress.setView(retryText);

                retryAddress.setPositiveButton(R.string.block_search_okay, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String editTextValue = retryText.getText().toString();

                        new BlockSearchTask().execute(editTextValue);

                    }

                });

                retryAddress.setNegativeButton(R.string.block_search_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                    }

                });

                retryAddress.show();

            }

        }
    }

    private JSONObject blockAddress(String address) {

        HttpURLConnection connection = null;

        URL url = null;

        try {
            url = new URL("http://btc.blockr.io/api/v1/block/info/" + address);
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

        if (reader != null) {

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

        }

        else {

            builder.append("{\"status\":\"fail\"}");

        }

        JSONObject json = null;

        try {
            json = new JSONObject(builder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    private void setBlockData (JSONObject result) {

        TextView blockNumber = (TextView) getActivity().findViewById(R.id.block_number_text);
        TextView blockHash = (TextView) getActivity().findViewById(R.id.block_hash_text);
        TextView blockSize = (TextView) getActivity().findViewById(R.id.block_size_text);
        TextView blockConfirms = (TextView) getActivity().findViewById(R.id.block_confirmation_text);
        TextView previousBlock = (TextView) getActivity().findViewById(R.id.previous_number_text);
        TextView nextBlock = (TextView) getActivity().findViewById(R.id.next_number_text);

        try {
            blockNumber.setText(result.getJSONObject("data").get("nb").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            blockHash.setText(result.getJSONObject("data").get("hash").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            blockSize.setText(result.getJSONObject("data").get("size").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            blockConfirms.setText(result.getJSONObject("data").get("confirmations").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            previousBlock.setText(result.getJSONObject("data").get("prev_block_nb").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            nextBlock.setText(result.getJSONObject("data").get("next_block_nb").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
