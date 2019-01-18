package com.elchananalon.decibelmeter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class LoudestMeasurements extends AppCompatActivity {

    private ArrayList<Measurement> measurmentsList;
    private MeasurementsAdapter myAdapter;
    private ListView measurementsView;
    private SQLiteDatabase measurementsDB = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loudest_measurements);

        // init view + list
        measurementsView = findViewById(R.id.dlist_measurements); //set the custom list view
        measurmentsList = new ArrayList<>();

        // open DB and then load last measurements
        measurementsDB = openOrCreateDatabase("Measurements", MODE_PRIVATE, null);
        String sql = "CREATE TABLE IF NOT EXISTS measurements (id integer primary key, location VARCHAR, timeTaken VARCHAR, result VARDOUBLE,waypoints VARCHAR);";
        measurementsDB.execSQL(sql);

        loadLastMeasurements();

        // Create adapter and set it to display
        myAdapter = new MeasurementsAdapter(this, R.layout.measurements_list_view, measurmentsList);

        measurementsView.setAdapter(myAdapter);
        measurementsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id)
            {
                System.out.println("====Tests====");



            }
        });
    }

    // To load the last measurements from DB
    private void loadLastMeasurements(){
        //String sql =  "SELECT * FROM measurements ORDER BY result LIMIT 5";
        String sql = "SELECT * FROM measurements ORDER BY result DESC LIMIT 5";
        Cursor cursor = measurementsDB.rawQuery(sql, null);

        int locationColumn = cursor.getColumnIndex("location");
        int timeTakenColumn = cursor.getColumnIndex("timeTaken");
        int resultColumn = cursor.getColumnIndex("result");
        int wayPointsColumn = cursor.getColumnIndex("waypoints");

        System.out.println("=============================== "+ cursor.getCount());
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
                    measurmentsList.add( new Measurement(Double.valueOf(result), location, waypoints, time));

                    System.out.println("d");

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
