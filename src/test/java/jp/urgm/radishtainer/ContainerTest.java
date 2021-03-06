package jp.urgm.radishtainer;

import java.lang.annotation.Annotation;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.inject.Provider;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import jp.urgm.radishtainer.Container;
import jp.urgm.radishtainer.test.Aaa;
import jp.urgm.radishtainer.test.Bbb;
import jp.urgm.radishtainer.test.Ccc2;
import jp.urgm.radishtainer.test.Ddd2;
import jp.urgm.radishtainer.test.Eee2;
import jp.urgm.radishtainer.test.Fff;
import jp.urgm.radishtainer.test.Ggg;
import jp.urgm.radishtainer.test.Hhh1;
import jp.urgm.radishtainer.test.Hhh2;
import jp.urgm.radishtainer.test.Iii1;
import jp.urgm.radishtainer.test.Iii2;
import jp.urgm.radishtainer.test.Iii3;
import jp.urgm.radishtainer.test.Jjj;
import jp.urgm.radishtainer.test.Kkk;
import jp.urgm.radishtainer.test.Lll;
import jp.urgm.radishtainer.test.Mmm;
import jp.urgm.radishtainer.test.Nnn;
import jp.urgm.radishtainer.test.Ooo2;
import jp.urgm.radishtainer.test.Ppp;
import jp.urgm.radishtainer.test.Qqq1;
import jp.urgm.radishtainer.test.Qqq2;
import jp.urgm.radishtainer.test.Qqq3;
import jp.urgm.radishtainer.test.Rrr;
import jp.urgm.radishtainer.test.Sss;
import jp.urgm.radishtainer.test.Ttt;
import jp.urgm.radishtainer.test.Uuu;
import jp.urgm.radishtainer.test.Www;
import jp.urgm.radishtainer.test.WwwProvider;
import jp.urgm.radishtainer.test.sub.Eee3;

