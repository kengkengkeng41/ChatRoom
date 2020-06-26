package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatRoom extends AppCompatActivity implements View.OnClickListener{
    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    boolean isRunning = false;
    private boolean isSend=false;
    private String myName;
    private String responseData;
    private int curr;                   //当前显示的消息条数
    private int jsonLen;                //获取到的json列表长度
    private Handler handler = new Handler(Looper.myLooper()){
        //获取当前进程的Looper对象传给handler
        @Override
        public void handleMessage(Message message){
            String message_Name = message.getData().getString("name");
            String message_msgC = message.getData().getString("msgContent");
            if(!message_msgC.equals("")){
                if(message_Name.equals(myName))
                    addNewMessage(message_msgC, Msg.TYPE_SENT);
                else
                    addNewMessage(message_msgC,Msg.TYPE_RECEIVED);
            }
        }
    };
    public void addNewMessage(String msg,int type){
        Msg message = new Msg(msg,type);
        msgList.add(message);
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Intent intent =getIntent();
        myName=intent.getStringExtra("username");
        curr=0;
        jsonLen=1;
        isRunning=true;
        inputText = findViewById(R.id.input_text);
        send=findViewById(R.id.send);
        send.setOnClickListener(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = new LinearLayoutManager(ChatRoom.this);
                msgRecyclerView= findViewById(R.id.msg_recycler_view);
                msgRecyclerView.setLayoutManager(layoutManager);
                adapter = new MsgAdapter(msgList);
                msgRecyclerView.setAdapter(adapter);
            }
        });
        new Thread(new Receive(), "接收线程").start();
        new Thread(new Send(), "发送线程").start();
    }

    public void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            jsonLen = jsonArray.length();
            for (; curr < jsonLen; curr++) {
                JSONObject jsonObject = jsonArray.getJSONObject(curr);
                String name = jsonObject.getString("name");
                String msgContent = jsonObject.getString("message");
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("msgContent", msgContent);  //往Bundle中存放数据
                message.setData(bundle);//mes利用Bundle传递数据
                handler.sendMessage(message);//用activity中的handler发送消息
            }
        } catch (Exception e) {
            Looper.prepare();
            Toast.makeText(ChatRoom.this, "解析json错误!", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    String msgEntity;
    @Override
    public void onClick(View view){
        String content = inputText.getText().toString();
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        StringBuilder sb = new StringBuilder();
        msgEntity = myName;
        sb.append(msgEntity).append("\n"+date+"\n"+content);
        msgEntity = sb.toString();
        if(!"".equals(msgEntity)){
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("name", myName);
            bundle.putString("msgContent", msgEntity);  //往Bundle中存放数据
            message.setData(bundle);//mes利用Bundle传递数据
            handler.sendMessage(message);//用activity中的handler发送消息
            inputText.setText("");
            isSend = true;
            curr++;
        }
        sb.delete(0,sb.length());
    }
    class Send implements Runnable{
        @Override
        public void run(){           //发送线程
            while(isRunning){
                if(isSend){
                    RequestBody requestBody = new FormBody.Builder()
                            .add("name",myName)
                            .add("message",msgEntity)
                            .build();
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request2 = new Request.Builder()
                                // 指定访问的服务器地址
                                .url(Resource.DiffUrl).post(requestBody)
                                .build();
                        Response response = client.newCall(request2).execute();
                        String responseData = response.body().string();
//                        parseJSONWithJSONObject(responseData);
                        isSend = false;
                    } catch (Exception e) {
                        Looper.prepare();
                        Toast.makeText(ChatRoom.this, "发送失败！", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }
        }
    }
    class Receive implements Runnable{
        public void run(){
            while(isRunning){
                if(!isSend) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                // 指定访问的服务器地址
                                .url(Resource.DiffUrl).get()
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        if (responseData != null && responseData.startsWith("\ufeff")) {
                            responseData = responseData.substring(1);
                        }
                        parseJSONWithJSONObject(responseData);
                    } catch (Exception e) {
                        Looper.prepare();
                        Toast.makeText(ChatRoom.this, "连接服务器失败！！！", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }
        }
    }
}
