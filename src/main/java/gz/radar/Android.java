package gz.radar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

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
