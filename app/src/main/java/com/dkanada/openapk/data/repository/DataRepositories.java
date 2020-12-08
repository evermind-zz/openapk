package com.dkanada.openapk.data.repository;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.dkanada.openapk.models.AppItem;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.GZIPByteCompressor;
import com.dkanada.openapk.utils.MD5;
import com.twitter.serial.stream.Serial;
import com.twitter.serial.stream.bytebuffer.ByteBufferSerial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class DataRepositories {


    private AppLists mAppLists = new AppLists();
    private PackageManager packageManager;
    private AppPreferences appPreferences;

    // serialized data of a former run of OpenAPK will be tried to be loaded
    private boolean mHasTriedLoadingCachePkgData = false;

    private static DataRepositories mInstance;

    private String TAG = DataRepositories.class.toString();
    private Context mContext;

    private final String mPkgDataCacheFile = "pkgCacheData";
    private byte[] mMd5OfPkgDataCacheFile = null;

    private MutableLiveData<Boolean> mIsUpdating = new MutableLiveData<>();

    private ExecutorService mExecutorService;
    private int mPosition = 0;

    private DataRepositories()
    {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized DataRepositories getInstance()
    {
        if (mInstance == null) {
            mInstance = new DataRepositories();
        };
        return mInstance;
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
        mContext = context;
        this.appPreferences = appPreferences;

        if (mDoUpdate || doUpdate)
        {
            mDoUpdate = false;

            new getInstalledApps().execute();
        }
    }

    public LiveData<AppLists> getAppListsLiveData() {
        return mAppListsLiveData;
    }

    class getInstalledApps extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            mAppLists.appDisabledList.clear();
            mAppLists.appSystemList.clear();
            mAppLists.appInstalledList.clear();

            tryLoadingAppDataFromCacheFile();
            retrievePkgInfoAndPopulateAppLists();
            sortAppLists();
            serializeAppDataToCacheFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAppListsLiveData.setValue(mAppLists);
        }

        private void tryLoadingAppDataFromCacheFile()
        {
            if (!mHasTriedLoadingCachePkgData) {
                if (deserializeDataFromFile())
                    mAppListsLiveData.postValue(mAppLists);

                mHasTriedLoadingCachePkgData = true;
                // in case we deserialize (read from cache) we recreate the AppLists
                // to make sure we do not immediately delete/clear the cache data to
                // fill it with the real data (we need this data to show something on the UI:))
                mAppLists = new AppLists();
            }
        }

        private void sortAppLists() {
            mAppLists.appInstalledList = sortAdapter(mAppLists.appInstalledList);
            mAppLists.appSystemList = sortAdapter(mAppLists.appSystemList);
            mAppLists.appDisabledList = sortAdapter(mAppLists.appDisabledList);
            mAppLists.appHiddenList = sortAdapter(mAppLists.appHiddenList);
            mAppLists.appFavoriteList = sortAdapter(mAppLists.appFavoriteList);
        }

        private void retrievePkgInfoAndPopulateAppLists() {
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
        }

        private byte[] readContentIntoByteArray(File file)
        {
            FileInputStream fis = null;
            byte[] binaryFile = new byte[(int) file.length()];
            try
            {
                //convert file into byte array
                fis = new FileInputStream(file);
                fis.read(binaryFile);
                fis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
            return binaryFile;
        }

        private boolean deserializeDataFromFile()
        {
            try {
                Serial serial = new ByteBufferSerial();
                File f = new File(mContext.getFilesDir(), mPkgDataCacheFile);
                if (!f.exists())
                    return false;

                byte[] compressed = readContentIntoByteArray(f);

                byte[] md5Bytes = MD5.bufferToMD5(compressed);
                mMd5OfPkgDataCacheFile = md5Bytes;

                //DBG String result = MD5.convertHashToString(md5Bytes);
                //DBG Log.d("MD5-deserial",  result);

                byte[] serializedData = GZIPByteCompressor.decompress(compressed);
                mAppLists = serial.fromByteArray(serializedData, AppLists.SERIALIZER);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private void serializeAppDataToCacheFile()
        {
            try {
                Serial serial = new ByteBufferSerial();
                byte[] serializedData = serial.toByteArray(mAppLists, AppLists.SERIALIZER);

                byte[] compressed = GZIPByteCompressor.compress(serializedData);
                byte[] md5Bytes = MD5.bufferToMD5(compressed);

                //DBG String result = MD5.convertHashToString(md5Bytes);
                //DBG Log.d("MD5-serial",  result);

                if (mMd5OfPkgDataCacheFile != null && Arrays.equals(mMd5OfPkgDataCacheFile, md5Bytes))
                {
                    // do not write cache file as existing is identical
                    return;
                }

                File f = new File(mContext.getFilesDir(), mPkgDataCacheFile);
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(compressed);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }
}
