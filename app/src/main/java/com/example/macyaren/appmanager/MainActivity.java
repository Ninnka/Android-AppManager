package com.example.macyaren.appmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends Activity implements IUninstall,
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{

    ListView listView;
    MyAdapter myAdapter;
    TextView tv_size;
    TextView tv_sort;
    List<AppInfo> listUpdate;
    String[] arr_sort = {"none", "name", "size", "time"};

    int currentSort = 0;
    public static final int s_name = 1;
    public static final int s_size = 2;
    public static final int s_time = 3;

    ProgressDialog progressDialog;

    Comparator<AppInfo> currentComparator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.main);
        tv_size = (TextView) findViewById(R.id.tv_size);
        tv_sort = (TextView) findViewById(R.id.tv_sort);

        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);
        myAdapter.setUninstall(this);
        updateData();
    }


    SearchView sv;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;//展开
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                updateData();
                return true;//收起
            }
        });

        sv = (SearchView) search.getActionView();
        sv.setSubmitButtonEnabled(true);
        sv.setQueryHint("search app");
        sv.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id){
            case R.id.sort_name:
                currentSort = s_name;
                updateData_sort(currentSort);
                break;
            case R.id.sort_size:
                currentSort = s_size;
                updateData_sort(currentSort);
                break;
            case R.id.sort_time:
                currentSort = s_time;
                updateData_sort(currentSort);
                break;
            case R.id.refresh:
                updateData();
                break;
            default:
                break;
        }

        return true;
    }

    private void updateData_sort(int currentSort) {
        switch (currentSort){
            case s_name:
                currentComparator = nameComparator;
                break;
            case s_size:
                currentComparator = sizeComparator;
                break;
            case s_time:
                currentComparator = timeComparator;
                break;
            default:
                break;
        }
        //List<AppInfo> list = myAdapter.getList();
        if(currentComparator != null){
            Collections.sort(listUpdate, currentComparator);
        }
        myAdapter.setList(listUpdate);
        myAdapter.notifyDataSetChanged();
        update_top();
    }

    public void update_top(){
        tv_sort.setText("排序:" + arr_sort[currentSort]);
        tv_size.setText("应用数:" + myAdapter.getCount());
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            updateData_sort(currentSort);
            progressDialog.dismiss();
        }
    };

    public void updateData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                listUpdate = Utils.getAppList(MainActivity.this);
                //myAdapter.setList(listUpdate);
                handler.sendEmptyMessage(1);
            }
        }).start();
        showProgressDialog();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("刷新列表");
        progressDialog.setMessage("请耐心等待");
        progressDialog.show();
    }

    Comparator<AppInfo> nameComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return lhs.appName.toLowerCase().compareTo(rhs.appName.toLowerCase());
        }
    };

    Comparator<AppInfo> sizeComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if(lhs.byteSize > rhs.byteSize){
                return -1;
            }else if(lhs.byteSize == rhs.byteSize){
                return 0;
            }else{
                return 1;
            }
        }
    };

    Comparator<AppInfo> timeComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if(lhs.updTime > rhs.updTime){
                return -1;
            }else if(lhs.updTime == rhs.updTime){
                return 0;
            }else{
                return 1;
            }
        }
    };



    public static final int CODE_UNINSTALL = 0;
    @Override
    public void btnOnClick(int pos, String packageName) {
        Utils.uninstallApk(this, packageName,CODE_UNINSTALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODE_UNINSTALL){
            updateData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo app = (AppInfo) parent.getItemAtPosition(position);
        Utils.openPackage(this,app.packageName);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_LONG).show();
        myAdapter.setList(Utils.getSearchResult(myAdapter.getList(), query));
        updateData_sort(currentSort);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
