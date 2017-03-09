package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.RootUtils;

public class ClearDataAsync extends AsyncTask<Void, String, Boolean> {
  private Context context;
  private Activity activity;
  private MaterialDialog dialog;
  private AppInfo appInfo;

  public ClearDataAsync(Context context, MaterialDialog dialog, AppInfo appInfo) {
    this.context = context;
    this.activity = (Activity) context;
    this.dialog = dialog;
    this.appInfo = appInfo;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    Boolean status = false;
    if (AppUtils.checkPermissions(activity)) {
      status = RootUtils.clearDataWithRootPermission(appInfo.getAPK());
    }
    return status;
  }

  @Override
  protected void onPostExecute(Boolean status) {
    super.onPostExecute(status);
    dialog.dismiss();
    if (status) {
      DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_clear_data_success), null, null, 2).show();
    } else {
      DialogUtils.showTitleContent(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
    }
  }
}