package net.hogedriven.backpaper0.radishtainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import javax.inject.Inject;

public class MethodInjector extends Injector {

    private final Method method;

    public MethodInjector(Method method) {
        this.method = method;
    }

    @Override
    public boolean isInjectable() {
        return method.isAnnotationPresent(Inject.class);
    }

    @Override
    public Object inject(Container container, Object target) {
        Class<?>[] types = method.getParameterTypes();
        Type[] genericTypes = method.getGenericParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] dependencies = getDependencies(container, types, genericTypes, annotations);
        if (Modifier.isPublic(method.getModifiers()) == false
                && method.isAccessible() == false) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(target, dependencies);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
