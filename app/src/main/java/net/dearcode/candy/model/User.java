package net.dearcode.candy.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.dearcode.candy.R;

/**
 *  * Created by c-wind on 2016/9/21 18:34
 *  * mail：root@codecn.org
 *  
 */
public class User {
    private long ID;
    private byte[] Avatar;
    private String Name;
    private String NickName;

    public User() {

    }

    public User(long ID, byte[] avatar, String name, String nickname) {
        this.ID = ID;
        this.Avatar = avatar;
        this.Name = name;
        this.NickName = nickname;
    }


    public long getID() {
        return ID;
    }

    public byte[] getAvatar() {
        return Avatar;
    }

    public Bitmap getAvatarBitmap(Resources def) {
        Bitmap bitmap;
        if (Avatar != null) {
            bitmap = BitmapFactory.decodeByteArray(Avatar, 0, Avatar.length);
        } else {
            bitmap = BitmapFactory.decodeResource(def, R.mipmap.test_da);
        }
        return bitmap;
    }

    public String getNickName() {
        return NickName;
    }

    public String getName() {
        return Name;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public void setAvatar(byte[] avatar) {
        Avatar = avatar;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }
}
