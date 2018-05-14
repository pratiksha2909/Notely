package com.notely.pratiksha.view;

/**
 * Created by pratiksha on 5/1/18.
 */

public class Pair {
    private String textId;
    private String text;

    public Pair(String textId, String text){
        this.textId = textId;
        this.text = text;
    }

    public String getTextId() {
        return textId;
    }

    public void setTextId(String textId) {
        this.textId = textId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}