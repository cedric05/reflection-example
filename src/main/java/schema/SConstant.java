package schema;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class SConstant {
    public final static String  separator = Pattern.quote(".");
    
    public static String getSetterName(String name) {
        return "set" + StringUtils.capitalise(name);
    }

    public static String getGetterName(String name) {
        return "get" + StringUtils.capitalize(name);
    }

    public Boolean isGetter(String s){
        return s.startsWith("get");
    }
}