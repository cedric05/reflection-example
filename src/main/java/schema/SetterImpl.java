package schema;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import static schema.SConstant.*;

public class SetterImpl<Dest> {
    public ArrayList<Method> methods;
    public String[] names;

    public void intialize(Class DestClass, String name)
            throws NoSuchFieldException, SecurityException, NoSuchMethodException {
        names = name.split(separator);
        Class finalClass = DestClass;
        methods = new ArrayList<Method>();
        for (String subName : names) {
            String getterName = getGetterName(subName);
            Method getterMethod = finalClass.getMethod(getterName);
            Method method = finalClass.getMethod(getSetterName(subName), getterMethod.getReturnType());
            methods.add(method);
            finalClass = getterMethod.getReturnType();
        }
        // Collections.reverse(methods);
    }


    public void setValue(Dest destObj, Object obj) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException,
            InstantiationException {
        int count=0;
        Object innerObject=destObj;
        for(String name:names){
            Method getterMethod = innerObject.getClass().getMethod(getGetterName(name));
            Object innerObjectAttribute = getterMethod.invoke(innerObject);
            if (innerObjectAttribute == null || count == names.length-1) {
                Class type = innerObject.getClass().getMethod(getGetterName(name)).getReturnType();
                if(count == names.length-1){
                    innerObjectAttribute = obj;
                }else{
                    innerObjectAttribute = type.newInstance();
                }
                Method setterMethod = methods.get(count);
                setterMethod.invoke(innerObject, innerObjectAttribute);
            }
            innerObject = innerObjectAttribute;
            count++;
        }
    }
    
}