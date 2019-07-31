package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/*
사용자 입력을 Firebase의 데이터베이스에 송신
Firebase의 데이터베이스에 새로운 데이터가 갱신됬을때 채팅창에 업로드
 */

public class ChatActivity extends AppCompatActivity {

    ListView chat_view;
    EditText chat_edit;
    Button chat_send, show_image_list, image_send;

    //MainActivity에서 전달한 값을 저장할 변수
    String CHAT_NAME, USER_NAME;
    //파이어베이스에 데이터베이스를 접근할때 사용하는 변수
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dataRef = firebaseDatabase.getReference();

    //파이어베이스에 저장소를 접근할때 사용할 변수
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference stoRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        intentListener();
        Listener();
    }

    private void Listener() {
        //리스너 등록
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //채팅 내용을 저장한 객체를 생성
                ChatData data = new ChatData(USER_NAME, chat_edit.getText().toString());
                //해당 객체를 파이어베이스의 리얼타임데이터베이스에 송신
                dataRef.child("chat").child(CHAT_NAME).push().setValue(data);
            }
        });

        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //암시적 인텐트 : 다른 어플리케이션의 기능을 활용할때 사용
                //ACTION_PICK : 사용자가 파일을 선택하는 요청을 하는 인텐트
                Intent intent = new Intent(Intent.ACTION_PICK);
                //해당인텐트로 이미지 파일만 선택할수있도록 설정
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });
        show_image_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ImagesActivity.class);
                intent.putExtra("chatName",CHAT_NAME);
                startActivity(intent);
            }
        });
        openChat();
    }

    private void intentListener() {
        //이전 액티비티가 넘겨준 채팅방이름, 유저이름을 추출
        Intent intent = getIntent();
        CHAT_NAME = intent.getStringExtra("chatName");
        USER_NAME = intent.getStringExtra("userName");
    }

    private void init() {
        //위젯연결
        image_send = findViewById(R.id.btn_show_image_send);
        show_image_list = findViewById(R.id.btn_show_image_list);
        chat_view = findViewById(R.id.chat_view);
        chat_edit = findViewById(R.id.chat);
        chat_send = findViewById(R.id.chat_send);

    }

    //암시적 인텐트로 받은 값을 기반으로 파이어베이스에 이미지 업로드


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            //Content // 경로를 file://로 변환하는 코드
            Uri phothoUri = data.getData();
            Cursor cursor = null;
            File tempFile;
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(phothoUri, proj, null, null);
            int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_idx);
            //선택한 파일의 경로를 바탕으로 File객체 생성
            tempFile = new File(path);
            //전체 경로중에서 파일이름+확장자명을 추출
            final String filename = tempFile.getName();
            //파이어베이스에 업로드
            try {
                InputStream stream = new FileInputStream(tempFile);
                //업로드하는 파일이 저장되는 경로를 지정
                StorageReference reference = stoRef.child(CHAT_NAME + "/" + filename);
                //UploadTask : 파이어베이스에 파일을 업로드하는 서브스레드를 관리하는 클래스
                UploadTask uploadTask = reference.putStream(stream);
                //성공적으로 업로드가 끝날경우
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //업로드가 완료됬다는 토스트 출력
                        Toast.makeText(ChatActivity.this, "이미지업로드가 완료됨", Toast.LENGTH_LONG).show();
                        //리얼 타임 데이터베이스에 업로드 사항을 등록
                        ChatData data = new ChatData(USER_NAME, filename);
                        //스토리지에 저장된파일의 등록을 저장하는 공간에 데이터 추가
                        dataRef.child("file").child(CHAT_NAME).push().setValue(data);
                        data.setMessage(filename + "이미지를 업로드 했습니다.");
                        dataRef.child("chat").child(CHAT_NAME).push().setValue(data);
                    }
                });

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //채팅방 내용을 가져오는 설정 함수
    private void openChat() {
        //채팅내용을 저장할 어댑터와 리스트뷰 연결
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1);

        chat_view.setAdapter(adapter);
        //파이어베이스의 데이터베이스에 특정폴더에 데이터가 변경됨을 리스너로 확인
        dataRef.child("chat").child(CHAT_NAME).addChildEventListener(new ChildEventListener() {

            //채팅 내용이 추가될때마다 호출되는 함수
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //ChatData 객체를 추출 - DataSnapshot 객체를 기반
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                //추출한 ChatData객체의 변수값을 바탕으로 어댑터에 아이템추가
                adapter.add(chatData.getUserName() + " : " + chatData.getMessage());
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
