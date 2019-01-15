package com.elchananalon.decibelmeter;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_measurements);

        measurementsView = findViewById(R.id.dlist_measurements);
        measurmentsList = new ArrayList<>();

        measurmentsList.add(new Measurement(52, 542,563,5426));

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
}
