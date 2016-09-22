package net.dearcode.candy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.dearcode.candy.model.MessageServiceConnection;
import net.dearcode.candy.view.MainFragment;

public class CandyActivity extends AppCompatActivity {
    private static final String TAG = "CandyMessage";
    private long id;
    private String user;
    private String pass;
    private TextView tvUserName;
    private TextView tvUserID;
    private SQLiteDatabase db;

    private CandyMessage candy = null;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static final int waitLogin = 1;
    private static final int waitRegister = 2;


    private void dbInit() {
        db = openOrCreateDatabase("candy.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user (id INTEGER, user TEXT, pass TEXT)");
        Cursor c = db.rawQuery("SELECT id, user,pass FROM user limit 1", null);
        if (c.moveToNext()) {
            id = c.getLong(0);
            user = c.getString(1);
            pass = c.getString(2);
        }
        c.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candy);

        dbInit();

        if ((candy = new MessageServiceConnection(CandyActivity.this).getConn()) == null) {
            Log.e(TAG, "bind service error");
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MainFragment.create(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "联系人";
                case 1:
                    return "会话";
                case 2:
                    return "朋友圈";
            }
            return null;
        }
    }
}