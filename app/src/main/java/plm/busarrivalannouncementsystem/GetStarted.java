package plm.busarrivalannouncementsystem;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class GetStarted extends FragmentActivity {
    private ViewPager viewPager;
    private View indicator1;
    private View indicator2;
    private View indicator3;
    private View indicator4;
    private View indicator5;
    private int WIZARD_PAGES_COUNT = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        indicator1 = (View) findViewById(R.id.indicator1);
        indicator2 = (View) findViewById(R.id.indicator2);
        indicator3 = (View) findViewById(R.id.indicator3);
        indicator4 = (View) findViewById(R.id.indicator4);
        indicator5 = (View) findViewById(R.id.indicator5);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GetStarted.ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new GetStarted.WizardPageChangeListener());
        updateIndicators(0);


    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new HelpWizardFragment(position,
                    R.layout.get_started_page1,
                    R.layout.get_started_page2,
                    R.layout.get_started_page3,
                    R.layout.get_started_page4,
                    R.layout.get_started_page5,
                    R.layout.get_started_page6
            );
        }

        @Override
        public int getCount() {
            return WIZARD_PAGES_COUNT;
        }


    }


    private class WizardPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            updateIndicators(position);
            if (position == WIZARD_PAGES_COUNT - 1) {
                startActivity(new Intent(GetStarted.this, HomeActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the
            // system to handle the
            // Back button. This calls finish() on this activity and pops the
            // back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void updateIndicators(int position) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int resizeValue = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 25, metrics);
        int defaultValue = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
        switch (position) {
            case 0:
                indicator1.getLayoutParams().height = resizeValue;
                indicator1.getLayoutParams().width = resizeValue;
                indicator1.requestLayout();

                indicator2.getLayoutParams().height = defaultValue;
                indicator2.getLayoutParams().width = defaultValue;
                indicator2.requestLayout();

                indicator3.getLayoutParams().height = defaultValue;
                indicator3.getLayoutParams().width = defaultValue;
                indicator3.requestLayout();

                indicator4.getLayoutParams().height = defaultValue;
                indicator4.getLayoutParams().width = defaultValue;
                indicator4.requestLayout();

                indicator5.getLayoutParams().height = defaultValue;
                indicator5.getLayoutParams().width = defaultValue;
                indicator5.requestLayout();
                break;

            case 1:
                indicator1.getLayoutParams().height = defaultValue;
                indicator1.getLayoutParams().width = defaultValue;
                indicator1.requestLayout();

                indicator2.getLayoutParams().height = resizeValue;
                indicator2.getLayoutParams().width = resizeValue;
                indicator2.requestLayout();

                indicator3.getLayoutParams().height = defaultValue;
                indicator3.getLayoutParams().width = defaultValue;
                indicator3.requestLayout();

                indicator4.getLayoutParams().height = defaultValue;
                indicator4.getLayoutParams().width = defaultValue;
                indicator4.requestLayout();

                indicator5.getLayoutParams().height = defaultValue;
                indicator5.getLayoutParams().width = defaultValue;
                indicator5.requestLayout();
                break;

            case 2:
                indicator1.getLayoutParams().height = defaultValue;
                indicator1.getLayoutParams().width = defaultValue;
                indicator1.requestLayout();

                indicator2.getLayoutParams().height = defaultValue;
                indicator2.getLayoutParams().width = defaultValue;
                indicator2.requestLayout();

                indicator3.getLayoutParams().height = resizeValue;
                indicator3.getLayoutParams().width = resizeValue;
                indicator3.requestLayout();

                indicator4.getLayoutParams().height = defaultValue;
                indicator4.getLayoutParams().width = defaultValue;
                indicator4.requestLayout();

                indicator5.getLayoutParams().height = defaultValue;
                indicator5.getLayoutParams().width = defaultValue;
                indicator5.requestLayout();
                break;

            case 3:
                indicator1.getLayoutParams().height = defaultValue;
                indicator1.getLayoutParams().width = defaultValue;
                indicator1.requestLayout();

                indicator2.getLayoutParams().height = defaultValue;
                indicator2.getLayoutParams().width = defaultValue;
                indicator2.requestLayout();

                indicator3.getLayoutParams().height = defaultValue;
                indicator3.getLayoutParams().width = defaultValue;
                indicator3.requestLayout();

                indicator4.getLayoutParams().height = resizeValue;
                indicator4.getLayoutParams().width = resizeValue;
                indicator4.requestLayout();

                indicator5.getLayoutParams().height = defaultValue;
                indicator5.getLayoutParams().width = defaultValue;
                indicator5.requestLayout();
                break;

            case 4:
                indicator1.getLayoutParams().height = defaultValue;
                indicator1.getLayoutParams().width = defaultValue;
                indicator1.requestLayout();

                indicator2.getLayoutParams().height = defaultValue;
                indicator2.getLayoutParams().width = defaultValue;
                indicator2.requestLayout();

                indicator3.getLayoutParams().height = defaultValue;
                indicator3.getLayoutParams().width = defaultValue;
                indicator3.requestLayout();

                indicator4.getLayoutParams().height = defaultValue;
                indicator4.getLayoutParams().width = defaultValue;
                indicator4.requestLayout();

                indicator5.getLayoutParams().height = resizeValue;
                indicator5.getLayoutParams().width = resizeValue;
                indicator5.requestLayout();
                break;
        }
    }
}
