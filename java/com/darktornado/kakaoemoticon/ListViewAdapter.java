package com.darktornado.kakaoemoticon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DarkTornado on 2021-03-08.
 */

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<Emoticon> list = new ArrayList<>();

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int index) {
        return list.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        Context ctx = parent.getContext();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_item, parent, false);
        }

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);

        Emoticon item = list.get(pos);
        icon.setImageDrawable(item.icon);
        title.setText(item.name);
        subtitle.setText(item.artist);

        return view;
    }

    public void setItems(ArrayList<Emoticon> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    private int dip2px(Context ctx, int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }
}
