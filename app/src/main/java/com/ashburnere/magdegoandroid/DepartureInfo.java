package com.ashburnere.magdegoandroid;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erik on 14.01.16.
 *
 * POJO which holds the departure information for one station and line.
 *
 */
public class DepartureInfo {

    public static enum LiveStatus {
        NO_LIVE_STATUS,
        LATE,
        IN_TIME
    }


    private String departure;
    private LiveStatus liveStatus;
    private String line;
    private String direction;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Default constructor.
     *
     * @param data a JSON object in format <code>{"line":"Str 6","direction":"Cracau, Pechauer Platz","delay":{"minutes":"0"},"departure":"2016-01-12T10:13:22.526Z"}</code>
     * @param requestTime the time of the original request
     */
    public DepartureInfo(final JSONObject data, final long requestTime) {

        try {

            // get line
            this.direction = data.getString(DepartureViewActivity.KEY_DIRECTION);

            // get direction
            this.line = data.getString(DepartureViewActivity.KEY_LINE);

            // parse the date and cut off the Z at the end (always UTC)
            String s = data.getString(DepartureViewActivity.KEY_DEPARTURE);
            Date time = dateFormat.parse(s.substring(0, s.length() - 1));
            // add one hour because time is in UTC
            long timeLong = time.getTime() + 3600000;

            int delayInMinutes = 0;
            if (data.has(DepartureViewActivity.KEY_DELAY)) {
                delayInMinutes = data.getJSONObject(DepartureViewActivity.KEY_DELAY).getInt("minutes");
                if (delayInMinutes <= 0) {
                    liveStatus = LiveStatus.IN_TIME;
                } else {
                    liveStatus = LiveStatus.LATE;
                    // add the delay to the departure time
                    timeLong += delayInMinutes * 60000;
                }
            } else {
                liveStatus = LiveStatus.NO_LIVE_STATUS;
            }

            long diffInMinutes = (timeLong - requestTime) / 60000;
            if (diffInMinutes == 0) {
                departure = "jetzt";
            } else if(diffInMinutes < 30) {
                departure = diffInMinutes + " min";
            } else {
                departure = new SimpleDateFormat("HH:mm").format(new Date(timeLong));
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(),
                    "Failed creating departure info for JSON object: " + data);
        }
    }

    public String getDeparture() {
        return departure;
    }

    public LiveStatus getLiveStatus() {
        return liveStatus;
    }

    public String getLine() {
        return line;
    }

    public String getDirection() {
        return direction;
    }


}
