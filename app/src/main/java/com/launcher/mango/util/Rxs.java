package com.launcher.mango.util;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * RxAndroid utils
 *
 * @author tic
 * created on 18-9-17
 */
public class Rxs {

    public static <T> Observable<T> create(Observable<T> observable) {
        return observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public static <T> Observable<T> create(Observable<T> observable, boolean async) {
        if (async) {
            return create(observable);
        } else {
            return observable;
        }
    }
}
