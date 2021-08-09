package com.example.midstart;



public class UserAccount {

    private String idToken; //Firebase Uid (고유 토큰 정보)
    private String emailId;
    private String password;

    private String name;




    //파이어베이스 특징-빈 생성자를 만들어줘야 함
    public UserAccount(){

    }
    public UserAccount(String idToken, String emailId,String password){
        this.idToken = idToken;
        this.emailId = emailId;
        this.password = password;

    }

    public String getIdToken(){
        return idToken;
    }
    public void setIdToken(String idToken){
        this.idToken=idToken;
    }
    public String getEmailId(){
        return emailId;
    }
    public void setEmailId(String emailId){
        this.emailId=emailId;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }


}
