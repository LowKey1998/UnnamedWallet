package com.jose.walletapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import jnr.ffi.annotations.In;

public class SeedPhraseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_backup_start);

        Intent intent=getIntent();
        TextView seed=findViewById(R.id.backup_subheader);
        seed.setText(intent.getStringExtra("secret"));


        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeedPhraseActivity.this,MyWalletActivity.class));
            }
        });
    }
}
