package net.dearcode.candy.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import net.dearcode.candy.Base;
import net.dearcode.candy.R;
import net.dearcode.candy.UserInfoActivity;
import net.dearcode.candy.controller.RPC;
import net.dearcode.candy.model.Event;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;
import net.dearcode.candy.util.Common;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 *  * Created by c-wind on 2016/9/28 18:12
 *  * mail：root@codecn.org
 *  
 */
public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        if (b == null) {
            Log.e(Common.LOG_TAG, "extras is null");
            return;
        }
        Message m = b.getParcelable("message");
        if (m == null) {
            Log.e(Common.LOG_TAG, "message is null");
            return;
        }

        if (m.getEvent() == Event.None) {
            if (m.isGroupMessage()) {
                Base.db.saveUserMessage(m.getId(), m.getGroup(), m.getFrom(), m.getMsg());
                Base.db.saveSession(m.getGroup(), true, m.getMsg());
            } else {
                Base.db.saveUserMessage(m.getId(), m.getFrom(), m.getFrom(), m.getMsg());
                Base.db.saveSession(m.getFrom(), false, m.getMsg());
            }
            //消息自己显示去
            Intent i = new Intent("net.dearcode.candy.chat");
            i.putExtras(b);
            context.sendBroadcast(i);
            return;
        }

        Base.db.saveSystemMessage(m.getId(), m.getEvent().ordinal(), m.getRelation().ordinal(), m.getGroup(), m.getFrom(), m.getMsg());
        switch (m.getEvent()) {
            case Friend:
                showFriendNotification(context, m);
                break;
            case Group:
                showFriendNotification(context, m);
                break;
        }


    }

    private void showNotification(Context ctx, Intent intent, String title, String content, String sub ) {
        NotificationManager manger = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        //Ticker是状态栏显示的提示
        builder.setTicker("Candy");
        //第一行内容  通常作为通知栏标题
        builder.setContentTitle(title);
        //第二行内容 通常是通知正文
        builder.setContentText(content);
        //第三行内容 通常是内容摘要什么的 在低版本机器上不一定显示
        builder.setSubText(sub);
        //ContentInfo 在通知的右侧 时间的下面 用来展示一些其他信息
        //builder.setContentInfo("2");
        //number设计用来显示同种通知的数量和ContentInfo的位置一样，如果设置了ContentInfo则number会被隐藏
        builder.setNumber(0);
        //可以点击通知栏的删除按钮删除
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Log.e(Common.LOG_TAG, "**********"+intent.getExtras().toString());
        //下拉显示的大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher));
        PendingIntent pIntent = PendingIntent.getActivity(ctx, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //点击跳转的intent
        builder.setContentIntent(pIntent);
        //通知默认的声音 震动 呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        manger.notify(0, notification);
    }


    private void showFriendNotification(Context ctx, final Message message) {
        ServiceResponse sr = new RPC() {
            @Override
            public ServiceResponse getResponse() throws Exception {
                return Base.getService().loadUserInfo(message.getFrom());
            }
        }.Call();
        if (sr.hasError()) {
            Log.e(Common.LOG_TAG, "loadUserInfo error:" + sr.getError());
            return;
        }
        User user = JSON.parseObject(sr.getData(), User.class);

        Intent intent = new Intent(ctx, UserInfoActivity.class);
        Bundle bundle = new Bundle();
        user.setToBundle(bundle);
        message.setToBundle(bundle);
        intent.putExtras(bundle);

        switch (message.getRelation()) {
            case ADD:
                showNotification(ctx, intent, "添加好友请求", "用户：" + user.getNickName(), "消息：" + message.getMsg());
            case CONFIRM:
                showNotification(ctx, intent, "添加好友成功", "用户：" + user.getNickName(), "");
            case REFUSE:
                showNotification(ctx, intent, "添加好友失败", "用户：" + user.getNickName(), "");

        }

        //showNotification(ctx, intent, "添加好友请求", "用户：" + user.getName(), "消息：" + message.getMsg());
    }
}

