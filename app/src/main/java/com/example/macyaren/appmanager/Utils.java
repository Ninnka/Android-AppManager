package com.example.macyaren.appmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MacyaRen on 2015/10/17.
 */
public class Utils {

    public static List<AppInfo> getAppList(Context context){
        List<AppInfo> list = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for(int i = 0; i < pList.size(); i++){
            PackageInfo packageInfo = pList.get(i);
            if(isThirdPartyApp(packageInfo.applicationInfo)
                    && !packageInfo.packageName.equals(context.getPackageName())){
                AppInfo appInfo = new AppInfo();
                appInfo.packageName = packageInfo.packageName;
                appInfo.versionName = packageInfo.versionName;
                appInfo.versionCode = packageInfo.versionCode;
                appInfo.insTime = packageInfo.firstInstallTime;
                appInfo.updTime = packageInfo.lastUpdateTime;
                appInfo.appName = (String) packageInfo.applicationInfo.loadLabel(pm);
                appInfo.icon = packageInfo.applicationInfo.loadIcon(pm);
                String dir = packageInfo.applicationInfo.publicSourceDir;
                long byteSize = new File(dir).length();
                appInfo.byteSize = byteSize;// 实际大小
                appInfo.size = getSize(byteSize);// 格式化好的大小
                //添加进app列表
                list.add(appInfo);
            }
        }
        return list;
    }

    public static boolean isThirdPartyApp(ApplicationInfo applicationInfo){
        boolean ai;
        if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
            ai = false;
        }else{
            ai = true;
        }
        return ai;
    }

    public static String getTime(long millis){
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String getSize(long size){
        return new DecimalFormat("0.##").format(size * 1.0/(1024*1024));
    }

    public static void uninstallApk(Activity context, String packageName, int requestCode){
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivityForResult(intent, requestCode);
    }

    public static void openPackage(Context context, String packageName){

        Intent intent =
                context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public static List<AppInfo> getSearchResult(List<AppInfo> list, String key){
        List<AppInfo> result = new ArrayList<AppInfo>();
        for(int i = 0; i < list.size(); i++){
            AppInfo app = list.get(i);
            if(app.appName.toLowerCase().contains(key.toLowerCase())){
                result.add(app);
            }
        }
        return result;
    }
}
