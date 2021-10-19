package com.example.all_in_won;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class AddExpensesActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    String[] currencies = null; // 선택할 수 있는 통화들
    String[] tags = null; // 태그(카테고리)들

    String title; // 현재 가계부 제목

    Spinner tagSpinner; // 태그(카테고리)선택 spinner
    Spinner currencySpinner; // 통화 선택 spinner

    EditText amount; // 금액
    EditText exchangedAmount; // 원화로 환전된 금액
    EditText detail; // 사용내역
    EditText whereToUse; // 사용처

    Button datePickButton; // 날짜 선택 버튼
    Button saveButton; // 저장버튼

    String selectedTag; // 선택된 태그(카테고리)
    String selectedCurrency; // 선택된 통화

    boolean isServiceConnected = false; // 환율 받아오는 서비스 컴포넌트와 바인드 되어있는가

    int id; // 현재 사용내역의 id

    private ExchangeRatesService exchangeRatesService = null; // 환율 받아오는 서비스 컴포넌트

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w("onServiceConnected()", "-----------------------------------------------------------------------------");
            exchangeRatesService = ((ExchangeRatesService.LocalBinder) service).getExchangeRatesService();
            isServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        currencies = getResources().getStringArray(R.array.currency);
        selectedCurrency = currencies[0];
        tags = getResources().getStringArray(R.array.tags);

        amount = findViewById(R.id.edit_text_add_expenses_amount);
        amount.setText("0");
        amount.setOnFocusChangeListener(this); // 환전된 금액 표시를 위함
        exchangedAmount = findViewById(R.id.edit_text_add_expenses_exchanged_amount);
        exchangedAmount.setText("0");
        detail = findViewById(R.id.edit_text_add_expenses_detail);
        whereToUse = findViewById(R.id.edit_text_add_where_to_use);
        tagSpinner = findViewById(R.id.spinner_tag);
        currencySpinner = findViewById(R.id.spinner_currency);

        datePickButton = findViewById(R.id.btn_dateButton);
        saveButton = findViewById(R.id.btn_input_save);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        id = intent.getIntExtra("id", -1); // 사용 내역 창에서 추가 버튼을 눌렀으면 id == -1, 각각의 내역을 눌러 수정창으로 왔으면 id == 각 내역의 id

        if(id != -1){ // 기존 내역 수정하는 경우
            setValuesAtTextViews(); // editTextView들을 기존의 값들로 채움
        }

        Log.w("bind start", "-----------------------------------------------------------------------------");
        Intent serviceIntent = new Intent();
        ComponentName componentName = new ComponentName("com.example.all_in_won", "com.example.all_in_won.ExchangeRatesService");
        serviceIntent.setComponent(componentName);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        Log.w("bind end", "-----------------------------------------------------------------------------");

        Log.w("itemSelected start", "-----------------------------------------------------------------------------");
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTag = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = parent.getItemAtPosition(position).toString();
                setExchangedAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.w("itemSelected end", "-----------------------------------------------------------------------------");
    }

    @Override
    public void onDestroy(){
        unbindService(serviceConnection);

        super.onDestroy();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v == amount){
            if(!hasFocus){
                setExchangedAmount(); // 환전된 금액 갱신하는 코드
            }
        }

    }

    private void setExchangedAmount(){
        Log.w("setExchangedAmount()", "-----------------------------------------------------------------------------");
        if(isServiceConnected){// 액티비티 생성시 editText에 focus가 가면서 서비스 연결 이전에 서비스가 호출되는 것을 막기 위함
            exchangeRatesService.getExchangeRate(Integer.parseInt(amount.getText().toString()), selectedCurrency, stringToCalendar(datePickButton.getText().toString()), exchangedAmount);
        }
    }

    public void showDatePickerDialog(View view){
        Calendar calendar = null;
        String dateText = datePickButton.getText().toString();

        if(dateText.equals("0000/00/00")){ // 현재 날짜로
            calendar = Calendar.getInstance(); // java.util.Calendar
        } else {// 선택돼있던 날짜로
            calendar = stringToCalendar(dateText);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String smonth = (month + 1) + "";
        String sDate = day + "";
        if(day < 10) sDate = "0" + sDate;
        if(month + 1 < 10) smonth = "0" + smonth;
        datePickButton.setText(year + "/" + smonth + "/" + sDate);

        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Button datePickButton = findViewById(R.id.btn_dateButton);
                String smonth = (month + 1) + "";
                String sDate = dayOfMonth + "";
                if(dayOfMonth < 10) sDate = "0" + sDate;
                if(month + 1 < 10) smonth = "0" + smonth;
                datePickButton.setText(year + "/" + smonth + "/" + sDate);
            }
        }, year, month, day);
        dateDialog.show();
    }

    private void setValuesAtTextViews(){ // 기존에 있던 사용내역을 불러온다
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase(); // 마지막에 총금액을 저장하기 때문에 writable로 가져온다
        String sql = "select date, amount, currency, exchangedAmount, detail, tag, whereToUse from tb_accountBook where isBudget = 0 and _id = " + id + " and title  = " + "\'" + title + "\'" + " order by date asc";
        Cursor cursor = null;
        cursor = db.rawQuery(sql, null);
        cursor.moveToNext();

        amount.setText(cursor.getInt(1)+"");
        exchangedAmount.setText(cursor.getInt(3)+"");
        detail.setText(cursor.getString(4));
        whereToUse.setText(cursor.getString(6));
        datePickButton.setText(cursor.getString(0));


        int tagIndex = 0;
        int currencyIndex = 0;

        for(int i = 0; i < currencies.length; ++i){
            if(currencies[i].equals(cursor.getString(2)+"")){
                currencyIndex = i;
                break;
            }
        }

        for(int i = 0; i < tags.length; ++i){
            if(tags[i].equals(cursor.getString(5)+"")){
                tagIndex = i;
                break;
            }
        }

        tagSpinner.setSelection(tagIndex);
        currencySpinner.setSelection(currencyIndex);
        selectedCurrency = currencies[currencyIndex];

        cursor.close();
        db.close();
    }

    public void clickDeleteButton(View view){ // 사용내역을 삭제한다

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = null;

        sql = "select * from tb_accountBook where title = \'" + title + "\' and isbudget = 0 and _id = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){ // 기존 내역 삭제
            try{
                sql = "delete from tb_accountBook where title = \'" + title + "\' and isbudget = 0 and _id = " + id;
                //showToast(sql);
                db.execSQL(sql);
            }catch(Exception e){
                db.close();
                return;
            }
        } else { // 삭제할 항목 없음
        }
        cursor.close();
        db.close();


        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.example.all_in_won",
                "com.example.all_in_won.DetailsActivity"
        );
        intent.setComponent(componentName);
        intent.putExtra("title", title);

        startActivity(intent);
        finish();
    }

    public void clickSaveButton(View view){ // 사용내역을 저장한다 (새로 추가, 수정)
        int amount;
        double exchangedAmount;

        try{
            amount = Integer.parseInt(this.amount.getText().toString());
            exchangedAmount = Double.parseDouble(this.exchangedAmount.getText().toString());
        } catch(Exception e){
            showToast("금액을 입력하세요.");
            return;
        }

        String detail = this.detail.getText().toString();
        String whereToUse = this.whereToUse.getText().toString();
        String date = datePickButton.getText().toString();

        if(date.equals("0000/00/00")){
            showToast("날짜를 선택하세요.");
            return;
        }
        if(detail.equals("")){
            showToast("사용 내역을 입력하세요.");
            return;
        }
        if(whereToUse.equals("")){
            showToast("사용처를 입력하세요.");
            return;
        }

        //사용내역 DB에 저장하는 코드
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = null;

        sql = "select * from tb_accountBook where title = \'" + title + "\' and isbudget = 0 and _id = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){ // 수정하는 경우
            try{
                sql = "update tb_accountBook set date = \'" + date + "\', amount = " + amount + ", currency = \'" + selectedCurrency + "\', exchangedAmount = " + exchangedAmount + ", detail = \'" + detail + "\', whereToUse = \'" + whereToUse + "\', tag = \'" + selectedTag + "\' where title = \'" + title + "\' and isbudget = 0 and _id = " + id;
                //showToast(sql);
                db.execSQL(sql);
            }catch(Exception e){
                db.close();
                return;
            }
        } else { // 새로 생성하는 경우
            try{
                sql = "insert into tb_accountBook (title, date, amount, currency, exchangedAmount, detail, whereToUse, tag, isBudget) values ( " + "\'" + title + "\', \'" + date + "\', " + amount  + ", \'" + selectedCurrency + "\', " + exchangedAmount + ", \'" + detail  + "\', \'" + whereToUse + "\', \'" + selectedTag  + "\', 0)";
                //showToast(sql);
                db.execSQL(sql);
            } catch(Exception e){
                db.close();
                return;
            }
        }
        cursor.close();
        db.close();


        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.example.all_in_won",
                "com.example.all_in_won.DetailsActivity"
        );
        intent.setComponent(componentName);
        intent.putExtra("title", title);
        //intent.putExtra("id", id);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(
                    "com.example.all_in_won",
                    "com.example.all_in_won.DetailsActivity"
            );
            intent.setComponent(componentName);
            intent.putExtra("title", title);

            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private Calendar stringToCalendar(String date){ // 문자열로 저장된 날짜를 Calendar객체로 옮긴다
        int year, month, day;
        Calendar calendar = Calendar.getInstance();

        year = Integer.parseInt(date.substring(0,4));
        month = Integer.parseInt(date.substring(5,7));
        day = Integer.parseInt(date.substring(8,10));

        calendar.set(year, month - 1, day, 0, 0, 0);

        return calendar;
    }

}
