package jp.techacademy.yasufumi.hagiwara.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    int AutoSlide = 1;

    Timer mTimer;
    Handler mHandler = new Handler();

    ImageView mImageView;
    Button mStartButton;
    Button mForwardButton;
    Button mBackButton;

    Cursor mCursor;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mStartButton = (Button) findViewById(R.id.start_button);
        mForwardButton = (Button) findViewById(R.id.forward_button);
        mBackButton = (Button) findViewById(R.id.back_button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AutoSlide == 0){
                    AutoSlide = 1;
                    mStartButton.setText("再生");
                    mForwardButton.setEnabled(true);
                    mBackButton.setEnabled(true);
                } else {
                    AutoSlide = 0;
                    mStartButton.setText("停止");
                    mForwardButton.setEnabled(false);
                    mBackButton.setEnabled(false);
                }

                if (AutoSlide == 0){
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mCursor.isLast()) {
                                            if (mCursor.moveToFirst()) {
                                                int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                                                Long id = mCursor.getLong(fieldIndex);
                                                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                                mImageView.setImageURI(imageUri);
                                                Log.d("ANDROID", "URI:" + imageUri.toString());
                                            }
                                        } else if(mCursor.moveToNext()) {
                                            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                                            Long id = mCursor.getLong(fieldIndex);
                                            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                            mImageView.setImageURI(imageUri);
                                            Log.d("ANDROID", "URI:" + imageUri.toString());
                                        }
                                    }
                                });
                            }
                        }, 100, 2000);
                    }
                } else {
                    if (mTimer != null){
                        mTimer.cancel();
                        mTimer = null;
                    }
                }
            }
        });


        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCursor.isLast()){
                    if(mCursor.moveToFirst()){
                        int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = mCursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        mImageView.setImageURI(imageUri);
                        Log.d("ANDROID", "URI:" + imageUri.toString());
                    }
                } else if(mCursor.moveToNext()) {
                    int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = mCursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    mImageView.setImageURI(imageUri);
                    //Log.d("ANDROID", "URI:" + imageUri.toString());
                }
            }
        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCursor.isFirst()){
                    if (mCursor.moveToLast()){
                        int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = mCursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        mImageView.setImageURI(imageUri);
                        //Log.d("ANDROID", "URI:" + imageUri.toString());
                    }
                } else if(mCursor.moveToPrevious()) {
                    int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = mCursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    mImageView.setImageURI(imageUri);
                    //Log.d("ANDROID", "URI:" + imageUri.toString());
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults) {
            switch (requestCode) {
                case PERMISSIONS_REQUEST_CODE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getContentsInfo();
                    }
                    break;
                default:
                    break;
            }
    }


    private void getContentsInfo(){
        ContentResolver resolver = getContentResolver();
        mCursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if(mCursor.moveToFirst()) {
                int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = mCursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                mImageView.setImageURI(imageUri);
                //Log.d("ANDROID", "URI:" + imageUri.toString());
        }
    }

    @Override
    protected void onDestroy(){
        mCursor.close();
        super.onDestroy();
    }

}
