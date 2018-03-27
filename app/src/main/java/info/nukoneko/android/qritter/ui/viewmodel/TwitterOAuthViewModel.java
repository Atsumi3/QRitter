package info.nukoneko.android.qritter.ui.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import info.nukoneko.android.qritter.R;
import info.nukoneko.android.qritter.util.AccessTokenContainer;
import info.nukoneko.android.qritter.util.RxWrap;
import info.nukoneko.android.qritter.util.TwitterUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public final class TwitterOAuthViewModel implements ViewModel {

    public final ObservableInt progressBarVisible;
    public final ObservableInt retryViewVisible;
    public final ObservableField<String> requestStatusText;
    public final ObservableField<String> errorText;
    private final String mCallbackUrl;
    @Nullable
    private Context mContext;
    @Nullable
    private Listener mListener;
    @Nullable
    private Twitter mTwitterInstance;
    @Nullable
    private RequestToken mRequestToken;

    public TwitterOAuthViewModel(@NonNull Context context, @NonNull Listener listener) {
        mContext = context;
        mListener = listener;

        progressBarVisible = new ObservableInt(View.INVISIBLE);
        retryViewVisible = new ObservableInt(View.INVISIBLE);
        requestStatusText = new ObservableField<>("");
        errorText = new ObservableField<>("");

        mCallbackUrl = String.format("%s://%s",
                mContext.getString(R.string.twitter_callback_scheme),
                mContext.getString(R.string.twitter_callback_host));

        mTwitterInstance = TwitterUtil.getTwitterInstance(mContext);
    }

    @Override
    public void destroy() {
        mContext = null;
        mListener = null;
    }

    public void onCreate() {
        requestRequestToken();
    }

    public void onNewIntent(Intent intent) {
        final Uri uri = intent.getData();
        if (mContext == null || uri == null || !uri.getScheme().equals(mContext.getString(R.string.twitter_callback_scheme))) {
            retryViewVisible.set(View.VISIBLE);
            errorText.set("何かがおかしいです。リトライができない場合は、一度前の画面に戻ってまた試してみてください。");
            return;
        }

        final String verifier = uri.getQueryParameter("oauth_verifier");
        if (TextUtils.isEmpty(verifier)) {
            retryViewVisible.set(View.VISIBLE);
            errorText.set("対応していないURLっぽいです。");
            return;
        }

        requestAccessToken(verifier);
    }

    private void requestRequestToken() {
        mRequestToken = null;
        progressBarVisible.set(View.VISIBLE);
        retryViewVisible.set(View.INVISIBLE);
        errorText.set("");
        RxWrap
                .createObservable(new RxWrap.Callable<RequestToken>() {
                    @Override
                    public RequestToken call() throws TwitterException {
                        requestStatusText.set("RequestToken取得中...");
                        if (mTwitterInstance != null) {
                            return mTwitterInstance.getOAuthRequestToken(mCallbackUrl);
                        }
                        throw new RuntimeException("Twitter Instance is null");
                    }
                })
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RequestToken>() {
                    @Override
                    public void accept(RequestToken requestToken) throws Exception {
                        requestStatusText.set("ブラウザで連携画面が開きます");
                        progressBarVisible.set(View.INVISIBLE);
                        mRequestToken = requestToken;
                        openAuthorizationUrl();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressBarVisible.set(View.INVISIBLE);
                        retryViewVisible.set(View.VISIBLE);
                        errorText.set(throwable.getLocalizedMessage());
                    }
                });
    }

    private void requestAccessToken(final String verifier) {
        progressBarVisible.set(View.VISIBLE);
        retryViewVisible.set(View.INVISIBLE);
        errorText.set("");

        RxWrap
                .createObservable(new RxWrap.Callable<AccessToken>() {
                    @Override
                    public AccessToken call() throws TwitterException {
                        requestStatusText.set("AccessToken取得中...");
                        if (mTwitterInstance != null) {
                            return mTwitterInstance.getOAuthAccessToken(mRequestToken, verifier);
                        }
                        throw new RuntimeException("Twitter Instance is null");
                    }
                })
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AccessToken>() {
                    @Override
                    public void accept(AccessToken accessToken) throws Exception {
                        if (mContext != null && mListener != null) {
                            AccessTokenContainer.saveAccessToken(mContext, accessToken);
                            mListener.onGetAccessTokenSuccess();
                            return;
                        }
                        throw new RuntimeException("Context or Listener is null");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressBarVisible.set(View.INVISIBLE);
                        retryViewVisible.set(View.VISIBLE);
                        errorText.set(throwable.getLocalizedMessage());
                        if (mListener != null) {
                            mListener.onGetAccessTokenSuccess();
                            return;
                        }
                        throw new RuntimeException("Listener is null");
                    }
                });
    }

    private void openAuthorizationUrl() {
        if (mContext == null || mRequestToken == null) {
            requestStatusText.set("何かがおかしいです。一度戻って、また試してみてください。");
            return;
        }
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mRequestToken.getAuthorizationURL())));
    }

    public void onRetryClick(@SuppressWarnings("unused") View view) {
        requestRequestToken();
    }

    public interface Listener {
        void onGetAccessTokenSuccess();
    }
}
