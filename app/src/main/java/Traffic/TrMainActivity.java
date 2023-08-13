package Traffic;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_in_won.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TrMainActivity extends AppCompatActivity {
    TrAddDB helper;
    SQLiteDatabase db;

    //데이터 결과를 넣기 위해
    Cursor cursor;

    TrAddAdapter adapter;
    ArrayList<TrAddVO> array;

    RecyclerView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tr_activity_main);

        getSupportActionBar().setTitle("교통편");

        //context는 activity
        helper=new TrAddDB(this);
        db=helper.getReadableDatabase();
        cursor=db.rawQuery("select * from juso" ,null);

        array=new ArrayList<TrAddVO>();

        while(cursor.moveToNext()){
            TrAddVO vo=new TrAddVO();
            vo.setId(cursor.getInt(0));
            vo.setName(cursor.getString(1));
            vo.setAdd(cursor.getString(2));
            array.add(vo);

        }
        list=findViewById(R.id.list);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        list.setLayoutManager(manager);


        adapter=new TrAddAdapter(array,this);
        list.setAdapter(adapter);

        FloatingActionButton btnadd=findViewById(R.id.btnadd);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout view=(LinearLayout)getLayoutInflater().inflate(R.layout.trinput,null);
                AlertDialog.Builder box=new AlertDialog.Builder(TrMainActivity.this);
                box.setTitle("교통편");
                box.setView(view);
                box.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edtname=view.findViewById(R.id.edtname);
                        EditText edtadd=view.findViewById(R.id.edtadd);
                        String strname=edtname.getText().toString();
                        String stradd=edtadd.getText().toString();

                        //데이터베이스에  추가
                        String sql="insert into juso(name,address) values(";
                        sql += "'" + strname + "',";
                        sql += "'" + stradd + "')";
                        db.execSQL(sql);

                        //화면에 추가
                        TrAddVO vo=new TrAddVO();
                        vo.setName(strname);
                        vo.setAdd(stradd);
                        array.add(vo);
                        //어덥터 Refresh
                        adapter.notifyDataSetChanged();

                    }
                });
                box.setNegativeButton("닫기",null);
                box.show();
            }
        });

    }
    //옵션메뉴등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trmenu, menu);
        MenuItem search=menu.findItem(R.id.search);

        SearchView searchView=(SearchView)search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str) {
                cursor=db.rawQuery("select * from juso where name like '%" + str + "%' or address like '%" + str + "%'",null);
                array.clear();
                while (cursor.moveToNext()){
                    TrAddVO vo=new TrAddVO();
                    vo.setId(cursor.getInt(0));
                    vo.setName(cursor.getString(1));
                    vo.setAdd(cursor.getString(2));
                    array.add(vo);
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemname:
                cursor=db.rawQuery("select * from juso order by name",null);
                break;
            case R.id.itemadd:
                cursor=db.rawQuery("select * from juso order by address",null);
                break;
        }
        array.clear();
        while (cursor.moveToNext()){
            TrAddVO vo=new TrAddVO();
            vo.setId(cursor.getInt(0));
            vo.setName(cursor.getString(1));
            vo.setAdd(cursor.getString(2));
            array.add(vo);
        }
        adapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }
}

