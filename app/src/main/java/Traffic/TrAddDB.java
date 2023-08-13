package Traffic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TrAddDB extends SQLiteOpenHelper {
    public TrAddDB(@Nullable Context context) {
        super(context, "juso.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table juso(id integer primary key autoincrement,name text,address text)");
        db.execSQL("insert into juso(name,address) values('김경민','시카고')");
        db.execSQL("insert into juso(name,address) values('홍경민','북한')");
        db.execSQL("insert into juso(name,address) values('나경민','LA')");
        db.execSQL("insert into juso(name,address) values('오경민','독일')");
        db.execSQL("insert into juso(name,address) values('남궁경민','러시아')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
