package info.nukoneko.android.qritter.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import info.nukoneko.android.qritter.ui.common.BaseActivity;
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

public final class MainActivity extends BaseActivity {
    @Nullable
    private RecyclerView mRecyclerView;

    @Nullable
    private TimelineAdapter mAdapter;

    @Nullable
    private Observable<Status> mTwitterStreamObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AccessTokenContainer.hasAccessToken(getApplicationContext())) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("アカウントが見つかりませんでした");
            alertBuilder.setMessage("認証を行ってください");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    TwitterOAuthActivity.startActivity(MainActivity.this);
                }
            });
            alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertBuilder.create().show();
            return;
        }
        mRecyclerView = new RecyclerView(this);
        setContentView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRecyclerView == null) return;

        if (mAdapter == null) {
            final RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter != null) {
                mAdapter = (TimelineAdapter) adapter;
            } else {
                mAdapter = new TimelineAdapter();
                mRecyclerView.setAdapter(mAdapter);
            }
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        }

        if (mTwitterStreamObserver == null) {
            mTwitterStreamObserver = createTwitterStream();
            mTwitterStreamObserver.subscribe(new Consumer<Status>() {
                @Override
                public void accept(Status status) throws Exception {
                    mAdapter.insert(status);
                }
            });
        }
    }

    @NonNull
    private Observable<Status> createTwitterStream() {
        return Observable.create(new ObservableOnSubscribe<Status>() {
            @Override
            public void subscribe(final ObservableEmitter<Status> emitter) throws Exception {
                final TwitterStream stream = TwitterUtil.getTwitterStreamInstance(MainActivity.this);
                assert stream != null;
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
}
