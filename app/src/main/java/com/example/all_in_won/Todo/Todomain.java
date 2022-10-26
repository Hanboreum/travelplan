package com.example.all_in_won.Todo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.all_in_won.R;

public class Todomain extends AppCompatActivity {
    private static final String TAG = "Todomain";

    Fragment mainFragment;
    EditText inputToDo;
    Context context;

    public static com.example.all_in_won.Todo.NoteDatabase noteDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todomain);

        mainFragment = new com.example.all_in_won.Todo.MainFragment();

        //getSupportFragmentManager 을 이용하여 이전에 만들었던 **FrameLayout**에 `fragment_main.xml`이 추가
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                saveToDo();

                Toast.makeText(getApplicationContext(),"추가되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
        openDatabase();
    }
    private void saveToDo(){
        inputToDo = findViewById(R.id.inputToDo);

        //EditText에 적힌 글을 가져오기
        String todo = inputToDo.getText().toString();

        //테이블에 값을 추가하는 sql구문 insert...
        String sqlSave = "insert into " + com.example.all_in_won.Todo.NoteDatabase.TABLE_NOTE + " (TODO) values (" +
                "'" + todo + "')";

        //sql문 실행
        com.example.all_in_won.Todo.NoteDatabase database = com.example.all_in_won.Todo.NoteDatabase.getInstance(context);
        database.execSQL(sqlSave);

        //저장과 동시에 EditText 안의 글 초기화
        inputToDo.setText("");
    }


    public void openDatabase() {
        // open database
        if (noteDatabase != null) {
            noteDatabase.close();
            noteDatabase = null;
        }

        noteDatabase = com.example.all_in_won.Todo.NoteDatabase.getInstance(this);
        boolean isOpen = noteDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Note database is open.");
        } else {
            Log.d(TAG, "Note database is not open.");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (noteDatabase != null) {
            noteDatabase.close();
            noteDatabase = null;
        }
    }


}