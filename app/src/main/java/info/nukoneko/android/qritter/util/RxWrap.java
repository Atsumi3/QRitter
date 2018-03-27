package info.nukoneko.android.qritter.util;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import twitter4j.TwitterException;

public class RxWrap {
    public static <T> Observable<T> create(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, T>() {
                    @Override
                    public T apply(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        return null;
                    }
                });
    }

    public interface Callable<T> {
        T call() throws TwitterException;
    }

    public static <T> Observable<T> createObservable(final Callable<T> observable) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(observable.call());
                    emitter.onComplete();
                } catch (TwitterException e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
