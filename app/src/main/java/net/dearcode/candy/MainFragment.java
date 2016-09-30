package net.dearcode.candy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;

import net.dearcode.candy.controller.Contacts;
import net.dearcode.candy.controller.SessionInfo;
import net.dearcode.candy.model.Session;
import net.dearcode.candy.model.User;
import net.dearcode.candy.util.Common;
import net.dearcode.candy.view.ImageView;

import java.util.ArrayList;

/**
 *  * Created by c-wind on 2016/9/22 17:41
 *  * mail：root@codecn.org
 *  
 */
public class MainFragment extends Fragment {

    public static final String ARG_PAGE = "page_num";
    private User account;

    // 当前页
    private int currentPageNum;

    public MainFragment() {
    }

    public static MainFragment create(int pagerNum) {

        MainFragment myPageFrament = new MainFragment();
        Bundle arg = new Bundle();
        arg.putInt(ARG_PAGE, pagerNum);
        myPageFrament.setArguments(arg);

        return myPageFrament;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPageNum = getArguments().getInt(ARG_PAGE);
        Log.i("INFO", "onCreate:" + currentPageNum);
    }

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORWARD_SLASH = "/";

    private static String resourceIdToPath(Context context, int resourceId) {
        return ANDROID_RESOURCE + context.getPackageName() + FORWARD_SLASH + resourceId;
    }

    private void startChatActivity(boolean isGroup, long id) {
        Intent i = new Intent(getActivity(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("isGroup", isGroup);
        b.putLong("id", id);
        i.putExtras(b);
        startActivity(i);
        getActivity().finish();

    }

    private void initContactsData(final View root) {
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.fg_rv_contacts);
        rv.setAdapter(new RecyclerView.Adapter() {
            ArrayList<User> users = Contacts.getContacts();

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, null);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        startChatActivity(false, users.get(position).getID());
                    }
                });
                return new MyHolder(item);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                MyHolder h = (MyHolder) holder;
                h.ivAvatar.setImageBitmap(users.get(position).getAvatarBitmap(getResources()));
                h.tvName.setText(users.get(position).getName());
                h.item.setTag(position);
            }

            @Override
            public int getItemCount() {
                return users.size();
            }

            class MyHolder extends RecyclerView.ViewHolder {
                View item;
                ImageView ivAvatar;
                TextView tvName;

                public MyHolder(View item) {
                    super(item);
                    this.item = item;
                    ivAvatar = (ImageView) item.findViewById(R.id.ui_iv_avatar);
                    tvName = (TextView) item.findViewById(R.id.ui_tv_name);
                }
            }
        });

    }

    private View[] rootViews = new View[3];

    private void initContactsView(View root) {
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.fg_rv_contacts);
        RecyclerViewHeader header = (RecyclerViewHeader) root.findViewById(R.id.fg_rv_contacts_header);
        RelativeLayout rlSearch = (RelativeLayout) header.findViewById(R.id.fc_rl_search);
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(i, 0);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        rv.setItemAnimator(new DefaultItemAnimator());
        header.attachTo(rv);
    }

    private void initSessionView(View root) {
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.fg_rv_session);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        rv.setItemAnimator(new DefaultItemAnimator());
    }


    ArrayList<Session> data;

    private void initSessionData(final View root) {
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.fg_rv_session);
        data = SessionInfo.loadSessionList();

        rv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, null);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        if (data.get(position).isGroup()) {
                            startChatActivity(true, data.get(position).getGroup());
                        } else {
                            startChatActivity(false, data.get(position).getUser());
                        }
                    }
                });
                return new MyHolder(item);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MyHolder h = (MyHolder) holder;
                h.ivAvatar.setImageBitmap(data.get(position).getAvatarBitmap(getResources()));
                h.tvTitle.setText(data.get(position).getTitle());
                h.tvDate.setText(data.get(position).getDate());
                h.tvMsg.setText(data.get(position).getMsg());
                h.item.setTag(position);
            }

            @Override
            public int getItemCount() {
                return data.size();
            }

            class MyHolder extends RecyclerView.ViewHolder {
                View item;
                ImageView ivAvatar;
                TextView tvTitle;
                TextView tvDate;
                TextView tvMsg;

                public MyHolder(View root) {
                    super(root);
                    this.item = root;
                    this.ivAvatar = (ImageView) root.findViewById(R.id.si_iv_avatar);
                    this.tvTitle = (TextView) root.findViewById(R.id.si_tv_title);
                    this.tvDate = (TextView) root.findViewById(R.id.si_tv_date);
                    this.tvMsg = (TextView) root.findViewById(R.id.si_tv_msg);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(Common.LOG_TAG, "onCreateView:" + currentPageNum);

        View rootView = null;
        RecyclerView rv = null;

        switch (currentPageNum) {
            case 0:
                if (rootViews[0] == null) {
                    rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
                    initContactsView(rootView);
                    initContactsData(rootView);
                    rootViews[0] = rootView;
                }
                return rootViews[0];
            case 1:
                if (rootViews[1] == null) {
                    rootView = inflater.inflate(R.layout.fragment_session, container, false);
                    initSessionView(rootView);
                    initSessionData(rootView);
                    rootViews[1] = rootView;
                }
                return rootViews[1];

            case 2:
                rootView = inflater.inflate(R.layout.fragment_candy, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText("这里要显示朋友圈");
                return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_control, container, false);
        TextView tvUserName = (TextView) rootView.findViewById(R.id.fc_tv_user_name);
        TextView tvUserID = (TextView) rootView.findViewById(R.id.fc_tv_user_id);
        ImageView ivUserAvatar = (ImageView) rootView.findViewById(R.id.fc_iv_user_avatar);

        account = Base.db.loadAccount();

        if (TextUtils.isEmpty(account.getName())) {
            tvUserName.setText("未登录，点我登录吧");
            return rootView;
        }

        tvUserName.setText(account.getName());
        tvUserID.setText("ID:" + account.getID());
        byte[] avatar = account.getAvatar();
        Bitmap bitmap;
        if (avatar != null) {
            bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_da);
        }
        ivUserAvatar.setImageBitmap(bitmap);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("INFO", "MyFragment Destroy...");
    }

}