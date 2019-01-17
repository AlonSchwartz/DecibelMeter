package com.elchananalon.decibelmeter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MeasurementsAdapter extends ArrayAdapter<Measurement> {

    private ArrayList<Measurement> measurments;
    private Context context;
    private int resource;

    public MeasurementsAdapter(Context context, int resource, ArrayList<Measurement> measurments) {
        super(context, resource, measurments);
        this.measurments = measurments;
        this.context = context;
        this.resource = resource;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // For getting the view of the xml
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Actually getting the view
        View view = layoutInflater.inflate(resource, null, false);

        // binding elements to view elements
        ImageView mapTicker = view.findViewById(R.id.img_mapTicker);
        TextView result = view.findViewById(R.id.txt_result);
        TextView location = view.findViewById(R.id.txt_location);
        TextView timeTaken = view.findViewById(R.id.txt_location);
        TextView date = view.findViewById(R.id.txt_date);

        //getting the contact of the specified position
        Measurement measurment = measurments.get(position);


        //adding values to the list
        //mapTicker.setImageDrawable(context.getResources().getDrawable());
        result.setText(Double.toString(measurment.getDb()) + " dB");
        //location.setText(measurment.getPlace()); // will be changed to Address
        location.setText(measurment.getPlace());
        //timeTaken.setText(measurment.getCurr_time());
        date.setText(measurment.getCurr_time());


        mapTicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Debug", "Ticker onClick");

            }
        });

        return view;
    }



}
