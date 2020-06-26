package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {//承接View.OnClickListener接口
    private Button btn_cnt;
    private EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_cnt = (Button) findViewById(R.id.btn_cnt);
        et_name = findViewById(R.id.et_name);
        btn_cnt.setOnClickListener(MainActivity.this);
    }

    public void onClick(View view) {
        String name = et_name.getText().toString();
        if ("".equals(name)) {
            Toast.makeText(this, "请输入用户名：", Toast.LENGTH_SHORT).show();
                //如果输入的用户名为空的话，那么下端会出现提示
        } else {
            Intent intent=new Intent(MainActivity.this,ChatRoom.class);
            intent.putExtra("username",name);
            startActivity(intent);
        }
    }
}

