package jp.urgm.radishtainer;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import javax.inject.Provider;

public abstract class Binding {

    public abstract Object getInstance(Container container);

    public abstract ClassInfo getClassInfo();

    public static Binding newClassBinding(Class<?> impl, Scope scope) {
        return new ClassBinding(impl, scope);
    }

    public static Binding newInstanceBinding(Object instance) {
        return new InstanceBinding(instance);
    }

    public static <T> Binding newProviderBinding(Provider<T> provider) {
        return new ProviderBinding(provider);
    }

    private static class ClassBinding extends Binding {

        private final Class<?> impl;
        private final Scope scope;

        public ClassBinding(Class<?> impl, Scope scope) {
            this.impl = impl;
            this.scope = scope;
        }

        @Override
        public Object getInstance(Container container) {
            return scope.getInstance(container, impl);
        }

        @Override
        public ClassInfo getClassInfo() {
            return new ClassInfo(impl);
        }
    }

    private static class InstanceBinding extends Binding {

        private final Object instance;

        public InstanceBinding(Object instance) {
            this.instance = instance;
        }

        @Override
        public Object getInstance(Container container) {
            container.inject(instance);
            return instance;
        }

        @Override
        public ClassInfo getClassInfo() {
            return new ClassInfo(instance.getClass());
        }
    }

    private static class ProviderBinding extends Binding {

        private final Provider<?> provider;

        public ProviderBinding(Provider<?> provider) {
            this.provider = provider;
        }

        @Override
        public Object getInstance(Container container) {
            return provider.get();
        }

        @Override
        public ClassInfo getClassInfo() {
            return Arrays
                    .stream(provider.getClass().getGenericInterfaces())
                    .filter(type -> type instanceof ParameterizedType)
                    .map(type -> (ParameterizedType) type)
                    .filter(pt -> pt.getRawType() == Provider.class)
                    .map(pt -> new ClassInfo((Class<?>) pt
                            .getActualTypeArguments()[0])).findFirst()
                    .orElseThrow(IllegalStateException::new);
        }
    }
}
