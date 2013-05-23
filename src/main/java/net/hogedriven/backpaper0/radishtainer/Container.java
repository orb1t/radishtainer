package net.hogedriven.backpaper0.radishtainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Container {

    private List<Descriptor<?>> descriptors = new ArrayList<>();

    public <T> void add(Class<T> type, Annotation qualifier, Class<? extends T> impl) {
        Descriptor<T> descriptor = new Descriptor<>(type, qualifier, impl);
        descriptors.add(descriptor);
    }

    public <T> T getInstance(Class<T> type, Annotation qualifier) {
        for (Descriptor<?> descriptor : descriptors) {
            if (descriptor.match(type, qualifier)) {
                Object instance = newInstance(descriptor);
                inject(instance);
                return (T) instance;
            }
        }
        throw new RuntimeException();
    }

    private Object newInstance(Descriptor<?> descriptor) {
        Class<?> clazz = descriptor.impl;
        List<Injector> injectors = new ArrayList<>();
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            Injector injector = new ConstructorInjector(constructor);
            if (injector.isInjectable()) {
                injectors.add(injector);
            }
        }
        if (injectors.isEmpty()) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                Injector injector = new ConstructorInjector(constructor);
                injectors.add(injector);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return injectors.get(0).inject(this, null);
    }

    public void inject(Object target) {
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> clazz = target.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            classes.add(clazz);
        }
        Collections.reverse(classes);
        List<Injector> injectors = new ArrayList<>();
        for (Class<?> clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                Injector injector = new FieldInjector(field);
                if (injector.isInjectable()) {
                    injectors.add(injector);
                }
            }
            for (Method method : clazz.getDeclaredMethods()) {
                MethodInjector injector = new MethodInjector(method);
                for (Injector other : injectors) {
                    if (other instanceof MethodInjector) {
                        if (injector.isOverrideForm((MethodInjector) other)) {
                            injectors.remove(other);
                            break;
                        }
                    }
                }
                if (injector.isInjectable()) {
                    injectors.add(injector);
                }
            }
        }
        for (Injector injector : injectors) {
            injector.inject(this, target);
        }
    }
}
