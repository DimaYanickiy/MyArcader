package com.spiner.spinthis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Game extends AppCompatActivity {

    private static final String SAVED_DIF = "saved_pref";
    SharedPreferences sPref;

    @BindView(R.id.point1) ImageView point1;
    @BindView(R.id.point2) ImageView point2;
    @BindView(R.id.point3) ImageView point3;
    @BindView(R.id.point4) ImageView point4;
    @BindView(R.id.point5) ImageView point5;
    @BindView(R.id.point6) ImageView point6;
    @BindView(R.id.point7) ImageView point7;
    @BindView(R.id.point8) ImageView point8;
    @BindView(R.id.point9) ImageView point9;
    @BindView(R.id.point10) ImageView point10;
    @BindView(R.id.point11) ImageView point11;
    @BindView(R.id.point12) ImageView point12;
    @BindView(R.id.point13) ImageView point13;
    @BindView(R.id.point14) ImageView point14;
    @BindView(R.id.point15) ImageView point15;

    @BindView(R.id.tokens) TextView tokens;

    @BindView(R.id.spin) ImageButton spin_1;
    @BindView(R.id.spin5) ImageButton spin_5;
    @BindView(R.id.buttonBack) ImageButton buttonBack;
    
    @BindDrawable(R.drawable.citrus) Drawable citrus;
    @BindDrawable(R.drawable.mellow) Drawable mellow;
    @BindDrawable(R.drawable.sliva) Drawable sliva;
    @BindDrawable(R.drawable.greap) Drawable greap;

    private boolean running = false;
    private int balance;
    private int seconds = 0;
    private int tokensToPlay;
    private int wonTokens;

    private int index1, index2, index3, index4, index5, index6, index7, index8, index9, index10, index11, index12, index13, index14, index15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        loadIntPref();
        if(balance < 20){
            balance+=100;
        }
        tokens.setText("Tokens: " + Integer.toString(balance));
        timer();
    }

    private void timer(){
        Handler handler = new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                if(running){
                    seconds++;
                    spinLoop();
                }
                handler.postDelayed(this, 100);
            }
        });
    }

    private void spinLoop(){
        switch (seconds) {
            case 10:
                setImages(point1);
                setImages(point2);
                setImages(point3);
                break;
            case 11:
                setImages(point4);
                setImages(point5);
                setImages(point6);
                break;
            case 12:
                setImages(point7);
                setImages(point8);
                setImages(point9);
                break;
            case 13:
                setImages(point10);
                setImages(point11);
                setImages(point12);
                break;
            case 14:
                setImages(point13);
                setImages(point14);
                setImages(point15);
                gameStop();
                break;
            default:
                setImages(point1);
                setImages(point2);
                setImages(point3);
                setImages(point4);
                setImages(point5);
                setImages(point6);
                setImages(point7);
                setImages(point8);
                setImages(point9);
                setImages(point10);
                setImages(point11);
                setImages(point12);
                setImages(point13);
                setImages(point14);
                setImages(point15);
        }
    }

    @OnClick(R.id.spin)
    void onClickSpin(){
        if(!running){
            tokensToPlay = 1;
            gameStart();
        }
    }

    @OnClick(R.id.spin5)
    void onClickSpin5(){
        if(!running){
            tokensToPlay = 5;
            gameStart();
        }
    }

    private void gameStart(){
        running = true;
        balance -= tokensToPlay;
        seconds = 0;
        tokens.setText("Tokens: " + Integer.toString(balance));
    }

    private void gameStop(){
        running = false;
        checkLines();
        if(wonTokens == 0) tokens.setText("Tokens: " + Integer.toString(balance));
        else tokens.setText("Tokens: " + Integer.toString(balance) + " +" + Integer.toString(wonTokens));
    }

    private void checkLines(){
        if(index2 == index5 || index5 == index8 || index8 == index11 || index11 == index14){
            balance += tokensToPlay*2;
            wonTokens = tokensToPlay*2;
        }
        if((index2 == index5 && index5 == index8) || (index5 == index8 && index8 == index11) || (index8 == index11 && index11 == index14)){
            balance += tokensToPlay*6;
            wonTokens = tokensToPlay*6;
        }
        if((index2 == index5 && index5 == index8 && index8 == index11) || (index5 == index8 && index8 == index11 && index11 == index14)){
            balance += tokensToPlay*12;
            wonTokens = tokensToPlay*12;
        }
        if(index2 == index5 && index5 == index8 && index8 == index11 && index11 == index14){
            balance += tokensToPlay*24;
            wonTokens = tokensToPlay*24;
        }
        if(index2 != index5 && index5 != index8 && index8 != index11 && index11 != index14){
            wonTokens = 0;
        }
    }

    private void setImages(ImageView view){
        Random random = new Random();
        if(view.getId() == R.id.point1){
            index1 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index1);
        }
        if(view.getId() == R.id.point2){
            index2 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index2);
        }
        if(view.getId() == R.id.point3){
            index3 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index3);
        }
        if(view.getId() == R.id.point4){
            index4 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index4);
        }
        if(view.getId() == R.id.point5){
            index5 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index5);
        }
        if(view.getId() == R.id.point6){
            index6 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index6);
        }
        if(view.getId() == R.id.point7){
            index7 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index7);
        }
        if(view.getId() == R.id.point8){
            index8 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index8);
        }
        if(view.getId() == R.id.point9){
            index9 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index9);
        }
        if(view.getId() == R.id.point10){
            index10 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index10);
        }
        if(view.getId() == R.id.point11){
            index11 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index11);
        }
        if(view.getId() == R.id.point12){
            index12 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index12);
        }
        if(view.getId() == R.id.point13){
            index13 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index13);
        }
        if(view.getId() == R.id.point14){
            index14 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index14);
        }
        if(view.getId() == R.id.point15){
            index15 = Math.abs(random.nextInt()%4) + 1;
            setImageWithIndexes(view, index15);
        }
    }
    private void setImageWithIndexes(ImageView view, int index){
        switch(index){
            case 1:
                view.setImageDrawable(citrus);
                break;
            case 2:
                view.setImageDrawable(greap);
                break;
            case 3:
                view.setImageDrawable(mellow);
                break;
            case 4:
                view.setImageDrawable(sliva);
                break;
            default:
                view.setImageDrawable(citrus);
                break;
        }
    }

    private void saveIntPref(){
        sPref = getSharedPreferences("PREF", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(SAVED_DIF, balance);
        ed.commit();
    }

    public void loadIntPref(){
        sPref = getSharedPreferences("PREF", MODE_PRIVATE);
        balance = sPref.getInt(SAVED_DIF, 1000);
    }

    @OnClick(R.id.buttonBack)
    void onClickBack(){
        running = false;
        saveIntPref();
        finish();
    }

    @Override
    public void onBackPressed() {
        running = false;
        saveIntPref();
        finish();
        super.onBackPressed();
    }
}