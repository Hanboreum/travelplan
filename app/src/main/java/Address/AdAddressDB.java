package Address;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class AdAddressDB extends SQLiteOpenHelper {
    //Address
    public AdAddressDB(@Nullable Context context) {
        super(context, "address.db",null,1);
    }
    //DB가 만들어질때 TABLE 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tbladd(_id integer primary key autoincrement,name text,address text,tel text);");
        db.execSQL("insert into tbladd(name,address,tel) values('가','다구','010-1111-1111');");
        db.execSQL("insert into tbladd(name,address,tel) values('나','나구','010-0000-0000');");
        db.execSQL("insert into tbladd(name,address,tel) values('다','가구','010-1111-1111');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
