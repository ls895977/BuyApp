package com.vedeng.comm.base.permisson;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**********************************************************
 * @文件名称：
 * @文件作者：聂中泽
 * @创建时间：2016/12/7 15:40
 * @文件描述：权限管理
 * @修改历史：2016/12/7 创建初始版本
 **********************************************************/

public class AppPermission {

    private Object context;
    private String[] permissions;
    private int requestCode;

    private RationaleListener listener;

    AppPermission(Object context) {
        if (context instanceof Activity || context instanceof Fragment) {
            this.context = context;
        } else {
            throw new IllegalArgumentException(context.getClass().getName() + " is not support.");
        }
    }

    public static AppPermission get(Activity activity) {
        return new AppPermission(activity);
    }

    public static AppPermission get(Fragment fragment) {
        return new AppPermission(fragment);
    }

    public static boolean check(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public AppPermission permissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public AppPermission requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public AppPermission rationaleListener(RationaleListener listener) {
        this.listener = listener;
        return this;
    }

    public void request() {
        if (permissions.length == 0) {
            handleResponse(new int[0]);
        } else if (context instanceof Activity || context instanceof Fragment) {
            final String[] deniedPermissions = getDeniedPermissions();
            if (deniedPermissions.length > 0) {
                String[] rationalePermissions = getShowRationalePermissions(deniedPermissions);
                if (rationalePermissions.length > 0 && listener != null) {
                    listener.showRequestPermissionRationale(requestCode, new Rationale() {
                        @Override
                        public void cancel() {
                            handleResponse(getPermissionResult());
                        }

                        @Override
                        public void resume() {
                            request(deniedPermissions);
                        }
                    });
                } else {
                    request(deniedPermissions);
                }
            } else {
                handleResponse(allPermissionGranted());
            }
        }
    }

    private int[] allPermissionGranted() {
        int[] grantResults = new int[permissions.length];
        for (int i = 0; i < grantResults.length; i++)
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        return grantResults;
    }

    private void request(String[] deniedPermissions) {
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) context, deniedPermissions, requestCode);
        } else if (context instanceof Fragment) {
            ((Fragment) context).requestPermissions(deniedPermissions, requestCode);
        }
    }

    private String[] getShowRationalePermissions(String[] deniedPermissions) {
        List<String> resultList = new ArrayList<>();
        if (context instanceof Activity) {
            for (String permission : deniedPermissions)
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission))
                    resultList.add(permission);
        } else if (context instanceof Fragment) {
            for (String permission : deniedPermissions)
                if (((Fragment) context).shouldShowRequestPermissionRationale(permission))
                    resultList.add(permission);
        } else {
            throw new IllegalArgumentException(context.getClass().getName() + " is not support.");
        }
        return resultList.toArray(new String[resultList.size()]);

    }

    private int[] getPermissionResult() {
        int[] results = new int[permissions.length];
        for (int i = 0; i < results.length; i++)
            results[i] = ContextCompat.checkSelfPermission(getContext(), permissions[i]);
        return results;
    }

    private String[] getDeniedPermissions() {
        ArrayList<String> resultList = new ArrayList<>();
        for (String permission : permissions) {
            if (!check(getContext(), permission)) {
                resultList.add(permission);
            }
        }
        return resultList.toArray(new String[resultList.size()]);
    }

    private Context getContext() {
        if (context instanceof Activity)
            return ((Activity) context).getApplicationContext();
        else if (context instanceof Fragment) {
            return ((Fragment) context).getContext();
        }
        throw new IllegalArgumentException(context.getClass().getName() + " is not support.");
    }

    private void handleResponse(int[] result) {
        if (context instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) context).onRequestPermissionsResult(requestCode, permissions, result);
            } else if (context instanceof ActivityCompat.OnRequestPermissionsResultCallback) {
                ((ActivityCompat.OnRequestPermissionsResultCallback) context).onRequestPermissionsResult(requestCode, permissions, result);
            } else {
                throw new RuntimeException(context.getClass().getName() + " should implements " + ActivityCompat.OnRequestPermissionsResultCallback.class.getName());
            }
        } else if (context instanceof Fragment) {
            ((Fragment) context).onRequestPermissionsResult(requestCode, permissions, result);
        }
    }
}
