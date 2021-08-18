package com.example.midstart;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserAccount {

    private String idToken; //Firebase Uid (고유 토큰 정보)
    private String emailId;
    private String password;

    private String name;

    public List<diary> diaryList;




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

    public void addDiary(diary d){

        diaryList.add(d);
    }

    public void setDiaryList(){
        diaryList=new ArrayList();
        diary d=new diary("0000-00-00 00","오늘은 밋스타트를 시작한 날이야.");
        diaryList.add(d);
    }
    public void removeTodayDiary(){
        if(!diaryList.isEmpty()&&diaryList.size()!=1)
            diaryList.remove(diaryList.size()-1);
    }

    public boolean checkdiaryDate(String newDate){
        String date;

        diary d;

        if( diaryList.isEmpty() ){
            date= "2000-01-01 00:00:00";
        }
        else {
            d= diaryList.get(diaryList.size()-1);
            date=d.getDate();
        }

        date=date.substring(0,10);
        String tempDate=newDate.substring(0,10);
        if(date.equals(tempDate)) return false;
        else return true;

    }
    public String getDiaryListAll(){
       String s="일기목록-임시(달력 옮기면 지울 예정)\n";
       for(int i=1;i<diaryList.size();i++){
           s= s+diaryList.get(i).getDate()+" : "+ diaryList.get(i).getDiary()+"\n";
       }
       return s;
    }

    //일기 개수+1을 리턴(회원가입 시 생기는 첫번째 일기가 포함돼서 한 개 더 많음)
    public int getDiaryNum(){
        return diaryList.size();
    }

    //해당하는 인덱스의 diary를 리턴
    public diary getcertainDiary(int index){
        return diaryList.get(index);
    }



}
