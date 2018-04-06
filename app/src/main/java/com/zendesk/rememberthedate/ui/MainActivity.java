package com.zendesk.rememberthedate.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zendesk.rememberthedate.Global;
import com.zendesk.rememberthedate.R;
import com.zendesk.rememberthedate.model.UserProfile;
import com.zendesk.rememberthedate.push.PushUtils;
import com.zendesk.rememberthedate.storage.AppStorage;
import com.zendesk.util.StringUtils;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;


public class MainActivity extends AppCompatActivity {

    public static final int POS_DATE_LIST = 0;
    public static final int POS_PROFILE = 1;
    public static final int POS_HELP = 2;

    public static final String EXTRA_VIEWPAGER_POSITION = "extra_viewpager_pos";

    private AppStorage storage;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
//    private Menu menu;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        setSupportActionBar(toolbar);

        storage = Global.getStorage(getApplicationContext());
        initialiseChatSdk();

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(new FabPageChangeListener(this, fab));
//        viewPager.addOnPageChangeListener(new MenuPageChangeListener(this, menu));
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        final int viewPagerPos = getIntent().getIntExtra(EXTRA_VIEWPAGER_POSITION, POS_DATE_LIST);
        viewPager.setCurrentItem(viewPagerPos);

    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);
        fab = findViewById(R.id.action_bar_add);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PushUtils.checkPlayServices(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
//        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                CreateProfileActivity.start(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initialiseChatSdk() {
        final UserProfile profile = storage.getUserProfile();
        if (StringUtils.hasLength(profile.getEmail())) {
            // Init Zopim Visitor info
            final VisitorInfo.Builder build = new VisitorInfo.Builder()
                    .email(profile.getEmail());

            if (StringUtils.hasLength(profile.getName())) {
                build.name(profile.getName());
            }

            ZopimChat.setVisitorInfo(build.build());
        }
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POS_DATE_LIST: {
                    return DateFragment.newInstance();
                }
                case POS_PROFILE: {
                    return ProfileFragment.newInstance();
                }
                case POS_HELP: {
                    return HelpFragment.newInstance();
                }
            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POS_DATE_LIST: {
                    return DateFragment.FRAGMENT_TITLE;
                }
                case POS_PROFILE: {
                    return ProfileFragment.FRAGMENT_TITLE;
                }
                case POS_HELP: {
                    return HelpFragment.FRAGMENT_TITLE;
                }
            }
            return null;
        }
    }

    private static class FabPageChangeListener implements ViewPager.OnPageChangeListener {

        private final Context context;
        private final FloatingActionButton fab;

        private FabPageChangeListener(Context context, FloatingActionButton fab) {
            this.context = context;
            this.fab = fab;
            configureFabForDateList();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Intentionally empty
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {

                case POS_DATE_LIST: {
                    configureFabForDateList();
                    break;
                }

                case POS_PROFILE: {
                    fab.hide();
                    break;
                }

                default: {
                    fab.hide();
                    break;
                }
            }
        }

        private void configureFabForDateList() {
            fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_light));
            fab.setOnClickListener(view -> EditDateActivity.start(context));

            fab.show();
        }

        private void configureFabForProfile() {
            fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_edit_light));
//            fab.setOnClickListener(view -> CreateProfileActivity.start(context));
            fab.hide();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Intentionally empty
        }
    }

    private static class MenuPageChangeListener implements ViewPager.OnPageChangeListener {

        private final Context context;
        private final Menu menu;

        private MenuPageChangeListener(Context context, Menu menu) {
            this.context = context;
            this.menu = menu;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Intentionally empty
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {

                case POS_DATE_LIST: {
                    menu.findItem(R.id.action_delete).setVisible(false);
                    break;
                }

                case POS_PROFILE: {
                    menu.findItem(R.id.action_delete).setVisible(true);
                    break;
                }

                default: {
                    menu.findItem(R.id.action_delete).setVisible(false);
                    break;
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
