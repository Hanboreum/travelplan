package com.example.all_in_won;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Budgets { // 저장된 예산들을 편하게 읽기 위한 클래스
    private int foodBudget = 0;
    private int transportationBudget = 0;
    private int roomBudget = 0;
    private int shoppingBudget = 0;
    private int dailySuppliesBudget = 0;
    private int etcBudget = 0;
    private int totalBudget = 0;
    private int[] budgets;

    // title로 가계부 제목을 넘겨받아 해당 가계부의 예산들을 DB에서 불러온다
    Budgets(Context context, String title){
        String tag = null;
        String[] tags = context.getResources().getStringArray(R.array.tags);
        int exchangedAmount = 0;
        budgets = new int[tags.length + 1];

        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select exchangedAmount, tag from tb_accountBook where isBudget = 1 and title  = " + "\'" + title + "\'";
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(sql, null);
        } catch (Exception e){

        }

        if(cursor != null){
            while(cursor.moveToNext()){
                tag = cursor.getString(1);
                exchangedAmount = cursor.getInt(0);
                if(tag.equals("총예산")){
                    budgets[tags.length] = exchangedAmount;
                    totalBudget = exchangedAmount;
                } else {
                    for(int i = 0; i < tags.length; ++i){
                        if(tag.equals(tags[i])){
                            budgets[i] = exchangedAmount;
                        }
                    }
                }

                foodBudget = budgets[0];
                transportationBudget = budgets[1];
                roomBudget = budgets[2];
                shoppingBudget = budgets[3];
                dailySuppliesBudget = budgets[4];
                etcBudget = budgets[5];
            }
        }

        cursor.close();
        db.close();

    }

    public int getFoodBudget(){return foodBudget;}
    public int getTransportationBudget(){return transportationBudget;}
    public int getRoomBudget(){return roomBudget;}
    public int getShoppingBudget(){return shoppingBudget;}
    public int getDailySuppliesBudget(){return dailySuppliesBudget;}
    public int getEtcBudget(){return etcBudget;}
    public int getTotalBudget(){return totalBudget;}

    public final int[] getBudgets() {
        return budgets;
    }
}
