package com.pl.deepbisdksample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pl.deepbisdk.DeepBiManager;

public class CustomEventActivity extends Activity {

    TextView txtName;
    TextView txtData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_event);

        txtName = (TextView)findViewById(R.id.txtEventName);
        txtData = (TextView)findViewById(R.id.txtEventData);
    }

    public void onClick(View v) {
        String eventName = txtName.getText().toString();
        String eventData = txtData.getText().toString();
        DeepBiManager.sendCustomEvent(eventName, eventData);
    }
}
