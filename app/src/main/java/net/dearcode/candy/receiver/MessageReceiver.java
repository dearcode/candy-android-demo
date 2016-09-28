package net.dearcode.candy.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import net.dearcode.candy.R;

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
        builder.setContentTitle("添加好友请求");
        Bundle b = intent.getExtras();
        builder.setContentText("来自:" + b.getLong("id") + ":" + b.getString("msg"));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);
        /*
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_template_customer);
        remoteViews.setTextViewText(R.id.title,"Notification");
        remoteViews.setTextViewText(R.id.text,"song"+index);
        if(command==CommandNext){
            remoteViews.setImageViewResource(R.id.btn1,R.drawable.ic_pause_white);
        }else if(command==CommandPlay){
            if(playerStatus==StatusStop){
                remoteViews.setImageViewResource(R.id.btn1,R.drawable.ic_pause_white);
            }else{
                remoteViews.setImageViewResource(R.id.btn1,R.drawable.ic_play_arrow_white_18dp);
            }
        }
        Intent Intent1 = new Intent(this,MediaService.class);
        Intent1.putExtra("command",CommandPlay);
        //getService(Context context, int requestCode, @NonNull Intent intent, @Flags int flags)
        //不同控件的requestCode需要区分开 getActivity broadcoast同理
        PendingIntent PIntent1 =  PendingIntent.getService(this,5,Intent1,0);
        remoteViews.setOnClickPendingIntent(R.id.btn1,PIntent1);

        Intent Intent2 = new Intent(this,MediaService.class);
        Intent2.putExtra("command",CommandNext);
        PendingIntent PIntent2 =  PendingIntent.getService(this,6,Intent2,0);
        remoteViews.setOnClickPendingIntent(R.id.btn2,PIntent2);

        Intent Intent3 = new Intent(this,MediaService.class);
        Intent3.putExtra("command",CommandClose);
        PendingIntent PIntent3 =  PendingIntent.getService(this,7,Intent3,0);
        remoteViews.setOnClickPendingIntent(R.id.btn3,PIntent3);

        builder.setContent(remoteViews);
        */
        Notification notification = builder.build();
        manger.notify(0, notification);
    }
}

