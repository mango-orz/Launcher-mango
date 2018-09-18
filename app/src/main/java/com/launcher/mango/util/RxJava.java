package com.launcher.mango.util;

import android.util.Log;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * RxAndroid utils
 *
 * @author tic
 * created on 18-9-17
 */
public class RxJava<T> {

    private static final String TAG = RxJava.class.getSimpleName();

    private Observable<T> mObservable;
    private Flowable<T> mFlowable;

    private Consumer<Throwable> mThrowable;
    private Consumer<? super T> mNextConsumer;
    private Action mCompleteAction;
    /**
     * for cancel
     */
    private Disposable mDispose;

    private RxJava() {
    }

    public static <T> RxJava<T> create() {
        return new RxJava<>();
    }

    public RxJava<T> observable(ObservableOnSubscribe<T> e) {
        Preconditions.assertNotNull(e);
        mObservable = Observable.create(e);
        return this;
    }

    public RxJava<T> flowable(FlowableOnSubscribe<T> e, BackpressureStrategy strategy) {
        Preconditions.assertNotNull(e);
        mFlowable = Flowable.create(e, strategy);
        return this;
    }

    public RxJava<T> subscribeOn(boolean async) {
        if (async) {
            if (mObservable != null) {
                mObservable = mObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io());
            } else if (mFlowable != null) {
                mFlowable = mFlowable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io());
            }
        }
        return this;
    }

    /**
     * async subscribeOn
     */
    public RxJava<T> subscribeOn() {
        subscribeOn(true);
        return this;
    }

    public RxJava<T> onNext(Consumer<? super T> onNext) {
        Preconditions.assertNotNull(onNext);
        mNextConsumer = onNext;
        return this;
    }

    public RxJava<T> onThrowable(Consumer<Throwable> consumer) {
        Preconditions.assertNotNull(consumer);
        mThrowable = consumer;
        return this;
    }

    public RxJava<T> onComplete(Action action) {
        Preconditions.assertNotNull(action);
        mCompleteAction = action;
        return this;
    }

    public RxJava<T> go() {
        Consumer<? super T> nextConsumer = mNextConsumer == null ?
                emptyNext() : mNextConsumer;
        Consumer<Throwable> throwable = mThrowable == null ?
                Throwable::printStackTrace : mThrowable;
        Action action = mCompleteAction == null ?
                emptyAction() : mCompleteAction;

        if (mObservable != null) {
            mDispose = mObservable.subscribe(nextConsumer, throwable, action);
        } else if (mFlowable != null) {
            mDispose = mFlowable.subscribe(nextConsumer, throwable, action);
        }
        return this;
    }

    /**
     * cancel Dispose
     */
    public void cancel() {
        if (mDispose != null) {
            mDispose.dispose();
        }
    }

    private Action emptyAction() {
        return () -> Log.i(TAG, "onComplete");
    }

    private Consumer<T> emptyNext() {
        return data -> Log.i(TAG, "onNext --> " + data);
    }
}
