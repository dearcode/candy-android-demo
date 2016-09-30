package net.dearcode.candy.controller;

import android.os.RemoteException;
import android.util.Log;

import net.dearcode.candy.Base;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;
import net.dearcode.candy.util.Common;

/**
 *  * Created by c-wind on 2016/9/30 13:23
 *  * mail：root@codecn.org
 *  
 */
public class UserInfo {

    public static User getUserInfo(final long id) {
        User user = Base.db.loadUser(id);
        if (user != null) {
            return user;
        }
        ServiceResponse sr = new RPC() {
            public ServiceResponse getResponse() throws Exception {
                return Base.getService().loadUserInfo(id);
            }
        }.Call();
        if (sr.hasError()) {
            Log.e(Common.LOG_TAG, "rpc load userInfo error:"+sr.getError());
            return null;
        }
        user = sr.getUser();

        Base.db.saveUser(user.getID(), user.getName(), user.getNickName(), user.getAvatar());

        return user;
    }

}
