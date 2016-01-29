package pl.marchuck.ritual;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import pl.marchuck.ritual.face.FaceOverlayView;

public class SelectPhotoActivity extends AppCompatActivity {
    public static final String TAG = SelectPhotoActivity.class.getSimpleName();
    static Bitmap staticBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    FloatingActionButton mFab;
    //    ImageView mImageView;
    private FaceOverlayView mFaceOverlayView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        injectViews();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    void injectViews() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
//        mImageView = (ImageView) findViewById(R.id.imageView);
        mFaceOverlayView = (FaceOverlayView) findViewById(R.id.imageView);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

//            mImageView.setImageBitmap(imageBitmap);
            mFaceOverlayView.setBitmap(imageBitmap);
            mFaceOverlayView.setCallback(new FaceOverlayView.BitmapCallback() {
                @Override
                public void onReceived(Bitmap bmp) {
                    Log.d(TAG, "onReceived: ");
                    staticBitmap = bmp;
                    Intent i = new Intent(SelectPhotoActivity.this, MainActivity.class);

                    startActivity(i);
                    finish();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
