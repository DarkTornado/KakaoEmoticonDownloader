package com.darktornado.kakaoemoticon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by DarkTornado on 2021-03-08.
 */

public class Utils {


    public static String getWebText(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            if (con != null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder str = new StringBuilder(br.readLine());
                String line = "";
                while ((line = br.readLine()) != null) {
                    str.append("\n").append(line);
                }
                br.close();
                isr.close();
                return str.toString();
            }
        } catch (Exception e) {
//            toast(e.toString());
        }
        return null;
    }

    public static boolean requestPermission(Activity ctx) {
        if (Build.VERSION.SDK_INT < 23) return true;
        if (ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ctx.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

}
