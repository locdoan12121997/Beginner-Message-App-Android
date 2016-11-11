package vinh.chatroom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentPageAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Tab1", "Tab2", "Tab3" };
    public FragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
            case 0:
                return MainFirstFragment.newInstance(arg0);
            case 1:
                return MainSecondFragment.newInstance(arg0);
            case 2:
                return  MainThirdFragment.newInstance(arg0);
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
