package com.darktornado.kakaoemoticon;

import android.graphics.drawable.Drawable;

/**
 * Created by DarkTornado on 2021-03-08.
 */

public class Emoticon {
    String name, artist, url;
    Drawable icon;

    public Emoticon(String name, String artist, String url, Drawable icon) {
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.icon = icon;
    }
}
