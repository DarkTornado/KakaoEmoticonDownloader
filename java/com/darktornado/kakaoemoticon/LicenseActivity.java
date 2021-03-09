package com.darktornado.kakaoemoticon;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LicenseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        loadLicenseInfo(layout, "카카오톡 이모티콘 추출기", "license", "GPL 3.0", "Dark Tornado", true);
        loadLicenseInfo(layout, "Material Design", "license_apache", "Apache License 2.0", "Google", false);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
    }

    private void loadLicenseInfo(LinearLayout layout, String name, String fileName, final String license, String dev, boolean tf) {
        int pad = dip2px(10);
        TextView title = new TextView(this);
        if (tf) title.setText(Html.fromHtml("<b>" + name + "<b>"));
        else title.setText(Html.fromHtml("<br><b>" + name + "<b>"));
        title.setTextSize(24);
        title.setTextColor(Color.BLACK);
        title.setPadding(pad, 0, pad, dip2px(1));
        layout.addView(title);
        TextView subtitle = new TextView(this);
        subtitle.setText("  by " + dev + ", " + license);
        subtitle.setTextSize(20);
        subtitle.setTextColor(Color.BLACK);
        subtitle.setPadding(pad, 0, pad, pad);
        layout.addView(subtitle);

        final String value = loadLicense(fileName);
        TextView txt = new TextView(this);
        if (value.length() > 1500) {
            txt.setText(Html.fromHtml(value.substring(0, 1500).replace("\n", "<br>") + "...<font color='#757575'><b>[Show All]</b></font>"));
            txt.setOnClickListener(v -> showDialog(license, value));
        } else {
            txt.setText(value);
        }
        txt.setTextSize(17);
        txt.setTextColor(Color.BLACK);
        txt.setPadding(pad, pad, pad, pad);
        txt.setBackgroundColor(Color.argb(50, 0, 0, 0));
        layout.addView(txt);
    }

    private String loadLicense(String name) {
        try {
            InputStreamReader isr = new InputStreamReader(getAssets().open(name + ".txt"));
            BufferedReader br = new BufferedReader(isr);
            StringBuilder str = new StringBuilder(br.readLine());
            String line = "";
            while ((line = br.readLine()) != null) {
                str.append("\n").append(line);
            }
            isr.close();
            br.close();
            return str.toString();
        } catch (Exception e) {
            toast(e.toString());
            return "라이선스 정보 불러오기 실패";
        }
    }

    public void showDialog(String title, String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(title);
            dialog.setMessage(msg);
            dialog.setNegativeButton("닫기", null);
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void toast(final String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

}

