package com.push.sweateliteathletics.softrasol.MyFragments;


import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.push.sweateliteathletics.softrasol.BroadcastService;
import com.push.sweateliteathletics.softrasol.Helper.UploadGoalData;
import com.push.sweateliteathletics.softrasol.MyActivities.ChallengesActivity;
import com.push.sweateliteathletics.softrasol.MyActivities.ExerciseActivity;
import com.push.sweateliteathletics.softrasol.MyActivities.FitBodyProgramsActivity;
import com.push.sweateliteathletics.softrasol.MyActivities.FitMindProgramsActivity;
import com.push.sweateliteathletics.softrasol.MyActivities.MobilityActivity;
import com.push.sweateliteathletics.softrasol.MyActivities.NutritionActivity;
import com.push.sweateliteathletics.softrasol.R;
import com.push.sweateliteathletics.softrasol.services.AlarmReceiver;
import com.push.sweateliteathletics.softrasol.services.Mt60minService;
import com.push.sweateliteathletics.softrasol.services.utils.NotificationScheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static com.push.sweateliteathletics.softrasol.services.Alarm60min.milisecremaingin;

/**
 * A simple {@link Fragment} subclass.
 */
public class FitBodyNavFragment extends Fragment {


    public FitBodyNavFragment() {
        // Required empty public constructor
    }

    private View mView;
    private RelativeLayout mNutririon, mExercise, mMobility, mChallenges,
            mPrograms, mMyPush;
    private AlertDialog alert11;

    TextView mTxtTitle, mTxtDescription;

    private String goal;

    //Date Dialog
    private String date = "";
    private int mYear;
    private int mMonth;
    private int mDay;

