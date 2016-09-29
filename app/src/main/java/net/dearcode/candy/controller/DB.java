package net.dearcode.candy.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.dearcode.candy.model.User;

/**
 *  * Created by c-wind on 2016/9/29 12:43
 *  * mail：root@codecn.org
 *  
 */
public class DB {
    private static SQLiteDatabase db;

    public boolean isFriend(long id) {
        Cursor c = db.rawQuery("SELECT id FROM friend where id= " + id + " limit 1", null);
        if (c.moveToNext()) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public void addFriend(long id) {
        db.execSQL("insert into friend (id) values ( " + id + ")");
    }


    public User loadUserInfo() {
        User user = null;
        Cursor c = db.rawQuery("SELECT id, user,nickname,password, avatar FROM user_info limit 1", null);
        if (c.moveToNext()) {
            user = new User();
            user.setID(c.getLong(0));
            user.setName(c.getString(1));
            user.setNickName(c.getString(2));
            user.setPassword(c.getString(3));
            user.setAvatar(c.getBlob(4));
        }
        c.close();
        return user;
    }

    public void saveUserPassword(long id, String user, String password) {
        db.execSQL("replace into user_info (id, user, password) values (?,?,?)", new Object[]{id, user, password});
    }

    public DB(Context ctx) {
        db = ctx.openOrCreateDatabase("candy.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user_info (id INTEGER PRIMARY KEY, user TEXT, name TEXT, password TEXT, nickname TEXT, avatar BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS friend(id INTEGER PRIMARY KEY)");
    }
}
