package com.example.firebasesample;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /*
    Firebase의 database를 접근해 채팅이력이 있는 채팅방을 표시
    사용자들에게 접속할 채팅방과 id를 입력받아 다음액티비티에 전달
     */


    EditText edt_chatroom, edt_id;
    Button btn_connect;
    ListView chat_list;

    //Firebase의 데이터베이스에 접근하기위한 변수
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference ref = firebaseDatabase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        buttonListeners();
    }

    private void buttonListeners() {
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //채팅 화면으로 이동
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                //사용자가 입력한 채팅방 이름과 아이디를 다음 화면에 전달
                intent.putExtra("chatName", edt_chatroom.getText().toString());
                intent.putExtra("userName", edt_id.getText().toString());
                startActivity(intent);
            }
        });
        //채팅방 목록을 로드하는 함수 호출
        showChatList();
    }

    private void init() {
        edt_chatroom = findViewById(R.id.edt_chatroom);
        btn_connect = findViewById(R.id.btn_connect);
        chat_list = findViewById(R.id.chatroom_list);
        chat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //선택한 항목의 TextView()
                TextView text = view.findViewById(android.R.id.text1);
                String chat_room = text.getText().toString();
                Intent intent = new Intent(MainActivity.this,
                        ChatActivity.class);
                intent.putExtra("chatName", chat_room);
                intent.putExtra("userName", edt_id.getText().toString());
                startActivity(intent);
            }
        });
        edt_id = findViewById(R.id.edt_id);
    }

    //리스트 어댑터에 채팅목록을 추가 및 리스트뷰 갱식
    private void showChatList() {

        //채팅목록을 저장하는 어댑터 객체 생성 및 리스트 뷰에 등록
        //값이 들어올때마다 리스트를 갱신하기 위해 ArrayAdapter를 사용, 수정과 삭제가 용이한 자료형
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1);

        chat_list.setAdapter(adapter);

        //Firebase 서버에서 주는 데이터에 맞춰 어뎁터 객체 갱신
        ref.child("chat").addChildEventListener(new ChildEventListener() {
            //데이터 추가가 됨을 확인했을때 호출되는 메소드
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // DataSnapshot : 추가된 데이터 항목에 대한 정보가 저장된 객체
                adapter.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
