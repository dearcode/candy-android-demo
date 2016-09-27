package net.dearcode.candy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.dearcode.candy.model.MessageServiceConnection;
import net.dearcode.candy.model.User;
import net.dearcode.candy.view.MainFragment;

public class CandyActivity extends AppCompatActivity {
    private static final String TAG = "CandyMessage";
    private long id;
    private String avatar;
    private String user;
    private String pass;
    private String nickname;
    private TextView tvUserName;
    private TextView tvUserID;
    private SQLiteDatabase db;

    private static MessageServiceConnection conn ;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static final int waitLogin = 1;
    private static final int waitRegister = 2;

    public User getUser() {
        return new User(id, avatar, user, nickname);
    }

    private void dbInit() {
        db = openOrCreateDatabase("candy.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user (id INTEGER, user TEXT, pass TEXT, nickname TEXT, avatar TEXT)");
        Cursor c = db.rawQuery("SELECT id, user,pass,avatar FROM user limit 1", null);
        if (c.moveToNext()) {
            id = c.getLong(0);
            user = c.getString(1);
            pass = c.getString(2);
//            nickname = c.getString(3);
 //           avatar = c.getString(4);
        }
        c.close();
    }

    public static  CandyMessage getCandy() {
        return conn.getCandy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conn = new MessageServiceConnection(CandyActivity.this);
        setContentView(R.layout.activity_candy);

        dbInit();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //如果没有保存的账号，就让他登录
        if (user == null || pass == null || user.isEmpty() || pass.isEmpty()) {
            Intent i = new Intent(CandyActivity.this, LoginActivity.class);
            startActivityForResult(i, waitLogin);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        Bundle b = data.getExtras();
        long id;

        switch (requestCode) {
            case waitLogin:
                if (b.getBoolean("Redirect") && b.getString("RedirectTo").equals("Register")) {
                    Intent i = new Intent(CandyActivity.this, RegisterActivity.class);
                    startActivityForResult(i, waitRegister);
                    return;
                }
                user = b.getString("user");
                pass = b.getString("pass");
                id = b.getLong("id");
                db.execSQL("insert into user (id, user,pass) values (?,?,?)", new Object[]{id, b.getString("user"), b.getString("pass")});
                break;
            case waitRegister:
                if (b.getBoolean("Redirect") && b.getString("RedirectTo").equals("Login")) {
                    Intent i = new Intent(CandyActivity.this, LoginActivity.class);
                    startActivityForResult(i, waitLogin);
                    return;
                }
                Intent i = new Intent(CandyActivity.this, LoginActivity.class);
                i.putExtras(b);
                startActivityForResult(i, waitLogin);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_candy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent bintent = new Intent(CandyActivity.this, LoginActivity.class);
            startActivityForResult(bintent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
