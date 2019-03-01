package com.enuos.jimat.activity.goods;

import android.content.Intent;
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
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.order.OrderDetailsActivity;
import com.enuos.jimat.utils.CustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebPayActivity extends BaseActivity {

    @BindView(R.id.web_pay_back)
    ImageView mBack;
    @BindView(R.id.web_pay_text_title)
    TextView mWebTextTitle;
    @BindView(R.id.web_pay_webView)
    WebView mWebWebView;

    // 加载 loading 框
    private CustomDialog customDialog;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_pay);
        ButterKnife.bind(this);

        customDialog = new CustomDialog(this, R.style.CustomDialog);

        initWebView();

        orderId = getIntent().getStringExtra("orderId");
        // 加载标题
        mWebTextTitle.setText(getIntent().getStringExtra("title"));
        // 加载 url
        customDialog.show();
        mWebWebView.loadUrl(getIntent().getStringExtra("url"));

    }

    /**
     * 点击事件
     */
    @OnClick({R.id.web_pay_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回 A-B-C  C直接回A
            case R.id.web_pay_back:
//                Intent intent = new Intent(mBaseActivity, MineOrderActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
                Intent intent = new Intent(mBaseActivity, OrderDetailsActivity.class);
                intent.putExtra("orderId", orderId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
