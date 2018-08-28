package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 5000;
    ImageView logo;
    ImageView logo2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo=findViewById(R.id.wholeBusSplash);
        logo2=findViewById(R.id.logoSplash);
        Animation bounce = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bounce);
        logo.startAnimation(bounce);

        logo2.setVisibility(logo2.INVISIBLE);

        final Animation startFadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        final Animation startFadeInAnimation =AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);

        startFadeOutAnimation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                logo.setVisibility(logo.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.startAnimation(startFadeOutAnimation);
                logo2.startAnimation(startFadeInAnimation);
            }
        },3000);




//        Animation fadeOut = new AlphaAnimation(1, 0);
//        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
//        fadeOut.setStartOffset(3000);
//        fadeOut.setDuration(1000);
//        AnimationSet animation = new AnimationSet(false); //change to false
//        animation.addAnimation(fadeOut);
//        logo.setAnimation(animation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
