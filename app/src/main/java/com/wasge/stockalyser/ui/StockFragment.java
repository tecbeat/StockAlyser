package com.wasge.stockalyser.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.wasge.stockalyser.MainActivity;
import com.wasge.stockalyser.R;
import com.wasge.stockalyser.util.ApiManager;
import com.wasge.stockalyser.util.DatabaseManager;
import com.wasge.stockalyser.util.REQUEST_TYPE;
import com.wasge.stockalyser.util.ToastyAsyncTask;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class StockFragment extends Fragment {


    private String TAG = "StockFragment";
    private MainActivity mainActivity;
    private DatabaseManager dbManager;
    private ChartProcess c;
    private ApiManager mng;

    private LiveChart liveChart;
    private View root;
    private String interval = "15min";

    /**
     * Stock Data:
     * columns: symbol, name, exchange, currency, average;
     **/
    private String symbol, name, exchange, currency, average, date;

    //for testing
    boolean watched = false;

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"entered onResume()");
        if(isInWatchlist())
            mainActivity.setBookmarkStyle(R.drawable.ic_baseline_bookmark);
        else
            mainActivity.setBookmarkStyle(R.drawable.ic_baseline_bookmark_border);
        mainActivity.setBookmarkVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"entered onPause()");
        mainActivity.setBookmarkVisibility(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate() entered");
        mainActivity = null;
        if (getActivity() instanceof MainActivity)
            mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            dbManager = mainActivity.getDatabaseManager();
            mainActivity.subscribeToMain(R.id.navigation_stock, this);
        }
        this.symbol = mainActivity.getSymbol_for_stock_fragment();
        this.c = new ChartProcess(mainActivity);
        this.mng  = new ApiManager(getContext());
        new IntervalDataTask(mainActivity).execute();

    }

    /**
     * Creates a view for the Stockfragment
     * an inizialise it with ChartProcess
     **/
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView() entered");
        root = inflater.inflate(R.layout.fragment_stock, container, false);
        final TextView currentPrice = root.findViewById(R.id.current_price);
        final TextView percentPrice = root.findViewById(R.id.percent_price);
        final TextView inter = root.findViewById(R.id.intervall);
        liveChart = root.findViewById(R.id.live_chart);
        TabLayout tabLayout = root.findViewById(R.id.tablayout);

        c.setData(liveChart, dbManager, symbol, currentPrice, percentPrice);
        c.setTab(tabLayout, liveChart, dbManager, inter, symbol, currentPrice, percentPrice);

        setData(dbManager.getDisplayData(symbol));
        if(symbol != null) {
            if(dbManager.hasStockInfo(symbol))
                setData(dbManager.getDisplayData(symbol));
            else
                new StockDataTask(mainActivity).execute(symbol);
        }
        return root;
    }

    private void setData(String[] data) {
        TextView symbol = root.findViewById(R.id.symbol);
        if(data == null) {
            symbol.setText(this.symbol);
            Log.e(TAG,"error setting data, null recieved");
            return;
        }



        TextView name = root.findViewById(R.id.name_data);
        TextView exchange = root.findViewById(R.id.exchenge_data);
        TextView currency = root.findViewById(R.id.currency_data);
        TextView date = root.findViewById(R.id.date_data);
        TextView open = root.findViewById(R.id.open_data);
        TextView high = root.findViewById(R.id.high_data);
        TextView low = root.findViewById(R.id.low_data);
        TextView close = root.findViewById(R.id.close_data);
        TextView volumen = root.findViewById(R.id.volumen_data);
        TextView avgvolumen = root.findViewById(R.id.avgvolume_data);
        TextView preclose = root.findViewById(R.id.preclose_data);
        TextView range = root.findViewById(R.id.range_data);
        TextView perchange = root.findViewById(R.id.perchange_data);
        TextView yearlow = root.findViewById(R.id.yearlow_data);
        TextView yearhigh = root.findViewById(R.id.yearhigh_data);
        TextView yearlowchange = root.findViewById(R.id.yearlowchange_data);
        TextView yearhighchange = root.findViewById(R.id.yearhighchange_data);
        TextView perlowchange = root.findViewById(R.id.yearperlowchange_data);
        TextView perhighchange = root.findViewById(R.id.yearperhighchange_data);

        try {

            for(int i = 0; i < 5; i++){
                if(i == data.length) {
                    throw new Exception("Error setting data for Stock Fragment," +
                            " data might be incorrect or corrupted!");
                }
                else if(data[i] == null) {
                    throw new Exception("Error setting data for Stock Fragment," +
                            " data might be incorrect or corrupted!");
                }
            }

            //symbol = 0, name = 1, exchange = 2, currency = 3, date = 4, open = 5,
            // high = 6, low = 7, close = 8, volume = 9, avgvolume = 10, preclose = 11,
            // range = 12, perchange = 13, yearlow = 14, yearhigh = 15, yearlowchange = 16,
            // yearhighchange = 17, yearlowchangeper = 18, yearhighchangeper = 19

            this.name = data[1];
            this.exchange = data[2];
            this.currency = data[3];
            this.date = data[4];
            this.average = data[5];


            symbol.setText(this.symbol);
            name.setText(this.name);
            exchange.setText(this.exchange);
            currency.setText(this.currency);
            date.setText(this.date);
            open.setText(data[5]);
            high.setText(data[6]);
            low.setText(data[7]);
            close.setText(data[8]);
            volumen.setText(data[9]);
            avgvolumen.setText(data[10]);
            preclose.setText(data[11]);
            range.setText(data[12].replace(" ","\n"));
            perchange.setText(data[13]);
            yearlow.setText(data[14]);
            yearhigh.setText(data[15]);
            yearlowchange.setText(data[16]);
            yearhighchange.setText(data[17]);
            perlowchange.setText(data[18]);
            perhighchange.setText(data[19]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGraph(float[] output) {
        if (output == null)
            return;
        Dataset dataset2 = new Dataset(c.getDataPoints(output));
        liveChart.setSecondDataset(dataset2)
                .drawBaselineFromFirstPoint()
                .drawDataset();
    }

    public void toggleCurrentToWatchlist(){
        //for testing
        watched = !watched;

        if(isInWatchlist())
            removeFromWatchlist();
        else
            addToWatchlist();
        updateBookmark();
    }

    private void removeFromWatchlist(){
        if(dbManager.removeFromWatchlist(symbol))
            Log.d(TAG, "successfully removed stock: " + symbol + " from watchlist!");
        else {
            Toast.makeText(getContext(), "Remove failed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "failed to remove stock: " + symbol + " from watchlist!");
        }
    }

    private void addToWatchlist(){
        if(dbManager.addToWatchlist(new String[]{symbol, name, exchange, currency, average, date}))
            Log.d(TAG, "successfully added stock: " + symbol + " to watchlist!");
        else {
            Toast.makeText(getContext(), "failed to add stock: " + symbol + " to watchlist!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "failed to add stock: " + symbol + " to watchlist!");
        }

    }

    private void updateBookmark(){
        if(isInWatchlist())
            mainActivity.setBookmarkStyle(R.drawable.ic_baseline_bookmark);
        else
            mainActivity.setBookmarkStyle(R.drawable.ic_baseline_bookmark_border);
    }

    private boolean isInWatchlist(){

        String[] ids = dbManager.getWatchlistStockIDs();
        if(ids == null || ids.length < 1)
            return false;
        for (String id: ids){
            if(id == null)
                continue;
            if(symbol.contentEquals(id))
                return true;
        }
        return false;
    }

    private class StockDataTask extends ToastyAsyncTask<Object,Integer,Integer>{

        private String defaultMessage = "Error occured, couldn't load data properly!";
        public StockDataTask(Context context) {
            super(context);
            message = defaultMessage;
        }

        @Override
        protected void onPostExecute(Integer code) {
            switch (code){
                case 429:
                    errorOccured();
                    duration = Toast.LENGTH_LONG;
                    message = "Too many requests with same API key, please upgrade your plan at https://twelvedata.com/prime.";
                    super.onPostExecute(code);
                    duration = Toast.LENGTH_SHORT;
                    message = defaultMessage;
                case 400:
                    errorOccured();
                    duration = Toast.LENGTH_LONG;
                    message = "Stock not accessible with current API Key, please upgrade your plan at https://twelvedata.com/prime.";
                    super.onPostExecute(code);
                    duration = Toast.LENGTH_SHORT;
                    message = defaultMessage;
                    break;
                case 0:
                    super.onPostExecute(code);
                    break;
                case -1:
                    errorOccured();
                    super.onPostExecute(code);
                    break;
            }
            setData(dbManager.getDisplayData(symbol));
        }

        @Override
        protected Integer doInBackground(Object... objects) {
            try {
                return dbManager.handleData(REQUEST_TYPE.CURRENT_STATUS, new JSONObject(mng.getUrlInformation(mng.buildUrl("quote", symbol))));
            } catch (JSONException e) {
                errorOccured();
                e.printStackTrace();
            }
            return 0;
        }
    }

    private class IntervalDataTask extends ToastyAsyncTask<Object,Integer,Integer>{

        String defaultMessage = "Error occured, Graph Data couldn't load data properly!";
        public IntervalDataTask(Context context) {
            super(context);
            message = defaultMessage;
        }

        /**
         * get interval data
         * 1min, 5min, 15min, 30min, 45min, 1h, 2h, 4h, 1day, 1week, 1month
         */
        @Override
        protected Integer doInBackground(Object... objects) {
            try {
                int daily = dbManager.handleData(REQUEST_TYPE.DAILY,   new JSONObject(mng.getUrlInformation(mng.buildUrl("time_series", symbol,"15min","96"))));
                int weekly = dbManager.handleData(REQUEST_TYPE.WEEKLY,  new JSONObject(mng.getUrlInformation(mng.buildUrl("time_series", symbol,"2h","84"))));
                int monthly = dbManager.handleData(REQUEST_TYPE.MONTHLY, new JSONObject(mng.getUrlInformation(mng.buildUrl("time_series", symbol,"4h","186"))));
                int yearly = dbManager.handleData(REQUEST_TYPE.YEARLY,  new JSONObject(mng.getUrlInformation(mng.buildUrl("time_series", symbol,"1day","356"))));
                int max = dbManager.handleData(REQUEST_TYPE.MAX,     new JSONObject(mng.getUrlInformation(mng.buildUrl("time_series", symbol,"1week","200"))));
                return max(daily,weekly,monthly,yearly,max);
            } catch (Exception e) {
                errorOccured();
                e.printStackTrace();
                return -1;
            }
        }

        private int max(int... ints){
            int out = 0;
            for (int i = 0; i < ints.length;i++){
                out = Math.max(out, ints[i]);
            }
            return out;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            switch (code){
                case 429:
                    errorOccured();
                    duration = Toast.LENGTH_LONG;
                    message = "Too many requests with same API key, please upgrade your plan at https://twelvedata.com/prime.";
                    super.onPostExecute(code);
                    duration = Toast.LENGTH_SHORT;
                    message = defaultMessage;
                case 400:
                    errorOccured();
                    duration = Toast.LENGTH_LONG;
                    message = "Stock not accessible with current API Key, please upgrade your plan at https://twelvedata.com/prime.";
                    super.onPostExecute(code);
                    duration = Toast.LENGTH_SHORT;
                    message = defaultMessage;
                    break;
                case 0:
                    super.onPostExecute(code);
                    break;
                case -1:
                    errorOccured();
                    super.onPostExecute(code);
                    break;
            }
            c.setLiveChart(c.getDataPoints(c.getData(c.tabLayout.getSelectedTabPosition(),c.symbol,dbManager)));
        }
    }
}