package com.vedeng.httpclient;

import com.vedeng.comm.base.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**********************************************************
 * @文件名称：ResponseEntityToModule.java
 * @文件作者：聂中泽
 * @创建时间：2013-10-24 下午03:16:56
 * @文件描述：转换JSON格式数据为自定义数据模型
 * @修改历史：2013-10-24创建初始版本
 **********************************************************/
public class ResponseEntityToModule {
    /**
     * 根据JSON格式的字符串，通过反射把值设置到数据模型中对应的属性
     *
     * @param jsonContent
     * @param clazz
     * @return 返回Class类的实例化对象
     */
    public static Object parseJsonToModule(String jsonContent, Class<?> clazz) {
        Object moduleObj = null;
        try {
            JSONObject jsonObj = new JSONObject(jsonContent);
            moduleObj = parseJsonObjectToModule(jsonObj, clazz);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moduleObj;
    }

    /**
     * 根据JSON对象，通过反射把值设置到数据模型中对应的属性
     *
     * @param jsonObj
     * @param clazz
     * @return 返回Class类的实例化对象
     */
    public static <T> T parseJsonObjectToModule(JSONObject jsonObj, Class<T> clazz) {
        T moduleObj = null;
        try {
            moduleObj = clazz.newInstance();
            setFieldValue(moduleObj, jsonObj, clazz);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moduleObj;
    }

    /**
     * 设置属性值(递归父类和子类中的所有对象)
     *
     * @param moduleObj
     * @param jsonObj
     * @param clazz
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws JSONException
     * @throws InstantiationException
     */
    private static void setFieldValue(Object moduleObj, JSONObject jsonObj, Class<?> clazz)
            throws IllegalArgumentException, IllegalAccessException, JSONException, InstantiationException, NoSuchFieldException {
        if (clazz.getSuperclass() != null) {
            setFieldValue(moduleObj, jsonObj, clazz.getSuperclass());
        }
        Field[] fields = clazz.getDeclaredFields();
        Class<?> cls;
        String name;
        for (Field f : fields) {
            f.setAccessible(true);
            cls = f.getType();
            name = f.getName();
            if (!jsonObj.has(name) || jsonObj.isNull(name)) {
                continue;
            }
            // 如果是基本类型boolean,byte,char,short,int,long,float,double,void
            if (cls.isPrimitive() || isWrappedPrimitive(cls)) {
                setPrimitiveFieldValue(f, moduleObj, jsonObj.get(name));
            } else {
                if (cls.isAssignableFrom(String.class)) {
                    f.set(moduleObj, jsonObj.getString(name));
                } else if (cls.isAssignableFrom(ArrayList.class)) {
                    parseJsonArrayToList(f, name, moduleObj, jsonObj);
                } else if (cls.getName().equals(Map.class.getName())) {
                    parseJsonToMap(f, name, moduleObj, jsonObj);
                } else {
                    if (jsonObj.get(name) instanceof JSONObject) {
                        Object obj = parseJsonObjectToModule(jsonObj.getJSONObject(name), cls.newInstance().getClass());
                        f.set(moduleObj, obj);
                    } else {
                        throw new NoSuchFieldException("NoSuchField jsonObject matched target object");
                    }
                }
            }
        }
    }

    private static ArrayList<Object> parseJsonArrayToList(Field field, String fieldName, Object moduleObj,
                                                          JSONObject jsonObj) throws JSONException, IllegalArgumentException, IllegalAccessException {
        ArrayList<Object> objList = new ArrayList<Object>();
        Type fc = field.getGenericType();
        if (fc instanceof ParameterizedType)// 是泛型
        {
            ParameterizedType pt = (ParameterizedType) fc;
            if (pt.getActualTypeArguments()[0] instanceof Class)// 是指定类型,而非"?"
            {
                Class<?> clss = (Class<?>) pt.getActualTypeArguments()[0];

                if (jsonObj.get(fieldName) instanceof JSONArray) {
                    JSONArray array = jsonObj.getJSONArray(fieldName);
                    for (int i = 0; i < array.length(); i++) {
                        if (array.get(i) instanceof JSONObject) {
                            objList.add(parseJsonObjectToModule(array.getJSONObject(i), clss));
                        } else {
                            if (clss.isAssignableFrom(array.get(i).getClass())) {
                                objList.add(array.get(i));
                            }
                        }
                    }
                }
                field.set(moduleObj, objList);
            }
        }
        return objList;
    }

    /**
     * 设置模型中原始类型的值
     *
     * @param field
     * @param moduleObj
     * @param jsonObj
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NumberFormatException
     */
    private static void setPrimitiveFieldValue(Field field, Object moduleObj, Object jsonObj)
            throws IllegalArgumentException, IllegalAccessException {
        // 模型中属性的类型与数据源中的属性类型相同
        if (field.getType().isAssignableFrom(jsonObj.getClass())) {
            field.set(moduleObj, jsonObj);
        } else {
            field.set(moduleObj, makeTypeSafeValue(field.getType(), jsonObj.toString()));
        }
    }

    /**
     * 根据类型返回对应类型的值
     *
     * @param type
     * @param value
     * @return
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     */
    private static final Object makeTypeSafeValue(Class<?> type, String value) throws NumberFormatException,
            IllegalArgumentException {
        if (int.class == type || Integer.class == type) {
            return Integer.parseInt(value);
        } else if (long.class == type || Long.class == type) {
            return Long.parseLong(value);
        } else if (short.class == type || Short.class == type) {
            return Short.parseShort(value);
        } else if (char.class == type || Character.class == type) {
            return value.charAt(0);
        } else if (byte.class == type || Byte.class == type) {
            return Byte.valueOf(value);
        } else if (float.class == type || Float.class == type) {
            return Float.parseFloat(value);
        } else if (double.class == type || Double.class == type) {
            return Double.parseDouble(value);
        } else if (boolean.class == type || Boolean.class == type) {
            return Boolean.valueOf(value);
        } else {
            return value;
        }
    }

    /**
     * 是否为包装过的原始类型
     *
     * @param type
     * @return
     */
    private static boolean isWrappedPrimitive(Class<?> type) {
        if (type.getName().equals(Boolean.class.getName()) || type.getName().equals(Byte.class.getName())
                || type.getName().equals(Character.class.getName()) || type.getName().equals(Short.class.getName())
                || type.getName().equals(Integer.class.getName()) || type.getName().equals(Long.class.getName())
                || type.getName().equals(Float.class.getName()) || type.getName().equals(Double.class.getName())) {
            return true;
        }
        return false;
    }

    public static Map<String, Object> parseJsonToMap(Field field, String fieldName, Object moduleObj, JSONObject jsonObj)
            throws JSONException, IllegalAccessException, IllegalArgumentException {
        Map<String, Object> map = new HashMap<String, Object>();

        if (jsonObj != JSONObject.NULL) {
            String strJson = null;
            JSONObject object = null;
            if (jsonObj.get(fieldName) instanceof String) {
                strJson = jsonObj.optString(fieldName, null);
                if (Utils.isEmpty(strJson)) {
                    return map;
                } else {
                    object = new JSONObject(strJson);
                }
            } else if (jsonObj.get(fieldName) instanceof JSONObject) {
                object = jsonObj.getJSONObject(fieldName);
            }

            Iterator<String> keysItr = object.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = object.get(key);

                if (value instanceof String) {
                    value = String.valueOf(value);
                }
                map.put(key, value);
            }

            field.set(moduleObj, map);

        }
        return map;
    }

}
