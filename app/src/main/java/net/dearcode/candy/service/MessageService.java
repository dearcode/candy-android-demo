package net.dearcode.candy.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import net.dearcode.candy.CandyMessage;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;

import java.io.IOException;

import go.candy.Candy;
import go.candy.CandyClient;
import go.candy.FriendList;
import go.candy.MessageHandler;
import go.candy.UserInfo;


/**
 * Created by Administrator on 2016/9/12.
 */
public class MessageService extends Service {
    private static final String TAG = "CandyMessage";
    private MessageClient msgClient;

    private class MessageClient  implements MessageHandler {

        @Override
        public void onError(String s) {
            Log.e(TAG, "onError");

        }

        @Override
        public void onUnHealth(String s) {
            Log.e(TAG, "onUnHealth");

        }

        @Override
        public  void onRecv(long id, long method, long group, long from, long to, String msg) {
            Log.e(TAG, "onRecv");
        }
    }

    private CandyMessage.Stub serviceBinder = new CandyMessage.Stub() {
        @Override
        public ServiceResponse loadUserInfo(long id) throws RemoteException {
            Log.e(TAG, "will loadInfo user:" + id);
            ServiceResponse sr = new ServiceResponse();
            try {
                String data = client.getUserInfoByID(id);
                Log.e(TAG, "getUserInfo ok , info:" + data);
                sr.setData(data);
            } catch (Exception e) {
                Log.e(TAG, "register error:" + e.getMessage());
                sr.setError(e.getMessage());
            }

            return sr;
        }

        public ServiceResponse register(String user, String pass) throws RemoteException {
            Log.e(TAG, "will register user:" + user + " pass:" + pass);
            ServiceResponse sr = new ServiceResponse();
            try {
                long id = client.register(user, pass);
                Log.e(TAG, "register ok , id:" + id);
                sr.setId(id);
            } catch (Exception e) {
                Log.e(TAG, "register error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        public ServiceResponse loadFriendList() throws RemoteException {
            ServiceResponse sr = new ServiceResponse();
            try {
                sr.setData(client.loadFriendList());
                Log.e(TAG, "loadFriendList ok recv:" + sr.getData());
            } catch (Exception e) {
                Log.e(TAG, "loadFriendList error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public ServiceResponse login(String user, String pass) throws RemoteException {
            ServiceResponse sr = new ServiceResponse();
            try {
                long id = client.login(user, pass);
                Log.e(TAG, "login ok , id:" + id);
                sr.setId(id);
            } catch (Exception e) {
                Log.e(TAG, "login error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public long[] searchUser(String user) throws RemoteException {
            return new long[0];
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public IBinder onBind(Intent i) {
        Log.e(TAG, "============> TestService.onBind");
        return serviceBinder;
    }

    @Override
    public boolean onUnbind(Intent i) {
        Log.e(TAG, "============> TestService.onUnbind");
        return false;
    }

    @Override
    public void onRebind(Intent i) {
        Log.e(TAG, "============> TestService.onRebind");
    }

    CandyClient client;

    @Override
    public void onCreate() {
        msgClient = new MessageClient();
        try {
            client = Candy.newCandyClient("candy.dearcode.net:9000", msgClient);
            client.start();
        }catch (Exception e) {
            Log.e(TAG, "start candy client error:"+e.getMessage());
            try {
                client.stop();
            }catch (Exception err) {
                Log.e(TAG, "stop candy client error:"+err.getMessage());
            }
        }
        Log.e(TAG, "onCreate connect canndy success");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(TAG, "============> TestService.onStart");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "============> TestService.onDestroy");
    }

}
