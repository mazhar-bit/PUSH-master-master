package com.push.sweateliteathletics.softrasol.MyActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.push.sweateliteathletics.softrasol.R;
import com.push.sweateliteathletics.softrasol.SignIn;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingss_push);
    }

    public void BackClick(View view) {
        onBackPressed();
    }

    public void ProfileClick(View view) {
        startActivity(new Intent(getApplicationContext(), EditNameActivity.class));
    }

    public void PrivacyPolicyClick(View view) {
        startActivity(new Intent(getApplicationContext(), WebViewAcitivity.class));
    }

    public void LogoutClick(View view) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this);
        builder1.setMessage("Are you sure you want to sign out.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SettingsActivity.this, SignIn.class));
                        finish();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }
}