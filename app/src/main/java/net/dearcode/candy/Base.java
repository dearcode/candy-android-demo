package net.dearcode.candy;

import android.accounts.Account;
import android.app.Application;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import net.dearcode.candy.controller.DB;
import net.dearcode.candy.controller.ServiceBinder;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;
import net.dearcode.candy.util.Common;

/**
 *  * Created by c-wind on 2016/9/29 10:56
 *  * mail：root@codecn.org
 *  
 */
public class Base extends Application {
    public static DB db;
    public static User account;
    private static ServiceBinder binder;

    public static void updateAccount(long id, String name, String password) {
        db.saveAccount(id, name, password);
        account = db.loadAccount();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DB(this);
        binder = new ServiceBinder(this);
        account = db.loadAccount();
        Log.e(Common.LOG_TAG, "onCreate bind");

    }

    @Override
    public void onTerminate() {
        Log.e(Common.LOG_TAG, "onTerminate");
        super.onTerminate();
        binder.Disconnect();
    }

    public static CandyMessage getService() {
        return binder.getCandy();
    }

}
