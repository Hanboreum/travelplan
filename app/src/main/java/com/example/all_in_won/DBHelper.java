package com.example.all_in_won;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, "accountBookDB", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String accountBookSQL = "create table tb_accountBook (" +
                "title," +
                "_id integer primary key autoincrement, " +
                "date, " +
                "amount, " +
                "currency, " +
                "exchangedAmount," +
                "detail, " +
                "whereToUse, " +
                "tag, " +
                "isBudget)";
        db.execSQL(accountBookSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == DATABASE_VERSION) {
            db.execSQL("drop table tb_accountBook");
            onCreate(db);
        }
    }
}
