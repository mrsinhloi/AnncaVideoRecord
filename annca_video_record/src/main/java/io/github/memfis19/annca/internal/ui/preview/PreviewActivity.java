package io.github.memfis19.annca.internal.ui.preview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import io.github.memfis19.annca.R;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;
import io.github.memfis19.annca.internal.ui.BaseAnncaActivity;
import io.github.memfis19.annca.internal.utils.AnncaImageLoader;
import io.github.memfis19.annca.internal.utils.Utils;
import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.VideoInfo;
import tcking.github.com.giraffeplayer2.VideoView;

/**
 * Created by memfis on 7/6/16.
 */
public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PreviewActivity";

    private final static String MEDIA_ACTION_ARG = "media_action_arg";
    public final static String FILE_PATH_ARG = "file_path_arg";
    private final static String RESPONSE_CODE_ARG = "response_code_arg";
    private final static String VIDEO_POSITION_ARG = "current_video_position";
    private final static String VIDEO_IS_PLAYED_ARG = "is_played";
    public final static String MIME_TYPE_VIDEO = "video";
    public final static String MIME_TYPE_IMAGE = "image";

    private int mediaAction;
    private String previewFilePath;
    private Context context = this;

    private FrameLayout photoPreviewContainer;
    private FrameLayout mVideoLayout;
    private VideoView mVideoView;
    private ImageView imagePreview;
    private ViewGroup buttonPanel;
    private View cropMediaAction;
    private TextView ratioChanger;


    private int currentPlaybackPosition = 0;
    private boolean isVideoPlaying = true;

    private int currentRatioIndex = 0;
    private float[] ratios;
    private String[] ratioLabels;


    public static Intent newIntent(Context context,
                                   @AnncaConfiguration.MediaAction int mediaAction,
                                   String filePath) {

        return new Intent(context, PreviewActivity.class)
                .putExtra(MEDIA_ACTION_ARG, mediaAction)
                .putExtra(FILE_PATH_ARG, filePath);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        String originalRatioLabel = getString(R.string.preview_controls_original_ratio_label);
        ratioLabels = new String[]{originalRatioLabel, "1:1", "4:3", "16:9"};
        ratios = new float[]{0f, 1f, 4f / 3f, 16f / 9f};


        mVideoLayout = (FrameLayout) findViewById(R.id.video_layout);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        photoPreviewContainer = (FrameLayout) findViewById(R.id.photo_preview_container);
        buttonPanel = (ViewGroup) findViewById(R.id.preview_control_panel);
        View confirmMediaResult = findViewById(R.id.confirm_media_result);
        View reTakeMedia = findViewById(R.id.re_take_media);
        View cancelMediaAction = findViewById(R.id.cancel_media_action);
        cropMediaAction = findViewById(R.id.crop_image);
        ratioChanger = (TextView) findViewById(R.id.ratio_image);
        ratioChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRatioIndex = (currentRatioIndex + 1) % ratios.length;
                ratioChanger.setText(ratioLabels[currentRatioIndex]);
            }
        });

        cropMediaAction.setVisibility(View.GONE);
        ratioChanger.setVisibility(View.GONE);

        if (cropMediaAction != null)
            cropMediaAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

        if (confirmMediaResult != null)
            confirmMediaResult.setOnClickListener(this);

        if (reTakeMedia != null)
            reTakeMedia.setOnClickListener(this);

        if (cancelMediaAction != null)
            cancelMediaAction.setOnClickListener(this);

        Bundle args = getIntent().getExtras();

        mediaAction = args.getInt(MEDIA_ACTION_ARG);
        previewFilePath = args.getString(FILE_PATH_ARG);

        if (mediaAction == AnncaConfiguration.MEDIA_ACTION_VIDEO) {
            displayVideo(savedInstanceState);
        } else if (mediaAction == AnncaConfiguration.MEDIA_ACTION_PHOTO) {
            displayImage();
        } else {
//            String mimeType = Utils.getMimeType(previewFilePath);
            String mimeType = Utils.getMimeType(previewFilePath, context);

            if (mimeType.contains(MIME_TYPE_VIDEO)) {
                displayVideo(savedInstanceState);
            } else if (mimeType.contains(MIME_TYPE_IMAGE)) {
                displayImage();
            } else finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveVideoParams(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void displayImage() {
//        videoPreviewContainer.setVisibility(View.GONE);
        mVideoLayout.setVisibility(View.GONE);
        showImagePreview();
        ratioChanger.setText(ratioLabels[currentRatioIndex]);
    }

    private void showImagePreview() {
        imagePreview = new ImageView(this);
        AnncaImageLoader.Builder builder = new AnncaImageLoader.Builder(this);
        builder.load(previewFilePath).build().into(imagePreview);
        photoPreviewContainer.removeAllViews();
        photoPreviewContainer.addView(imagePreview);
    }


    private void displayVideo(Bundle savedInstanceState) {
        cropMediaAction.setVisibility(View.GONE);
        ratioChanger.setVisibility(View.GONE);
        if (savedInstanceState != null) {
            loadVideoParams(savedInstanceState);
        }
        photoPreviewContainer.setVisibility(View.GONE);
        mVideoLayout.setVisibility(View.VISIBLE);


        //////////////////////////////////////////////////////////////////////////////////////
        /*mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (mMediaController == null) return false;
                        if (mMediaController.isShowing()) {
                            mMediaController.hide();
                            showButtonPanel(true);
                        } else {
                            showButtonPanel(false);
                            mMediaController.show();
                        }

                        *//*if(mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                        }else{
                            mediaPlayer.start();
                        }*//*
                        break;
                }
                return true;
            }
        });*/

//        mVideoView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        int aspectRatio = VideoInfo.AR_ASPECT_FIT_PARENT;
        GiraffePlayer player = mVideoView
                .setVideoPath(previewFilePath)
                .getPlayer();

        player.aspectRatio(aspectRatio);
        player.start();


    }


    private void saveVideoParams(Bundle outState) {
        try {
            if (mVideoView != null && mVideoView.getPlayer()!=null && mVideoView.getVideoInfo().getUri() != null) {
                outState.putInt(VIDEO_POSITION_ARG, mVideoView.getPlayer().getCurrentPosition());
                outState.putBoolean(VIDEO_IS_PLAYED_ARG, mVideoView.getPlayer().isPlaying());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVideoParams(Bundle savedInstanceState) {
        currentPlaybackPosition = savedInstanceState.getInt(VIDEO_POSITION_ARG, 0);
        isVideoPlaying = savedInstanceState.getBoolean(VIDEO_IS_PLAYED_ARG, true);
    }

    private void showButtonPanel(boolean show) {
        if (show) {
            buttonPanel.setVisibility(View.VISIBLE);
        } else {
            buttonPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        if (view.getId() == R.id.confirm_media_result) {
            resultIntent.putExtra(RESPONSE_CODE_ARG, BaseAnncaActivity.ACTION_CONFIRM).putExtra(FILE_PATH_ARG, previewFilePath);
        } else if (view.getId() == R.id.re_take_media) {
            deleteMediaFile();
            resultIntent.putExtra(RESPONSE_CODE_ARG, BaseAnncaActivity.ACTION_RETAKE);
        } else if (view.getId() == R.id.cancel_media_action) {
            deleteMediaFile();
            resultIntent.putExtra(RESPONSE_CODE_ARG, BaseAnncaActivity.ACTION_CANCEL);
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteMediaFile();
    }

    private boolean deleteMediaFile() {
        File mediaFile = new File(previewFilePath);
        return mediaFile.delete();
    }

    public static boolean isResultConfirm(@NonNull Intent resultIntent) {
        return BaseAnncaActivity.ACTION_CONFIRM == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }

    public static String getMediaFilePatch(@NonNull Intent resultIntent) {
        return resultIntent.getStringExtra(FILE_PATH_ARG);
    }

    public static boolean isResultRetake(@NonNull Intent resultIntent) {
        return BaseAnncaActivity.ACTION_RETAKE == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }

    public static boolean isResultCancel(@NonNull Intent resultIntent) {
        return BaseAnncaActivity.ACTION_CANCEL == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }


}