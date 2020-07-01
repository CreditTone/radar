package gz.radar;

import android.app.Activity;

public class ActivityInfo {
    private Activity activity;
    private String title;
    private String name;
    private boolean onTop;
    private boolean paused;
    private boolean stopped;
    private String currentFocusView = "";

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isOnTop() {
        return onTop;
    }

    public void setOnTop(boolean onTop) {
        this.onTop = onTop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public String getCurrentFocusView() {
        return currentFocusView;
    }

    public void setCurrentFocusView(String currentFocusView) {
        this.currentFocusView = currentFocusView;
    }
}
