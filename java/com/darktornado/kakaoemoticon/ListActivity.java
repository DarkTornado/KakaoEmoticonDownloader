package com.darktornado.kakaoemoticon;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ListActivity extends Activity {

    final String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);
        StrictMode.enableDefaults();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        ArrayList<Emoticon> items = parseData();
        ListView list = new ListView(this);
        ListViewAdapter adapter = new ListViewAdapter();
        adapter.setItems(items);
        list.setAdapter(adapter);
        list.setOnItemClickListener((adapterView, view, pos, id) -> {
            if (Utils.requestPermission(ListActivity.this)) download(items.get(pos));
            else toast("내장메모리 접근 권한을 허용해주세요.");
        });
        layout.addView(list);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);

        setContentView(layout);
    }

    private void download(Emoticon item) {
        try {
            new File(sdcard + "/KakaoEmoticons/" + item.name + "/").mkdirs();
            final AlertDialog dialog = showProgress();
            new Thread(() -> {
                try {
                    String data0 = Utils.getWebText("https://e.kakao.com/api/v1/items/t/" + item.url);
                    JSONArray data = new JSONObject(data0).getJSONObject("result").getJSONArray("thumbnailUrls");
                    int suc = 0, fal = 0;
                    for (int n = 0; n < data.length(); n++) {
                        boolean succeed = copyFromWeb(data.getString(n), sdcard + "/KakaoEmoticons/" + item.name + "/" + n + ".png");
                        if (succeed) suc++;
                        else fal++;
                    }
                    toast("다운로드 완료\n" + data.length() + "개 중 " + suc + "개 성공, " + fal + "개 실패");
                    runOnUiThread(dialog::dismiss);
                } catch (Exception e) {
                    toast("다운로드 실패\n" + e.toString());
                }
            }).start();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private ArrayList<Emoticon> parseData() {
        try {
            String data0 = getIntent().getStringExtra("data");
            JSONArray data = new JSONObject(data0).getJSONObject("result").getJSONArray("content");
            ArrayList<Emoticon> items = new ArrayList<>();
            for (int n = 0; n < data.length(); n++) {
                JSONObject tmp = data.getJSONObject(n);
                String url = data.getJSONObject(n).getString("titleImageUrl");
                Drawable icon = getImageDrawable(url);
                Emoticon item = new Emoticon(tmp.getString("title"), tmp.getString("artist"), tmp.getString("titleUrl"), icon);
                items.add(item);
            }
            return items;
        } catch (Exception e) {
            toast("이모티콘 정보 분석 실패\n" + e.toString());
        }
        return null;
    }

    public BitmapDrawable getImageDrawable(String link) throws Exception {
        URL url = new URL(link);
        URLConnection con = url.openConnection();
        con.setUseCaches(false);
        con.setConnectTimeout(5000);
        BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
        Bitmap bitmap = BitmapFactory.decodeStream(bis);
        BitmapDrawable image = new BitmapDrawable(Bitmap.createScaledBitmap(bitmap, dip2px(50), dip2px(50), false));
        bis.close();
        return image;
    }

    private boolean copyFromWeb(String url, String path) {
        try {
            URLConnection con = new URL(url).openConnection();
            if (con != null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                File file = new File(path);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int buf;
                while ((buf = bis.read()) != -1) {
                    bos.write(buf);
                }
                bis.close();
                bos.close();
                fos.close();
            }
            return true;
        } catch (Exception e) {
//            toast("Download Failed.\n" + e.toString());
            return false;
        }
    }

    private AlertDialog showProgress() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        ProgressBar bar = new ProgressBar(this);
        layout.addView(bar);
        TextView txt = new TextView(this);
        txt.setText("다운로드 중...");
        txt.setTextSize(14);
        txt.setGravity(Gravity.CENTER);
        layout.addView(txt);
        int pad = dip2px(5);
        layout.setPadding(pad, pad, pad, pad);
        dialog.setView(layout);
        dialog.show();
        dialog.getWindow().setLayout(dip2px(170), -2);
        return dialog;
    }

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ListActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * getResources().getDisplayMetrics().density);
    }
}
