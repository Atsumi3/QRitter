package info.nukoneko.android.qritter;import android.app.Activity;import android.content.Context;import android.os.Bundle;import android.support.v7.widget.LinearLayoutManager;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.StaggeredGridLayoutManager;import info.nukoneko.android.qritter.common.BaseActivity;import info.nukoneko.android.qritter.twitter.SimpleStreamListener;import info.nukoneko.android.qritter.twitter.TimelineAdapter;import info.nukoneko.android.qritter.twitter.TwitterOAuthActivity;import info.nukoneko.android.qritter.util.TwitterUtil;import info.nukoneko.android.qritter.util.async.AsyncListener;import info.nukoneko.android.qritter.util.async.SimpleAsync;import rx.Observable;import rx.Subscriber;import rx.android.schedulers.AndroidSchedulers;import rx.functions.Action1;import rx.schedulers.Schedulers;import twitter4j.Status;import twitter4j.Twitter;import twitter4j.TwitterException;import twitter4j.TwitterStream;import twitter4j.User;/** * Created by TEJNEK on 2016/03/17. */public class MainActivity extends BaseActivity {    RecyclerView recyclerView;    TimelineAdapter mAdapter;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        if (TwitterUtil.hasAccessToken(this)) {            recyclerView = new RecyclerView(this);            setContentView(recyclerView);            Initialize();//            Twitter twitter = TwitterUtil.getTwitterInstance(this);//            new SimpleAsync(this).start(new AsyncListener<User>() {//                @Override//                public User doTask() throws TwitterException {//                    return twitter.showUser(twitter.getId());//                }////                @Override//                public void onResult(Activity context, User result) {//                    SimpleProfileActivity.startActivity(context, result);//                }//            });//            twitterSimpleStream(this).subscribe(status -> {//                System.out.println(status.getText());//            });        } else {            TwitterOAuthActivity.startActivity(this);        }    }    private void Initialize(){        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(5,StaggeredGridLayoutManager.VERTICAL);        mAdapter = new TimelineAdapter();        this.recyclerView.setAdapter(mAdapter);        this.recyclerView.setLayoutManager(mLayoutManager);        twitterSimpleStream(this).subscribe(status -> {            mAdapter.insert(status, 0);        });    }    public Observable<Status> twitterSimpleStream(Context context){        return Observable.create(new Observable.OnSubscribe<Status>() {            @Override            public void call(Subscriber<? super Status> subscriber) {                final TwitterStream stream = TwitterUtil.getTwitterStreamInstance(context);                assert stream != null;                stream.addListener(new SimpleStreamListener() {                    @Override                    public void onStatus(Status status) {                        subscriber.onNext(status);                    }                    @Override                    public void onException(Exception ex) {                        subscriber.onError(ex);                    }                });                stream.user();            }        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());    }}