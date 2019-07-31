package com.example.firebasesample;

/*
ID와 채팅내용을 저장할 클래스(리스트 뷰에 들어가는 아이템을 저장할 때도 사용가능)
*/

public class ChatData {
    private String userName, message;
    public ChatData(){

    }
    public ChatData(String userName, String message){
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
