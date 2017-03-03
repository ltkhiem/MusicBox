package com.nimah.khiem.musicbox;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.daimajia.easing.linear.Linear;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GetSongListTask.ListSong,
                    GetSongQueryTask.QuerySong, GetAlbumTask.ListAlbum{

    private ArrayList<Song> SongList;
    private ArrayList<Album> AlbumList;
    private AlbumItemAdapter albumItemAdapter;
    private SongItemAdapter songItemAdapter;
    private GridView gvAlbum;
    static DraggableView draggableView;
    private ListView lvSongs;
    static int selectedAlbumId;

    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private ImageView ivPlay, ivSeekLeft, ivSeekRight;
    private double startTime = 0;
    private double finalTime = 0;
    private TextView tvMediaPlayerTitle;
    int oneTimeOnly = 0;
    private Handler mediaHandler = new Handler();;
    private int selectedSongId;

    PagerSlidingTabStrip tabs;
    MyPagerAdapter pagerAdapter;
    ArrayList<String> musicCategory;

    private UserProfile user;
    private ProfilePictureView ppv_Avatar;
    private View navbarView;
    private TextView tv_UserName;
    private TextView tv_UserEmail;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    public static int shareMusicLink;
    public static ImageView ivCover;
    private FrameLayout llDragViewTop;
    private LinearLayout llPlayerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Start here
        initComponents();
        initializeDraggableView();
        hookListeners();
        //initGridView();
        //initSearchView();
        initMediaPlayControl();
        initFacebook();

    }

    private void initComponents() {
        musicCategory = new ArrayList<>();
        musicCategory.add("http://nhacso.net/albums-noi-bat.html");
        musicCategory.add("http://nhacso.net/the-loai-album/nhac-viet-nam-12/nhac-tre-1.html?view_type=highlight");
        musicCategory.add("http://nhacso.net/the-loai-album/au-my-21.html?view_type=highlight");
        musicCategory.add("http://nhacso.net/the-loai-album/han-quoc-16.html?view_type=highlight");
        musicCategory.add("http://nhacso.net/the-loai-album/nhac-hoa-15.html?view_type=listen");
        musicCategory.add("http://nhacso.net/the-loai-album/nhac-nhat-32.html?view_type=listen");
        musicCategory.add("http://nhacso.net/the-loai-album/cac-nuoc-khac-18.html?view_type=listen");
        musicCategory.add("http://nhacso.net/the-loai-album/nhac-chu-de-51.html?view_type=highlight");

        draggableView = (DraggableView) findViewById(R.id.draggable_view);
        lvSongs = (ListView) findViewById(R.id.lvSongs);
        mediaPlayer = new MediaPlayer();

        llPlayerLayout = (LinearLayout) findViewById(R.id.llPlayerLayout);
        llDragViewTop = (FrameLayout) findViewById(R.id.llDragViewTop);
        ivCover = (ImageView) findViewById(R.id.ivCover);
        tvMediaPlayerTitle = (TextView) findViewById(R.id.tvMediaPlayerTitle);
        mediaPlayer = new MediaPlayer();
        seekBar = (SeekBar) findViewById(R.id.sbMediaPlayer);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivSeekLeft = (ImageView) findViewById(R.id.ivSeekLeft);
        ivSeekRight = (ImageView) findViewById(R.id.ivSeekRight);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(pagerAdapter);

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(0);
        changeColor(getResources().getColor(R.color.colorAccent));


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tv_UserName = (TextView) headerView.findViewById(R.id.tv_username);
        tv_UserEmail = (TextView) headerView.findViewById(R.id.tv_useremail);
        ppv_Avatar = (ProfilePictureView) headerView.findViewById(R.id.ppv_avatar);

    }

    private void initializeDraggableView() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                draggableView.setVisibility(View.GONE);
                draggableView.closeToRight();
            }
        }, 10);
    }

    private void hookListeners() {
        draggableView.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                getSupportActionBar().hide();
                llDragViewTop.setPadding(0, 0, 0, 0);
            }

            @Override
            public void onMinimized() {
                getSupportActionBar().show();
                llDragViewTop.setPadding(10, 10, 10, 10);
            }

            @Override
            public void onClosedToLeft() {
                OnStopMedia();
                selectedSongId = 1451030;
            }

            @Override
            public void onClosedToRight() {
                OnStopMedia();
                selectedSongId = 1451030;
            }
        });
    }


    private void initGridView() {
        gvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAlbumId = position;
                new GetSongListTask(MainActivity.this).execute(AlbumList.get(selectedAlbumId).getDetailUrl());
                draggableView.setVisibility(View.VISIBLE);
                draggableView.maximize();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
//        draggableView.minimize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new GetSongQueryTask(MainActivity.this).execute(query);
                HideSoftKeyboard(MainActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public static void HideSoftKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "sadasd", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, IdentifyActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void getSongList(ArrayList<Song> songlist) {
        SongList = songlist;
        songItemAdapter = new SongItemAdapter(MainActivity.this, R.layout.song_item_layout, SongList);
        lvSongs.setAdapter(songItemAdapter);
        selectedSongId=0;
        ControlMediaPlayer();
        initListView();
    }

    private void initListView() {
        selectedSongId = 0;
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSongId = position;
                ivCover.performClick();
                ControlMediaPlayer();
            }
        });
    }

    private void ControlMediaPlayer() {
        oneTimeOnly = 0;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        OnStopMedia();

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float layoutWidth = displayMetrics.widthPixels / displayMetrics.density;
        Picasso.with(this).load(SongList.get(selectedSongId).getImageUrl()).resize((int) layoutWidth, 250).centerCrop().into(ivCover);
        tvMediaPlayerTitle.setText(SongList.get(selectedSongId).getTitle());
        Uri myUri = Uri.parse(SongList.get(selectedSongId).getSourceUrl());
        try {
            mediaPlayer.setDataSource(this, myUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initMediaPlayControl(){
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                OnPlayMedia();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (selectedSongId + 1 < SongList.size()) {
                    selectedSongId += 1;
                    ControlMediaPlayer();
                }
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    OnPauseMedia();
                    Picasso.with(MainActivity.this).load(R.drawable.ic_play_arrow_white).into(ivPlay);
                } else {
                    OnPlayMedia();
                    Picasso.with(MainActivity.this).load(R.drawable.ic_pause_white).into(ivPlay);
                }

            }
        });

        ivSeekLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnMediaSeekLeft();
            }
        });

        ivSeekRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnMediaSeekRight();
            }
        });


        final Handler mHandler = new Handler();
        final Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                llPlayerLayout.setVisibility(View.INVISIBLE);
            }
        };
        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llPlayerLayout.getVisibility() == View.VISIBLE) {
                    llPlayerLayout.setVisibility(View.INVISIBLE);
                } else {
                    llPlayerLayout.setVisibility(View.VISIBLE);

                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 5000);
                }

            }
        });


    }

    private void OnPlayMedia() {
        mediaPlayer.start();
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        if (oneTimeOnly == 0) {
            seekBar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

        seekBar.setProgress((int) startTime);
        mediaHandler.postDelayed(UpdateSongTime, 100);
    }

    private void OnPauseMedia(){
        mediaPlayer.pause();
    }

    private void OnMediaSeekRight(){
        int temp = (int)startTime;
        if((temp+5000)<=finalTime){
            startTime = startTime + 5000;
            mediaPlayer.seekTo((int) startTime);
        }
    }

    private void OnMediaSeekLeft() {
        int temp = (int)startTime;
        if((temp-5000)>0){
            startTime = startTime - 5000;
            mediaPlayer.seekTo((int) startTime);
        }
    }

    private void OnStopMedia() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            seekBar.setProgress((int) startTime);
            mediaHandler.postDelayed(this, 100);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getSongQueryResult(ArrayList<Song> songlist) {
        SongList = songlist;
        songItemAdapter = new SongItemAdapter(MainActivity.this, R.layout.song_item_layout, SongList);
        lvSongs.setAdapter(songItemAdapter);
        selectedSongId=0;
        ControlMediaPlayer();
        initListView();
        draggableView.setVisibility(View.VISIBLE);
        draggableView.maximize();
    }

    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);

        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        getSupportActionBar().setBackgroundDrawable(ld);
    }

    @Override
    public void getAlbumList(ArrayList<Album> albumList) {
        SuperAwesomeCardFragment.setUpGridView(this, albumList);
    }

    public static class ShareVar  extends Application {

        public static DraggableView getDraggableView() {
            return draggableView;
        }

        public static void setSelectedAlbumId(int newVal) {
            selectedAlbumId = newVal;
        }

        public static void callShareMusicLink(int link){
            shareMusicLink = link;
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        Context context;

        private final String[] TITLES = {"Nổi Bật", "Việt Nam", "Âu Mỹ", "Hàn Quốc", "Trung Quốc",
                "Nhật Bản", "Các Nước Khac", "Nhạc Theo Chủ Đề"};

        public MyPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
            f.setParams(context, musicCategory.get(position));
            return f;
        }
    }


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void initFacebook() {
        user = new UserProfile();
        if (isLoggedIn() == true) {
            getUserProfile();
            getUserFriendList();
        }
    }


    private void getUserFriendList(){
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.d("fl", objects.toString());

                    }
                });
        request.executeAsync();
    }

    private void getUserProfile(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        Log.d("abc", object.toString());
                        try {
                            user.setFb_id(object.getString("id"));
                            user.setFb_name(object.getString("name"));
                            if (object.has("email")) {
                                user.setFb_email(object.getString("email"));
                            } else {
                                user.setFb_email("Enjoy the music!");
                            }
                            if (object.has("birthday")) {
                                user.setFb_birthday(object.getString("birthday"));
                            } else {
                                user.setFb_birthday("");
                            }
                            updateLayout();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle params = new Bundle();
        params.putString("fields", "id,name,email,birthday");
        request.setParameters(params);
        request.executeAsync();
    }

    private void updateLayout() {
        tv_UserName.setText(user.getFb_name());
        tv_UserEmail.setText(user.getFb_email());
        ppv_Avatar.setProfileId(user.getFb_id());
    }


}
