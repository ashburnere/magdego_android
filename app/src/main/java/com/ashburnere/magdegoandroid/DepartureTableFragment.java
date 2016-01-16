package com.ashburnere.magdegoandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents the departure times for a station.
 */
public class DepartureTableFragment extends Fragment {
	
    public static final String ARG_OBJECT = "object";
    private String station;
    private JSONArray data;
  
    /**
     * Sets the data to show in this fragment
     * 
     * @param data
     */
    public void setData(final String station, final JSONArray data) {
    	this.station = station;
    	this.data = data;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) 
    {
    	final View rootView = inflater.inflate(R.layout.departure_fragment, container, false);
        try {
            ((TextView)rootView.findViewById(R.id.departure_station)).setText(this.station);
        	final TableLayout table = (TableLayout)rootView.findViewById(R.id.departure_table);
			this.fillTable(table);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return rootView;
    }

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private static final long now = new Date().getTime();
	private void fillTable(final TableLayout table) throws JSONException {

		if (this.data == null) {
			return;
		}

		// this.data.getJSONObject(index)

		addHeaderRow(table);
		for (int i = 0; i < this.data.length(); i++) {
		
			JSONObject row = this.data.getJSONObject(i);
			DepartureInfo departureInfo = new DepartureInfo(row, now);
			String departureText;

			this.addTableRow(table,
					departureInfo.getDeparture(),
					departureInfo.getDirection(),
					departureInfo.getLine(),
					departureInfo.getLiveStatus());
	
		}
    }
	private void addHeaderRow(final TableLayout table) {
		TableRow tablerow = new TableRow(this.getActivity());
		tablerow.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.WRAP_CONTENT));


		TextView tv;

		// departure time
		tv = new TextView(this.getActivity());
		tv.setText(R.string.departure);


		TableRow.LayoutParams cellLayoutParams = new TableRow.LayoutParams(
				0, TableRow.LayoutParams.MATCH_PARENT, 0.2f);
		tv.setLayoutParams(cellLayoutParams);
		tablerow.addView(tv);

		tv = new TextView(this.getActivity());
		tv.setText(R.string.direction);
		cellLayoutParams = new TableRow.LayoutParams(
				0, TableRow.LayoutParams.MATCH_PARENT, 0.6f);
		tv.setLayoutParams(cellLayoutParams);
		tablerow.addView(tv);


		tv = new TextView(this.getActivity());
		tv.setText(R.string.line);
		cellLayoutParams = new TableRow.LayoutParams(
				0, TableRow.LayoutParams.MATCH_PARENT, 0.2f);
		tv.setLayoutParams(cellLayoutParams);
		tablerow.addView(tv);

		table.addView(tablerow);
	}
	private void addTableRow(final TableLayout table, final String time, 
			final String direction, final String line, final DepartureInfo.LiveStatus liveStatus)
	{
		TableRow tablerow = new TableRow(this.getActivity());
		tablerow.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.WRAP_CONTENT));

		TextView tv;

		// departure time
		tv = new TextView(this.getActivity());
		tv.setText(time);
		TableRow.LayoutParams cellLayoutParams = new TableRow.LayoutParams(
				0, TableRow.LayoutParams.MATCH_PARENT, 0.2f);
		tv.setLayoutParams(cellLayoutParams);
		tablerow.addView(tv);

		switch (liveStatus) {
			case IN_TIME:
				tv.setTextColor(Color.GREEN);
				break;
			case LATE:
				tv.setTextColor(Color.RED);
				break;
			default:
				// do nothing
				break;
		}

		// direction
		tv = new TextView(this.getActivity());
		tv.setText(direction);
		cellLayoutParams = new TableRow.LayoutParams(
				0, TableRow.LayoutParams.MATCH_PARENT, 0.6f);
		tv.setLayoutParams(cellLayoutParams);
		tablerow.addView(tv);
		
		// line
		tv = new TextView(this.getActivity());
		tv.setText(line);
		cellLayoutParams = new TableRow.LayoutParams(
				0, TableRow.LayoutParams.MATCH_PARENT, 0.2f);
		tv.setLayoutParams(cellLayoutParams);
		tablerow.addView(tv);

		table.addView(tablerow);
	}
}
