package net.dearcode.candy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import net.dearcode.candy.model.User;

public class CandyActivity extends AppCompatActivity {
    private TextView tvUserName;
    private TextView tvUserID;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static final int FromLogin = 1;
    public static final int FromRegister = 2;
    public static final int FromMain = 3;
    public static final int FromChat = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candy);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b == null) {
            b = savedInstanceState;
        }
        onStart(b);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void onStart(Bundle b) {
        int from = b.getInt("from");
        switch (from) {
            case FromLogin:
                if (b.getBoolean("Redirect") && b.getString("RedirectTo").equals("Register")) {
                    Intent i = new Intent(CandyActivity.this, RegisterActivity.class);
                    startActivity(i);
                    return;
                }
                Base.updateAccount(b.getLong("id"), b.getString("user"), b.getString("password"));
                break;
            case FromRegister:
                if (b.getBoolean("Redirect") && b.getString("RedirectTo").equals("Login")) {
                    Intent i = new Intent(CandyActivity.this, LoginActivity.class);
                    startActivity(i);
                    return;
                }
                break;
            default:
                break;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final String[] items = {"联系人", "会话", "朋友圈", "设置"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MainFragment.create(position);
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return items[position];
        }
    }


    private long lastTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - lastTime) > 2000) {
                lastTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
