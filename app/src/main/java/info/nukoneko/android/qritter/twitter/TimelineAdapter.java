package info.nukoneko.android.qritter.twitter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.nukoneko.android.qritter.R;
import info.nukoneko.android.qritter.databinding.ItemTweetQrBinding;
import twitter4j.Status;

/**
 * Created by atsumi on 2016/03/17.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.BindingHolder> {

    private ArrayList<Status> tweets = new ArrayList<>();

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet_qr, parent, false);
        return new BindingHolder(v);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        holder.getBinding().setTweet(tweets.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void insert(Status status, int position){
        tweets.add(0, status);
        this.notifyItemInserted(position);
    }

    static class BindingHolder extends RecyclerView.ViewHolder {

        private final ItemTweetQrBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ItemTweetQrBinding getBinding() {
            return binding;
        }
    }
}
