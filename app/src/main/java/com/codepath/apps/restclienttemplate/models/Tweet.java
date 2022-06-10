package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    public String body;
   // public String createdAt;
    public User user;
    public String embedImageUrl;
    private Date createdAt;
    public static final String TAG = "Tweet";

    //empty constructor needed by the parceler library
    public Tweet() {
    }

    //turn json to java tweet object
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException, ParseException {
        Tweet tweet = new Tweet();
        JSONObject entities = jsonObject.getJSONObject("entities");

        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        if(entities.has("media")) {
            JSONArray mediaArray = jsonObject.getJSONObject("entities").getJSONArray("media");
            Log.d(TAG, "has a media array" + mediaArray);
            JSONObject o = (JSONObject) mediaArray.get(0);
            Log.d(TAG, "url of the media attachment" + o.getString("media_url_https"));
            tweet.embedImageUrl = o.getString("media_url_https");

        }


        //tweet.body = jsonObject.getString("text");
        //tweet.createdAt = jsonObject.getString("created_at");
        tweet.createdAt = Tweet.parseTwitterDate(jsonObject.getString("created_at"));
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

        return tweet;

    }

    //pass in a json array and get a list of tweets
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException, ParseException {
        List<Tweet> tweets = new ArrayList<>();

        //loop thru the json array
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }
    public static Date parseTwitterDate(String date) throws ParseException
    {
        final String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        try {
            return sf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sf.parse(date);
    }

    public String getRelativeTimeAgo() {
        Date system_date = createdAt;
        Date user_date = new Date();
        double diff = Math.floor((user_date.getTime() - system_date.getTime()) / 1000);
        if (diff <= 1) {return "just now";}
        if (diff < 20) {return diff + "s";}
        if (diff < 40) {return "30s";}
        if (diff < 60) {return "45s";}
        if (diff <= 90) {return "1m";}
        if (diff <= 3540) {return Math.round(diff / 60) + "m";}
        if (diff <= 5400) {return "1h";}
        if (diff <= 86400) {return Math.round(diff / 3600) + "h";}
        if (diff <= 129600) {return "1d";}
        if (diff < 604800) {return Math.round(diff / 86400) + "d";}
        if (diff <= 777600) {return "1w";}
        return Math.round(diff / 604800) + "w";
    }

}
