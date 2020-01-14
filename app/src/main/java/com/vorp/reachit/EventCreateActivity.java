package com.vorp.reachit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventCreateActivity extends MapScreen{

    private Calendar calendar = Calendar.getInstance();
    private TextView startDate;
    private Date eventDate;
    private boolean datePicked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_create);

        Button btn = (Button)findViewById(R.id.button);
        TextView backButton = findViewById(R.id.backButton);
        final EditText text = (EditText)findViewById(R.id.edit_text);
        final AutoCompleteTextView autocomplete =  findViewById(R.id.autocrr);
        Button datePicker = findViewById(R.id.startDate);
        Button timePicker = findViewById(R.id.timePick);
        startDate=findViewById(R.id.date);
        GeocoderAdapter adapter = new GeocoderAdapter(this);
        final GeoPoint location = new GeoPoint(getIntent().getDoubleExtra("LAT",-1.0),getIntent().getDoubleExtra("LONG",-1.0));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Log.d("GEOPOINTTEST",location.getLatitude()+" "+location.getLongitude());
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(EventCreateActivity.this,timerPickerListener,
                        Calendar.HOUR, Calendar.MINUTE, false);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EventCreateActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        autocomplete.setText(getIntent().getStringExtra("ADDRESS"));

        autocomplete.setLines(1);
        autocomplete.setAdapter(adapter);



        btn.setOnClickListener(new View.OnClickListener() {
            TextView nameAlerts = findViewById(R.id.nameInputAlerts);
            TextView addressAlerts = findViewById(R.id.addressInputAlerts);
            @Override
            public void onClick(View view) {
                if(text.getText().length()>0&&datePicked&&autocomplete.getText().length()>0) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", text.getText().toString());
                    data.put("emoji", "lol");
                    data.put("latlong", location);
                    data.put("startdate",new Date(calendar.getTimeInMillis()));

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Events").document(GenerateHash.getHash(20))
                            .set(data, SetOptions.merge());

                    view.setVisibility(View.GONE);
                    ProgressBar progressBar = findViewById(R.id.indeterminateBar);
                    progressBar.setVisibility(View.VISIBLE);

                } else{
                    if(text.getText().length()==0)
                        nameAlerts.setText(R.string.required);
                    if(autocomplete.getText().length()==0)
                        addressAlerts.setText(R.string.required);
                    if(!datePicked){
                        startDate.setTextColor(getResources().getColor(R.color.alert));
                        startDate.setText(R.string.date_not_picked);
                    }
                }
            }
        });

    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d",Locale.getDefault());
            calendar.set(year,monthOfYear,dayOfMonth);
            eventDate = calendar.getTime();
            String s =calendar.getDisplayName(Calendar.DATE,Calendar.LONG, Locale.US);
            startDate.setText(sdf.format(calendar.getTime()));
            startDate.setTextColor(getResources().getColor(R.color.colorAccent));
            datePicked=true;
        }

    };

    private TimePickerDialog.OnTimeSetListener timerPickerListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        }
    };

}
