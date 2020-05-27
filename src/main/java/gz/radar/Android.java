package gz.radar;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Android {

    public static String getTopActivity(Context ctx) {
        try{
           ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
           return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    public static Activity getActivity(Application application,String activityClz) {
        try{
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("");
            
            //需要用到application.registerActivityLifecycleCallbacks，修改系统前注入到逻辑。所以必须系统源码配合修改
            //application.registerActivityLifecycleCallbacks();
        }catch (Exception e){
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

    public static View findViewById(Object viewContainer,int id) {
        if (viewContainer == null) {
            return null;
        }
        View view = null;
        if (viewContainer instanceof Activity) {
            Activity activity = (Activity) viewContainer;
            view = activity.findViewById(id);
        }else if (viewContainer instanceof View) {
            view = ((View)viewContainer).findViewById(id);
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
            return "false:Not found the View by id:"+id;
        }
        if (view instanceof TextView) {
            return ((TextView)view).getText().toString();
        }
        return "false:"+view.getClass().getName() +" can't cast to TextView.";
    }


}
