package net.dearcode.candy;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.util.Common;
import net.dearcode.candy.util.Errors;

/**
 *  * Created by c-wind on 2016/9/21 15:47
 *  * mail：root@codecn.org
 *  
 */
public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView tvUser;
    private EditText tvPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvUser = (AutoCompleteTextView) findViewById(R.id.email);
        tvPass = (EditText) findViewById(R.id.password);
        TextView tvSignup = (TextView) findViewById(R.id.link_signup);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null && b.getBoolean("Register")) {
            tvUser.setText(b.getString("user"));
            tvPass.setText(b.getString("password"));
            Toast.makeText(this, "密码都帮你输入好了，点登录吧", Toast.LENGTH_SHORT).show();
        }

        Button btnLogin = (Button) findViewById(R.id.sign_in_button);
        tvSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putBoolean("Redirect", true);
                b.putString("RedirectTo", "Register");
                b.putInt("from", CandyActivity.FromLogin);
                backToCandyActivity(b);
            }
        });
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ServiceResponse sr = Base.getService().login(Common.GetString(tvUser.getText()), Common.GetString(tvPass.getText()));
                    if (sr.hasError()) {
                        Snackbar.make(view, Errors.ParseError(getApplicationContext(),sr.getError()), Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    Bundle b = new Bundle();
                    b.putString("user", Common.GetString(tvUser.getText()));
                    b.putString("password", Common.GetString(tvPass.getText()));
                    b.putLong("uid", sr.getId());
                    b.putInt("from", CandyActivity.FromLogin);
                    backToCandyActivity(b);
                } catch (RemoteException e) {
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void backToCandyActivity(Bundle b) {
        Intent i = new Intent(this, CandyActivity.class);
        i.putExtras(b);
        startActivity(i);
        finish();
    }
}

