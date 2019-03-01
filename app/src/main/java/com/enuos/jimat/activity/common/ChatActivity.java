package com.enuos.jimat.activity.common;

import android.os.Bundle;

import com.enuos.jimat.R;

public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MyChatFragment myChatFragment = new MyChatFragment();
        myChatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, myChatFragment).commit();
    }
}
