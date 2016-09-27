// ICandyMessage.aidl
package net.dearcode.candy;

import net.dearcode.candy.model.ServiceResponse;

// Declare any non-default types here with import statements

interface CandyMessage {

    ServiceResponse register(String user, String pass);

    ServiceResponse login(String user, String pass);

    ServiceResponse loadFriendList();

    ServiceResponse loadUserInfo(long ID);

    ServiceResponse searchUser(String user);

    ServiceResponse addFriend(long ID, String msg);

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
