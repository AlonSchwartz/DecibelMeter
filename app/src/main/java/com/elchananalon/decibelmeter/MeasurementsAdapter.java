package com.elchananalon.decibelmeter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        TextView date = view.findViewById(R.id.txt_date);

        //getting the contact of the specified position
        Measurement measurment = measurments.get(position);


        //adding values to the list
        result.setText(Double.toString(measurment.getDb()) + " dB");
        location.setText(measurment.getPlace());
        date.setText(measurment.getCurr_time());


        // Open location of google maps - if google maps is installed and enabled
        mapTicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGoogleMapsInstalledAndEnabled()) {
                    String uri = "geo:0,0?q=" + measurments.get(position).getWaypoints() + "(" + measurments.get(position).getPlace() + ")";
                    Uri gmmIntentUri = Uri.parse(uri);
                    Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    intent.setPackage("com.google.android.apps.maps");

                    context.startActivity(intent);
                }
                else{
                    Toast.makeText(context, "Google maps is disabled or not installed", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
    public boolean isGoogleMapsInstalledAndEnabled()
    {
        try
        {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            if (info.enabled)
                return true;
            return false;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }



}
