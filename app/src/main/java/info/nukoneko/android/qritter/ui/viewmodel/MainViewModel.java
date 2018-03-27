package info.nukoneko.android.qritter.ui.viewmodel;

import android.content.Context;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import info.nukoneko.android.qritter.ui.twitter_oauth.TwitterOAuthActivity;
import info.nukoneko.android.qritter.util.AccessTokenContainer;
import info.nukoneko.android.qritter.util.SimpleStreamListener;
import info.nukoneko.android.qritter.util.TwitterUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import twitter4j.Status;
import twitter4j.TwitterStream;

public final class MainViewModel implements ViewModel {

    public final ObservableInt recyclerViewVisible;
    public final ObservableInt authorizeButtonVisible;

    @Nullable
    private Context mContext;
    @Nullable
    private DataListener mDataListener;

    private Observable<Status> mTwitterStreamObserver;

    public MainViewModel(@NonNull Context context, @NonNull DataListener dataListener) {
        mContext = context;
        mDataListener = dataListener;
        recyclerViewVisible = new ObservableInt(View.GONE);
        authorizeButtonVisible = new ObservableInt(View.GONE);
    }

    public void onResume() {
        if (mContext == null) return;

        final boolean hasToken = AccessTokenContainer.hasAccessToken(mContext);
        recyclerViewVisible.set(hasToken ? View.VISIBLE : View.GONE);
        authorizeButtonVisible.set(hasToken ? View.GONE : View.VISIBLE);

        getTwitterStreamObserver()
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) throws Exception {
                        if (mDataListener != null) mDataListener.onStatusReceived(status);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mContext == null) return;
                        Toast.makeText(mContext, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void destroy() {
        mContext = null;
        mTwitterStreamObserver = null;
    }

    public void onAuthorizeClick(View view) {
        if (mContext != null) TwitterOAuthActivity.createIntent(mContext);
    }

    @NonNull
    private Observable<Status> getTwitterStreamObserver() {
        if (mTwitterStreamObserver == null) {
            mTwitterStreamObserver = Observable.create(new ObservableOnSubscribe<Status>() {
                @Override
                public void subscribe(final ObservableEmitter<Status> emitter) throws Exception {
                    if (mContext == null) {
                        throw new RuntimeException("Context is null.");
                    }
                    final TwitterStream stream = TwitterUtil.getTwitterStreamInstance(mContext);
                    if (stream == null) {
                        throw new RuntimeException("Cannot create TwitterStream.");
                    }
                    stream.addListener(new SimpleStreamListener() {
                        @Override
                        public void onStatus(Status status) {
                            emitter.onNext(status);
                        }

                        @Override
                        public void onException(Exception ex) {
                            emitter.onError(ex);
                        }
                    });
                    stream.user();
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
        }
        return mTwitterStreamObserver;
    }

    public interface DataListener {
        void onStatusReceived(Status status);
    }
}
