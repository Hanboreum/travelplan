package com.example.all_in_won;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    long initTime;

    AlertDialog addHouseholdBookDialog;
    LinearLayout householdBookLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기 설정 어플 처음 실행 시에만 실행
        SharedPreferences settingPref = new SettingSharedPreferences(this).getSettingSharedPreferences();
        if(settingPref.getBoolean("isFirstExecute", true)){
            SharedPreferences.Editor editor = settingPref.edit();
            editor.putBoolean("isFirstExecute", false);
            editor.putString("alarmTime", "22:00");
            editor.putBoolean("isAlarmON", false);
            editor.putString("graph", "pie");
            editor.putString("alarmAccountBookTitle", null);
            editor.commit();
        }

        householdBookLayout = findViewById(R.id.tab_Household_book_list);

        showAllAccountBooks();
    }

    @Override
    public boolean onLongClick(View view){

        PopupMenu popupMenu = setDeletePopup(view);
        popupMenu.show();

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - initTime > 2000) {
                showToast("뒤로가기 버튼을 한번 더 누르면 종료합니다.");
                initTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 가계부 목록중 하나를 클릭하면 실행되는 함수
    // 클릭된 가계부의 내역 화면으로 이동한다.
    public void showDetailsActivity(View view){
        // view가 each_details_of_usage여야함
        TextView titleTextView = view.findViewById(R.id.text_household_book_title); // id 못찾았을 때 어찌되는지
        String title = titleTextView.getText().toString();

        showDetailsActivity(title);
    }

    public void showDetailsActivity(String title){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.example.all_in_won",
                "com.example.all_in_won.DetailsActivity"
        );

        intent.putExtra("title", title); // 가계부 이름을 전달해서 어떤 가계부의 목록을 보여줘야 하는지 알려준다
        intent.setComponent(componentName);

        startActivity(intent);
        finish();
    }

    // 가계부 추가 버튼을 누르면 다이얼로그를 띄워주는 함수
    public void showAddHouseholdBookDialog(View view){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("가계부  추가");

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View addHouseholdBookDialogView = inflater.inflate(R.layout.add_household_book_dialog, null);
        final EditText householdBookTitle = addHouseholdBookDialogView.findViewById(R.id.edit_text_add_household_book_title);

        dialogBuilder.setView(addHouseholdBookDialogView);
        dialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog == addHouseholdBookDialog && which == Dialog.BUTTON_POSITIVE){

                    String title = householdBookTitle.getText().toString();

                    if(addNewAccountBook(title)){ // 새로운 가계부 추가
                        showDetailsActivity(title); // 가계부 내역 창으로 이동
                    }
                }
            }
        });
        dialogBuilder.setNegativeButton("취소", null);

        addHouseholdBookDialog = dialogBuilder.create();
        addHouseholdBookDialog.show();
    }

    public void showSettingActivity(View view){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.example.all_in_won",
                "com.example.all_in_won.SettingActivity"
        );

        intent.setComponent(componentName);

        startActivity(intent);
    }

    // 가계부 추가 실패시에는 false 리턴하도록 한다 (이미 같은 이름의 가계부가 있는 경우)
    private boolean addNewAccountBook(String title){
        try{
            DBHelper helper = new DBHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();

            // 이름이 같은 가계부가 있으면 추가되지 않는다
            String sql = "select title from tb_accountBook where isBudget = 1 and tag = \'title\'";
            Cursor cursor = db.rawQuery(sql, null);
            while(cursor.moveToNext()){
                String exTitles = cursor.getString(0);
                if(exTitles.equals(title)) {
                    showToast("사용할 수 없는 가계부 제목입니다.");
                    return false;
                }
            }
            cursor.close();

            sql = "insert into tb_accountBook (title, tag, isBudget) values ( " + "\'" + title + "\', \'title\' , 1)"; // detail창에서 불러오지 않기 위해 isBudget을 1로 설정
            db.execSQL(sql);

            String[] tags = getResources().getStringArray(R.array.tags);
            // 모든 예산 0으로 해서 추가
            for(int i = 0; i < tags.length; ++i){
                sql = "insert into tb_accountBook (title, exchangedAmount, tag, isBudget) values ( " + "\'" + title + "\', "+ 0 + ",  \'" + tags[i]  + "\', 1)";
                db.execSQL(sql);
            }
            sql = "insert into tb_accountBook (title, exchangedAmount, tag, isBudget) values ( " + "\'" + title + "\', "+ 0 + ",  \'" + "총예산"  + "\', 1)";
            db.execSQL(sql);

            db.close();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // DB에서 모든 가계부들을 불러와서 가계부 목록에 띄우는 함수
    private void showAllAccountBooks(){

        SQLiteDatabase db = null;
        Cursor cursor = null;
        String sql = null;
        try{
            DBHelper helper = new DBHelper(this);
            db = helper.getReadableDatabase();
            sql = "select title, amount from tb_accountBook where isBudget = 1 and tag = 'title' order by _id asc";
            cursor = db.rawQuery(sql, null);
        } catch (Exception e){
            e.printStackTrace();//에러메시지 토스트로 출력
            return;
        }

        // 지출, 잔액 보여줘야함. 총 예산과 지출 총합 필요
        int balance = 0; // 잔액
        int totalExchangedAmount = 0; // 지출 총액
        int totalBudget = 0;
        String title = null;

        while(cursor.moveToNext()){
            try{
                title = cursor.getString(0);
                totalExchangedAmount = cursor.getInt(1);
                Budgets budgets = new Budgets(this, title);
                totalBudget = budgets.getTotalBudget();
                balance = totalBudget - totalExchangedAmount;

                showAccountBookOverview(title, balance, totalBudget, totalExchangedAmount);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();

    }

    // 인자로 받은 정보들로 가계부 요약창 만들어 가계부 목록에 띄움
    private void showAccountBookOverview(String title, int balance, int totalBudget, int totalExchangedAmount){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View householdBookOverview = inflater.inflate(R.layout.household_book_overview, null);

        TextView titleTextView = householdBookOverview.findViewById(R.id.text_household_book_title);
        TextView balanceTextView = householdBookOverview.findViewById(R.id.text_balance);
        TextView totalExchangedAmountTextView = householdBookOverview.findViewById(R.id.text_total_exchanged_amount);
        TextView totalBudgetTextView =  householdBookOverview.findViewById(R.id.text_total_budget);

        titleTextView.setText(title);
        balanceTextView.setText(balance + "");
        totalBudgetTextView.setText(totalBudget + "");
        totalExchangedAmountTextView.setText(totalExchangedAmount + "");
        householdBookOverview.setOnLongClickListener(this); // 가계부 삭제 기능을 위해 setLongClickListener
        householdBookLayout.addView(householdBookOverview, 1);
    }

    private PopupMenu setDeletePopup(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);

        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.delete_popup, menu);

        TextView titleView = view.findViewById(R.id.text_household_book_title);
        final String title = titleView.getText().toString();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(title + " 삭제");
                builder.setMessage("\n정말 삭제하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DBHelper helper = new DBHelper(MainActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        String sql = "delete from tb_accountBook where title = \'" + title + "\'";
                        db.execSQL(sql);

                        Intent intent = new Intent();
                        ComponentName componentName = new ComponentName(
                                "com.example.all_in_won",
                                "com.example.all_in_won.MainActivity"
                        );
                        intent.setComponent(componentName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);

                        finish();
                    }
                });
                builder.setNegativeButton("아니요", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });

        return popupMenu;
    }


    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}


