package net.dearcode.candy.controller;

import com.alibaba.fastjson.JSON;

import net.dearcode.candy.CandyActivity;
import net.dearcode.candy.CandyMessage;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 *  * Created by c-wind on 2016/9/26 16:50
 *  * mail：root@codecn.org
 *  
 */
public class Contacts {
    public Contacts(CandyMessage api) {

    }

    public static ArrayList<User> getContacts() {
        ArrayList<User> users = new ArrayList<>();
        try {
            ServiceResponse sr = CandyActivity.getCandy().loadFriendList();
            users = new ArrayList<>(JSON.parseArray(sr.getData(), User.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

}
