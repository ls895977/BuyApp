package com.enuos.jimat.activity.common;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.utils.CustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebActivity extends BaseActivity {

    @BindView(R.id.web_text_title)
    TextView mWebTextTitle;
    @BindView(R.id.web_webView)
    WebView mWebWebView;
    @BindView(R.id.web_back)
    ImageView mBack;
    @BindView(R.id.web_back_rl)
    RelativeLayout webBackRl;

    // 加载 loading 框
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);

        customDialog = new CustomDialog(this, R.style.CustomDialog);

        initWebView();

        // 加载标题
        mWebTextTitle.setText(getIntent().getStringExtra("title"));
        // 加载 url
        customDialog.show();
        mWebWebView.loadUrl(getIntent().getStringExtra("url"));

    }

    /**
     * 点击事件
     */
    @OnClick({R.id.web_back_rl})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.web_back_rl:
                finish();
                break;
        }
    }

    /**
     * 初始化web内核设置
     */
    private void initWebView() {
        WebSettings webSettings = mWebWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebWebView.setBackgroundColor(Color.TRANSPARENT); // WebView 背景透明效果
        mWebWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                customDialog.show();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                customDialog.dismiss();
                Log.e("TAG", "网页加载失败");
            }
        });

        // 进度条
        mWebWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    customDialog.dismiss();
                    Log.e("TAG", "加载完成");
                }
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebWebView.canGoBack()) {
            mWebWebView.goBack();
            return true;
        } else {
            finish();
        }
        return false;
    }
}
