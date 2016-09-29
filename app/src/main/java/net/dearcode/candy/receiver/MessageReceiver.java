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
import net.dearcode.candy.CandyActivity;
import net.dearcode.candy.R;
import net.dearcode.candy.UserInfoActivity;
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
        showAddFriendNotification(context, intent);
    }


    private void showAddFriendNotification(Context ctx, Intent intent) {
        NotificationManager manger = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        Bundle b = intent.getExtras();

        ServiceResponse sr = new ServiceResponse();
        try {
            sr = Base.getService().loadUserInfo(b.getLong("from"));
        } catch (RemoteException e) {
            Log.e(Common.LOG_TAG, e.getMessage());
        }

        if (sr.hasError()) {
            Log.e(Common.LOG_TAG, "loadUserInfo error:" + sr.getError());
            return;
        }
        User user = JSON.parseObject(sr.getData(), User.class);


        //Ticker是状态栏显示的提示
        builder.setTicker("Candy");
        //第一行内容  通常作为通知栏标题
        builder.setContentTitle("添加好友请求");
        //第二行内容 通常是通知正文
        builder.setContentText("用户："+user.getName());
        //第三行内容 通常是内容摘要什么的 在低版本机器上不一定显示
        builder.setSubText("消息："+b.getString("msg"));
        //ContentInfo 在通知的右侧 时间的下面 用来展示一些其他信息
        //builder.setContentInfo("2");
        //number设计用来显示同种通知的数量和ContentInfo的位置一样，如果设置了ContentInfo则number会被隐藏
        builder.setNumber(0);
        //可以点击通知栏的删除按钮删除
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //下拉显示的大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher));
        Intent i = new Intent(ctx, UserInfoActivity.class);
        user.setBundle(b);
        b.putString("Action", "AddFriend");
        i.putExtras(b);
        PendingIntent pIntent = PendingIntent.getActivity(ctx, 1, i, 0);
        //点击跳转的intent
        builder.setContentIntent(pIntent);
        //通知默认的声音 震动 呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        manger.notify(0, notification);


    }
}

