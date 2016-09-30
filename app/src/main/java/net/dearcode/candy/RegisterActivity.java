package net.dearcode.candy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.util.Common;
import net.dearcode.candy.util.Errors;

/**
 *  * Created by c-wind on 2016/9/21 15:47
 *  * mail：root@codecn.org
 *  
 */
public class RegisterActivity extends AppCompatActivity {
    private AutoCompleteTextView tvUser;
    private EditText etPass;
    private EditText etPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvUser = (AutoCompleteTextView) findViewById(R.id.email);
        etPass = (EditText) findViewById(R.id.password);
        etPass2 = (EditText) findViewById(R.id.password2);

        Button tvUserSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        tvUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = Common.GetString(tvUser.getText());
                String password = Common.GetString(etPass.getText());
                String password2 = Common.GetString(etPass2.getText());

                if (TextUtils.isEmpty(user)) {
                    Snackbar.make(view, "用户名不能为空", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
                    Snackbar.make(view, "密码不能为空", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.equals(password, password2)) {
                    Snackbar.make(view, "两次密码不一致", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                try {
                    ServiceResponse sr = Base.getService().register(user, password);
                    if (sr.hasError()) {
                        Snackbar.make(view, Errors.ParseError(getApplicationContext(), sr.getError()), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    Base.updateAccount(sr.getId(), user, password);
                    backToMainActivity();
                } catch (Exception e) {
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void backToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private long lastTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - lastTime) > 2000) {
                lastTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

