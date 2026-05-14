package com.example.everything;

import android.content.Intent;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.everything.models.api.CreateEventRequest;
import com.example.everything.models.api.EventDto;
import com.google.android.material.textfield.TextInputEditText;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDate, etTime, etLocation, etDescription;
    private String communityId;
    private String communityName;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        communityId = getIntent().getStringExtra("communityId");
        communityName = getIntent().getStringExtra("communityName");

        setupToolbar();
        setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Event");
        }
    }

    private void setupViews() {
        etTitle = findViewById(R.id.etEventTitle);
        etDate = findViewById(R.id.etEventDate);
        etTime = findViewById(R.id.etEventTime);
        etLocation = findViewById(R.id.etEventLocation);
        etDescription = findViewById(R.id.etEventDescription);
        btnCreate = findViewById(R.id.btnCreateEvent);
        TextView tvCommunity = findViewById(R.id.tvEventCommunity);

        if (communityName != null) {
            tvCommunity.setText("Creating event in: " + communityName);
        }

        etDate.setOnClickListener(v -> showDatePicker());

        etTime.setOnClickListener(v -> showTimePicker());

        btnCreate.setOnClickListener(v -> createEvent());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String[] months = {"January","February","March","April","May",
                    "June","July","August","September","October","November","December"};
            String formatted = months[selectedMonth] + " " + selectedDay + ", " + selectedYear;
            etDate.setText(formatted);
        }, year, month, day).show();
    }

    private void showTimePicker() {
        // start the clock on the current hour
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {

            String amPm = selectedHour >= 12 ? "PM" : "AM";
            int displayHour = selectedHour % 12;
            if (displayHour == 0) displayHour = 12;
            String formatted = String.format("%d:%02d %s",
                    displayHour, selectedMinute, amPm);
            etTime.setText(formatted);
        }, hour, minute, false).show();
    }

    private void createEvent() {
        String title = etTitle.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time.isEmpty()) {
            Toast.makeText(this, "Please pick a time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (location.isEmpty()) {
            etLocation.setError("Location is required");
            return;
        }
        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            return;
        }

        String eventDateTime = combineDateTime(date, time);
        if (eventDateTime == null) {
            Toast.makeText(this, "Invalid date or time format", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCreate.setEnabled(false);
        btnCreate.setText("Creating...");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        CreateEventRequest request = new CreateEventRequest(title, description, eventDateTime, location);

        apiService.createCommunityEvent(communityIdInt, request).enqueue(new Callback<EventDto>() {
            @Override
            public void onResponse(Call<EventDto> call, Response<EventDto> response) {
                btnCreate.setEnabled(true);
                btnCreate.setText("Create Event");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateEventActivity.this, "Event created!", Toast.LENGTH_SHORT).show();
                    
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("eventCreated", true);
                    setResult(RESULT_OK, resultIntent);
                    
                    finish();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventDto> call, Throwable t) {
                btnCreate.setEnabled(true);
                btnCreate.setText("Create Event");
                Toast.makeText(CreateEventActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String combineDateTime(String dateStr, String timeStr) {
        try {
            SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
            
            Date date = dateOnlyFormat.parse(dateStr);
            Date time = timeOnlyFormat.parse(timeStr);
            
            if (date != null && time != null) {
                // Combine date and time
                Calendar dateCal = Calendar.getInstance();
                dateCal.setTime(date);
                
                Calendar timeCal = Calendar.getInstance();
                timeCal.setTime(time);
                
                dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                dateCal.set(Calendar.SECOND, 0);
                
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                return isoFormat.format(dateCal.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}