package com.dkanada.openapk.activities;

import android.content.Context;

import com.dkanada.openapk.data.repository.DataRepositories;
import com.dkanada.openapk.utils.AppPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private DataRepositories mDataRepositories = DataRepositories.getInstance();

    public LiveData<DataRepositories.AppLists> getAppListsLiveData() {
        return mDataRepositories.getAppListsLiveData();
    }

    public void initOrUpdatePackageLists(Context context, boolean doUpdate, AppPreferences appPreferences)
    {
        mDataRepositories.initOrUpdatePackageLists(context,doUpdate,appPreferences);
    }
}
