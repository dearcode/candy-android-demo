package net.dearcode.candy.model;

import android.provider.ContactsContract;

/**
 *  * Created by c-wind on 2016/9/21 18:34
 *  * mail：root@codecn.org
 *  
 */
public class User {
    private long id;
    private String avatar;
    private String name;
    private String nickname;

    public User(long id, String avatar, String name, String nickname) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.nickname = nickname;
    }

    public long getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public String getName() {
        return name;
    }
}
