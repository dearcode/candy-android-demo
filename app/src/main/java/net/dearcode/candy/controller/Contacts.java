package net.dearcode.candy.controller;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import net.dearcode.candy.Base;
import net.dearcode.candy.CandyActivity;
import net.dearcode.candy.model.FriendList;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;
import net.dearcode.candy.util.Common;

import java.util.ArrayList;

/**
 *  * Created by c-wind on 2016/9/26 16:50
 *  * mail：root@codecn.org
 *  
 */
public class Contacts {
    public static ArrayList<User> getContacts() {
        ArrayList<User> users = new ArrayList<>();
        ServiceResponse sr = new RPC() {
            public ServiceResponse getResponse() throws Exception {
                return Base.getService().loadFriendList();
            }
        }.Call();
        if (sr.hasError()) {
            Log.e(Common.LOG_TAG, "rpc load friend list error:" + sr.getError());
            return users;
        }
        FriendList list = sr.getFriendList();
        if (list == null || list.Users == null || list.Users.length == 0) {
            return users;
        }
        for (final long id : list.Users) {
            sr = new RPC() {
                public ServiceResponse getResponse() throws Exception {
                    return Base.getService().loadUserInfo(id);
                }
            }.Call();
            if (sr.hasError()) {
                Log.e(Common.LOG_TAG, "rpc load userInfo error:" + sr.getError());
                return users;
            }
            users.add(sr.getUser());
            Base.db.saveFriend(id);
        }

        return users;
    }

}
