package com.dyb.clock;

public class Fruit {
    private String todoText;     //使用id锁定水果图片
    private String todoTime;   //对应的水果名字
    public Fruit(String todoText, String todoTime) {
        super();
        this.todoText = todoText;
        this.todoTime = todoTime;
    }

//    public Fruit(String todoText, String todoTimeText) {
//    }

    public String getTodoText() {
        return todoText;
    }
    public void setTodoText(String todoText) {
        this.todoText = todoText;
    }
    public String getTodoTime() {
        return todoTime;
    }
    public void setTodoTime(String todoTime) {
        this.todoTime = todoTime;
    }
}
