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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;

import net.dearcode.candy.controller.Contacts;
import net.dearcode.candy.controller.RPC;
import net.dearcode.candy.controller.SessionInfo;
import net.dearcode.candy.model.ServiceResponse;
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
    private int currentPageNum;
    private View[] rootViews = new View[3];

    private long friendLastModify;
    private long sessionLastModify;

    RecyclerView rvContacts;

    ArrayList<Session> sessions  = new ArrayList<>();
    ArrayList<User> users =  new ArrayList<>();

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
        users = Contacts.getContacts();
        sessions = SessionInfo.loadSessionList();
        Log.i("INFO", "onCreate:" + currentPageNum);
    }

    private void startChatActivity(boolean isGroup, long id) {
        Intent i = new Intent(getActivity(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("isGroup", isGroup);
        if (isGroup) b.putLong("gid", id);
        else b.putLong("uid", id);

        i.putExtras(b);
        startActivity(i);
        getActivity().finish();

    }


    @Override
    public void onResume() {
        super.onResume();

        if (friendLastModify != Base.friendLastModify && rvContacts != null) {
            //reload friend list
            users = Contacts.getContacts();
            friendLastModify = Base.friendLastModify;
            rvContacts.getAdapter().notifyDataSetChanged();
        }

        if (sessionLastModify != Base.sessionLastModify && rvSession != null) {
            //reload session list
            sessions = SessionInfo.loadSessionList();
            sessionLastModify = Base.sessionLastModify;
            rvSession.getAdapter().notifyDataSetChanged();
        }

    }

    RecyclerView rvSession;

    private void initContactsData(final View root) {
        rvSession = (RecyclerView) root.findViewById(R.id.fg_rv_contacts);
        rvSession.setAdapter(new RecyclerView.Adapter() {

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

    private void initContactsView(View root) {
        rvContacts = (RecyclerView) root.findViewById(R.id.fg_rv_contacts);
        RecyclerViewHeader header = (RecyclerViewHeader) root.findViewById(R.id.fg_rv_contacts_header);
        RelativeLayout rlSearch = (RelativeLayout) header.findViewById(R.id.fc_rl_search);
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(i, 0);
            }
        });

        rvContacts.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        rvContacts.setItemAnimator(new DefaultItemAnimator());
        header.attachTo(rvContacts);
    }

    private void initSessionView(View root) {
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.fg_rv_session);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        rv.setItemAnimator(new DefaultItemAnimator());
    }

    private void initControlView(View root) {
        account = Base.db.loadAccount();

        TextView tvUserName = (TextView) root.findViewById(R.id.fc_tv_user_name);
        TextView tvUserID = (TextView) root.findViewById(R.id.fc_tv_user_id);
        ImageView ivUserAvatar = (ImageView) root.findViewById(R.id.fc_iv_user_avatar);
        Button btnExit = (Button) root.findViewById(R.id.fc_btn_exit);

        if (TextUtils.isEmpty(account.getName())) {
            tvUserName.setText("未登录，点我登录吧");
            return ;
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



        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceResponse sr = new RPC() {
                    @Override
                    public ServiceResponse getResponse() throws Exception {
                        return Base.getService().logout();
                    }
                }.Call();
                if (sr.hasError()) {
                    Log.e(Common.LOG_TAG, "logout error:" + sr.getError());
                }
                Base.delAccount();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }


    private void initSessionData(final View root) {
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.fg_rv_session);

        rv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, null);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        if (sessions.get(position).isGroup()) {
                            startChatActivity(true, sessions.get(position).getGroup());
                        } else {
                            startChatActivity(false, sessions.get(position).getUser());
                        }
                    }
                });
                return new MyHolder(item);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MyHolder h = (MyHolder) holder;
                Session session = sessions.get(position);
                h.ivAvatar.setImageBitmap(session.getAvatarBitmap(getResources()));
                h.tvTitle.setText(session.getTitle());
                h.tvDate.setText(session.getDate());
                h.tvMsg.setText(session.getMsg());
                h.item.setTag(position);
            }

            @Override
            public int getItemCount() {
                return sessions.size();
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

        View rootView;

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

            default:
                rootView = inflater.inflate(R.layout.fragment_control, container, false);
                initControlView(rootView);
                return rootView;
        }
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