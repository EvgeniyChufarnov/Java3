package lesson7;

import lesson7.annotations.AfterSuite;
import lesson7.annotations.BeforeSuite;
import lesson7.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class TestExecutor {
    private static final int LOWEST_PRIORITY = 1;
    private static final int HIGHEST_PRIORITY = 10;
    private static final int BEFORE_ALL_PRIORITY = 0;
    private static final int AFTER_ALL_PRIORITY = 11;

    private static final String BEFORE_EXCEPTION_PREFIX = "More than 1 @BeforeSuite annotation class ";
    private static final String AFTER_EXCEPTION_PREFIX = "More than 1 @AfterSuite annotation class ";
    private static final String OUT_OF_BOUNDS_EXCEPTION_PREFIX = "Priority is out of bound for ";

    public static void start(String className) {
        try {
            start(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static <T> void start(Class<T> aClass) {
        Map<Integer, List<Method>> orderedMethods = getSortedMethods(aClass);
        executeMethods(orderedMethods, aClass);
    }

    private static <T> Map<Integer, List<Method>> getSortedMethods(Class<T> aClass) {
        Method[] methods = aClass.getDeclaredMethods();
        Map<Integer, List<Method>> orderedMethods = new TreeMap<>();

        for (Method method : methods) {
            setAccessToPublic(method);

            if (method.isAnnotationPresent(BeforeSuite.class)) {
                checkPresenceAndAdd(orderedMethods, method, BEFORE_ALL_PRIORITY,
                        BEFORE_EXCEPTION_PREFIX + aClass.getName());

            } else if (method.isAnnotationPresent(AfterSuite.class)) {
                checkPresenceAndAdd(orderedMethods, method, AFTER_ALL_PRIORITY,
                        AFTER_EXCEPTION_PREFIX + aClass.getName());

            } else if (method.isAnnotationPresent(Test.class)) {
                checkLimitAndAdd(orderedMethods, method);
            }
        }

        return orderedMethods;
    }

    private static void setAccessToPublic(Method method) {
        if (!method.isAccessible())
            method.setAccessible(true);
    }

    private static void checkPresenceAndAdd(Map<Integer, List<Method>> orderedMethods, Method method, int priority, String exceptionMessage) {
        if (orderedMethods.containsKey(priority)) {
            throw new RuntimeException(exceptionMessage);
        } else {
            addMethodToOrderedMap(orderedMethods, method, priority);
        }
    }

    private static void checkLimitAndAdd(Map<Integer, List<Method>> orderedMethods, Method method) {
        Test testAnnotation = method.getAnnotation(Test.class);
        int priority = testAnnotation.value();

        if (priority < LOWEST_PRIORITY || priority > HIGHEST_PRIORITY) {
            throw new RuntimeException(OUT_OF_BOUNDS_EXCEPTION_PREFIX + method.getName());
        }

        addMethodToOrderedMap(orderedMethods, method, priority);
    }

    private static void addMethodToOrderedMap(Map<Integer, List<Method>> orderedMethods, Method method, int priority) {
        if (orderedMethods.containsKey(priority)) {
            orderedMethods.get(priority).add(method);
        } else {
            List<Method> list = new ArrayList<>();
            list.add(method);
            orderedMethods.put(priority, list);
        }
    }

    private static <T> void executeMethods(Map<Integer, List<Method>> orderedMethods, Class<T> aClass) {
        try {
            T instance = aClass.newInstance();

            orderedMethods.forEach((key, methods) -> executeListOfMethods(methods, instance));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static <T> void executeListOfMethods(List<Method> methods, T instance) {
        try {
            for (Method method : methods) {
                method.invoke(instance);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
