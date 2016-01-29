package pl.marchuck.ritual.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by Łukasz Marczak
 *
 * @since 29.01.16
 */
public class FaceOverlayView extends View {

    public static final String TAG = FaceOverlayView.class.getSimpleName();
    private Bitmap mBitmap;
    private Bitmap outputBitmap;
    private SparseArray<Face> mFaces;

    public interface BitmapCallback {
        void onReceived(Bitmap bmp);
    }

    BitmapCallback callback;

    public void setCallback(BitmapCallback callback) {
        this.callback = callback;
    }

    public FaceOverlayView(Context context) {
        this(context, null);
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        FaceDetector detector = new FaceDetector.Builder(getContext())
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        if (!detector.isOperational()) {
            //Handle contingency
            Log.e(TAG, "Handle contingency");
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            mFaces = detector.detect(frame);
            detector.release();
        }
//        logFaceData();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((mBitmap != null) && (mFaces != null)) {
//            double scale = getScale(canvas);
            getBitmap();
        }
    }

    private double getScale(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int) (imageWidth * scale), (int) (imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }
//    public void drawFace() {
//        Bitmap dstBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(dstBitmap);
//        canvas.getScale(srcBitmap, -left, -top, mPaint);
//        mPaint.setShader(new BitmapShader(dstBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//        dstBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(dstBitmap);
//        canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, mPaint);
//    }
//
//    public void dd() {
//        Bitmap bitmap = Bitmap.createBitmap(imgView.getWidth(), imgView.getHeight(), Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);
//        imgView.draw(canvas);
//        Bitmap result = Bitmap.createBitmap(bitmap, imgView.getLeft() + 10, imgView.getTop() + 50, imgView.getWidth() - 20, imgView.getHeight() - 100);
//        bitmap.recycle();
//    }

    public void getBitmap() {
        //paint should be defined as a member variable rather than
        //being created on each onDraw request, but left here for
        //emphasis.
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        Face face = mFaces.valueAt(0);

        left = (int) (face.getPosition().x);
        top = (int) (face.getPosition().y);
        right = (int) ((face.getPosition().x + face.getWidth()));
        bottom = (int) ((face.getPosition().y + face.getHeight()));

//        canvas.drawRect(left, top, right, bottom, paint);
        Matrix mMatrix = new Matrix();
        try {
            Log.d(TAG, "getBitmap: left = " + left);
            Log.d(TAG, "getBitmap: right = " + right);
            Log.d(TAG, "getBitmap: bottom = " + bottom);
            Log.d(TAG, "getBitmap: top = " + top);
            Log.d(TAG, "getBitmap: height = " + mBitmap.getHeight());
            Log.d(TAG, "getBitmap: width = " + mBitmap.getWidth());
            if (top < 0) top = 0;
            if (left < 0) left = 0;
            Bitmap bmp = Bitmap.createBitmap(mBitmap, left, top, right, bottom, mMatrix, false);
            if (callback != null) callback.onReceived(bmp);
        } catch (Exception c) {
            Log.d(TAG, "getBitmap: " + c.getMessage());
            c.printStackTrace();
        }
    }
}
