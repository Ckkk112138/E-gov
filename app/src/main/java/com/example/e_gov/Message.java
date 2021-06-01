package com.example.e_gov;

public class Message {
    public static final int RECEIVED = 0;
    public static final int SENT = 1;
    private String content;
    private int type;

    public Message(String content, int type)
    {
        this.content = content;
        this.type = type;
    }

    public String getContent()
    {
        return content;
    }

    public int getType()
    {
        return type;
    }
}
