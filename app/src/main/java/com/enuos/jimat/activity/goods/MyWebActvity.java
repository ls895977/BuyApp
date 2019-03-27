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
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.order.OrderDetailsActivity;
import com.enuos.jimat.utils.CustomDialog;

public class MyWebActvity extends BaseActivity {
    private WebView mWebWebView;
    private CustomDialog customDialog;
    private String orderId;
    TextView mWebTextTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mywebactvity);
//        initView();
//        initData();
    }

    public void initView() {
//        customDialog = new CustomDialog(this, R.style.CustomDialog);
//        mWebWebView = findViewById(R.id.web_pay_webView);
//        mWebTextTitle = findViewById(R.id.web_pay_text_title);
//        findViewById(R.id.web_pay_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mBaseActivity, OrderDetailsActivity.class);
//                intent.putExtra("orderId", orderId);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    public void initData() {
//        orderId = getIntent().getStringExtra("orderId");
//        // 加载标题
//        mWebTextTitle.setText(getIntent().getStringExtra("title"));
//        // 加载 url
////        customDialog.show();
////        initWebView();
//        mWebWebView.loadUrl(getIntent().getStringExtra("url"));
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
}
