package com.example.netty4client;

import io.netty.bootstrap.Bootstrap;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class MainActivity extends Activity {
    
    private EditText mIpEdt;
    private EditText mPortEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mIpEdt = (EditText) findViewById(R.id.edt_ip);
        mIpEdt.setText(Client.HOST);
        mPortEdt = (EditText) findViewById(R.id.edt_port);
        mPortEdt.setText(Client.PORT+"");
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                String ip = mIpEdt.getText().toString();
                if (!TextUtils.isEmpty(ip)) {
                    Client.HOST = ip;
                }
                
                int port = Integer.parseInt(mPortEdt.getText().toString());
                if (port!=0) {
                    Client.PORT = port;
                }
                
                System.out.println("netty 4 client");
                
                Client.configureBootstrap(new Bootstrap()).connect();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
