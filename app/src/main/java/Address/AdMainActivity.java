package Address;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.all_in_won.R;

public class AdMainActivity extends AppCompatActivity {
    AdAddressDB helper;
    SQLiteDatabase db;
    CursorAdapter adapter;
    //SimpleCursorAdapter adapter;
    //Gittest
    ListView list;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adactivity_main);

        helper = new AdAddressDB(this);
        db = helper.getReadableDatabase();
        //data를 select 해서 cursor에 넣기
        cursor = db.rawQuery("select * from tbladd order by name", null);
        /*adapter=new SimpleCursorAdapter(this,
                R.layout.item,cursor,
                new String[]{"name","address","tel"},
                new int[]{R.id.txtname,R.id.txtaddress,
                R.id.txttel});

         */
        adapter = new MyAdapter(this, cursor);
        list = findViewById(R.id.list);
        list.setAdapter(adapter);

        Button btninsert = findViewById(R.id.btninsert);
        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdMainActivity.this, AdInsertActivity.class);
                startActivity(intent);
            }
        });
    }

    //커서 어덥터
    public class MyAdapter extends CursorAdapter {
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.aditem, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView txtname = view.findViewById(R.id.txtname);
            txtname.setText(cursor.getString(1));
            TextView txtaddress = view.findViewById(R.id.txtaddress);
            txtaddress.setText(cursor.getString(2));
            TextView txttel = view.findViewById(R.id.txttel);
            txttel.setText(cursor.getString(3));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemname:
                cursor = db.rawQuery("select * from tbladd order by name", null);
                break;
            case R.id.itemaddress:
                cursor = db.rawQuery("select * from tbladd order by address", null);
                break;
            case R.id.itemtel:
                cursor = db.rawQuery("select * from tbladd order by tel", null);
                break;
        }
        adapter.changeCursor(cursor);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        cursor = db.rawQuery("select * from tbladd order by name", null);
        adapter.changeCursor(cursor);
        super.onRestart();
    }


}