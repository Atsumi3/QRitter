package info.nukoneko.android.qritter.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import info.nukoneko.android.qritter.entity.ModelAccessTokenObject;
import io.realm.Realm;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

final public class TwitterUtil {
    private static final String apiKey = "ijKpGLCQMqp4OMJRddbwIMNtE";
    private static final String apiSecret = "q4UDvYcouwGTZfirRfMLaLGHJynW6TVcxOzGEZQ7alnjsXCpfI";

    /***
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     * @return TwitterInstance
     */
    public static Twitter getTwitterInstance() {
        final TwitterFactory factory = new TwitterFactory();
        final Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(apiKey, apiSecret);

        if (hasAccessToken()) {
            twitter.setOAuthAccessToken(loadAccessToken(null));
        }
        return twitter;
    }

    /***
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     * @return TwitterStreamInstance
     */
    @Nullable
    public static TwitterStream getTwitterStreamInstance() {
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        {
            builder.setOAuthConsumerKey(apiKey);
            builder.setOAuthConsumerSecret(apiSecret);
            AccessToken accessToken = loadAccessToken(null);
            if (accessToken != null) {
                builder.setOAuthAccessToken(accessToken.getToken());
                builder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());
            } else {
                return null;
            }
        }
        final twitter4j.conf.Configuration configuration = builder.build();
        return new TwitterStreamFactory(configuration).getInstance();
    }

    @Nullable
    private static AccessToken loadAccessToken(@Nullable Long userId) {
        final ModelAccessTokenObject tokenObject = getAccount(userId);
        if (tokenObject == null) {
            return null;
        }
        return new AccessToken(
                tokenObject.getUserToken(),
                tokenObject.getUserTokenSecret(),
                tokenObject.getUserId());
    }

    /***
     * アクセストークンが存在する場合はtrueを返します。
     * @return false or true
     */
    public static boolean hasAccessToken() {
        final Realm realm = Realm.getDefaultInstance();
        return realm.where(ModelAccessTokenObject.class).findAll().size() > 0;
    }


    /* ---  database 操作 --- */

    public static void addAccount(@NonNull AccessToken accessToken) {
        final ModelAccessTokenObject tokenObject = new ModelAccessTokenObject();
        tokenObject.setUserId(accessToken.getUserId());
        tokenObject.setUserName(accessToken.getScreenName());
        tokenObject.setUserScreenName(accessToken.getScreenName());
        tokenObject.setUserToken(accessToken.getToken());
        tokenObject.setUserTokenSecret(accessToken.getTokenSecret());

        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tokenObject);
        realm.commitTransaction();

        realm.close();
    }

    @NonNull
    private static ArrayList<ModelAccessTokenObject> getAccounts() {
        final Realm realm = Realm.getDefaultInstance();
        final ArrayList<ModelAccessTokenObject> results = new ArrayList<>();
        for (ModelAccessTokenObject token : realm.where(ModelAccessTokenObject.class).findAll()) {
            results.add(token);
        }
        return results;
    }

    @Nullable
    private static ModelAccessTokenObject getAccount(@Nullable Long userId) {
        if (userId == null) {
            final ArrayList<ModelAccessTokenObject> list = getAccounts();
            return list.size() > 0 ? list.get(0) : null;
        }
        final Realm realm = Realm.getDefaultInstance();
        return realm
                .where(ModelAccessTokenObject.class)
                .equalTo("userId", userId)
                .findFirst();
    }
}
