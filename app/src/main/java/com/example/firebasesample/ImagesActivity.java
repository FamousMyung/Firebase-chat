package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
리스트뷰에 해당 채팅방에 업로드된 파일 목록이 뜨는 화면
클릭시 ImageActivity에게 선택한 파일의 이름을 전달
 */

public class ImagesActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dataRef = firebaseDatabase.getReference();

    ListView list_Image;
    String CHAT_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        init();
        intentListener();
        adapter();
    }


    private void adapter() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1);

        list_Image.setAdapter(adapter);

        //파이어베이스의 데이터베이스에 file/채팅방이름/ 에 데이터가 변화를 감지하는 리스너
        dataRef.child("file").child(CHAT_NAME).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatData data = dataSnapshot.getValue(ChatData.class);
                adapter.add(data.getMessage());
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

        list_Image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title = view.findViewById(android.R.id.text1);
                Intent intent1 = new Intent(ImagesActivity.this, ImageActivity.class);
                //다운받을 파일의 경로를 전달 채팅방이름/파일명
                intent1.putExtra("image", CHAT_NAME + "/" + title.getText().toString());
                startActivity(intent1);
            }
        });
    }

    private void intentListener() {
        Intent intent = getIntent();
        CHAT_NAME = intent.getStringExtra("chatName");
    }

    private void init() {
        list_Image = findViewById(R.id.images);
    }
}