package info.nukoneko.android.qritter.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.nukoneko.android.qritter.R;
import info.nukoneko.android.qritter.databinding.ItemTweetQrBinding;
import twitter4j.Status;

final class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.BindingHolder> {

    private final ArrayList<Status> mList = new ArrayList<>();

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet_qr, parent, false);
        return new BindingHolder(v);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        holder.getBinding().setTweet(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    void insert(Status status) {
        mList.add(0, status);
        notifyItemInserted(0);
    }

    static class BindingHolder extends RecyclerView.ViewHolder {

        private final ItemTweetQrBinding mBinding;

        public BindingHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public ItemTweetQrBinding getBinding() {
            return mBinding;
        }
    }
}
