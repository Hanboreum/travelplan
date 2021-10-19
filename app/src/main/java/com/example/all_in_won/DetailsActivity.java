package com.example.all_in_won;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    String title; // 가계부의 제목
    TabHost tabHost;
    ExpensesByTag expensesByTag = null; // 태그(카테고리)별 지출 금액
    Budgets budgetsByTag = null; //  태그(카테고리)별 예산

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tabHost = findViewById(R.id.host);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("tabEach");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.list1, null));
        spec.setContent(R.id.scrollView_details);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tabStatistic");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.chart1, null));
        spec.setContent(R.id.tab_statistic);
        tabHost.addTab(spec);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");

        showAllExpenses();
        expensesByTag = new ExpensesByTag();
        budgetsByTag = new Budgets(this, title);
        showDetailsByTag(expensesByTag, budgetsByTag);

        setChart(expensesByTag);// expensesByTag가 생성된 뒤에 호출되어야함

    }

    private void setChart(ExpensesByTag expensesByTag){
        SharedPreferences sharedPreferences = new SettingSharedPreferences(this).getSettingSharedPreferences();
        if(sharedPreferences.getString("graph", "pie").equals("pie")){
            setPieChart(expensesByTag);
        } else {
            setBarChart(expensesByTag);
        }
    }

    public void showAddExpensesActivity(View view){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.example.all_in_won",
                "com.example.all_in_won.AddExpensesActivity"
        );

        intent.setComponent(componentName);
        intent.putExtra("title", title);

        // 어떤  뷰를 클릭해서  실행되었는지  확인
        if(view instanceof Button){
        } else {
            TextView expenseID = view.findViewById(R.id.text_expenseID);
            int id = Integer.parseInt(expenseID.getText().toString());
            intent.putExtra("id", id);
            //showToast(id + "");
        }

        startActivity(intent);
        finish();
    }

    public void showMakeBudgetActivity(View view){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.example.all_in_won",
                "com.example.all_in_won.MakeBudgetActivity"
        );

        intent.setComponent(componentName);
        intent.putExtra("title", title);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(
                    "com.example.all_in_won",
                    "com.example.all_in_won.MainActivity"
            );
            intent.setComponent(componentName);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void doNothingOnClick(View view){
        return;
    }

    // 상세 내역창에 모든 사용 내역들 출력
    private void showAllExpenses(){
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase(); // 마지막에 총액수 저장을 하기 때문에 writable로 가져온다
        String sql = "select date, amount, currency, exchangedAmount, detail, tag, _id from tb_accountBook where isBudget = 0 and title  = " + "\'" + title + "\'" + " order by date asc";
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(sql, null);
        } catch (Exception e){

        }

        String prevDate = "";
        String nextDate;
        String name;
        String category;
        String amount;
        String currency;
        String exchangedAmount;
        int id = 0;

        int totalExchangedAmount = 0;
        int nExchangedAmount = 0;

        View dailyDetails = null;
        TableLayout dailyTableLayout = null;
        LayoutInflater inflater;

        if(cursor != null){
            while(cursor.moveToNext()){
                nextDate = cursor.getString(0);

                name = cursor.getString(4);
                category = cursor.getString(5);
                amount = cursor.getInt(1) + "";
                currency = cursor.getString(2);
                nExchangedAmount = cursor.getInt(3);
                exchangedAmount = nExchangedAmount + "";
                id = cursor.getInt(6);

                totalExchangedAmount += nExchangedAmount;

                // 새로운 사용내역 추가하는 코드
                // 새로운 사용내역 생성
                inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View detailOfUsage = inflater.inflate(R.layout.each_details_of_usage, null);

                TextView nameTextView = detailOfUsage.findViewById(R.id.text_name_of_detail_of_usage);
                TextView categoryTextView = detailOfUsage.findViewById(R.id.text_category_of_detail_of_usage);
                TextView amountTextView = detailOfUsage.findViewById(R.id.text_amount_of_detail_of_usage);
                TextView currencyTextView = detailOfUsage.findViewById(R.id.text_currency_of_detail_of_usage);
                TextView exchangedAmountTextView = detailOfUsage.findViewById(R.id.text_WON_amount_of_detail_of_usage);
                TextView expenseID = detailOfUsage.findViewById(R.id.text_expenseID);

                nameTextView.setText(name);
                categoryTextView.setText(category);
                amountTextView.setText(amount);
                currencyTextView.setText(currency);
                exchangedAmountTextView.setText(exchangedAmount);
                expenseID.setText(id+"");

                if(!prevDate.equals(nextDate)){
                    // 새로운 날짜 생성
                    dailyDetails = inflater.inflate(R.layout.details_daily, null);
                    TextView dateTextView =  dailyDetails.findViewById(R.id.text_details_daily_date);
                    dateTextView.setText(nextDate);
                    dailyTableLayout = dailyDetails.findViewById(R.id.tableLayout_details_of_usage);
                }

                // 날짜 하위에 사용내역 추가
                dailyTableLayout.addView(detailOfUsage);
                //
                LinearLayout tabDetails = findViewById(R.id.tab_Details);
                if(!prevDate.equals(nextDate)) tabDetails.addView(dailyDetails, 0);

                prevDate = nextDate;
            }

            cursor.close();
        }

        sql = "update tb_accountBook set amount = " + totalExchangedAmount + " where title = \'" + title + "\' and tag = \'title\' and isBudget = 1";
        db.execSQL(sql);

        db.close();
    }

    // 통계창 출력 내용 세팅
    private void showDetailsByTag(ExpensesByTag expensesByTag, Budgets budgetsByTag){
        TextView expensesTextView = findViewById(R.id.text_food_expenses);
        expensesTextView.setText(expensesByTag.getFoodExpenses() + "");
        expensesTextView = findViewById(R.id.text_transportation_expenses);
        expensesTextView.setText(expensesByTag.getTransportationExpenses() + "");
        expensesTextView = findViewById(R.id.text_room_charge);
        expensesTextView.setText(expensesByTag.getRoomCharge() + "");
        expensesTextView = findViewById(R.id.text_shopping_expenses);
        expensesTextView.setText(expensesByTag.getShoppingExpenses() + "");
        expensesTextView = findViewById(R.id.text_daily_supplies_expenses);
        expensesTextView.setText(expensesByTag.getDailySuppliesExpenses() + "");
        expensesTextView = findViewById(R.id.text_etc_expenses);
        expensesTextView.setText(expensesByTag.getEtcExpenses() + "");

        TextView budgetsTextView = findViewById(R.id.text_food_budget);
        budgetsTextView.setText(budgetsByTag.getFoodBudget() + "");
        budgetsTextView = findViewById(R.id.text_transportation_budget);
        budgetsTextView.setText(budgetsByTag.getTransportationBudget() + "");
        budgetsTextView = findViewById(R.id.text_room_budget);
        budgetsTextView.setText(budgetsByTag.getRoomBudget() + "");
        budgetsTextView = findViewById(R.id.text_shopping_budget);
        budgetsTextView.setText(budgetsByTag.getShoppingBudget() + "");
        budgetsTextView = findViewById(R.id.text_daily_supplies_budget);
        budgetsTextView.setText(budgetsByTag.getDailySuppliesBudget() + "");
        budgetsTextView = findViewById(R.id.text_etc_budget);
        budgetsTextView.setText(budgetsByTag.getEtcBudget() + "");
    }

    private class ExpensesByTag{ //  태그(카테고리)별 지출 금액을 편하게 읽기 위한 클래스
        private int foodExpenses = 0;
        private int transportationExpenses = 0;
        private int roomCharge = 0;
        private int shoppingExpenses = 0;
        private int dailySuppliesExpenses = 0;
        private int etcExpenses = 0;

        ExpensesByTag(){
            String tag = null;
            int exchangedAmount = 0;

            DBHelper helper = new DBHelper(DetailsActivity.this);
            SQLiteDatabase db = helper.getReadableDatabase();
            String sql = "select exchangedAmount, tag from tb_accountBook where isBudget = 0 and title  = " + "\'" + title + "\'";
            Cursor cursor = null;
            try{
                cursor = db.rawQuery(sql, null);
            } catch (Exception e){

            }

            if(cursor != null){
                while(cursor.moveToNext()){
                    tag = cursor.getString(1);
                    exchangedAmount = cursor.getInt(0);
                    switch(tag){
                        case "식비":
                            foodExpenses += exchangedAmount;
                            break;
                        case "교통비":
                            transportationExpenses += exchangedAmount;
                            break;
                        case "숙박비":
                            roomCharge += exchangedAmount;
                            break;
                        case "쇼핑":
                            shoppingExpenses += exchangedAmount;
                            break;
                        case "생활용품":
                            dailySuppliesExpenses += exchangedAmount;
                            break;
                        case "기타":
                            etcExpenses += exchangedAmount;
                    }
                }
            }

            cursor.close();
            db.close();

        }

        private int getFoodExpenses(){return foodExpenses;}
        private int getTransportationExpenses(){return transportationExpenses;}
        private int getRoomCharge(){return roomCharge;}
        private int getShoppingExpenses(){return shoppingExpenses;}
        private int getDailySuppliesExpenses(){return dailySuppliesExpenses;}
        private int getEtcExpenses(){return etcExpenses;}
        private Integer[] getExpensesArray(){
            String[] tags = getResources().getStringArray(R.array.tags);
            Integer[] expensesArray = new Integer[tags.length];

            expensesArray[0] = new Integer(getFoodExpenses());
            expensesArray[1] = new Integer(getTransportationExpenses());
            expensesArray[2] = new Integer(getRoomCharge());
            expensesArray[3] = new Integer(getShoppingExpenses());
            expensesArray[4] = new Integer(getDailySuppliesExpenses());
            expensesArray[5] = new Integer(getEtcExpenses());

            return expensesArray;
        }
    }

    // 원형 그래프 세팅
    private void setPieChart(ExpensesByTag expensesByTag){
        if(expensesByTag == null) return;

        PieChart pieChart = findViewById(R.id.pi_chart);
        BarChart barChart = findViewById(R.id.bar_chart);

        pieChart.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.INVISIBLE);

        pieChart.setUsePercentValues(false); // 퍼센트로 된 값들을 넣을 것인지
        pieChart.setExtraOffsets(5,10,5,5); // 상하좌우 간격

        pieChart.setDrawHoleEnabled(false); // true - 도넛모향 그래프
        pieChart.setTouchEnabled(false); // 터치하더라도 아무것도 안되도록

        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description); // 그래프에 우측 하단에 표시되는 description

        ArrayList<PieEntry> val = new ArrayList<>(); // 그래프에 표시할 값
        Integer[] expenses = expensesByTag.getExpensesArray();
        String[] tags = getResources().getStringArray(R.array.tags);
        for(int i = 0; i < expenses.length; ++i){
            if(expenses[i] != 0) val.add(new PieEntry(expenses[i],tags[i]));
        }

        PieDataSet pieDataSet = new PieDataSet(val,null);
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setSliceSpace(1.5f); // 그래프 항목 사이 간격

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextColor(Color.WHITE); // 그래프에 표시되는 값들의 색
        pieData.setValueTextSize(15f); // 그래프에 표시되는 값들의 크기

        pieChart.setData(pieData);
    }

    // 막대 그래프 세팅
    private void setBarChart(ExpensesByTag expensesByTag){
        if(expensesByTag == null) return;

        PieChart pieChart = findViewById(R.id.pi_chart);
        BarChart barChart = findViewById(R.id.bar_chart);

        pieChart.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.VISIBLE);

        ArrayList<BarEntry> entries = new ArrayList<>();
        Integer[] expenses = expensesByTag.getExpensesArray();
        for(int i = 0; i < expenses.length; ++i){
            entries.add(new BarEntry (i, expenses[i]));
        }
        BarDataSet dataSet = new BarDataSet(entries, null);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        BarData data = new BarData(dataSet);
        barChart.setData(data);


        barChart.setTouchEnabled(false);
        barChart.setDescription(null);
        YAxis left = barChart.getAxisLeft();
        left.setAxisMinimum(0);
        barChart.getAxisRight().setEnabled(false);
        XAxis bottomAxis = barChart.getXAxis();
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setDrawGridLines(false);
        barChart.setDrawValueAboveBar(false);

        String[] tags = getResources().getStringArray(R.array.tags);
        bottomAxis.setLabelCount(tags.length); // 배열 길이 이용

        final ArrayList<String> tagsList = new ArrayList<>();
        for(int i = 0; i < tags.length; ++i){
            tagsList.add(tags[i]);
        }

        bottomAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value >= 0 && value <= tagsList.size() - 1){
                    return tagsList.get((int) value);
                }
                return "";
            }
        });
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
