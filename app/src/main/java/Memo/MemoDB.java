package Memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


//SQLiteOpenHelper 상속
public class MemoDB extends SQLiteOpenHelper {
    public MemoDB(@Nullable Context context) {
        super(context, "memo.db", null, 1);
    }
    //테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table memo(_id integer primary key autoincrement,content text,wdate text)");
        db.execSQL("insert into memo(content,wdate) values('수영장 청소','2019/11/09 06:10:30')");
        db.execSQL("insert into memo(content,wdate) values('컴퓨터 조립','2019/11/19 06:10:30')");
        db.execSQL("insert into memo(content,wdate) values('운동화','2019/11/04 06:10:30')");
        db.execSQL("insert into memo(content,wdate) values('스트레칭','2019/11/01 06:10:30')");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}