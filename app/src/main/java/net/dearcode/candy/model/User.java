package net.dearcode.candy.model;

/**
 *  * Created by c-wind on 2016/9/21 18:34
 *  * mail：root@codecn.org
 *  
 */
public class User {
    private long id;
    private String avatar;
    private String name;

    public User(long id, String avatar, String name) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }
}