    long miliSecsDate;
    long ms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_fit_body_nav, container, false);

        widgetsInitialization();
        exerciseClick();
        mobilityClick();
        challengesClick();
        programsClick();
        myPushClick();
        nutritionCLick();
        getHomeScreenData();

        return mView;
    }

    private void getHomeScreenData() {

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("fitbody");
        DocumentReference documentReference = collectionReference
                .document("home_screen_details");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        mTxtTitle.setText(task.getResult().getString("title"));
                        mTxtDescription.setText(task.getResult().getString("description"));
                    }
                }
            }
        });



    }


    private void nutritionCLick() {
        mNutririon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NutritionActivity.class));
            }
        });

        layoutAnimation(mNutririon);
    }

    private void myPushClick() {
        layoutAnimation(mMyPush);
        mMyPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().
                        getSharedPreferences("fitbody", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                final String time = sharedPreferences.getString("time", "");
                final String goal = sharedPreferences.getString("goal", "");
                final String date = sharedPreferences.getString("date", "");

                if (!sharedPreferences.contains("time")) {
                    Log.d("dxdiag", "Share Preg Not Exists " + time);
                    showAlertDialog("", "");
                } else if (time.equals("") || time.equals("0") || Long.parseLong(time) < 0) {
                    showAlertDialog("", "");
                    Log.d("dxdiag", "Time Up" + time);
                } else {
                    Log.d("dxdiag", " Data Exista " + time);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setTitle("Push Goal");
                    builder1.setMessage(

                            "My goal is " + goal + " till " +
                                    date
                    );

                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Update",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    showAlertDialog(goal, date);
                                }
                            });

                    builder1.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

                Log.d("dxdiag", "Share Pref Time " + time);

            }

        });
    }



    private void showAlertDialog(String pGoal, final String pDate) {

        boolean forEdit = false;

        if (!pGoal.equals("") && !pDate.equals("")) {
            forEdit = true;
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setCancelable(true);
        View myView = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_set_push_goal, null);
        builder1.setView(myView);
        builder1.setCancelable(false);

        final EditText mEdtGoal = myView.findViewById(R.id.edt_dialog_fitmind_goal);
        final TextView mTxtDate = myView.findViewById(R.id.txt_dialog_fitmind_goal_date);
        final TextView mTxtHeader = myView.findViewById(R.id.txt_dialog_header);

        mEdtGoal.setText(pGoal);
        mTxtDate.setText(pDate);

        mTxtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(mTxtDate, pDate);
            }
        });

        Button mBtnCancel = myView.findViewById(R.id.btn_cancel_dialog_fitmind);
        Button mBtnSubmit = myView.findViewById(R.id.btn_submit_dialog_fitmind);

        if (forEdit) {
            mBtnSubmit.setText("Edit");
            mTxtHeader.setText("Edit Push Goal");
        }

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert11.cancel();
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goal = mEdtGoal.getText().toString().trim();

                if (date.isEmpty()) {
                    Toast.makeText(getActivity(), "Choose Date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (goal.isEmpty()) {
                    mEdtGoal.setError("Required");
                    mEdtGoal.requestFocus();
                    return;
                }

                SharedPreferences sharedPreferences = getActivity()
                        .getSharedPreferences("fitbody", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("time", miliSecsDate + "");
                editor.putString("is_started", "true");
                editor.putString("goal", goal);
                editor.putString("date", date);
                editor.putString("type", "fitbody");
                editor.putString("content1", goal);
                editor.apply();

                NotificationScheduler.setReminder(getContext(), AlarmReceiver.class, ms, 1000);
                if (!isMyServiceRunning(BroadcastService.class)) {
                    milisecremaingin = 0;


                    getActivity().startService(new Intent(getActivity(), Mt60minService.class));
                }


                alert11.cancel();


            }
        });


        alert11 = builder1.create();
        alert11.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void programsClick() {
        mPrograms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FitBodyProgramsActivity.class));
            }
        });

        layoutAnimation(mPrograms);
    }

    private void challengesClick() {
        mChallenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChallengesActivity.class));
            }
        });
        layoutAnimation(mChallenges);
    }

    private void mobilityClick() {
        mMobility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MobilityActivity.class));
            }
        });

        layoutAnimation(mMobility);
    }

    private void exerciseClick() {
        mExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ExerciseActivity.class));
            }
        });

        layoutAnimation(mExercise);
    }

    private void nutritionClick() {
    }

    private void widgetsInitialization() {

        mNutririon = mView.findViewById(R.id.fitbody_nutrition);
        mExercise = mView.findViewById(R.id.fitbody_exercise);
        mMobility = mView.findViewById(R.id.fitbody_mobility);
        mChallenges = mView.findViewById(R.id.fitbody_challenges);
        mPrograms = mView.findViewById(R.id.fitbody_programs);
        mMyPush = mView.findViewById(R.id.fitbody_mypush);

        mTxtTitle = mView.findViewById(R.id.title);
        mTxtDescription = mView.findViewById(R.id.description);

    }

    private void datePicker(final TextView mTxtDate, String prevSelectedDate) {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        if (!prevSelectedDate.equals("")) {
            Log.e("hi5", prevSelectedDate);
            String[] dateArr = prevSelectedDate.split("/");
            mYear = Integer.parseInt(dateArr[0]);
            mMonth = Integer.parseInt(dateArr[1]) - 1;
            mDay = Integer.parseInt(dateArr[2]);
        } else {
            Log.e("hi5", prevSelectedDate);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }



        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date = year+"/"+(monthOfYear + 1)+"/"+dayOfMonth;
                        mTxtDate.setText(date);

                        Date date = new Date();
                        String cday          = (String) DateFormat.format("dd",   date); // 20
                        String cmonthNumber  = (String) DateFormat.format("MM",   date); // 06
                        String cyear         = (String) DateFormat.format("yyyy", date); // 2013

                        LocalDate start = LocalDate.of(Integer.parseInt(cyear), Integer.parseInt(cmonthNumber),
                                Integer.parseInt(cday));
                        LocalDate end = LocalDate.of(year,(monthOfYear + 1),dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date mDate = sdf.parse(String.valueOf(end));
                            long timeInMilliseconds = mDate.getTime();
                            ms = timeInMilliseconds;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String futureDays = ChronoUnit.DAYS.between(start, end)+"";

                        miliSecsDate = TimeUnit.DAYS.toMillis(Long.parseLong(futureDays));


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void layoutAnimation(RelativeLayout layout) {

        layout.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(Color.parseColor("#161618"), PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}
