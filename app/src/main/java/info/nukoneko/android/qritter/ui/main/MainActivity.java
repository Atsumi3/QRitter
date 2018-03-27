package info.nukoneko.android.qritter.ui.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;

import info.nukoneko.android.qritter.R;
import info.nukoneko.android.qritter.databinding.ActivityMainBinding;
import info.nukoneko.android.qritter.ui.viewmodel.MainViewModel;
import twitter4j.Status;

public final class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;
    private TimelineAdapter mAdapter;

    private final MainViewModel.DataListener mDataListener = new MainViewModel.DataListener() {
        @Override
        public void onStatusReceived(Status status) {
            mAdapter.insert(status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new MainViewModel(this, mDataListener);
        binding.setViewModel(mViewModel);

        mAdapter = new TimelineAdapter();
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.onResume();
    }

    @Override
    protected void onDestroy() {
        mViewModel.destroy();
        super.onDestroy();
    }
}
