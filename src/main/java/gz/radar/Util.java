package gz.radar;

import android.os.Bundle;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public class Util {

    public static String toJSONString(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof Bundle){
            Bundle bundle = (Bundle) obj;
            JSONObject jsonObject = new JSONObject();
            for (String key : bundle.keySet()) {
                try {
                    //bundle.containsKey()
                    jsonObject.put(key, bundle.get(key));
                } catch (JSONException e) {
                    //e.printStackTrace();
                    try {
                        jsonObject.put(key, "Not Found");
                    } catch (JSONException ex) {
                    }
                }
            }
            //android.os.Parcel.obtain().marshall()
            Message.obtain().peekData();
            return jsonObject.toString();
        }
        return "{}";
    }
}
