package jp.urgm.radishtainer.test;

import jp.urgm.radishtainer.event.Observes;

public class Ooo1 {

    public boolean withAtObserversSuper;

    public boolean noAtObserversSuper;

    public boolean noOverridedSuper;

    public void handle1(@Observes Aaa event) {
        withAtObserversSuper = true;
    }

    public void handle2(Aaa event) {
        noAtObserversSuper = true;
    }

    public void handle3(@Observes Aaa event) {
        noOverridedSuper = true;
    }
}
