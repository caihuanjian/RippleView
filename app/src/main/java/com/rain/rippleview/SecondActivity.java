package com.rain.rippleview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.rain.rippleview.ripple.Point;
import com.rain.rippleview.ripple.Ripple;
import com.rain.rippleview.ripple.RippleView;

/**
 * Created by HwanJ.Choi on 2017-9-22.
 */

public class SecondActivity extends AppCompatActivity {

    private RippleView mRippleView;

    private View topLayout;
    private View bottomLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("动态发布");
        toolbar.setTitleTextAppearance(this, R.style.action_bar_title);
        toolbar.setNavigationIcon(R.mipmap.ic_close_white);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRippleView = (RippleView) findViewById(R.id.ripple);
        topLayout = findViewById(R.id.top);
        bottomLayout = findViewById(R.id.bottom);
        mRippleView.setStateChangedListener(new RippleView.onStateChangedListener() {
            @Override
            public void onOpen() {
                Toast.makeText(SecondActivity.this, "open", Toast.LENGTH_SHORT).show();
                animViewIn();
            }

            @Override
            public void onClose() {
                Toast.makeText(SecondActivity.this, "close", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
            }
        });

        final Bundle bundle = getIntent().getExtras();
        final Point point = bundle.getParcelable(Ripple.ARG_START_LOCATION);
        getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                topLayout.setTranslationY(-topLayout.getHeight());
                bottomLayout.setTranslationY(bottomLayout.getHeight());
                mRippleView.open(point);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        animViewOut();
        topLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRippleView.close();
            }
        }, 200);
    }

    private void animViewIn() {
        topLayout.animate().translationY(0).setDuration(400).setInterpolator(new DecelerateInterpolator()).start();
        bottomLayout.animate().translationY(0).setDuration(400).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void animViewOut() {
        topLayout.animate().translationY(-topLayout.getHeight()).alpha(0).setDuration(400).setInterpolator(new AccelerateInterpolator()).start();
        bottomLayout.animate().translationY(bottomLayout.getHeight()).alpha(0).setDuration(400).setInterpolator(new AccelerateInterpolator()).start();
    }
}
