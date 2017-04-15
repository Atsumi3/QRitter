package info.nukoneko.android.qritter.util;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import twitter4j.TwitterException;

public class RxWrap {
    public static <T> Observable<T> create(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, T>() {
                    @Override
                    public T call(Throwable throwable) {
                        throwable.printStackTrace();
                        return null;
                    }
                });
    }

    public interface Callable<T> {
        T call() throws TwitterException;
    }

    public static <T> Observable<T> createObservable(final Callable<T> observable) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(observable.call());
                    subscriber.onCompleted();
                } catch (TwitterException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
