package com.example.aaron.stormy.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;


public class ColorBook {

    private int[] mBackgroundIds;

    public ColorBook(Context context, String[] colors){
        mBackgroundIds = new int[colors.length];
        Resources res = context.getResources();
        int resID;
        for(int i = 0; i < colors.length; i++){
            resID = res.getIdentifier(colors[i], "drawable", context.getPackageName());
            mBackgroundIds[i] = resID;
        }
    }

    public int getBackgroudId(int id){
        return mBackgroundIds[id];
    }

}
