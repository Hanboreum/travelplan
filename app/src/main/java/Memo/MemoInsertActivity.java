package Memo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.all_in_won.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MemoInsertActivity extends AppCompatActivity {
    MemoDB helper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_activity_insert);

        getSupportActionBar().setTitle("메모 입력");
        //뒤로가기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        helper=new MemoDB(this);
        db=helper.getWritableDatabase();

        //저장 버튼에 이벤트 달기
        FloatingActionButton btnsave=findViewById(R.id.btnsave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtcontent=findViewById(R.id.edtcontent);
                String strcontent=edtcontent.getText().toString();

                //현재날짜 저장
                Date now=new Date();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd hh:mm ss");

                //이상한 시간 조정
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                String strnow=sdf.format(now);


                String sql="insert into memo(content,wdate) values(";
                sql += "'" + strcontent + "',";
                sql += "'" + strnow + "')";
                db.execSQL(sql);
                Toast.makeText(MemoInsertActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }
    //뒤로가기 옵션 버튼에 이벤트주기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}