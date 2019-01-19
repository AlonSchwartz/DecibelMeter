package com.elchananalon.decibelmeter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class LastMeasurements extends AppCompatActivity {

    private ArrayList<Measurement> measurmentsList;
    private MeasurementsAdapter myAdapter;
    private ListView measurementsView;
    private SQLiteDatabase measurementsDB = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_measurements);

        // init view + list
        measurementsView = findViewById(R.id.dlist_measurements); //set the custom list view
        measurmentsList = new ArrayList<>();

        // open DB and then load last measurements
        measurementsDB = openOrCreateDatabase("Measurements", MODE_PRIVATE, null);
        String sql = "CREATE TABLE IF NOT EXISTS measurements (id integer primary key, location VARCHAR, timeTaken VARCHAR, result VARDOUBLE,  waypoints VARCHAR);";
        measurementsDB.execSQL(sql);

        loadLastMeasurements();

        // Create adapter and set it to display
        myAdapter = new MeasurementsAdapter(this, R.layout.measurements_list_view, measurmentsList);
        measurementsView.setAdapter(myAdapter);

    }

    // To load the last measurements from DB
    private void loadLastMeasurements(){
        String sql =  "SELECT * FROM measurements";
        Cursor cursor = measurementsDB.rawQuery(sql, null);

        int locationColumn = cursor.getColumnIndex("location");
        int timeTakenColumn = cursor.getColumnIndex("timeTaken");
        int resultColumn = cursor.getColumnIndex("result");
        int wayPointsColumn = cursor.getColumnIndex("waypoints");

        cursor.moveToFirst();

        // try-finally to MAKE SURE that even if some error occurred while reading cursor - the cursor will be closed. it will prevent memory leak.
        // We want cursor the be closed as soon as we finish our work with it.
        try {
            if (cursor != null && (cursor.getCount() > 0)) {
                // As long we have data - get it and add it to the measurements list
                do {
                    String location = cursor.getString(locationColumn);
                    String time = cursor.getString(timeTakenColumn);
                    String result = cursor.getString(resultColumn);
                    String waypoints = cursor.getString(wayPointsColumn);

                    // index is 0 to show latest measurements first
                    measurmentsList.add(0, new Measurement(Double.valueOf(result), location,waypoints, time));

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

    }

    protected void onDestroy()
    {
        measurementsDB.close();
        super.onDestroy();
    }
}
