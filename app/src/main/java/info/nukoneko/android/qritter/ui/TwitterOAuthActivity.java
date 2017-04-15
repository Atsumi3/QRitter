package info.nukoneko.android.qritter.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import info.nukoneko.android.qritter.R;
import info.nukoneko.android.qritter.ui.common.BaseActivity;
import info.nukoneko.android.qritter.util.RxWrap;
import info.nukoneko.android.qritter.util.TwitterUtil;
import rx.functions.Action1;
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
        mTwitter = TwitterUtil.getTwitterInstance();

        RxWrap.create(RxWrap.createObservable(new RxWrap.Callable<RequestToken>() {
            @Override
            public RequestToken call() throws TwitterException {
                return mTwitter.getOAuthRequestToken(getString(R.string.twitter_callback_uri));
            }
        })).subscribe(new Action1<RequestToken>() {
            @Override
            public void call(RequestToken requestToken) {
                mRequestToken = requestToken;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthorizationURL())));
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri == null ||
                !uri.toString().startsWith(getString(R.string.twitter_callback_uri))) {
            return;
        }

        final String verifier = uri.getQueryParameter("oauth_verifier");
        if (verifier == null || verifier.length() == 0) {
            return;
        }

        RxWrap.create(RxWrap.createObservable(new RxWrap.Callable<AccessToken>() {
            @Override
            public AccessToken call() throws TwitterException {
                return mTwitter.getOAuthAccessToken(mRequestToken, verifier);
            }
        })).subscribe(new Action1<AccessToken>() {
            @Override
            public void call(AccessToken accessToken) {
                TwitterUtil.addAccount(accessToken);
                finish();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                finish();
            }
        });
    }
}
