package com.wasge.stockalyser.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class apimanager {

    public static String buildUrl(){
        return "https://api.twelvedata.com/time_series?symbol=AAPL&interval=5min&apikey=2d9d8679a270405da1f46095b5b1ae27";
    }


    public static String getUrlInformation(String rowUrl) throws MalformedURLException {
        URL url = new URL(rowUrl);
        StringBuilder s = new StringBuilder();
            try (
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            for (String line; (line = reader.readLine()) != null;) {

                s.append(line);
            }
        } catch (
        IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    } // end of main

    public static void parseJsonFromInterval(String input){
        try {
            JSONObject meta = (JSONObject) new JSONObject(input).get("meta");
            JSONArray values = new JSONObject(input).getJSONArray("values");

            System.out.println(meta.get("symbol"));
            System.out.println(meta.get("symbol"));
            System.out.println(meta.get("symbol"));
            System.out.println(meta.get("symbol"));

            for (int i = 0; i < values.length(); i++){

                System.out.print(values.getJSONObject(i).get("volume"));
                System.out.print(values.getJSONObject(i).get("datetime"));
                System.out.print(values.getJSONObject(i).get("high"));
                System.out.print(values.getJSONObject(i).get("low"));
                System.out.print(values.getJSONObject(i).get("close"));
                System.out.println(values.getJSONObject(i).get("open"));
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}