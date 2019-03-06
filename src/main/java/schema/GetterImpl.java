package schema;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Pattern;
import static schema.SConstant.separator;

import org.apache.commons.lang.StringUtils;

public class GetterImpl<Source> {
    public ArrayList<Method> methods;
    public Class ReturnType;

    @SuppressWarnings("rawtypes")
    public void initialize(Class SourceClass, String name) throws NoSuchMethodException, SecurityException {
        String[] names= name.split(separator);
        ArrayList<Method> methodlist = new ArrayList<Method>();
        for(String subName:names){
            Method m = SourceClass.getMethod(getGetterName(subName));
            SourceClass = m.getReturnType();
            methodlist.add(m);
        }
        methods = methodlist;
        ReturnType = methodlist.get(methodlist.size()-1).getReturnType();
        return;

    }

    public Object getReturnValue(Source source) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Object obj = source;
        for(Method subMethod:methods){
            obj = subMethod.invoke(obj);
        }
        return obj;
    }

    public static String getGetterName(String name){
        return "get"+ StringUtils.capitalise(name);
    }
}
