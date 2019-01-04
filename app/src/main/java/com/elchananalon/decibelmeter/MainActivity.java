package com.elchananalon.decibelmeter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private Button btn_startMeasurement, btn_lastMeasurements, btn_loudestMeasurements;
    private SQLiteDatabase measurementsDB = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reference to UI components
        btn_startMeasurement = findViewById(R.id.btn_measure);
        btn_lastMeasurements = findViewById(R.id.btn_lastMeasures);
        btn_loudestMeasurements = findViewById((R.id.btn_loudest));

        // event listeners
        btn_startMeasurement.setOnClickListener(this);
        btn_lastMeasurements.setOnClickListener(this);
        btn_loudestMeasurements.setOnClickListener(this);

        // Init Database
        measurementsDB = openOrCreateDatabase("Measurements", MODE_PRIVATE, null);
        String sql = "CREATE TABLE IF NOT EXISTS contacts (id integer primary key, location VARCHAR, timeTaken VARCHAR, result VARCHAR);";
        measurementsDB.execSQL(sql);

    }


    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.btn_measure:
                Intent in = new Intent(this, Measurment.class);
                startActivity(in);
                break;

            case R.id.btn_lastMeasures:
                Intent in2 = new Intent(this, LastMeasurments.class);
                startActivity(in2);
                break;

            case R.id.btn_loudest:
                Intent in3 = new Intent(this, LoudestMeasurements.class);
                startActivity(in3);
                break;
        }
    }
}
