package com.darktornado.kakaoemoticon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

public class MainActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "앱 정보").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 1, 0, "라이선스 정보").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showDialog("앱 정보", "카카오톡 이모티콘 검색 및 미리보기 이미지들을 다운로드 받을 수 있는 앱으로, 이 앱을 사용함으로서 발생하는 모든 일에 대하여 이 앱의 개발자는 책임을 지지 않아요.");
                break;
            case 1:
                Intent intent = new Intent(this, LicenseActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        TextView txt1 = new TextView(this);
        txt1.setText("검색어 : ");
        txt1.setTextSize(18);
        layout.addView(txt1);
        final TextView txt2 = new EditText(this);
        txt2.setHint("검색어 입력...");
        layout.addView(txt2);

        Button search = new Button(this);
        search.setText("검색");
        search.setOnClickListener(view -> {
            final String input = txt2.getText().toString();
            if (input.equals("")) toast("입력된 내용이 없어요.");
            else new Thread(() -> searchEmoticon(input)).start();
        });
        layout.addView(search);

        TextView txt = new TextView(this);
        txt.setText("\n이 앱은 카카오톡 및 카카오와 관련이 없어요.\n이 앱과 개발자는 이 앱을 사용함으로서 발생하는 모든 일에 대하여 책임을 지지 않아요.");
        txt.setTextSize(18);
        layout.addView(txt);

        TextView maker = new TextView(this);
        maker.setText("\n© 2021 Dark Tornado, All rights reserved.\n");
        maker.setTextSize(13);
        maker.setGravity(Gravity.CENTER);
        layout.addView(maker);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);

        setContentView(layout);
    }

    private void searchEmoticon(final String input) {
        try {
            String data0 = Utils.getWebText("https://e.kakao.com/api/v1/search?query=" + URLEncoder.encode(input, "UTF-8") + "&page=0&size=20");
            JSONArray data = new JSONObject(data0).getJSONObject("result").getJSONArray("content");

            int count = data.length();
            if (count == 0) {
                toast("검색 결과가 없어요.");
            } else {
                Intent intent = new Intent(this, ListActivity.class);
                intent.putExtra("data", data0);
                startActivity(intent);
                toast("검색결과가 " + count + "개 있어요.");
            }
        } catch (Exception e) {
            toast("이모티콘 검색 실패 실패\n" + e.toString());
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
        runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show());
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * getResources().getDisplayMetrics().density);
    }

}
