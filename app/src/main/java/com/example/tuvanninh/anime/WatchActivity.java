package com.example.tuvanninh.anime;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.tuvanninh.universalvideoview.UniversalMediaController;
import com.example.tuvanninh.universalvideoview.UniversalVideoView;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class WatchActivity extends AppCompatActivity implements UniversalVideoView.VideoViewCallback {

    private static final String TAG = "WatchingFilmActivity";
    String filmURL, movieURL;
    EpisodeAdapter epsAdapter;
    ArrayList<String> listEps;
    GridView gridEps;
    boolean firstLoad = true;
    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;
    View mVideoLayout;
    private int mSeekPosition = 0;
    private int cachedHeight;
    private boolean isFullscreen;
    FilmInfo filmInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_watch);
        receiveData();
        initLayout();
        new LoadFilmList(this).execute(filmURL);
    }

    private void initLayout() {
        gridEps = (GridView) findViewById(R.id.listEps);
        listEps = new ArrayList<>();
        epsAdapter = new EpisodeAdapter(this, R.layout.item_grid, listEps);
        gridEps.setAdapter(epsAdapter);
        gridEps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vEps = (TextView) view.findViewById(R.id.griditem);
                vEps.setBackgroundColor(R.color.colorYellow);
                new LoadFilmList(WatchActivity.this).execute(listEps.get(position));
//                Log.d("abcd", movieURL);
            }
        });

        mVideoLayout = findViewById(R.id.video_layout);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoViewCallback(this);
        setVideoAreaSize();

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

    }

    private void startFilm(final String filmPath){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mVideoView.setVideoPath(filmPath);
                        mVideoView.start();
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
//                mVideoView.setVideoPath(movieURL);
                //mVideoView.requestFocus();
            }
        });
    }

    private void receiveData() {
        filmInfo = (FilmInfo) getIntent().getSerializableExtra("filmURL");
        filmURL = filmInfo.tmp;
//        if (!filmInfo.isSearchAnime) firstLoad = false;
//        Log.d("abcd", filmURL);

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ");
        if (mVideoView != null) {
            mSeekPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null && mSeekPosition >= 0){
            mVideoView.seekTo(mSeekPosition);
            mVideoView.start();
        }
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;

        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);


        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);

        }

        switchTitleBar(!isFullscreen);
    }

    private void switchTitleBar(boolean show) {

        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (show) {
                supportActionBar.show();
            } else {
                supportActionBar.hide();
            }
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPause UniversalVideoView callback");
        mSeekPosition = mediaPlayer.getCurrentPosition();
        Log.d("abcd", String.valueOf(mSeekPosition));
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onStart UniversalVideoView callback");
        mediaPlayer.seekTo(mSeekPosition);
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingEnd UniversalVideoView callback");

    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }


    private class LoadFilmList extends LoadDataTask {

        public LoadFilmList(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            org.jsoup.nodes.Document doc = Jsoup.parse(s);
            String body = doc.body().toString();
            if (filmInfo.position <= 2 && filmInfo.isSearchAnime){
                int start = body.indexOf("file: \"") + 7;
                int end = body.indexOf("\",label:", start);
                if (start < 0 || end <0) return;
                movieURL = body.substring(start,end);
                startFilm(movieURL);

                if (firstLoad){
                    Elements eps = doc.body().select("div.ep_film").select("a");
                    for (org.jsoup.nodes.Element it: eps){
                        String epsInfo = it.attr("href");
                        listEps.add(epsInfo);
                    }
                    firstLoad = false;
                    epsAdapter.notifyDataSetChanged();
                }
            }
            else if (filmInfo.position >= 2 && !filmInfo.isSearchAnime){
                int start = body.indexOf("file:\"") + 6;
                int end = body.indexOf("\",label:", start);
                if (start < 0 || end <0) return;
                movieURL = body.substring(start,end).replace('\\',' ').replaceAll(" ","");
                startFilm(movieURL);

                if (firstLoad){
                    Elements eps = doc.body().select("ul.tn-uldef").select("a");
                    for (org.jsoup.nodes.Element it: eps){
                        String epsInfo = "http://xuongphim.tv" + it.attr("href");
                        listEps.add(epsInfo);
                    }
                    firstLoad = false;
                    epsAdapter.notifyDataSetChanged();
                }
            }





        }
    }
}