package com.example.deviceinfo;

import android.graphics.drawable.Drawable;


/**
 * @author Antonio Pietroluongo
 */
public class Item {
    private int type;
    private String str1;
    private String str2;
    private String str3;
    private String str4;
    private Drawable icon;


    public Item(int type, String str1) {
        this(type, str1, null, null, null, null);
    }

    public Item(int type, String str1, String str2) {
        this(type, str1, str2, null, null, null);
    }

    public Item(int type, String str1, String str2, String str3, String str4, Drawable icon) {
        this.type = type;
        this.str1 = str1;
        this.str2 = str2;
        this.str3 = str3;
        this.str4 = str4;
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public String getStr3() {
        return str3;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }

    public String getStr4() {
        return str4;
    }

    public void setStr4(String str4) {
        this.str4 = str4;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
