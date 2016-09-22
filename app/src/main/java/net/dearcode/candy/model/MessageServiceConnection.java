package net.dearcode.candy.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import net.dearcode.candy.CandyMessage;
import net.dearcode.candy.service.MessageService;

/**
 *  * Created by c-wind on 2016/9/21 15:47
 *  * mail：root@codecn.org
 *  
 */
public class MessageServiceConnection implements ServiceConnection {
    private CandyMessage conn = null;
    private Context ctx;

    public MessageServiceConnection(Context ctx) {
        this.ctx = ctx;
        Intent i = new Intent(ctx, MessageService.class);
        ctx.bindService(i, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        conn = CandyMessage.Stub.asInterface(iBinder);
    }

    public CandyMessage getConn() {
        return this.conn;
    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        conn = null;
    }

    public void Disconnect() {
        ctx.unbindService(this);
    }
}
