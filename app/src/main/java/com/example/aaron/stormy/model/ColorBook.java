package com.example.aaron.stormy.model;

import android.graphics.Color;

public class ColorBook {

    private String[] mColors;

    public ColorBook(String[] colors){
        mColors = colors;
    }

    public int getColor(int id){
        String color;
        color = mColors[id];
        int colorAsInt = Color.parseColor(color);
        return colorAsInt;
    }

}
