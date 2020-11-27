package com.dkanada.openapk.data.repository;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.dkanada.openapk.models.AppItem;
import com.dkanada.openapk.utils.AppPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class DataRepositories {

    public class AppLists
    {
        public List<AppItem> appInstalledList = new ArrayList<>();
        public List<AppItem> appSystemList = new ArrayList<>();
        public List<AppItem> appDisabledList = new ArrayList<>();
        public List<AppItem> appHiddenList = new ArrayList<>();
        public List<AppItem> appFavoriteList = new ArrayList<>();
    }

    private AppLists mAppLists = new AppLists();
    private PackageManager packageManager;
    private AppPreferences appPreferences;


    private static DataRepositories instance;

    private String TAG = DataRepositories.class.toString();
    private Context mContext;

    private MutableLiveData<Boolean> mIsUpdating = new MutableLiveData<>();

    private ExecutorService mExecutorService;
    private int mPosition = 0;

    private DataRepositories()
    {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized DataRepositories getInstance()
    {
        if (instance == null) {
            instance = new DataRepositories();
        };
        return instance;
    }

    public LiveData<Boolean> getIsUpdating(){
        mIsUpdating.notify();
        return mIsUpdating;

    }
    private MutableLiveData<AppLists> mAppListsLiveData = new MutableLiveData<>();

    boolean mDoUpdate = true;
    public void initOrUpdatePackageLists(Context context, boolean doUpdate, AppPreferences appPreferences)
    {
        packageManager = context.getPackageManager();
        this.appPreferences = appPreferences;

        if (mDoUpdate || doUpdate)
        {
            mDoUpdate = false;

            new getInstalledApps().execute();
        }

    }

    class getInstalledApps extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            mAppLists.appDisabledList.clear();
            mAppLists.appSystemList.clear();
            mAppLists.appInstalledList.clear();

            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            for (PackageInfo packageInfo : packages) {
                AppItem appItem = new AppItem(packageInfo);
                if (!packageInfo.applicationInfo.enabled) {
                    appItem.disable = true;
                    mAppLists.appDisabledList.add(appItem);
                } else if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    appItem.system = true;
                    mAppLists.appSystemList.add(appItem);
                } else {
                    mAppLists.appInstalledList.add(appItem);
                }
            }

            mAppLists.appInstalledList = sortAdapter(mAppLists.appInstalledList);
            mAppLists.appSystemList = sortAdapter(mAppLists.appSystemList);
            mAppLists.appDisabledList = sortAdapter(mAppLists.appDisabledList);
            mAppLists.appHiddenList = sortAdapter(mAppLists.appHiddenList);
            mAppLists.appFavoriteList = sortAdapter(mAppLists.appFavoriteList);
            return null;
        }

        private List<AppItem> sortAdapter(List<AppItem> list) {
            Collections.sort(list, new Comparator<AppItem>() {
                @Override
                public int compare(AppItem one, AppItem two) {
                    switch (appPreferences.getSortMethod()) {
                        case "0":
                            return one.getPackageLabel().compareTo(two.getPackageLabel());
                        case "1":
                            return one.getPackageName().compareTo(two.getPackageName());
                        case "2":
                            return one.getInstall().compareTo(two.getInstall());
                        case "3":
                            return one.getUpdate().compareTo(two.getUpdate());
                        default:
                            return one.getPackageLabel().compareTo(two.getPackageLabel());
                    }
                }
            });
            return list;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            mAppListsLiveData.setValue(mAppLists);
        }
    }

    public LiveData<DataRepositories.AppLists> getAppListsLiveData() {
        return mAppListsLiveData;
    }
}
