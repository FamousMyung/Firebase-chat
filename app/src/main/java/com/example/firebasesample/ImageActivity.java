package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
/*
ImagesActivity가 준 경로를 바탕으로 파이어베이스에서 이미지를 다운받아 화면에 출력
 */

public class ImageActivity extends AppCompatActivity {

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference stoRef = firebaseStorage.getReference();
    ImageView image;
    String download_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        init();
        intentListener();
    }

    private void intentListener() {
        Intent intent = getIntent();
        download_image = intent.getStringExtra("image");
        //파이어베이스에 다운로드 요청처리
        //다운받을 파일의 최대 사이즈
        final long FILE_SIZE = 2048 * 2048;
        StorageReference down_ref = stoRef.child(download_image);
        down_ref.getBytes(FILE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //다운받은 파일의 바이트벼열을 비트맵 객체로 변환
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImageActivity.this, "다운로드 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        image = findViewById(R.id.down_image);

    }
}