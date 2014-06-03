
package com.airbitz.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbitz.R;
import com.airbitz.activities.NavigationActivity;
import com.airbitz.adapters.VenueAdapter;
import com.airbitz.api.AirbitzAPI;
import com.airbitz.models.BusinessSearchResult;
import com.airbitz.models.SearchResult;
import com.airbitz.utils.ListViewUtility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2/12/14.
 */
public class VenueFragment extends Fragment implements
                                           GestureDetector.OnGestureListener,
                                           BusinessDirectoryFragment.BusinessScrollListener {

    public static final String TAG = VenueFragment.class.getSimpleName();
    private ListView mVenueListView;
    private List<BusinessSearchResult> mVenues;
    private List<BusinessSearchResult> mTempVenues;
    private boolean mLoadFlag = false;
    private String mNextUrl = "null";
    private boolean isGettingMoreVenueFinished = true;
    private boolean isFirstLoad = true;
    private VenueAdapter mVenueAdapter;
    private TextView mNoResultView;
//    private View mLoadingFooterView;

    private String mLocationName;
    private String mBusinessName;
    private String mBusinessType;

    private boolean loadingVisible = true;
    private int venueAmount = 0;

    private GetVenuesTask mGetVenuesTask;
    private GestureDetector mGestureDetector;

    private boolean mIsInBusinessDirectory = false;
    private boolean mIsInMapBusinessDirectory = false;


    public boolean getIsBusinessDirectory() {
        return mIsInBusinessDirectory;
    }

    public boolean getVisibilityLoading(){ return loadingVisible; }

    public ListView getVenueListView() {
        return mVenueListView;
    }

    public List<BusinessSearchResult> getVenues() {
        return mVenues;
    }

    public void setVenues(List<BusinessSearchResult> venues) {
        mVenues = venues;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_venue, container, false);
        mGestureDetector = new GestureDetector(this);

        // Set-up list
        mVenueListView = (ListView) view.findViewById(R.id.listView);
        mNoResultView = (TextView) view.findViewById(R.id.no_result_view);

        if (mVenues == null) {
            mVenueListView.setVisibility(View.INVISIBLE);
            mVenues = new ArrayList<BusinessSearchResult>();

            mIsInBusinessDirectory = false;
            mIsInMapBusinessDirectory = false;
            if (getParentFragment().getClass().toString().equalsIgnoreCase(BusinessDirectoryFragment.class.toString())) {
                String latLon = "" + getLatFromSharedPreference() + "," + getLonFromSharedPreference();

                mIsInBusinessDirectory = true;
                mGetVenuesTask = new GetVenuesTask(getActivity());
                mGetVenuesTask.execute(latLon);
                ((BusinessDirectoryFragment) getParentFragment()).setBusinessScrollListener(this);

            } else if (getParentFragment().getClass().toString().equalsIgnoreCase(MapBusinessDirectoryFragment.class.toString()))  {
                mIsInMapBusinessDirectory = true;
                mLocationName = getArguments().getString(BusinessDirectoryFragment.LOCATION);
                mBusinessType = getArguments().getString(BusinessDirectoryFragment.BUSINESSTYPE);
                mBusinessName = getArguments().getString(BusinessDirectoryFragment.BUSINESS);

                mGetVenuesTask = new GetVenuesTask(getActivity());
                mGetVenuesTask.execute(mBusinessName, mLocationName, mBusinessType);
            } else {
                mGetVenuesTask = new GetVenuesTask(getActivity());
                String latlong = "" + getLatFromSharedPreference() + "," + getLonFromSharedPreference();
                mGetVenuesTask.execute(latlong);
            }

            int timeout = 15000;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override public void run() {
                    if (mGetVenuesTask.getStatus() == AsyncTask.Status.RUNNING)
                        mGetVenuesTask.cancel(true);
                }
            }, timeout);
        } else {
            setListView(mVenues);
        }

        mVenueListView.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });

        if (mIsInBusinessDirectory) {
            ListViewUtility.setListViewHeightBasedOnChildren(mVenueListView);
        }

        return view;
    }

    private void hideLoadingIndicator() {
       if (mIsInBusinessDirectory) {
           ((BusinessDirectoryFragment)getParentFragment()).hideLoadingIndicator();
           loadingVisible = false;//TODO
      }
    }

    @Override public void onScrollEnded() {
        if (isFirstLoad) {
            isFirstLoad = false;
            GetRemainingFirstVenuesTask getRemainingFirstVenuesTask = new GetRemainingFirstVenuesTask(getActivity());
            getRemainingFirstVenuesTask.execute("");
            venueAmount = 20;
        } else {
            if( venueAmount < 100) {
                if (isGettingMoreVenueFinished) {
                    isGettingMoreVenueFinished = false;

                    GetMoreVenuesTask getMoreVenuesTask = new GetMoreVenuesTask(getActivity());
                    getMoreVenuesTask.execute(mNextUrl);
                    venueAmount += 20;
                }
            }

        }
    }

    private class GetVenuesTask extends AsyncTask<String, Void, String> {

        AirbitzAPI mApi = AirbitzAPI.getApi();
        Context mContext;

        public GetVenuesTask(Context context) {
            mContext = context;
        }

        @Override protected void onPreExecute() {
//            mVenueListView.addFooterView(mLoadingFooterView);
            if (mIsInBusinessDirectory) {
                ListViewUtility.setListViewHeightBasedOnChildren(mVenueListView, mContext);
            }
            
            Log.d(TAG, "VenueFragment: GetVenuesTask");
        }

        @Override protected String doInBackground(String... params) {
            String result = "";
            if (mIsInBusinessDirectory) {
                result = mApi.getSearchByLatLong(params[0], "", "", "1");

            } else if (mIsInMapBusinessDirectory) {
                String latlong = "" + getLatFromSharedPreference() + "," + getLonFromSharedPreference();
                result = mApi.getSearchByCategoryOrBusinessAndLocation(params[0], params[1], "", "",
                                                                       "1", params[2], latlong);
            }

            return result;
        }

        @Override protected void onCancelled() {
//            mVenueListView.removeFooterView(mLoadingFooterView);
            mNoResultView.setVisibility(View.VISIBLE);
            hideLoadingIndicator();
            Toast.makeText(mContext, "Can not retrieve data",
                           Toast.LENGTH_LONG).show();
            super.onCancelled();
        }

        @Override protected void onPostExecute(String searchResult) {
//            mProgressDialog.dismiss();
            try {
                mVenues.clear();
                processSearchResults(searchResult);

                setListView(mVenues);

                if (mIsInBusinessDirectory) {
                    ListViewUtility.setListViewHeightBasedOnChildren(mVenueListView, mContext);
                }
            } catch (JSONException e) {
                mNoResultView.setVisibility(View.VISIBLE);
                hideLoadingIndicator();
                e.printStackTrace();
                this.cancel(true);
            } catch (Exception e) {
                mNoResultView.setVisibility(View.VISIBLE);
                hideLoadingIndicator();
                e.printStackTrace();
                this.cancel(true);
            }

        }
    }

    private void showDirectoryDetailFragment(String id, String name, String distance) {
        Bundle bundle = new Bundle();
        bundle.putString(DirectoryDetailFragment.BIZID, id);
        bundle.putString("", name);
        bundle.putString("", distance);
        Fragment fragment = new DirectoryDetailFragment();
        fragment.setArguments(bundle);
        ((NavigationActivity) getActivity()).pushFragment(fragment);
    }


    public void setListView(String searchResults) {

        try {
            processSearchResults(searchResults);
            setListView(mVenues);

        } catch (JSONException e) {
            mNoResultView.setVisibility(View.VISIBLE);
            hideLoadingIndicator();
            e.printStackTrace();
        } catch (Exception e) {
            mNoResultView.setVisibility(View.VISIBLE);
            hideLoadingIndicator();
            e.printStackTrace();
        }
    }

    private void setListView(List<BusinessSearchResult> venues) {
        mVenueListView.setVisibility(View.VISIBLE);
        mVenueAdapter = new VenueAdapter(getActivity(), venues);
        mVenueListView.setAdapter(mVenueAdapter);
        preloadVenueImages();

        Log.d(TAG, "items in list view: " + String.valueOf(mVenueAdapter.getCount()));

        mVenueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDirectoryDetailFragment(mVenues.get(i).getId(), mVenues.get(i).getName(), mVenues.get(i).getDistance());
            }
        });

        mVenueListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override public void onScroll(AbsListView view,
                                           int firstVisibleItem,
                                           int visibleItemCount,
                                           int totalItemCount) {

                if (!mNextUrl.equalsIgnoreCase("null")) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                        if (mLoadFlag == false) {
                            mLoadFlag = true;
                            if (!mIsInBusinessDirectory) {

                                GetMoreVenuesTask getMoreVenuesTask = new GetMoreVenuesTask(getActivity());
                                getMoreVenuesTask.execute(mNextUrl);
                            }
                        }
                    }
                }
            }
        });
        Log.d(TAG, "refreshing list view");
    }

    void processSearchResults(String searchResult) throws JSONException {

        SearchResult results = new SearchResult(new JSONObject(searchResult));

        mNextUrl = "null";
        mTempVenues = new ArrayList<BusinessSearchResult>();
        if (results != null) {
            mNextUrl = results.getNextLink();
            mTempVenues = results.getBusinessSearchObjectArray();
            Log.d(TAG, "total venues from results: " + String.valueOf(mTempVenues.size()));
        }

        if (mTempVenues.isEmpty() && mVenues.isEmpty()) {
            mNoResultView.setVisibility(View.VISIBLE);
            hideLoadingIndicator();
        } else {

            if (mIsInBusinessDirectory) {

                if (mTempVenues.size() >= 5) {
                    for (int i = 0; i < 5; i++) {
                        mVenues.add(mTempVenues.get(i));
                    }

                    for (int i = 4; i >= 0; i--) {
                        mTempVenues.remove(i);
                    }
                } else {
                    for (int i = 0; i < mTempVenues.size(); i++) {
                        mVenues.add(mTempVenues.get(i));
                    }
                    for (int i = mTempVenues.size() - 1; i >= 0; i--) {
                        mTempVenues.remove(i);
                    }
                }
            } else {
                mVenues = mTempVenues;
            }
            mNoResultView.setVisibility(View.GONE);
        }

        Log.d(TAG, "total venues: " + String.valueOf(mVenues.size()));
    }

    private void preloadVenueImages() {
        if (mVenues != null) {
            for (BusinessSearchResult venue : mVenues) {
                Picasso.with(getActivity()).load(venue.getProfileImage().getImageThumbnail()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.d(TAG, "Loaded from: " + from.toString());
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            }
        }
    }

    private class GetMoreVenuesTask extends AsyncTask<String, Void, String> {

        AirbitzAPI mApi = AirbitzAPI.getApi();
        Context mContext;

        public GetMoreVenuesTask(Context context) {
            mContext = context;
        }

        @Override protected void onPreExecute() {
//            mVenueListView.addFooterView(mLoadingFooterView);
            if (mIsInBusinessDirectory) {
                ListViewUtility.setListViewHeightBasedOnChildren(mVenueListView, mContext);
            }
        }

        @Override protected String doInBackground(String... params) {
            if (params[0].equalsIgnoreCase("null")) {
                return "";
            }
            return mApi.getRequest(params[0]);
        }

        @Override protected void onCancelled() {
//            mVenueListView.removeFooterView(mLoadingFooterView);
            mNoResultView.setVisibility(View.VISIBLE);
            hideLoadingIndicator();
            Toast.makeText(mContext, "Can not retrieve data",
                           Toast.LENGTH_LONG).show();
            super.onCancelled();
        }

        @Override protected void onPostExecute(String searchResult) {
            if (!searchResult.isEmpty()) {
                try {
                    mLoadFlag = false;
                    SearchResult results = new SearchResult(new JSONObject(searchResult));
                    mNextUrl = results.getNextLink();
                    mVenues.addAll(results.getBusinessSearchObjectArray());
                    setListView(mVenues);

                    if (mIsInBusinessDirectory) {
                        ListViewUtility.setListViewHeightBasedOnChildren(mVenueListView,mContext);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    this.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.cancel(true);

                }
            } else {
                hideLoadingIndicator();
            }
            if(venueAmount >= 100){
                hideLoadingIndicator();
            }
            isGettingMoreVenueFinished = true;
        }
    }

    private class GetRemainingFirstVenuesTask extends AsyncTask<String, Void, List<BusinessSearchResult>> {

        AirbitzAPI mApi = AirbitzAPI.getApi();
        Context mContext;
//        ProgressDialog mProgressDialog;

        public GetRemainingFirstVenuesTask(Context context) {
            mContext = context;
        }

        @Override protected void onPreExecute() {
//            mProgressDialog = new ProgressDialog(mContext);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.setMessage("Getting more venues list...");
//            mProgressDialog.setIndeterminate(true);
//            // mProgressDialog.setCancelable(false);
//            if (mIsInBusinessDirectory) {
//                mProgressDialog.show();
//            } else {
//                mProgressDialog.show();
//            }
        }

        @Override protected List<BusinessSearchResult> doInBackground(String... params) {
            return mTempVenues;
        }

        @Override protected void onCancelled() {
//            mProgressDialog.dismiss();
            mNoResultView.setVisibility(View.VISIBLE);
            hideLoadingIndicator();
            Toast.makeText(mContext, "Can not retrieve data",
                           Toast.LENGTH_LONG).show();
            super.onCancelled();
        }

        @Override protected void onPostExecute(List<BusinessSearchResult> searchResult) {
            if (!searchResult.isEmpty()) {

                mVenues.addAll(mTempVenues);
                mVenueAdapter.notifyDataSetChanged();
                preloadVenueImages();
                if (mIsInBusinessDirectory) {
                    ListViewUtility.setListViewHeightBasedOnChildren(mVenueListView, mContext);
                }
            }
//            mProgressDialog.dismiss();
        }
    }

    private float getStateFromSharedPreferences(String key) {
        if(getActivity() == null){
            System.out.println("WTF MY ACTIVITY KILLED ITSELF");
        }
        SharedPreferences pref = getActivity().getSharedPreferences(BusinessDirectoryFragment.PREF_NAME,
                                                                    Context.MODE_PRIVATE);
        return pref.getFloat(key, -1);
    }

    private double getLatFromSharedPreference() {
        double lat = (double) getStateFromSharedPreferences(BusinessDirectoryFragment.LAT_KEY);
        return lat;
    }

    private double getLonFromSharedPreference() {
        double lon = (double) getStateFromSharedPreferences(BusinessDirectoryFragment.LON_KEY);
        return lon;
    }

    @Override public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override public void onShowPress(MotionEvent motionEvent) {

    }

    @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override public void onLongPress(MotionEvent motionEvent) {

    }

    @Override public boolean onFling(MotionEvent start, MotionEvent finish, float v, float v2) {
        if (start != null & finish != null) {

            float yDistance = Math.abs(finish.getY() - start.getY());

            if ((finish.getRawX() > start.getRawX()) && (yDistance < 10)) {
                float xDistance = Math.abs(finish.getRawX() - start.getRawX());

                if (xDistance > 100) {
                    getActivity().finish();
                    return true;
                }
            }

        }
        return false;
    }

}
