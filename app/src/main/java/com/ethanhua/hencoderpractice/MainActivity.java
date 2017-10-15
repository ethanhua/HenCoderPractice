package com.ethanhua.hencoderpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PraiseButton praiseButton = (PraiseButton) findViewById(R.id.btn_praise);
        praiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseButton.setChecked(!praiseButton.isChecked());
                if (praiseButton.isChecked()) {
                    Toast.makeText(MainActivity.this, "已点赞", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "已取消点赞", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
