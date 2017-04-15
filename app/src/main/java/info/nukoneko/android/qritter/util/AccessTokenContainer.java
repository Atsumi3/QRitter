package info.nukoneko.android.qritter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import twitter4j.auth.AccessToken;

public final class AccessTokenContainer {
    private static final String PREFERENCE_NAME = "AccessTokenContainer";

    private static final String PREFERENCE_KEY_ACCESS_TOKEN = "access_token";
    private static final String PREFERENCE_KEY_ACCESS_TOKEN_SECRET = "access_token_secret";
    private static final String PREFERENCE_KEY_USER_ID = "user_id";

    private static SharedPreferences getPreference(@NonNull final Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void saveAccessToken(@NonNull final Context context, @NonNull final AccessToken accessToken) {
        getPreference(context).edit()
                .putString(PREFERENCE_KEY_ACCESS_TOKEN, accessToken.getToken())
                .putString(PREFERENCE_KEY_ACCESS_TOKEN_SECRET, accessToken.getTokenSecret())
                .putLong(PREFERENCE_KEY_USER_ID, accessToken.getUserId())
                .apply();
    }

    @Nullable
    public static synchronized AccessToken getSavedAccessToken(@NonNull final Context context) {
        final SharedPreferences pref = getPreference(context);
        final String accessToken = pref.getString(PREFERENCE_KEY_ACCESS_TOKEN, "");
        final String accessTokenSecret = pref.getString(PREFERENCE_KEY_ACCESS_TOKEN_SECRET, "");
        final long userId = pref.getLong(PREFERENCE_KEY_USER_ID, -1L);
        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(accessTokenSecret) || userId == -1L) {
            Log.e("UserToken", "Failed to get AccessToken from cache.");
            return null;
        }
        return new AccessToken(accessToken, accessTokenSecret, userId);
    }

    public static boolean hasAccessToken(@NonNull final Context context) {
        return getSavedAccessToken(context) != null;
    }
}
