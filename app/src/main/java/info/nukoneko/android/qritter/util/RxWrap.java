package info.nukoneko.android.qritter.util;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import twitter4j.TwitterException;

public final class RxWrap {
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
