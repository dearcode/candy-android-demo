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

import net.dearcode.candy.controller.ServiceBinder;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.util.Common;

/**
 *  * Created by c-wind on 2016/9/21 15:47
 *  * mail：root@codecn.org
 *  
 */
public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "Candy";
    private AutoCompleteTextView tvUser;
    private EditText tvPass;
    private ServiceBinder conn;

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
            tvPass.setText(b.getString("pass"));
            Toast.makeText(this, "密码都帮你输入好了，点登录吧", Toast.LENGTH_SHORT).show();
        }

        conn = new ServiceBinder(this);

        Button btnLogin = (Button) findViewById(R.id.sign_in_button);
        tvSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putBoolean("Redirect", true);
                b.putString("RedirectTo", "Register");
                backToMain(b);
            }
        });
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ServiceResponse sr = conn.getCandy().login(Common.GetString(tvUser.getText()), Common.GetString(tvPass.getText()));
                    if (sr.hasError()) {
                        Snackbar.make(view, sr.getError(), Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    Bundle b = new Bundle();
                    b.putString("user", Common.GetString(tvUser.getText()));
                    b.putString("pass", Common.GetString(tvPass.getText()));
                    b.putLong("id", sr.getId());
                    backToMain(b);
                } catch (RemoteException e) {
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
        Toast.makeText(this, "不登录不准走", Toast.LENGTH_SHORT).show();
    }
}

