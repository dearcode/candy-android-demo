package net.dearcode.candy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.util.Common;
import net.dearcode.candy.view.ImageView;

public class UserInfoActivity extends AppCompatActivity {
    TextView tvName;
    TextView tvID;
    TextView tvNickname;
    Button btnAddFriend;
    ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        ivAvatar = (ImageView) findViewById(R.id.au_iv_avatar);
        tvName = (TextView) findViewById(R.id.au_tv_name);
        tvID = (TextView) findViewById(R.id.au_tv_userid);
        tvNickname = (TextView) findViewById(R.id.au_tv_nickname);
        btnAddFriend = (Button) findViewById(R.id.au_btn_add_friend);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        final long id = b.getLong("id");

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
        tvID.setText("ID:" + id);


        if (CandyActivity.isFriend(id)) {
            btnAddFriend.setVisibility(View.GONE);
        } else {
            btnAddFriend.setVisibility(View.VISIBLE);
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ServiceResponse sr = CandyActivity.getCandy().addFriend(id, "请求添加好友");
                        if (sr.hasError()) {
                            Log.e(Common.LOG_TAG, "addFriend error:" + sr.getError());
                            return;
                        }
                        if (sr.getData().equals("true")) {
                            CandyActivity.addFriend(id);
                            Log.e(Common.LOG_TAG, "addFriend success, id:" + id);
                            Snackbar.make(v, "好友验证通过, 现在可以聊天了", Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        Snackbar.make(v, "等待好友验证通过吧", Snackbar.LENGTH_LONG).show();
                    } catch (RemoteException e) {
                        Log.e(Common.LOG_TAG, "addFriend error:" + e.getMessage());
                    }
                }
            });
        }

    }

}
