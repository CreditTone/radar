package gz.radar;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Android {

    public static Application getApplication() throws Exception {
        Class<?> activityThread = Class.forName("android.app.ActivityThread");
        Method currentApplication = activityThread.getDeclaredMethod("currentApplication");
        Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
        Object current = currentActivityThread.invoke((Object)null);
        Object app = currentApplication.invoke(current);
        return (Application)app;
    }

    public static String getVersionName() throws Exception {
        Application context = getApplication();
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public static ActivityInfo[] getActivityInfos() throws Exception {
        ActivityInfo[] activityInfos = null;
        List<ActivityInfo> results = new ArrayList<>();
        ActivityInfo topActivity = null;
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
        for (Object activityClientRecord : activities.values()) {
            ActivityInfo activityInfo = new ActivityInfo();
            Class activityClietnRecordClass = activityClientRecord.getClass();
            Field pausedField = activityClietnRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            Field activityField = activityClietnRecordClass.getDeclaredField("activity");
            activityField.setAccessible(true);
            Field stoppedField = activityClietnRecordClass.getDeclaredField("stopped");
            stoppedField.setAccessible(true);
            Activity activity = (Activity) activityField.get(activityClientRecord);
            activityInfo.setActivity(activity);
            activityInfo.setName(activity.getClass().getName());
            activityInfo.setPaused(!pausedField.getBoolean(activityClientRecord));
            activityInfo.setOnTop(activityInfo.isPaused());
            activityInfo.setTitle(activity.getTitle().toString());
            activityInfo.setStopped(stoppedField.getBoolean(activityClientRecord));
            if (activityInfo.isOnTop()) {
                View currentFocusView = activity.getCurrentFocus();
                if (currentFocusView != null) {
                    activityInfo.setCurrentFocusView(currentFocusView.getClass().getName());
                }
                topActivity = activityInfo;
            }else{
                results.add(activityInfo);
            }
        }
        int length = results.size() + (topActivity != null? 1 : 0);
        activityInfos = new ActivityInfo[length];
        if (length == 0) {
            return activityInfos;
        }
        int index = 0;
        if (topActivity != null) {
            activityInfos[index] = topActivity;
            index ++;
        }
        for (ActivityInfo item : results) {
            activityInfos[index] = item;
            index ++;
        }
        return activityInfos;
    }

    public static Activity getActivity(Application application, String activityClz) {
        try {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("");

            //需要用到application.registerActivityLifecycleCallbacks，修改系统前注入到逻辑。所以必须系统源码配合修改
            //application.registerActivityLifecycleCallbacks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<View> getChildViews(Activity activity) {
        View view = activity.getWindow().getDecorView();
        return getChildViews(view);
    }

    public static List<View> getChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                allchildren.addAll(getChildViews(viewchild));
            }
        }
        return allchildren;
    }

    public static View findViewById(Object viewContainer, int id) {
        if (viewContainer == null) {
            return null;
        }
        View view = null;
        if (viewContainer instanceof Activity) {
            Activity activity = (Activity) viewContainer;
            view = activity.findViewById(id);
        } else if (viewContainer instanceof View) {
            view = ((View) viewContainer).findViewById(id);
        }
        return view;
    }

    public static boolean click(Object viewContainer, int btnId) {
        View view = findViewById(viewContainer, btnId);
        if (view == null) {
            return false;
        }
        if (view.isClickable() && view.isFocusable()) {
            view.performClick();
            return true;
        }
        return false;
    }

    public static String getViewText(Object viewContainer, int id) {
        View view = findViewById(viewContainer, id);
        if (view == null) {
            return "false:Not found the View by id:" + id;
        }
        if (view instanceof TextView) {
            return ((TextView) view).getText().toString();
        }
        return "false:" + view.getClass().getName() + " can't cast to TextView.";
    }


}
