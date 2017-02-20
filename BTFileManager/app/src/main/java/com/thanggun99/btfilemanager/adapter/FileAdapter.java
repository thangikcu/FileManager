package com.thanggun99.btfilemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanggun99.btfilemanager.R;
import com.thanggun99.btfilemanager.model.Item;

import java.util.List;

/**
 * Created by Thanggun99 on 08/10/2016.
 */
public class FileAdapter extends BaseAdapter {
    private Context c;
    private int id;
    private List<Item>items;

    public FileAdapter(Context context, int textViewResourceId, List<Item> objects) {
        c = context;
        id = textViewResourceId;
        items = objects;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            v = inflater.inflate(R.layout.file_view, parent, false);
            holder = new ViewHolder();
            holder.iv_ic_file = (ImageView) v.findViewById(R.id.fd_Icon1);
            holder.txtName = (TextView) v.findViewById(R.id.TextView01);
            holder.txtData = (TextView) v.findViewById(R.id.TextView02);
            holder.txtDate = (TextView) v.findViewById(R.id.TextViewDate);
            v.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        final Item o = items.get(position);
        if (o != null) {
            holder.iv_ic_file.setImageResource(o.getImage());
            holder.txtName.setText(o.getName());
            holder.txtData.setText(o.getData());
            holder.txtDate.setText(o.getDate());
        }
        return v;
    }

    private class ViewHolder{
        ImageView iv_ic_file;
        TextView txtName, txtData, txtDate;
    }
}
