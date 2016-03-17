package info.nukoneko.android.qritter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import info.nukoneko.android.qritter.common.BaseActivity;
import info.nukoneko.android.qritter.util.tools.PicassoImage;
import info.nukoneko.android.qritter.util.tools.ShowToast;
import twitter4j.User;

/**
 * Created by atsumi on 2016/03/17.
 */
public class SimpleProfileActivity extends BaseActivity {
    private static final String EXTRA_USER = "extra_user";
    private User twitterUser;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Object user = getIntent().getSerializableExtra(EXTRA_USER);
        if (user == null){
            ShowToast.showToast(R.string.error_load_user);
            finish();
            return;
        }
        twitterUser = (User) user;

        imageView = new ImageView(this);
        setContentView(imageView);

        ShowToast.showToast(twitterUser.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        PicassoImage.Builder(this).load(twitterUser.getBiggerProfileImageURLHttps()).into(imageView);
    }

    public static void startActivity(Context context, User twitterUser) {
        Intent intent = new Intent(context, SimpleProfileActivity.class);
        intent.putExtra(EXTRA_USER, twitterUser);
        context.startActivity(intent);
    }
}
