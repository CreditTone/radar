package gz.radar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RadarProxy {

    public static class RadarConstructorMethod {
        public String accessType;
        public int paramsNum;
        public String[] paramsClasses;
        public int throwsNum;
        public String[] throwsExceptions;
        public boolean isLocal;
        public boolean isOverWrite;
        public String describe;

        public RadarConstructorMethod(int modifier, String describe) {
            this.describe = describe;
            this.accessType = makeAccessType(modifier);
            String[] params = getMethodParams(describe);
            this.paramsNum = params.length;
            this.paramsClasses = params;
            String[] throwsExceptions = getThrowsExceptions(describe);
            this.throwsNum = throwsExceptions.length;
            this.throwsExceptions = throwsExceptions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RadarConstructorMethod that = (RadarConstructorMethod) o;

            if (paramsNum != that.paramsNum) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(paramsClasses, that.paramsClasses);
        }

        @Override
        public int hashCode() {
            int result = paramsNum;
            result = 31 * result + Arrays.hashCode(paramsClasses);
            return result;
        }
    }

    public static class RadarMethod extends RadarConstructorMethod {
        public String methodName;
        public String returnClass;
        public boolean isNative;
        public boolean isStatic;
        public boolean isFinally;

        public RadarMethod(int modifier, String describe) {
            super(modifier, describe);
            this.isNative = java.lang.reflect.Modifier.isNative(modifier);
            this.isStatic = java.lang.reflect.Modifier.isStatic(modifier);
            this.isFinally = java.lang.reflect.Modifier.isFinal(modifier);
            this.methodName = getMethodName(describe);
            this.returnClass = getType(describe);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            RadarMethod that = (RadarMethod) o;

            return methodName != null ? methodName.equals(that.methodName) : that.methodName == null;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
            return result;
        }
    }

    public static class RadarField {
        public String accessType;
        public String fieldClass;
        public String fieldName;
        public boolean isLocal;
        public boolean isStatic;
        public boolean isFinally;
        public String describe;

        public RadarField(java.lang.reflect.Field field) {
            this.describe = field.toString();
            this.accessType = makeAccessType(field.getModifiers());
            this.isStatic = java.lang.reflect.Modifier.isStatic(field.getModifiers());
            this.isFinally =java.lang.reflect.Modifier.isFinal(field.getModifiers());
            this.fieldName = field.getName();
            this.fieldClass = getType(describe);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RadarField that = (RadarField) o;

            if (fieldClass != null ? !fieldClass.equals(that.fieldClass) : that.fieldClass != null)
                return false;
            return fieldName != null ? fieldName.equals(that.fieldName) : that.fieldName == null;
        }

        @Override
        public int hashCode() {
            int result = fieldClass != null ? fieldClass.hashCode() : 0;
            result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
            return result;
        }

    }

    public static class RadarClassResult {
        public String superClassName;
        public String className;
        public RadarField[] fields;
        public RadarConstructorMethod[] constructorMethods;
        public RadarMethod[] methods;

        private void setFields(Collection<RadarField> fields) {
            this.fields = new RadarField[fields.size()];
            Iterator<RadarField> fiter =  fields.iterator();
            int i = 0;
            while (fiter.hasNext()) {
                this.fields[i] = fiter.next();
                i++;
            }
        }

        private void setRadarConstructorMethods(Collection<RadarConstructorMethod> radarConstructorMethods) {
            this.constructorMethods = new RadarConstructorMethod[radarConstructorMethods.size()];
            Iterator<RadarConstructorMethod> fiter =  radarConstructorMethods.iterator();
            int i = 0;
            while (fiter.hasNext()) {
                this.constructorMethods[i] = fiter.next();
                i++;
            }
        }

        private void setRadarMethods(Collection<RadarMethod> radarMethods) {
            this.methods = new RadarMethod[radarMethods.size()];
            Iterator<RadarMethod> fiter = radarMethods.iterator();
            int i = 0;
            while (fiter.hasNext()) {
                this.methods[i] = fiter.next();
                i++;
            }
        }
    }

    
    public static RadarClassResult discoverClass(String className) {
        try {
            Class<?> clz = Class.forName(className);
            RadarClassResult result = new RadarClassResult();
            result.className = className;
            result.superClassName = clz.getSuperclass().getName();
            Set<RadarField> radarFields = new HashSet<>();
            java.lang.reflect.Field[] declaredFields = clz.getDeclaredFields();
            if (declaredFields != null){
                for (int i = 0; i < declaredFields.length; i++) {
                    RadarField radarField = new RadarField(declaredFields[i]);
                    radarField.isLocal = true;
                    radarFields.add(radarField);
                }
            }
            java.lang.reflect.Field[] fields = clz.getFields();
            if (fields != null){
                for (int i = 0; i < fields.length; i++) {
                    RadarField radarField = new RadarField(fields[i]);
                    radarField.isLocal = false;
                    radarFields.add(radarField);
                }
            }
            result.setFields(radarFields);
            //构造方法
            Set<RadarConstructorMethod> radarConstructorMethods = new HashSet<RadarConstructorMethod>();
            Constructor<?>[] constructors = clz.getDeclaredConstructors();
            if (constructors != null){
                for (int i = 0; i < constructors.length; i++) {
                    RadarConstructorMethod raderConstructorMethod = new RadarConstructorMethod(constructors[i].getModifiers(), constructors[i].toString());
                    raderConstructorMethod.isLocal = true;
                    radarConstructorMethods.add(raderConstructorMethod);
                }
            }
            constructors = clz.getConstructors();
            if (constructors != null){
                for (int i = 0; i < constructors.length; i++) {
                    RadarConstructorMethod raderConstructorMethod = new RadarConstructorMethod(constructors[i].getModifiers(), constructors[i].toString());
                    boolean needToSkip = false;
                    for (RadarConstructorMethod raderConstructorMethodAdded : radarConstructorMethods) {
                        if (raderConstructorMethodAdded.isLocal && raderConstructorMethodAdded.equals(raderConstructorMethod)) {
                            raderConstructorMethod.isOverWrite = true;
                            needToSkip = true;
                            break;
                        }
                    }
                    if (needToSkip) {
                        continue;
                    }
                    radarConstructorMethods.add(raderConstructorMethod);
                }
            }
            result.setRadarConstructorMethods(radarConstructorMethods);
            //方法
            Set<RadarMethod> radarMethods = new HashSet<RadarMethod>();
            Method[] methods = clz.getDeclaredMethods();
            if (methods != null){
                for (int i = 0; i < methods.length; i++) {
                    RadarMethod radarMethod = new RadarMethod(methods[i].getModifiers(), methods[i].toString());
                    radarMethod.isLocal = true;
                    radarMethods.add(radarMethod);
                }
            }
            methods = clz.getMethods();
            if (methods != null){
                for (int i = 0; i < methods.length; i++) {
                    RadarMethod radarMethod = new RadarMethod(methods[i].getModifiers(), methods[i].toString());
                    boolean needToSkip = false;
                    for (RadarMethod radarMethodAdded : radarMethods) {
                        if (radarMethodAdded.isLocal && !radarMethodAdded.isNative && radarMethodAdded.equals(radarMethod)) {
                            radarMethodAdded.isOverWrite = true;
                            needToSkip = true;
                            break;
                        }
                    }
                    if (needToSkip) {
                        continue;
                    }
                    radarMethods.add(radarMethod);
                }
            }
            result.setRadarMethods(radarMethods);
            return result;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String makeAccessType(int modifiers) {
        if (java.lang.reflect.Modifier.isPublic(modifiers)) {
            return "public";
        }else if (java.lang.reflect.Modifier.isPrivate(modifiers)) {
            return "private";
        }else if (java.lang.reflect.Modifier.isProtected(modifiers)) {
            return "protected";
        }
        return "";
    }

    private static String getType(String describe) {
        String[] splits = describe.split(" ");
        int skipTimes = -1;
        for (int i = 0; i < splits.length ; i++) {
            String item = splits[i];
            skipTimes ++;
            if ("public".equals(item) || "protected".equals(item) || "private".equals(item)) {
                continue;
            }else if ("static".equals(item)) {
                continue;
            }else if ("final".equals(item)) {
                continue;
            }else if ("native".equals(item)) {
                continue;
            }
            if (item.contains(".") || item.equals("int") || item.equals("float") || item.equals("boolean") || item.equals("double") || item.equals("long") || item.equals("void") || item.equals("short") || item.equals("char")) {
                return item;
            }else if (item.endsWith("[]")) {
                return item;
            }
        }
        return "unkown";
    }

    private static String getMethodName(String describe) {
        Matcher matcher = Pattern.compile("([^.]+)\\(").matcher(describe);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unkown";
    }

    private static String[] getMethodParams(String describe) {
        Matcher matcher = Pattern.compile("[^.]+\\(([^\\)]*)\\)").matcher(describe);
        if (matcher.find()){
            String paramsLine = matcher.group(1).trim();
            if (paramsLine.isEmpty()) {
                return new String[0];
            }
            if (paramsLine.contains(",")){
                String[] params = paramsLine.split(",");
                for (int j = 0; j < params.length; j++){
                    params[j] = params[j].trim();
                }
                return params;
            }else{
                return new String[]{paramsLine};
            }
        }
        return new String[0];
    }

    private static String[] getThrowsExceptions(String describe) {
        Matcher matcher = Pattern.compile("\\)[\\s]*throws[\\s]+(.*)").matcher(describe);
        if (matcher.find()){
            String throwsExceptionsLine = matcher.group(1).trim();
            if (throwsExceptionsLine.contains(",")){
                String[] throwsExceptions = throwsExceptionsLine.split(",");
                for (int j = 0; j < throwsExceptions.length; j++){
                    throwsExceptions[j] = throwsExceptions[j].trim();
                }
                return throwsExceptions;
            }else{
                return new String[]{throwsExceptionsLine};
            }
        }
        return new String[0];
    }

    public static boolean exists(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }
}
