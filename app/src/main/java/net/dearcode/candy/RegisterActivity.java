package net.dearcode.candy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import net.dearcode.candy.controller.ServiceBinder;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.util.Common;

/**
 *  * Created by c-wind on 2016/9/21 15:47
 *  * mail：root@codecn.org
 *  
 */
public class RegisterActivity extends AppCompatActivity {
    private AutoCompleteTextView tvUser;
    private EditText etPass;
    private EditText etPass2;
    private RegisterActivity self;
    private ServiceBinder conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_register);

        tvUser = (AutoCompleteTextView) findViewById(R.id.email);
        etPass = (EditText) findViewById(R.id.password);
        etPass2 = (EditText) findViewById(R.id.password2);

        conn = new ServiceBinder(this);

        Button tvUserSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        tvUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ri = new Intent(RegisterActivity.this, CandyActivity.class);
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
                    ServiceResponse sr = conn.getCandy().register(user, password);
                    if (sr.hasError()) {
                        Snackbar.make(view, sr.getError(), Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    Bundle b = new Bundle();
                    b.putBoolean("Register", true);
                    b.putLong("id", sr.getId());
                    b.putString("user", user);
                    b.putString("pass", password);
                    backToMain(b);
                } catch (Exception e) {
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void backToMain(Bundle b) {
        Intent i = new Intent(this, CandyActivity.class);
        i.putExtras(b);
        this.setResult(RESULT_OK, i);
        conn.Disconnect();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle b = new Bundle();
        b.putBoolean("Redirect", true);
        b.putString("RedirectTo", "Login");
        backToMain(b);
    }
}

