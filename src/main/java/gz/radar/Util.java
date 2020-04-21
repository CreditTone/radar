package gz.radar;

import java.lang.reflect.Method;

public class Util {

    public static String toJSONString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            Class<?> fastJson = Class.forName("com.alibaba.fastjson.JSON");
            Method method = fastJson.getMethod("toJSONString", Object.class);
            Object ret = method.invoke(null, obj);
            if (ret != null) {
                return ret.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }
}
