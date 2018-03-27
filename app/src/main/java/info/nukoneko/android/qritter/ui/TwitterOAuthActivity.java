package info.nukoneko.android.qritter.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import info.nukoneko.android.qritter.R;
import info.nukoneko.android.qritter.ui.common.BaseActivity;
import info.nukoneko.android.qritter.util.AccessTokenContainer;
import info.nukoneko.android.qritter.util.RxWrap;
import info.nukoneko.android.qritter.util.TwitterUtil;
import io.reactivex.functions.Consumer;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOAuthActivity extends BaseActivity {
    private Twitter mTwitter;
    private RequestToken mRequestToken;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TwitterOAuthActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwitter = TwitterUtil.getTwitterInstance(this);

        RxWrap.create(RxWrap.createObservable(new RxWrap.Callable<RequestToken>() {
            @Override
            public RequestToken call() throws TwitterException {
                return mTwitter.getOAuthRequestToken(getCallbackUri());
            }
        })).subscribe(new Consumer<RequestToken>() {
            @Override
            public void accept(RequestToken requestToken) throws Exception {
                mRequestToken = requestToken;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthorizationURL())));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Uri uri = intent.getData();
        if (uri == null || !uri.getScheme().equals(getString(R.string.twitter_callback_scheme))) {
            return;
        }

        final String verifier = uri.getQueryParameter("oauth_verifier");
        if (TextUtils.isEmpty(verifier)) {
            return;
        }

        RxWrap.create(RxWrap.createObservable(new RxWrap.Callable<AccessToken>() {
            @Override
            public AccessToken call() throws TwitterException {
                return mTwitter.getOAuthAccessToken(mRequestToken, verifier);
            }
        })).subscribe(new Consumer<AccessToken>() {
            @Override
            public void accept(AccessToken accessToken) throws Exception {
                AccessTokenContainer.saveAccessToken(getApplicationContext(), accessToken);
                finish();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                finish();
            }
        });
    }

    @NonNull
    private String getCallbackUri() {
        return String.format("%s://%s", getString(R.string.twitter_callback_scheme), getString(R.string.twitter_callback_host));
    }
}
