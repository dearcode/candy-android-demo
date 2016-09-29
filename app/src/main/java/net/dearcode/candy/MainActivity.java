package net.dearcode.candy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;
import net.dearcode.candy.util.Common;

public class MainActivity extends Activity {

    TextView tvState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvState = (TextView) findViewById(R.id.am_tv_state);
        handler.postDelayed(runnable, 500);
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvState.setText((String) msg.obj);
            Intent i = null;
            switch (msg.what) {
                case 0:
                    Log.e(Common.LOG_TAG, "update msg:" + (String) msg.obj);
                    return;
                case 1:
                    i = new Intent(MainActivity.this, CandyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle b = new Bundle();
                    b.putInt("from", CandyActivity.FromMain);
                    i.putExtras(b);
                    Log.e(Common.LOG_TAG, "start candy ok");
                    break;
                case 2:
                    i = new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.e(Common.LOG_TAG, "start login ok");
                    break;
                case 3:
                    Toast.makeText(MainActivity.this, "连接服务端失败，服务不可用", Toast.LENGTH_LONG).show();
                    return;
            }
            MainActivity.this.startActivity(i);
            MainActivity.this.finish();
            handler.removeCallbacks(runnable);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(Common.LOG_TAG, "destroy");
    }

    private static final int StateInit = 1;
    private static final int StateConnectServer = 2;
    private static final int StateLoadUser = 3;
    private static final int StateLogin = 4;
    private static final int StateOver = 5;
    User u;
    int state = StateInit;
    private Runnable runnable = new Runnable() {

        public void run() {
            Message message = new Message();
            message.what = 0;

            Log.e(Common.LOG_TAG, "state:" + state);

            switch (state) {
                case StateInit:
                    if (Base.getService() == null) {
                        message.obj = "正在连接服务中...";
                    } else {
                        state = StateConnectServer;
                        message.obj = "正在连接服务端...";
                    }
                    break;
                case StateConnectServer:
                    CandyMessage candy = Base.getService();
                    try {
                        ServiceResponse sr = candy.connect();
                        if (sr.hasError()) {
                            message.obj = sr.getError();
                            Log.e(Common.LOG_TAG, "connect server error:" + sr.getError());
                            state = StateOver;
                            message.what = 3;
                            break;
                        }
                    } catch (RemoteException e) {
                        message.obj = e.getMessage();
                        Log.e(Common.LOG_TAG, "connect server error:" + e.getMessage());
                        message.what = 3;
                        state = StateOver;
                    }
                    Log.e(Common.LOG_TAG, "connect server success");
                    state = StateLoadUser;
                    message.obj = "加载用户信息...";
                    break;
                case StateLoadUser:
                    u = Base.db.loadUserInfo();
                    if (u == null) {
                        message.obj = "未发现用户，自己登录吧";
                        message.what = 2;
                        state = StateOver;
                        break;
                    }
                    message.obj = "用户：" + u.getName() + " 正在登录";
                    state = StateLogin;
                case StateLogin:
                    try {
                        ServiceResponse sr = Base.getService().login(u.getName(), u.getPassword());
                        if (sr.hasError()) {
                            message.obj = sr.getError();
                            message.what = 2;
                            state = StateOver;
                            break;
                        }
                    } catch (RemoteException e) {
                        message.obj = e.getMessage();
                        message.what = 2;
                        state = StateOver;
                        break;
                    }

                    message.what = 1;
                    message.obj = "登录成功";
                    state = StateOver;
                    break;
            }
            handler.sendMessage(message);

            if (state != StateOver)
                handler.postDelayed(this, 500);
        }
    };

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
