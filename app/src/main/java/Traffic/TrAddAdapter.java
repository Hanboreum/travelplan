package Traffic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.all_in_won.R;

import java.util.ArrayList;

public class TrAddAdapter extends RecyclerView.Adapter<TrAddAdapter.ViewHolder> {
    //생성자를 만들기 위해서
    //데이터를 저장할 공간
    ArrayList<TrAddVO> array;

    //어디 actuvuty에서 호출했는지
    Context context;



    public TrAddAdapter(ArrayList<TrAddVO> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //view 생성
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.tritem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final int id=array.get(position).getId();
        //final=값이 변하지 못하게함
        holder.txtname.setText(array.get(position).getName());
        holder.txtadd.setText(array.get(position).getAdd());
        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LinearLayout view=(LinearLayout)LayoutInflater.from(context).inflate(R.layout.trinput,null);
                final EditText edtname=view.findViewById(R.id.edtname);
                final EditText edtadd=view.findViewById(R.id.edtadd);
                edtname.setText(array.get(position).getName());
                edtadd.setText(array.get(position).getAdd());

                AlertDialog.Builder box=new AlertDialog.Builder(context);
                box.setTitle("주소수정" + id);
                box.setView(view);
                box.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TrAddDB helper=new TrAddDB(context);
                        SQLiteDatabase db=helper.getWritableDatabase();
                        String sql="update juso set name='" + edtname.getText().toString() + "'";
                        sql += ", address='" + edtadd.getText().toString() + "'";
                        sql += " where id=" +id;
                        db.execSQL(sql);

                        array.remove(position);
                        TrAddVO vo=new TrAddVO();
                        vo.setId(id);
                        vo.setName(edtname.getText().toString());
                        vo.setAdd(edtadd.getText().toString());
                        array.add(position,vo);
                        notifyDataSetChanged();

                    }
                });
                box.setNegativeButton("닫기",null);
                box.show();
                return false;
            }
        });
        holder.btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder  box=new AlertDialog.Builder(context);
                box.setMessage(id + "를(을)삭제하시겠습니까?");
                box.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TrAddDB helper=new TrAddDB(context);
                        SQLiteDatabase db=helper.getWritableDatabase();
                        String sql="delete from juso where id=" +id;
                        db.execSQL(sql);

                        array.remove(position);
                        notifyDataSetChanged();
                    }
                });
                box.setNegativeButton("닫기",null);
                box.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtname,txtadd;
        ImageView btndel;
        RelativeLayout item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtname=itemView.findViewById(R.id.txtname);
            txtadd=itemView.findViewById(R.id.txtadd);
            btndel=itemView.findViewById(R.id.btndel);
            item=itemView.findViewById(R.id.item);
        }
    }
}