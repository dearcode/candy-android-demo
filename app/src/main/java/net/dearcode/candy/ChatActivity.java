package net.dearcode.candy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.dearcode.candy.controller.RPC;
import net.dearcode.candy.controller.UserInfo;
import net.dearcode.candy.model.Event;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.model.Relation;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;
import net.dearcode.candy.util.Common;

import java.util.ArrayList;

/**
 *  * Created by c-wind on 2016/9/21 15:47
 *  * mail：root@codecn.org
 *  
 */
public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvTalk;
    private EditText etMessage;
    private Button btnSend;
    private ImageView ivFace;

    private long group;
    private long user;

    private boolean inGroup;

    private ArrayList<Message> msgs;
    private MyBroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //加载数据
        Bundle b = getIntent().getExtras();
        if (b == null) {
            b = savedInstanceState;
        }
        initChatMessage(b);

        //绑定控件
        initChatView();

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("net.dearcode.candy.chat");
        registerReceiver(myBroadcastReceiver, filter);
    }

    private void initChatMessage(Bundle b) {
        inGroup = b.getBoolean("isGroup");
        if (inGroup) {
            group = b.getLong("gid");
            msgs = Base.db.loadGroupMessage(group);
        } else {
            user = b.getLong("uid");
            msgs = Base.db.loadUserMessage(user);
        }
    }

    private void initChatView() {
        rvTalk = (RecyclerView) findViewById(R.id.ac_rv_talk);
        etMessage = (EditText) findViewById(R.id.ac_et_message);
        btnSend = (Button) findViewById(R.id.ac_btn_send);
        ivFace = (ImageView) findViewById(R.id.ac_iv_face);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String msg = Common.GetString(etMessage.getText());
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(ChatActivity.this, "没消息发送啥？？？", Toast.LENGTH_SHORT).show();
                    return;
                }

                ServiceResponse sr = new RPC() {
                    @Override
                    public ServiceResponse getResponse() throws Exception {
                        return Base.getService().sendMessage(group, user, msg);
                    }
                }.Call();
                if (sr.hasError()) {
                    Log.e(Common.LOG_TAG, "sendMessage error:" + sr.getError());
                    Toast.makeText(ChatActivity.this, sr.getError(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (inGroup) {
                    Base.db.saveGroupMessage(sr.getId(), group, Base.account.getID(), 0, msg);
                    Base.db.saveSession(group, true, msg);

                } else {
                    Base.db.saveUserMessage(sr.getId(), user, Base.account.getID(), msg);
                    Base.db.saveSession(user, false, msg);
                }
                Log.e(Common.LOG_TAG, "new Message id:" + sr.getId() + " group:" + group + " user:" + user + " msg:" + msg);
                etMessage.setText("");
                msgs.add(new Message(Event.None, Relation.DEL, sr.getId(), group, Base.account.getID(), user, msg));
                rvTalk.getAdapter().notifyDataSetChanged();
                rvTalk.smoothScrollToPosition(msgs.size() - 1);
            }
        });


        rvTalk.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View item;
                Log.e(Common.LOG_TAG, "viewType:" + viewType);
                if (viewType == 1) {
                    item = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_local, null);
                } else {
                    item = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_remote, null);
                }
                return new MyHolder(item);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MyHolder h = (MyHolder) holder;
                Message m = msgs.get(position);
                User user = UserInfo.getUserInfo(m.getFrom());
                h.ivAvatar.setImageBitmap(user.getAvatarBitmap(getResources()));
                h.tvUser.setText(user.getNickName());
                h.tvDate.setText(m.getDate());
                h.tvMsg.setText(m.getMsg());
            }

            @Override
            public int getItemViewType(int position) {
                Log.e(Common.LOG_TAG, "local message:" + (msgs.get(position).getFrom() == Base.account.getID()));
                if (msgs.get(position).getFrom() == Base.account.getID()) return 1;
                return 0;
            }

            @Override
            public int getItemCount() {
                return msgs.size();
            }

            class MyHolder extends RecyclerView.ViewHolder {
                ImageView ivAvatar;
                TextView tvUser;
                TextView tvDate;
                TextView tvMsg;

                public MyHolder(View root) {
                    super(root);
                    ivAvatar = (ImageView) root.findViewById(R.id.ci_iv_avatar);
                    tvUser = (TextView) root.findViewById(R.id.ci_tv_name);
                    tvDate = (TextView) root.findViewById(R.id.ci_tv_date);
                    tvMsg = (TextView) root.findViewById(R.id.ci_tv_msg);
                }
            }
        });

        rvTalk.setLayoutManager(new LinearLayoutManager(this));
        rvTalk.setItemAnimator(new DefaultItemAnimator());

        if (msgs.size() > 0) rvTalk.smoothScrollToPosition(msgs.size() - 1);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(ChatActivity.this, CandyActivity.class);
            Bundle b = new Bundle();
            b.putInt("from", CandyActivity.FromChat);
            i.putExtras(b);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            if (b == null) {
                Log.e(Common.LOG_TAG, "bundle is null");
                return;
            }
            Message m = b.getParcelable("message");
            if (m == null) {
                Log.e(Common.LOG_TAG, "message is null");
                return;
            }


            if ((m.isGroupMessage() && m.getGroup() == group) || (!m.isGroupMessage() && m.getFrom() == user)) {
                msgs.add(m);
                rvTalk.getAdapter().notifyDataSetChanged();
                rvTalk.smoothScrollToPosition(msgs.size() - 1);
            }
        }
    }

}

