package info.nukoneko.android.qritter.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

final public class TwitterUtil {
    private static final String API_KEY = "ijKpGLCQMqp4OMJRddbwIMNtE";
    private static final String API_SECRET = "q4UDvYcouwGTZfirRfMLaLGHJynW6TVcxOzGEZQ7alnjsXCpfI";

    /***
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     * @return TwitterInstance
     */
    public static Twitter getTwitterInstance(@NonNull final Context context) {
        final TwitterFactory factory = new TwitterFactory();
        final Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(API_KEY, API_SECRET);

        final AccessToken savedAccessToken = AccessTokenContainer.getSavedAccessToken(context);
        if (savedAccessToken != null) {
            twitter.setOAuthAccessToken(savedAccessToken);
        }
        return twitter;
    }

    /***
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     * @return TwitterStreamInstance
     */
    @Nullable
    public static TwitterStream getTwitterStreamInstance(@NonNull final Context context) {
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(API_KEY);
        builder.setOAuthConsumerSecret(API_SECRET);

        final AccessToken savedAccessToken = AccessTokenContainer.getSavedAccessToken(context);
        if (savedAccessToken != null) {
            builder.setOAuthAccessToken(savedAccessToken.getToken());
            builder.setOAuthAccessTokenSecret(savedAccessToken.getTokenSecret());
            final twitter4j.conf.Configuration configuration = builder.build();
            return new TwitterStreamFactory(configuration).getInstance();
        }
        return null;
    }
}