public class ContainerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_getInstance() throws Exception {
        Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        Aaa instance = c.getInstance(Aaa.class, null);
        assertThat(instance, is(notNullValue()));
    }

    @Test
    public void test_inject() throws Exception {
        Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        Bbb target = new Bbb();
        c.inject(target);
        assertThat("private field", target.getPrivateField(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("package private field", target.getPackagePrivateField(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("protected field", target.getProtectedField(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("public field", target.getPublicField(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("private method", target.getPrivateMethod(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("package private method", target.getPackagePrivateMethod(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("protected method", target.getProtectedMethod(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("public method", target.getPublicMethod(), not(sameInstance(Aaa.INSTANCE)));
    }

    @Test
    public void test_inject_order() throws Exception {
        Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        Ccc2 target = new Ccc2();
        c.inject(target);
        assertThat("super field", target.getSuperField(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("super method", target.getSuperMethod(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("sub field", target.getSubField(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("sub method", target.getSubMethod(), not(sameInstance(Aaa.INSTANCE)));

        assertTrue("super field < sub field", target.getSuperField().count < target.getSubField().count);
        assertTrue("super field < sub method", target.getSuperField().count < target.getSubMethod().count);
        assertTrue("super method < sub method", target.getSuperMethod().count < target.getSubMethod().count);
        assertTrue("super method < sub field", target.getSuperMethod().count < target.getSubField().count);
    }

    @Test
    public void test_inject_inheritance() throws Exception {
        Container c = newContainer();
        Ddd2 target = new Ddd2();
        c.inject(target);
        assertThat("super @Inject", target.withAtInjectSuper, is(false));
        assertThat("super no annotation", target.noAtInjectSuper, is(false));
        assertThat("sub @Inject", target.withAtInjectSub, is(true));
        assertThat("sub no annotation", target.noAtInjectSub, is(false));
    }

    @Test
    public void test_inject_no_override() throws Exception {
        Container c = newContainer();
        Eee2 target = new Eee2();
        c.inject(target);
        assertThat("private super", target.privateMethodSuper, is(true));
        assertThat("package private super", target.packagePrivateMethodSuper, is(false));
        assertThat("protected super", target.protectedMethodSuper, is(false));
        assertThat("public super", target.publicMethodSuper, is(false));
        assertThat("private sub", target.privateMethodSub, is(false));
        assertThat("package private sub", target.packagePrivateMethodSub, is(false));
        assertThat("protected sub", target.protectedMethodSub, is(false));
        assertThat("public sub", target.publicMethodSub, is(false));
    }

    @Test
    public void test_inject_no_override_diff_package() throws Exception {
        Container c = newContainer();
        Eee3 target = new Eee3();
        c.inject(target);
        assertThat("private super", target.privateMethodSuper, is(true));
        assertThat("package private super", target.packagePrivateMethodSuper, is(true));
        assertThat("protected super", target.protectedMethodSuper, is(false));
        assertThat("public super", target.publicMethodSuper, is(false));
        assertThat("private sub", target.privateMethodSub, is(false));
        assertThat("package private sub", target.packagePrivateMethodSub, is(false));
        assertThat("protected sub", target.protectedMethodSub, is(false));
        assertThat("public sub", target.publicMethodSub, is(false));
    }

    @Test
    public void test_getInstance_andinject() throws Exception {
        Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        c.addClass(Fff.class, null, null);
        Fff instance = c.getInstance(Fff.class, null);
        assertThat("instance", instance, notNullValue());
        assertThat("field injection", instance.getField(), not(sameInstance(Aaa.INSTANCE)));
        assertThat("method injection", instance.getMethod(), not(sameInstance(Aaa.INSTANCE)));
    }

    @Test
    public void test_constructor_injection() throws Exception {
        Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        c.addClass(Ggg.class, null, null);
        Ggg instance = c.getInstance(Ggg.class, null);
        assertThat("instance", instance, notNullValue());
        assertThat("constructor injection", instance.aaa, not(sameInstance(Aaa.INSTANCE)));
    }

    @Test
    public void test_getInstance_by_interface() throws Exception {
        Container c = newContainer();
        c.addClass(Hhh1.class, null, Hhh2.class);
        Hhh1 instance = c.getInstance(Hhh1.class, null);
        assertThat(instance, instanceOf(Hhh2.class));
    }
    @Iii1
    Object withIii1;

    @Test
    public void test_getInstance_by_qualifier() throws Exception {
        Annotation iii1 = ContainerTest.class.getDeclaredField("withIii1").getAnnotation(Iii1.class);
        Container c = newContainer();
        c.addClass(Iii2.class, null, null);
        c.addClass(Iii2.class, iii1, Iii3.class);
        Iii2 instance1 = c.getInstance(Iii2.class, null);
        Iii2 instance2 = c.getInstance(Iii2.class, iii1);
        assertThat(instance1, not(instanceOf(Iii3.class)));
        assertThat(instance2, instanceOf(Iii3.class));
    }

    @Test
    public void test_inject_by_qualifier() throws Exception {
        Annotation iii1 = ContainerTest.class.getDeclaredField("withIii1").getAnnotation(Iii1.class);
        Container c = newContainer();
        c.addClass(Jjj.class, null, null);
        c.addClass(Iii2.class, null, null);
        c.addClass(Iii2.class, iii1, Iii3.class);
        Jjj instance = c.getInstance(Jjj.class, null);
        assertThat("field plain", instance.getFieldPlain(), not(instanceOf(Iii3.class)));
        assertThat("field with qualifier", instance.getFieldWithQualifier(), instanceOf(Iii3.class));
        assertThat("constructor plain", instance.getConstructorPlain(), not(instanceOf(Iii3.class)));
        assertThat("constructor with qualifier", instance.getConstructorWithQualifier(), instanceOf(Iii3.class));
        assertThat("method plain", instance.getMethodPlain(), not(instanceOf(Iii3.class)));
        assertThat("method with qualifier", instance.getMethodWithQualifier(), instanceOf(Iii3.class));
    }

    @Test
    public void test_getProvider() throws Exception {
        Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        Provider<Aaa> provider = c.getProvider(Aaa.class, null);
        assertThat("provider", provider, notNullValue());
        assertThat("instance", provider.get(), notNullValue());
    }

    @Test
    public void test_inject_provider() throws Exception {
        Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        c.addClass(Kkk.class, null, null);
        Kkk instance = c.getInstance(Kkk.class, null);

        assertThat("provider field", instance.getField(), not(sameInstance(Kkk.PROVIDER)));
        assertThat("instance field", instance.getField().get(), notNullValue());
        assertThat("provider constructor", instance.getConstructor(), not(sameInstance(Kkk.PROVIDER)));
        assertThat("instance constructor", instance.getConstructor().get(), notNullValue());
        assertThat("provider method", instance.getMethod(), not(sameInstance(Kkk.PROVIDER)));
        assertThat("instance method", instance.getMethod().get(), notNullValue());
    }

    @Test
    public void test_getInstance_default_scope() throws Exception {
        Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        Aaa instance1 = c.getInstance(Aaa.class, null);
        Aaa instance2 = c.getInstance(Aaa.class, null);
        assertThat(instance1, not(sameInstance(instance2)));
    }

    @Test
    public void test_getInstance_singleton_scope() throws Exception {
        Container c = newContainer();
        c.addClass(Lll.class, null, null);
        Lll instance1 = c.getInstance(Lll.class, null);
        Lll instance2 = c.getInstance(Lll.class, null);
        assertThat(instance1, sameInstance(instance2));
    }

    @Test
    public void test_addInstance() throws Exception {
        Container c = newContainer();
        Mmm instance1 = new Mmm();
        c.addInstance(Mmm.class, null, instance1);
        c.addClass(Aaa.class, null, null);
        Mmm instance2 = c.getInstance(Mmm.class, null);
        assertThat(instance2, sameInstance(instance1));
        assertThat(instance2.aaa, not(sameInstance(Aaa.INSTANCE)));
    }

    @Test
    public void test_addProvider() throws Exception {
        Container c = newContainer();
        c.addProvider(Www.class, null, new WwwProvider());
        c.addClass(Aaa.class, null, null);
        Www instance = c.getInstance(Www.class, null);
        assertThat(instance, sameInstance(Www.INSTANCE));
        assertThat(instance.aaa, sameInstance(Aaa.INSTANCE));
    }

    @Test
    public void test_event() throws Exception {
        Container c = newContainer();
        c.addClass(Nnn.class, null, null);
        Nnn instance = c.getInstance(Nnn.class, null);
        assertThat(instance.aaa, sameInstance(Aaa.INSTANCE));
        Aaa event = new Aaa();
        c.fireEvent(event);
        assertThat(instance.aaa, sameInstance(event));
    }

    @Test
    public void test_event_inheritance() throws Exception {
        Container c = newContainer();
        c.addClass(Ooo2.class, null, null);
        Ooo2 instance = c.getInstance(Ooo2.class, null);
        c.fireEvent(new Aaa());
        assertThat("super @Observers", instance.withAtObserversSuper, is(false));
        assertThat("super no annotation", instance.noAtObserversSuper, is(false));
        assertThat("sub @Observers", instance.withAtObserversSub, is(true));
        assertThat("sub no annotation", instance.noAtObserversSub, is(false));
        assertThat("super no overrided", instance.noOverridedSuper, is(true));
        assertThat("sub no overrided", instance.noOverridedSub, is(true));
    }

    @Test
    public void test_event_inject() throws Exception {
        Container c = newContainer();
        c.addClass(Ppp.class, null, null);

        Aaa injected = new Aaa();
        c.addInstance(Aaa.class, null, injected);

        Annotation iii1 = ContainerTest.class.getDeclaredField("withIii1").getAnnotation(Iii1.class);
        Aaa withQualifier = new Aaa();
        c.addInstance(Aaa.class, iii1, withQualifier);

        Ppp instance = c.getInstance(Ppp.class, null);

        Aaa event = new Aaa();
        c.fireEvent(event);

        assertThat("event", instance.event, sameInstance(event));
        assertThat("injected", instance.injected, sameInstance(injected));
        assertThat("with qualifier", instance.withQualifier, sameInstance(withQualifier));
    }

    @Test
    public void test_custom_scope() throws Exception {
        Container c = newContainer();
        c.addScope(Qqq2.class, new Qqq1());
        c.addClass(Qqq3.class, null, null);

        c.fireEvent(new Aaa());
        Qqq3 instance1 = c.getInstance(Qqq3.class, null);
        Qqq3 instance2 = c.getInstance(Qqq3.class, null);

        c.fireEvent(new Aaa());
        Qqq3 instance3 = c.getInstance(Qqq3.class, null);

        assertThat("same scope", instance1, sameInstance(instance2));
        assertThat("difference scope", instance1, not(sameInstance(instance3)));
    }

    @Test
    public void test_multi_thread() throws Exception {
        final Container c = newContainer();
        c.addClass(Rrr.class, null, null);
        ExecutorService exec = Executors.newFixedThreadPool(2);
        Callable<Rrr> task = new Callable<Rrr>() {
            @Override
            public Rrr call() throws Exception {
                return c.getInstance(Rrr.class, null);
            }
        };
        Future<Rrr> future1 = exec.submit(task);
        Future<Rrr> future2 = exec.submit(task);
        TimeUnit.MILLISECONDS.sleep(100L);
        Rrr.start.countDown();
        assertThat(future1.get(), sameInstance(future2.get()));
    }

    @Test
    public void test_error_duplicate() throws Exception {
        final Container c = newContainer();
        c.addClass(Aaa.class, null, null);
        expectedException.expect(RuntimeException.class);
        c.addClass(Aaa.class, null, null);
    }

    @Test
    public void test_error_duplicate_scope() throws Exception {
        Container c = newContainer();
        c.addScope(Qqq2.class, new Qqq1());
        expectedException.expect(RuntimeException.class);
        c.addScope(Qqq2.class, new Qqq1());
    }

    @Test
    public void test_error_duplicate_instance() throws Exception {
        Container c = newContainer();
        c.addInstance(Aaa.class, null, Aaa.INSTANCE);
        expectedException.expect(RuntimeException.class);
        c.addInstance(Aaa.class, null, Aaa.INSTANCE);
    }

    @Test
    public void test_error_getInstance() throws Exception {
        Container c = newContainer();
        c.addInstance(Aaa.class, null, Aaa.INSTANCE);
        expectedException.expect(RuntimeException.class);
        c.addInstance(Aaa.class, null, Aaa.INSTANCE);
    }

    @Test
    public void test_error_getInstance_interface() throws Exception {
        Container c = newContainer();
        expectedException.expect(IllegalArgumentException.class);
        c.addClass(Hhh1.class, null, null);
    }

    @Test
    public void test_error_getInstance_annotation() throws Exception {
        Container c = newContainer();
        expectedException.expect(IllegalArgumentException.class);
        c.addClass(Iii1.class, null, null);
    }

    @Test
    public void test_error_getInstance_enum() throws Exception {
        Container c = newContainer();
        expectedException.expect(IllegalArgumentException.class);
        c.addClass(Sss.class, null, null);
    }

    @Test
    public void test_error_getInstance_not_exists() throws Exception {
        Container c = newContainer();
        expectedException.expect(NoSuchElementException.class);
        c.getInstance(Iii1.class, null);
    }

    @Test
    public void test_error_getProvider_not_exists() throws Exception {
        Container c = newContainer();
        expectedException.expect(NoSuchElementException.class);
        c.getProvider(Iii1.class, null);
    }

    @Test
    public void test_error_2_constructor_with_atInject() throws Exception {
        Container c = newContainer();
        expectedException.expect(IllegalArgumentException.class);
        c.addClass(Ttt.class, null, null);
    }

    @Test
    public void test_error_no_constructor_with_atInject() throws Exception {
        Container c = newContainer();
        expectedException.expect(IllegalArgumentException.class);
        c.addClass(Uuu.class, null, null);
    }

    protected Container newContainer() {
        return new Container();
    }
}
