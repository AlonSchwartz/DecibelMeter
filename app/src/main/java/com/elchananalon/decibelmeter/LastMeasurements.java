package com.elchananalon.decibelmeter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        measurementsView = findViewById(R.id.dlist_measurements);
        measurmentsList = new ArrayList<>();

        //measurmentsList.add(new Measurement(52, 542,563,5426));
        measurementsDB = openOrCreateDatabase("Measurements", MODE_PRIVATE, null);
        String sql = "CREATE TABLE IF NOT EXISTS measurements (id integer primary key, location VARCHAR, timeTaken VARCHAR, result VARDOUBLE);";
        measurementsDB.execSQL(sql);

        loadLastMeasurements();


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

    private void loadLastMeasurements(){
        String sql =  "SELECT * FROM measurements";
        Cursor cursor = measurementsDB.rawQuery(sql, null);

        int locationColumn = cursor.getColumnIndex("location");
        int timeTakenColumn = cursor.getColumnIndex("timeTaken");
        int resultColumn = cursor.getColumnIndex("result");

        //String contactName = cursor.getString(e);
        System.out.println("=============================== "+ cursor.getCount());
        cursor.moveToFirst();
        if(cursor != null && (cursor.getCount() > 0)){
            // As long we have data - get it and add it to the contacts list
            do{
                String location = cursor.getString(locationColumn);
                String time = cursor.getString(timeTakenColumn);
                String result = cursor.getString(resultColumn);

                // index is 0 to show latest measurements first
                measurmentsList.add(0,new Measurement(Double.valueOf(result), location,time));

                System.out.println("d");

            }while(cursor.moveToNext());
        }



    }
}
