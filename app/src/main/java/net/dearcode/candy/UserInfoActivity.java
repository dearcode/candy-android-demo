package net.dearcode.candy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.dearcode.candy.controller.RPC;
import net.dearcode.candy.model.Relation;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.util.Common;
import net.dearcode.candy.view.ImageView;

public class UserInfoActivity extends AppCompatActivity {
    TextView tvName;
    TextView tvID;
    TextView tvNickname;
    TextView tvMsg;
    Button btnAddFriend;
    Button btnConfirm;
    Button btnRefuse;
    Button btnChat;
    ImageView ivAvatar;
    long uid;

    private void showConfirmFriend() {
        btnChat.setVisibility(View.VISIBLE);
    }

    private void showRefuseFriend(String msg) {
        btnAddFriend.setVisibility(View.VISIBLE);
        tvMsg.setText(msg);
    }

    private void showAddFriend(String msg) {
        btnRefuse.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.VISIBLE);
        tvMsg.setText(msg);

        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceResponse sr = new RPC() {
                    public ServiceResponse getResponse() throws Exception {
                        return Base.getService().RefuseFriend(uid, "滚");
                    }
                }.Call();
                if (sr.hasError()) {
                    Snackbar.make(v, sr.getError(), Snackbar.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(v, "已拒绝", Snackbar.LENGTH_LONG).show();
                backToMain();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceResponse sr = new RPC() {
                    public ServiceResponse getResponse() throws Exception {
                        return Base.getService().ConfirmFriend(uid);
                    }
                }.Call();
                if (sr.hasError()) {
                    Snackbar.make(v, sr.getError(), Snackbar.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(v, "已添加，去聊天吧", Snackbar.LENGTH_LONG).show();
                Base.db.saveFriend(uid);
                Base.touchFriendList();
                backToMain();
            }
        });
    }

    private void showFriendInfo() {
        if (Base.db.isFriend(uid)) {
            btnAddFriend.setVisibility(View.GONE);
            return;
        }

        btnAddFriend.setVisibility(View.VISIBLE);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceResponse sr = new RPC() {
                    @Override
                    public ServiceResponse getResponse() throws Exception {
                        return Base.getService().addFriend(uid, "加我为好友吧");
                    }
                }.Call();
                if (sr.hasError()) {
                    Log.e(Common.LOG_TAG, "addFriend error:" + sr.getError());
                    return;
                }
                Snackbar.make(v, "等待好友验证通过吧", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void initView() {
        ivAvatar = (ImageView) findViewById(R.id.au_iv_avatar);
        tvName = (TextView) findViewById(R.id.au_tv_name);
        tvID = (TextView) findViewById(R.id.au_tv_userid);
        tvNickname = (TextView) findViewById(R.id.au_tv_nickname);
        tvMsg = (TextView) findViewById(R.id.au_tv_msg);
        btnAddFriend = (Button) findViewById(R.id.au_btn_add_friend);
        btnConfirm = (Button) findViewById(R.id.au_btn_confirm);
        btnRefuse = (Button) findViewById(R.id.au_btn_refuse);
        btnChat = (Button) findViewById(R.id.au_btn_open_chat);

        btnAddFriend.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
        btnRefuse.setVisibility(View.GONE);
        btnChat.setVisibility(View.GONE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        initView();

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b == null) {
            b = savedInstanceState;
        }

        Log.e(Common.LOG_TAG, "------"+b.toString());
        uid = b.getLong("uid");

        byte[] avatar = b.getByteArray("avatar");
        Bitmap bitmap;
        if (avatar != null) {
            bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_da);
        }
        ivAvatar.setImageBitmap(bitmap);

        tvNickname.setText(b.getString("nickname"));
        tvName.setText(b.getString("name"));
        tvID.setText("ID:" + uid);

        Relation relation = Relation.values()[b.getInt("relation")];
        switch (relation) {
            case ADD:
                showAddFriend(b.getString("msg"));
                break;
            case CONFIRM:
                showConfirmFriend();
                break;
            case REFUSE:
                showRefuseFriend(b.getString("msg"));
                break;
            default:
                showFriendInfo();
        }


    }

    private void backToMain() {
        Intent i = new Intent(UserInfoActivity.this, CandyActivity.class);
        Bundle b = new Bundle();
        b.putInt("from", CandyActivity.FromUserinfo);
        i.putExtras(b);
        startActivity(i);
        finish();
    }

}
