package cn.refactor.characterview;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.refactor.library.CharacterView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CharacterView cv = (CharacterView) findViewById(R.id.cv);
        cv.setBackgroundColor(Color.BLACK);
        cv.setText("√");
        cv.setTextColor(Color.WHITE);
        cv.setTextStyle(Typeface.NORMAL);
        cv.setTextSize(40);
        cv.setBorderWidth(2);
        cv.setBorderColor(Color.WHITE);
    }

}
