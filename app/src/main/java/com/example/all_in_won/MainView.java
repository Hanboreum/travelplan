package com.example.all_in_won;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.all_in_won.Cal.Calmain;
import com.example.all_in_won.Todo.Todomain;

import Address.AdMainActivity;
import Memo.MemoMainActivity;
import Traffic.TrMainActivity;


public class MainView extends AppCompatActivity {
    Button todo,memo,money,cal,stay,traffic,shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        setTitle("HOME");

        todo = findViewById(R.id.todo);
        memo = findViewById(R.id.memo);
        money = findViewById(R.id.money);
        cal = findViewById(R.id.cal);
        stay = findViewById(R.id.stay);
        traffic = findViewById(R.id.traffic);
        shop = findViewById(R.id.shop);

        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Todomain.class);
                startActivity(intent);
                getIntent();

            }
        });
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Calmain.class);
                startActivity(intent);
                getIntent();

            }
        });
        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                getIntent();

            }
        });
        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AdMainActivity.class);
                startActivity(intent);
                getIntent();

            }
        });
        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), TrMainActivity.class);
                startActivity(intent);
                getIntent();

            }
        });
        memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), MemoMainActivity.class);
                startActivity(intent);
                getIntent();

            }
        });


    }
}