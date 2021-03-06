/*
 DConnectSettingPageFragmentActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.ui.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import org.deviceconnect.android.R;
import org.deviceconnect.android.ui.adapter.DConnectFragmentPagerAdapter;
import org.deviceconnect.android.ui.adapter.DConnectPageCreater;

/**
 * デバイスプラグイン設定画面用 ベースフラグメントアクティビティ.
 * @author NTT DOCOMO, INC.
 */
public abstract class DConnectSettingPageFragmentActivity extends FragmentActivity implements
        DConnectPageCreater<Fragment> {
    
    /**
     * ページ用のビューページャー.
     */
    private ViewPager mViewPager;

    /**
     * ViewPagerを持つレイアウトを自動的に設定する.
     * サブクラスでオーバーライドする場合は setContentView を<strong>実行しないこと</strong>。
     * 
     * @param savedInstanceState パラメータ
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        mViewPager = (ViewPager) findViewById(R.id.setting_pager);
        mViewPager.setAdapter(new DConnectFragmentPagerAdapter(getSupportFragmentManager(), this));

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
            getActionBar().setTitle(R.string.activity_setting_page_title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * ViewPagerを取得する.
     * 
     * @return ViewPagerのインスタンス
     */
    protected ViewPager getViewPager() {
        return mViewPager;
    }
}
