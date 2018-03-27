package info.nukoneko.android.qritter.ui.twitter_oauth;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import info.nukoneko.android.qritter.R;
import info.nukoneko.android.qritter.databinding.ActivityTwitterOauthBinding;
import info.nukoneko.android.qritter.ui.viewmodel.TwitterOAuthViewModel;

public final class TwitterOAuthActivity extends AppCompatActivity {

    private final TwitterOAuthViewModel.Listener mListener = new TwitterOAuthViewModel.Listener() {
        @Override
        public void onGetAccessTokenSuccess() {
            finish();
        }
    };
    private TwitterOAuthViewModel mViewModel;

    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, TwitterOAuthActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTwitterOauthBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_twitter_oauth);
        mViewModel = new TwitterOAuthViewModel(this, mListener);
        binding.setViewModel(mViewModel);
        mViewModel.onCreate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mViewModel.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        mViewModel.destroy();
        super.onDestroy();
    }
}
