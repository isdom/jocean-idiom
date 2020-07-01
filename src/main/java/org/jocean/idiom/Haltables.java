package org.jocean.idiom;

import java.util.concurrent.TimeUnit;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

public class Haltables {

    private Haltables() {
        throw new IllegalStateException("No instances!");
    }

//    interface ChildHaltable extends Haltable {
//        public void setParent(Haltable parent);
//    }
//
//    public static class DelayHaltable implements ChildHaltable {
//
//        @Override
//        public Action1<Action0> onHalt() {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public Action0 doOnHalt(final Action0 onhalt) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public void setParent(final Haltable parent) {
//            parent.doOnHalt(onhalt);
//        }
//    }
    public final static HaltableFactory DELAY_30S = new HaltableFactory() {
        @Override
        public Haltable build() {
            return HaltableUtil.delay(30, TimeUnit.SECONDS);
        }};

    public final static Haltable NEVER = new Haltable() {
        @Override
        public Action1<Action0> onHalt() {
            return Actions.empty();
        }
        @Override
        public Action0 doOnHalt(final Action0 onhalt) {
            return Actions.empty();
    }};
}
