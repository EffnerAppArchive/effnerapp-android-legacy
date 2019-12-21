package de.effnerapp.effner;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import de.effnerapp.effner.data.DataStack;

public class MainActivity extends AppCompatActivity {
    public static TextView pageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_substitutions, R.id.navigation_schooltests,
                R.id.navigation_terms, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        pageTextView = findViewById(R.id.page_text);

//        LinearLayout layout = findViewById(R.id.linearlayout);
//        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
//
//        animationDrawable.setEnterFadeDuration(5000);
//
//        animationDrawable.setExitFadeDuration(2000);

//        Window window = getWindow();
//
//// clear FLAG_TRANSLUCENT_STATUS flag:
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//        int blue = getResources().getColor(R.color.icon_blue);
//        int green = getResources().getColor(R.color.icon_green);
//        int yellow = getResources().getColor(R.color.icon_yellow);
//
//        ValueAnimator animator = ValueAnimator.ofFloat(0,1);
//
//        animator.addUpdateListener(valueAnimator -> {
//            int currentColor = window.getStatusBarColor();
//            int toColor;
//            if(currentColor == blue) {
//                toColor = green;
//            } else if(currentColor == green) {
//                toColor = yellow;
//            } else {
//                toColor = blue;
//            }
//            float position = valueAnimator.getAnimatedFraction();
//
//            int blended = blendColors(currentColor, toColor, position);
//            window.setStatusBarColor(blended);
//        });
//        animator.setDuration(5000).setRepeatCount(ValueAnimator.INFINITE);
//        animator.start();

//        new Thread(() -> {
//            while (true) {
//                window.setStatusBarColor(ContextCompat.getColor(this, R.color.icon_blue));
//
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                window.setStatusBarColor(ContextCompat.getColor(this, R.color.icon_green));
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                window.setStatusBarColor(ContextCompat.getColor(this, R.color.icon_yellow));
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        });

    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

}
