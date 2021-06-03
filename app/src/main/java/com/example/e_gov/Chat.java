package com.example.e_gov;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;

    private String question;
    private String answer;

    DatabaseHelper myDb;
    private List<Message> msgList = new ArrayList<Message>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        initMsg();

        // initialize answer database.
        myDb = new DatabaseHelper(this);
        myDb.cleanFQAAnswer();
        myDb.setFQAAnswer("Yes,you are right.");
        myDb.setFQAAnswer("No.");



        adapter = new MsgAdapter(Chat.this,R.layout.list_chat,msgList);
        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        msgListView = findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if(!content.equals(""))
                {
                    Message msg = new Message(content, Message.SENT);
                    question = content;
                    msgList.add(msg);
                    // refresh ListView
                    adapter.notifyDataSetChanged();
                    // locate to the last line
                    msgListView.setSelection(msgList.size());
                    // clear input
                    inputText.setText("");

                    answer = myDb.getAnswer(question);
                    Message msg2 = new Message(answer,Message.RECEIVED);
                    msgList.add(msg2);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                }


            }
        });
    }

    private void initMsg()
    {
        Message msg1 = new Message("What can I help you?",Message.RECEIVED);
        msgList.add(msg1);

        Message msg2 = new Message("Hello World", Message.SENT);
        msgList.add(msg2);
    }



}
