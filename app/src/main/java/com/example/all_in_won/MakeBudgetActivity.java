package com.example.all_in_won;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MakeBudgetActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    String title;
    Budgets budgets;
    BudgetTextViews budgetTextViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_budget);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");

        budgets = new Budgets(this, title);
        budgetTextViews = new BudgetTextViews();
        budgetTextViews.setAllBudgets(budgets);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus){
            this.budgetTextViews.setSumOfEachCategory(); // 포커스 바뀌면 예산 총합 갱신
        }
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

    public void clickSaveButton(View view){
        this.budgetTextViews.setSumOfEachCategory();

        // 합계 총합과 같은지 검사
        if(budgetTextViews.getTotalBudget() == budgetTextViews.getSumOfEachCategory()){ // 각 카테고리별 예산 총합과 총 예산이 일치해야 저장된다
            saveBudgetsinDB(budgetTextViews, title);
        }else{
            showToast("총 예산과 합계가 일치해야 합니다.");
        }
    }

    private void saveBudgetsinDB(BudgetTextViews budgetTextViews, final String title){

        // 카테고리별 예산 저장
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "update tb_accountBook set exchangedAmount = " + budgetTextViews.getTotalBudget() + " where title = \'" + title + "\' and tag = \'총예산\' and isBudget = 1";
        db.execSQL(sql);
        sql = "update tb_accountBook set exchangedAmount = " + budgetTextViews.getFoodBudget() + " where title = \'" + title + "\' and tag = \'식비\' and isBudget = 1";
        db.execSQL(sql);
        sql = "update tb_accountBook set exchangedAmount = " + budgetTextViews.getRoomBudget() + " where title = \'" + title + "\' and tag = \'숙박비\' and isBudget = 1";
        db.execSQL(sql);
        sql = "update tb_accountBook set exchangedAmount = " + budgetTextViews.getTransportationBudget() + " where title = \'" + title + "\' and tag = \'교통비\' and isBudget = 1";
        db.execSQL(sql);
        sql = "update tb_accountBook set exchangedAmount = " + budgetTextViews.getShoppingBudget() + " where title = \'" + title + "\' and tag = \'쇼핑\' and isBudget = 1";
        db.execSQL(sql);
        sql = "update tb_accountBook set exchangedAmount = " + budgetTextViews.getDailySuppliesBudget() + " where title = \'" + title + "\' and tag = \'생활용품\' and isBudget = 1";
        db.execSQL(sql);
        sql = "update tb_accountBook set exchangedAmount = " + budgetTextViews.getEtcBudget() + " where title = \'" + title + "\' and tag = \'기타\' and isBudget = 1";
        db.execSQL(sql);

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

    private class BudgetTextViews{
        private EditText foodBudget;
        private EditText transportationBudget;
        private EditText roomBudget;
        private EditText shoppingBudget;
        private EditText dailySuppliesBudget;
        private EditText etcBudget;
        private EditText sumOfEachCategory;
        private EditText totalBudget;

        BudgetTextViews(){
            totalBudget = findViewById(R.id.edit_text_total_budget);
            foodBudget = findViewById(R.id.edit_text_make_budget_food);
            roomBudget = findViewById(R.id.edit_text_make_budget_room);
            transportationBudget = findViewById(R.id.edit_text_make_budget_transportation);
            shoppingBudget = findViewById(R.id.edit_text_make_budget_shopping);
            dailySuppliesBudget = findViewById(R.id.edit_text_make_budget_daily_supplies);
            etcBudget = findViewById(R.id.edit_text_make_budget_etc);
            sumOfEachCategory = findViewById(R.id.edit_text_make_budget_sum_of_each_category);

            totalBudget.setOnFocusChangeListener(MakeBudgetActivity.this);
            foodBudget.setOnFocusChangeListener(MakeBudgetActivity.this);
            roomBudget.setOnFocusChangeListener(MakeBudgetActivity.this);
            transportationBudget.setOnFocusChangeListener(MakeBudgetActivity.this);
            shoppingBudget.setOnFocusChangeListener(MakeBudgetActivity.this);
            dailySuppliesBudget.setOnFocusChangeListener(MakeBudgetActivity.this);
            etcBudget.setOnFocusChangeListener(MakeBudgetActivity.this);
        }

        // getter
        private int getDailySuppliesBudget() { return Integer.parseInt(dailySuppliesBudget.getText().toString()); }
        private int getEtcBudget() {
            return Integer.parseInt(etcBudget.getText().toString());
        }
        private int getFoodBudget() {
            return Integer.parseInt(foodBudget.getText().toString());
        }
        private int getRoomBudget() {
            return Integer.parseInt(roomBudget.getText().toString());
        }
        private int getTransportationBudget() { return Integer.parseInt(transportationBudget.getText().toString()); }
        private int getShoppingBudget() { return Integer.parseInt(shoppingBudget.getText().toString()); }
        public int getSumOfEachCategory() { return Integer.parseInt(sumOfEachCategory.getText().toString()); }
        public int getTotalBudget() {
            return Integer.parseInt(totalBudget.getText().toString());
        }

        // setter
        private void setDailySuppliesBudget(Integer dailySuppliesBudget) { this.dailySuppliesBudget.setText(dailySuppliesBudget.toString()); }
        private void setEtcBudget(Integer etcBudget) { this.etcBudget.setText(etcBudget.toString()); }
        private void setFoodBudget(Integer foodBudget) { this.foodBudget.setText(foodBudget.toString()); }
        private void setRoomBudget(Integer roomBudget) { this.roomBudget.setText(roomBudget.toString()); }
        private void setTransportationBudget(Integer transportationBudget) { this.transportationBudget.setText(transportationBudget.toString()); }
        private void setShoppingBudget(Integer shoppingBudget) { this.shoppingBudget.setText(shoppingBudget.toString()); }
        private void setTotalBudget(Integer totalBudget) { this.totalBudget.setText(totalBudget.toString()); }
        private void setSumOfEachCategory() {
            int total = 0;
            total += getDailySuppliesBudget();
            total += getEtcBudget();
            total += getFoodBudget();
            total += getRoomBudget();
            total += getShoppingBudget();
            total += getTransportationBudget();
            this.sumOfEachCategory.setText(Integer.toString(total));
        }
        private void setAllBudgets(Budgets budgets){
            setDailySuppliesBudget(budgets.getDailySuppliesBudget());
            setEtcBudget(budgets.getEtcBudget());
            setFoodBudget(budgets.getFoodBudget());
            setRoomBudget(budgets.getRoomBudget());
            setShoppingBudget(budgets.getShoppingBudget());
            setTransportationBudget(budgets.getTransportationBudget());
            setTotalBudget(budgets.getTotalBudget());
            setSumOfEachCategory();
        }
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
