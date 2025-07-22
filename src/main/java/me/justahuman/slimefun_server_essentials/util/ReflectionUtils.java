package me.justahuman.slimefun_server_essentials.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.IntFunction;

public class ReflectionUtils {
    public static <T> T[] getArrayField(Object object, String fieldName, IntFunction<T[]> generator) {
        return getArrayField(object.getClass(), object, fieldName, generator.apply(0), generator);
    }

    public static <T> T[] getArrayField(Object object, String fieldName, T[] defaultValue, IntFunction<T[]> generator) {
        return object != null
                ? getArrayField(object.getClass(), object, fieldName, defaultValue, generator)
                : defaultValue;
    }

    public static <T> T[] getArrayField(Class<?> clazz, Object object, String fieldName, T[] defaultValue, IntFunction<T[]> generator) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field.get(object) instanceof Object[] array) {
                return Arrays.stream(array)
                        .map(item -> (T) item)
                        .toArray(generator);
            } else {
                throw new IllegalArgumentException("Field " + fieldName + " is not an array");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static <T> T getField(Object object, String fieldName, T defaultValue) {
        return object != null
                ? getField(object.getClass(), object, fieldName, defaultValue)
                : defaultValue;
    }

    public static <T> T getField(Class<?> clazz, Object object, String fieldName, T defaultValue) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static <T> T getStaticField(Class<?> clazz, String fieldName, T defaultValue) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static <R> R callMethod(Object object, String methodName, R defaultValue, Class<?>[] paramTypes, Object[] args) {
        return callMethod(object.getClass(), object, methodName, defaultValue, paramTypes, args);
    }

    public static <R> R callMethod(Class<?> clazz, Object object, String methodName, R defaultValue, Class<?>[] paramTypes, Object[] args) {
        try {
            final Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return (R) method.invoke(object, args);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
