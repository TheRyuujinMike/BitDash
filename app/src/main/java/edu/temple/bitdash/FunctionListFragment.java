package edu.temple.bitdash;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FunctionListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FunctionListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FunctionListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FunctionListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FunctionListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FunctionListFragment newInstance(String param1, String param2) {
        FunctionListFragment fragment = new FunctionListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_function_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        String[] functionStrings = getResources().getStringArray(R.array.app_function_list);

        ListView functionList = (ListView) getView().findViewById(R.id.function_list);

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, functionStrings);

        functionList.setAdapter(adapter);

        functionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int orientation=getContext().getResources().getConfiguration().orientation;
                int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                if(orientation== Configuration.ORIENTATION_PORTRAIT && screenSize != Configuration.SCREENLAYOUT_SIZE_LARGE){

                    switch(position) {

                        case 0:
                            CurrentPriceFragment currentPriceFragment = new CurrentPriceFragment();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.list_space, currentPriceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            break;

                        case 1:
                            PriceChartFragment priceChartFragment = new PriceChartFragment();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.list_space, priceChartFragment)
                                    .addToBackStack(null)
                                    .commit();
                            break;

                        case 2:
                            BlockInfoFragment blockInfoFragment = new BlockInfoFragment();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.list_space, blockInfoFragment)
                                    .addToBackStack(null)
                                    .commit();
                            break;

                        case 3:
                            AddressBalanceFragment addressBalanceFragment = new AddressBalanceFragment();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.list_space, addressBalanceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            break;

                    }

                }
                else {

                    switch (position) {

                        case 0:
                            CurrentPriceFragment currentPriceFragment = new CurrentPriceFragment();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.right_fragment, currentPriceFragment)
                                    .commit();
                            break;

                        case 1:
                            PriceChartFragment priceChartFragment = new PriceChartFragment();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.right_fragment, priceChartFragment)
                                    .commit();
                            break;

                        case 2:
                            BlockInfoFragment blockInfoFragment = new BlockInfoFragment();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.right_fragment, blockInfoFragment)
                                    .commit();
                            break;

                        case 3:
                            AddressBalanceFragment addressBalanceFragment = new AddressBalanceFragment();

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.right_fragment, addressBalanceFragment)
                                    .commit();
                            break;

                    }

                }

            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
