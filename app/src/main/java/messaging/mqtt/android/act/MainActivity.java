package messaging.mqtt.android.act;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import messaging.mqtt.android.R;
import messaging.mqtt.android.crypt.DbEncryptOperations;
import messaging.mqtt.android.service.AsimService;
import messaging.mqtt.android.sharedPrefs.SharedPreferencesService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayout createPass, enterPass;
    private EditText newPass1, newPass2, pass;
    private Button createBtn, enterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsimService.preferencesService = new SharedPreferencesService(fillMitrilPreferences());

        if (!AsimService.running) {
            startService(new Intent(this, AsimService.class));
        }

        createPass = (LinearLayout) findViewById(R.id.createPass);
        enterPass = (LinearLayout) findViewById(R.id.enterPass);
        newPass1 = (EditText) findViewById(R.id.newPass1);
        newPass2 = (EditText) findViewById(R.id.newPass2);
        pass = (EditText) findViewById(R.id.pass);
        createBtn = (Button) findViewById(R.id.createBtn);
        enterBtn = (Button) findViewById(R.id.enterBtn);

        if ("".equals(AsimService.getPreferencesService().getPassword())) {
            enterPass.setVisibility(View.GONE);
            createPass.setVisibility(View.VISIBLE);
        } else {
            createPass.setVisibility(View.GONE);
            enterPass.setVisibility(View.VISIBLE);
        }

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPass1.getText() == null || newPass2.getText() == null) {
                    Toast.makeText(MainActivity.this, "Lütfen şifre belirleyiniz", Toast.LENGTH_LONG).show();
                } else if (!newPass1.getText().toString().equals(newPass2.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Şifreler aynı değil", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Şifre oluşturuldu.", Toast.LENGTH_LONG).show();
                    savePass();
                    Intent contact = new Intent(MainActivity.this, ContactActivity.class);
                    startActivity(contact);
                    finish();
                }
            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass.getText() == null) {
                    Toast.makeText(MainActivity.this, "Lütfen şifre giriniz", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        byte[] key = DbEncryptOperations.readKey();
                        byte[] decrypt = DbEncryptOperations.readPass();
                        String dec = new String(decrypt, "UTF-8");

                        if (pass.getText().toString().equals(dec)) {
                            Intent contact = new Intent(MainActivity.this, ContactActivity.class);
                            startActivity(contact);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Şifre Yanlış", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });


    }

    private void savePass() {
        try {
            String pass = newPass1.getText().toString();
            byte[] encrypt = DbEncryptOperations.encrypt(pass.getBytes());
            DbEncryptOperations.writeKey(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SharedPreferences fillMitrilPreferences() {

        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

}
