package org.jocean.idiom.rx;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public class RxDemo {

    public static void main(String[] args) {
        final StringBuilder sb = new StringBuilder();
        
        Observable.just("Hello, world!", "2", "3", "4")
        .flatMap(new Func1<Object, Observable<Object>>() {
            @Override
            public Observable<Object> call(final Object url) {
                sb.append(url);
                return Observable.empty();
            }
        },
        new Func1<Throwable, Observable<String>>() {
            @Override
            public Observable<String> call(Throwable e) {
                return Observable.error(e);
            }
        },
        new Func0<Observable<Object>>() {
            @Override
            public Observable<Object> call() {
                sb.append("onComplete");
//                return Observable.just((Object)"onComplete");
                return Observable.just((Object)sb.toString());
            }
        }
        )
        .subscribe(new Action1<Object>() {
            @Override
            public void call(Object t) {
                System.out.println("onNext:" + t);
            }},
            new Action1<Throwable>() {
            @Override
            public void call(Throwable e) {
                System.out.println("onError:" + e);
            }},
            new Action0() {
            @Override
            public void call() {
                System.out.println("onCompleted");
            }}
            );
        
    }

}
