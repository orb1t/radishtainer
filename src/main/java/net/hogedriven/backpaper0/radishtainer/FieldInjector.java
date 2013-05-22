package net.hogedriven.backpaper0.radishtainer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import javax.inject.Inject;

public class FieldInjector extends Injector {

    private final Field field;

    public FieldInjector(Field field) {
        this.field = field;
    }

    @Override
    public boolean isInjectable() {
        return field.isAnnotationPresent(Inject.class);
    }

    @Override
    public void inject(Container container, Object target) {
        Class<?> type = field.getType();
        Object dependency = container.getInstance(type);
        if (Modifier.isPublic(field.getModifiers()) == false
                && field.isAccessible() == false) {
            field.setAccessible(true);
        }
        try {
            field.set(target, dependency);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof FieldInjector) == false) {
            return false;
        }
        FieldInjector other = (FieldInjector) obj;
        return field.equals(other.field);
    }
}
