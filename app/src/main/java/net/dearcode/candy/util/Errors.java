package net.dearcode.candy.util;

/**
 * Created by guowei on 16-9-30.
 */
import android.content.Context;

import net.dearcode.candy.R;

import go.client.Client;
import go.client.Error;

public class Errors {
    // ErrorOK 成功
    public static final int ErrorOK = 0;
    // ErrorFailure 未知错误
    public static final int ErrorFailure = 1;
    // ErrorUserNameFormat 用户名格式错误
    public static final int ErrorUserNameFormat = 1000;
    // ErrorUserNameLen 用户名长度错误
    public static final int ErrorUserNameLen = 1001;
    // ErrorUserPasswdFormat 用户密码格式错误
    public static final int ErrorUserPasswdFormat = 1010;
    // ErrorUserPasswdLen 用户密码长度错误
    public static final int ErrorUserPasswdLen = 1011;
    // ErrorUserNickFormat 昵称格式错误
    public static final int ErrorUserNickFormat = 1020;
    // ErrorUserNickLen 昵称长度错误
    public static final int ErrorUserNickLen = 1021;


    public static String ParseError(Context context, String msg) {
        String retError = new String();
        Error err  = Client.errorParse(msg);
        switch ((int)err.getCode())
        {
            case ErrorOK:
                retError = context.getResources().getString(R.string.err_ok);
                break;
            case ErrorFailure:
                retError = context.getResources().getString(R.string.err_failure);
                break;
            case ErrorUserNameFormat:
                retError = context.getResources().getString(R.string.err_username_format);
                break;
            case ErrorUserNameLen:
                retError = context.getResources().getString(R.string.err_username_len);
                break;
            case ErrorUserPasswdFormat:
                retError = context.getResources().getString(R.string.err_userpasswd_format);
                break;
            case ErrorUserPasswdLen:
                retError = context.getResources().getString(R.string.err_userpasswd_len);
                break;
            case ErrorUserNickFormat:
                retError = context.getResources().getString(R.string.err_usernick_format);
                break;
            case ErrorUserNickLen:
                retError = context.getResources().getString(R.string.err_usernick_len);
                break;
            default:
                retError = context.getResources().getString(R.string.err_failure);
                break;
        }

        return retError;
    }
}
