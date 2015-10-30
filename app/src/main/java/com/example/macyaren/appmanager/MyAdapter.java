package com.example.macyaren.appmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by MacyaRen on 2015/10/17.
 */
public class MyAdapter extends BaseAdapter {

    private List<AppInfo> list;
    private LayoutInflater inflater;
    private IUninstall uninstall;

    public MyAdapter(Context context){
        //list = Utils.getAppList(context);
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<AppInfo> list){
        this.list = list;
    }

    public List<AppInfo> getList(){
        return list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item, null);
            holder = new ViewHolder();
            holder.logo = (ImageView) convertView.findViewById(R.id.logo);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.version = (TextView) convertView.findViewById(R.id.version);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            holder.btn = (Button) convertView.findViewById(R.id.btn);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo app = list.get(position);
        holder.logo.setImageDrawable(app.icon);
        holder.title.setText(app.appName);
        holder.version.setText("版本 : "+app.versionName );
        holder.size.setText("大小 : "+app.size + "M");
        final String pn = app.packageName;
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(),"you click uninstall", Toast.LENGTH_SHORT).show();
                uninstall.btnOnClick(position, pn);
            }
        });
        return convertView;
    }

    public void setUninstall(IUninstall uninstall){
        this.uninstall = uninstall;
    }

    public class ViewHolder{
        ImageView logo;
        TextView title;
        TextView version;
        TextView size;
        Button btn;
    }
}
