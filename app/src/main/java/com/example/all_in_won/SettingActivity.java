package com.example.all_in_won;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    Spinner graphSpinner; // 그래프 종류 선택 spinner
    Spinner accountBookSpinner; // 알림 받을 가계부 선택 spinner
    Spinner saveAccountBookSpinner; // 엑셀 파일로 내보낼 가계부 선택 spinner
    Button timeSetButton; // 시간 설정 버튼
    Switch alarmOnOffSwitch; // 알림 온오프 스위치
    String selectedGraph; // 선택된 그래프 종류
    boolean isAlarmON; // 알림 ON인지 OFF인지
    SharedPreferences settingPref; // 설정 내용 저장할 sharedPreference
    String alarmAccountBookTitle; // 알림 받을 가계부
    String saveAccountBookTitle; // 엑셀 파일로 내보낼 가계부
    ArrayList<String> titles; // 어플에 저장되어있는 가계부들

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settingPref = new SettingSharedPreferences(this).getSettingSharedPreferences();

        saveAccountBookSpinner = findViewById(R.id.spinner_save_account_book);
        graphSpinner = findViewById(R.id.spinner_graph);
        accountBookSpinner = findViewById(R.id.spinner_account_book);
        timeSetButton = findViewById(R.id.btn_time_set);
        alarmOnOffSwitch = findViewById(R.id.switch_alarm_on_off);

        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.graph, android.R.layout.simple_spinner_dropdown_item);
        graphSpinner.setAdapter(arrayAdapter);
        titles = getAccountBookTitles();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, titles);
        accountBookSpinner.setAdapter(arrayAdapter);
        saveAccountBookSpinner.setAdapter(arrayAdapter);

        // 그래프 종류 선택 spinner
        graphSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGraph = parent.getItemAtPosition(position).toString();
                if(selectedGraph.equals("원형 그래프")){ // 선택된 그래프 저장
                    selectedGraph = "pie";
                } else {
                    selectedGraph = "bar";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 알림 받을 가계부 선택 spinner
        accountBookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alarmAccountBookTitle = parent.getItemAtPosition(position).toString(); // 선택된 가계부 제목 저장
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 엑셀 파일로 내보낼 가계부 선택 spinner
        saveAccountBookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveAccountBookTitle = parent.getItemAtPosition(position).toString(); // 선택된 가계부 제목 저장
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alarmOnOffSwitch.setOnCheckedChangeListener(this);

        loadSetting(); // 기존의 설정을 설정 창으로 가져옴
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == alarmOnOffSwitch){
            isAlarmON = isChecked; // 알림 ON인지 OFF인지 저장
        }
    }

    private void loadSetting() { // 기존의 설정을 가져온다
        // 알람 ON/OFF 가져온다
        alarmOnOffSwitch.setChecked(settingPref.getBoolean("isAlarmON", false));

        // 그래프 설정 가져온다
        if(settingPref.getString("graph", "pie").equals("bar")){
            graphSpinner.setSelection(1);
        } else{
            graphSpinner.setSelection(0);
        }

        // 알람 설정한 가계부 가져온다
        alarmAccountBookTitle = settingPref.getString("alarmAccountBookTitle", null);
        int titleIndex = 0;
        if(alarmAccountBookTitle != null){
            for(int i = 0; i < titles.size(); ++i){
                if(titles.get(i).equals(alarmAccountBookTitle)){
                    titleIndex = i;
                    break;
                }
            }
        }
        accountBookSpinner.setSelection(titleIndex);

        // 알람 시간 가져온다
        timeSetButton.setText(settingPref.getString("alarmTime", "22:00"));
    }

    public void clickExcelSaveButton(View view){
        String title = saveAccountBookTitle;
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.example.all_in_won",
                "com.example.all_in_won.ExcelService"
        );

        intent.setComponent(componentName);
        intent.putExtra("title", title);

        startService(intent); // 엑셀 파일 저장하는 서비스 호출
    }

    // 저장된 모든 가계부의 제목을 가져온다
    private ArrayList<String> getAccountBookTitles(){
        ArrayList<String> titles = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cursor = null;
        String sql = null;
        try{
            DBHelper helper = new DBHelper(this);
            db = helper.getReadableDatabase();
            sql = "select title from tb_accountBook where isBudget = 1 and tag = 'title' order by _id asc";
            cursor = db.rawQuery(sql, null);

            while(cursor.moveToNext()){
                titles.add(cursor.getString(0));
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return titles;
    }

    public void showTimePickerDialog(View view){
        // 현재 시간으로
//        Calendar c = Calendar.getInstance();
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);

        //선택돼있던  시간으로
        String timeText = timeSetButton.getText().toString();
        int hour = Integer.parseInt(timeText.substring(0,2));
        int minute = Integer.parseInt(timeText.substring(3,5));

        TimePickerDialog timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedMin;
                String selectedHour;
                if(hourOfDay<10) selectedHour = "0" + hourOfDay;
                else selectedHour = hourOfDay + "";
                if(minute<10) selectedMin = "0" + minute;
                else selectedMin = minute + "";

                Button timePickButton = findViewById(R.id.btn_time_set);
                timePickButton.setText(selectedHour + ":" + selectedMin);
            }
        }, hour, minute, false);
        timeDialog.show();
    }

    public void clickSaveButton(View view){
        String time = timeSetButton.getText().toString();

        // sharedPreference에 저장
        SharedPreferences.Editor editor = settingPref.edit();
        editor.putString("alarmTime", time);
        editor.putBoolean("isAlarmON", isAlarmON);
        editor.putString("graph", selectedGraph);
        editor.putString("alarmAccountBookTitle", alarmAccountBookTitle);
        editor.commit();

        // calendar에 알림 시간 set
        int hourOfDay = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(3));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 20160548, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        finish();
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
