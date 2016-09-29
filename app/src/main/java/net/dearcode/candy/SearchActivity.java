package net.dearcode.candy;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;
import net.dearcode.candy.model.UserList;
import net.dearcode.candy.util.Common;
import net.dearcode.candy.view.ImageView;

import java.util.ArrayList;

/**
 *  * Created by c-wind on 2016/9/21 15:47
 *  * mail：root@codecn.org
 *  
 */
public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "Candy";
    private SearchView svUser;
    private ListView lvUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        svUser = (SearchView) findViewById(R.id.as_sv_user);
        lvUsers = (ListView) findViewById(R.id.as_lv_users);
        svUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return search(query, false);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return search(newText, true);
            }
        });
    }

    //TODO 查本地数据库
    private ArrayList<User> searchLocal(String name) {
        ArrayList<User> list = new ArrayList<>();
        return list;
    }

    private ArrayList<User> searchRemote(String name) {
        ArrayList<User> list = new ArrayList<>();
        try {
            ServiceResponse sr = Base.getService().searchUser(name);
            if (sr.hasError()) {
                Log.e(Common.LOG_TAG, "search error:" + sr.getError());
                return list;
            }
            UserList users = JSON.parseObject(sr.getData(), UserList.class);
            list = users.getUsers();
        } catch (RemoteException e) {
            Log.e(Common.LOG_TAG, "search error:" + e.getMessage());
        }
        return list;
    }

    private class MyAdp extends BaseAdapter {
        private ArrayList<User> users = new ArrayList<>();

        public MyAdp(ArrayList<User> users) {
            this.users = users;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            class Holder {
                ImageView ivAvater;
                TextView tvName;
            }

            Holder h;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, null);
                h = new Holder();
                h.ivAvater = (ImageView) convertView.findViewById(R.id.ui_iv_avatar);
                h.tvName = (TextView) convertView.findViewById(R.id.ui_tv_name);
                convertView.setTag(h);
            } else {
                h = (Holder) convertView.getTag();
            }

            h.ivAvater.setImageBitmap(users.get(position).getAvatarBitmap(getResources()));
            h.tvName.setText(users.get(position).getName());
            return convertView;
        }
    }

    private boolean search(String name, boolean local) {
        ArrayList<User> list;
        if (local) {
            list = searchLocal(name);
        } else {
            list = searchRemote(name);
        }

        final ArrayList<User> users = list;
        if (users.size() == 0) {
            if (!local) {
                Toast.makeText(this, "未找到用户", Toast.LENGTH_LONG).show();
            }
            return false;
        }

        lvUsers.setAdapter(new MyAdp(users));
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User u = users.get(position);
                Intent i = new Intent(SearchActivity.this, UserInfoActivity.class);
                Bundle b = new Bundle();
                b.putString("name", u.getName());
                b.putString("nickname", u.getNickName());
                b.putLong("id", u.getID());
                b.putByteArray("nickname", u.getAvatar());
                b.putString("From", "Search");
                i.putExtras(b);
                startActivityForResult(i, 0);
            }
        });
        return true;
    }

}

