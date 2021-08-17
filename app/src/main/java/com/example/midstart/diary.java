package com.example.midstart;

public class diary {

    public String todayDate;
    public String diary_content;
    public diary(){

    }
    public diary(String t, String d){
         this.todayDate=t;
         this.diary_content=d;
    }

    public String getDate(){
        return this.todayDate;
    }
    public String getDiary(){
        return this.diary_content;
    }


}
