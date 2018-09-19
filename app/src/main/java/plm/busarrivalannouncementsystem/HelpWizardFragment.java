package plm.busarrivalannouncementsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpWizardFragment extends Fragment {
    int wizard_page_position;
    int page1, page2, page3, page4, page5, page6;

    public HelpWizardFragment() {
    }

    @SuppressLint("ValidFragment")
    public HelpWizardFragment(int wizard_page_position, int page1, int page2, int page3, int page4, int page5) {
        this.wizard_page_position = wizard_page_position;
        this.page1 = page1;
        this.page2 = page2;
        this.page3 = page3;
        this.page4 = page4;
        this.page5 = page5;
    }

    @SuppressLint("ValidFragment")
    public HelpWizardFragment(int wizard_page_position, int page1, int page2, int page3, int page4, int page5, int page6) {
        this.wizard_page_position = wizard_page_position;
        this.page1 = page1;
        this.page2 = page2;
        this.page3 = page3;
        this.page4 = page4;
        this.page5 = page5;
        this.page6 = page6;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout_id = page1;
        switch (wizard_page_position) {
            case 0:
                layout_id = page1;
                break;

            case 1:
                layout_id = page2;
                break;

            case 2:
                layout_id = page3;
                break;

            case 3:
                layout_id = page4;
                break;

            case 4:
                layout_id = page5;
                break;
            case 5:
                layout_id = page6;
                break;
        }

        return inflater.inflate(layout_id, container, false);
    }
}
