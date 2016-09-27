package net.dearcode.candy.controller;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import net.dearcode.candy.CandyActivity;
import net.dearcode.candy.model.FriendList;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;

import java.util.ArrayList;

/**
 *  * Created by c-wind on 2016/9/26 16:50
 *  * mail：root@codecn.org
 *  
 */
public class Contacts {
    private static final String TAG = "Candy";
    public static ArrayList<User> getContacts() {
        ArrayList<User> users = new ArrayList<>();
        try {
            ServiceResponse sr = CandyActivity.getCandy().loadFriendList();
            if (sr.hasError()) {
                Log.e(TAG, "loadFriendList error:"+sr.getError());
                return users;
            }
            FriendList friendList = JSON.parseObject(sr.getData(), FriendList.class);
            for (long id: friendList.Users) {
                sr = CandyActivity.getCandy().loadUserInfo(id);
                if (sr.hasError()) {
                    Log.e(TAG, "loadUserInfo error:"+sr.getError());
                    return users;
                }
                User u = JSON.parseObject(sr.getData(), User.class);
                users.add(u);
            }
        } catch (Exception e) {
            Log.e(TAG, "getContacts error:"+ e.getMessage());
        }
        return users;
    }

}
