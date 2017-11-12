package com.ethanhua.hencoderpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class HencodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hencoder);
        final PraiseButton praiseButton = (PraiseButton) findViewById(R.id.btn_praise);
        praiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseButton.setChecked(!praiseButton.isChecked());
                if (praiseButton.isChecked()) {
                    Toast.makeText(HencodeActivity.this, "已点赞", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HencodeActivity.this, "已取消点赞", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final EditText editText = (EditText) findViewById(R.id.edit_praiseNum);
        findViewById(R.id.btn_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numStr = editText.getText().toString();
                if (TextUtils.isEmpty(numStr)) {
                    return;
                }
                try {
                    int a = Integer.parseInt(numStr);
                    praiseButton.setText(a);
                } catch (Exception e) {

                }
            }
        });
    }
}
