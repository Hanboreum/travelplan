package Address;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.all_in_won.R;

public class AdInsertActivity extends AppCompatActivity {
    AdAddressDB helper;
    SQLiteDatabase db;
    EditText edtname,edtaddress,edttel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adactivity_insert);
        edtname=findViewById(R.id.edtname);
        edtaddress=findViewById(R.id.edtaddress);
        edttel=findViewById(R.id.edttel);
        helper=new AdAddressDB(this);
        db=helper.getWritableDatabase();
    }

    public void mClick(View v){
        switch (v.getId()){
            case R.id.btnsave:
                String strname=edtname.getText().toString();
                String straddress=edtaddress.getText().toString();
                String strtel=edttel.getText().toString();
                String sql="insert into tbladd(name,tel,address) values(";
                sql += "'"+strname+"',";
                sql += "'"+strtel+"',";
                sql += "'"+straddress+"')";
                db.execSQL(sql);
                Toast.makeText(AdInsertActivity.this,"저장됐습니다",
                        Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.btncancel:
                //닫고 메인으로 돌아가기
                finish();
                break;
        }
    }


}
