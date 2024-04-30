package me.justahuman.slimefun_server_essentials.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtils {
    public static <T> T getField(Object object, String fieldName, T defaultValue) {
        try {
            final Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return (T) field.get(object);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static <R> R callMethod(Object object, String methodName, R defaultValue, Object... args) {
        try {
            final Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
            final Method method = object.getClass().getDeclaredMethod(methodName, paramTypes);
            return (R) method.invoke(object, args);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }
}
