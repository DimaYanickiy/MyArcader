package com.spiner.spinthis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.spin)
    ImageButton btnStart;

    @BindView(R.id.imageButton3)
    ImageButton btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.spin)
    void onClickStart(){
        startActivity(new Intent(MainActivity.this, Game.class));
    }

    @OnClick(R.id.imageButton3)
    void onClickExit(){
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}