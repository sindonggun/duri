package com.example.administrator.duri;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;


class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    //EFFECT
    public static final String EFFECT_AQUA="aqua";
    public static final String EFFECT_BLACKBOARD ="blackboard";
    public static final String EFFECT_MONO = "mono";
    public static final String EFFECT_NEGATIVE = "negative";
    public static final String EFFECT_NONE = "none";
    public static final String EFFECT_POSTERIZE ="posterize";
    public static final String EFFECT_SEPIA = "sepia";
    public static final String EFFECT_SOLARIZE = "solarize";
    public static final String EFFECT_WHITEBOARD = "whiteboard";

    //FLASH_MODE
    public static final String FLASH_MODE_AUTO = "auto";
    public static final String FLASH_MODE_OFF = "off";
    public static final String FLASH_MODE_ON = "on";
    public static final String FLASH_MODE_RED_EYE = "red-eye";
    public static final String FLASH_MODE_TORCH = "torch";

    //FOCUS_MODE
    public static final String FOCUS_MODE_AUTO = "auto";
    public static final String FOCUS_MODE_FIXED = "fixed";
    public static final String FOCUS_MODE_INFINITY = "infinity";
    public static final String FOCUS_MODE_MACRO = "macro";

    //SCENE_MODE
    public static final String SCENE_MODE_ACTION = "action";
    public static final String SCENE_MODE_AUTO = "auto";
    public static final String SCENE_MODE_BEACH = "beach";
    public static final String SCENE_MODE_CANDLELIGHT = "candlelight";
    public static final String SCENE_MODE_FIREWORKS = "fireworks";
    public static final String SCENE_MODE_LANDSCAPE = "landscape";
    public static final String SCENE_MODE_NIGHT = "night";
    public static final String SCENE_MODE_NIGHT_PORTRAIT = "night-portrait";
    public static final String SCENE_MODE_PARTY = "party";
    public static final String SCENE_MODE_PORTRAIT = "portrait";
    public static final String SCENE_MODE_SNOW = "snow";
    public static final String SCENE_MODE_SPORTS = "sports";
    public static final String SCENE_MODE_STEADYPHOTO = "steadyphoto";
    public static final String SCENE_MODE_SUNSET = "sunset";
    public static final String SCENE_MODE_THEATRE ="theatre";

    //WHITE_BALANCE
    public static final String WHITE_BALANCE_AUTO = "auto";
    public static final String WHITE_BALANCE_CLOUDY_DAYLIGHT = "cloudy-daylight";
    public static final String WHITE_BALANCE_DAYLIGHT = "daylight";
    public static final String WHITE_BALANCE_FLUORESCENT = "fluorescent";
    public static final String WHITE_BALANCE_INCANDESCENT = "incandescent";
    public static final String WHITE_BALANCE_SHADE = "shade";
    public static final String WHITE_BALANCE_TWILIGHT = "twilight";
    public static final String WHITE_BALANCE_WARM_FLUORESCENT="warm-fluorescent";

    Preview(Context context, SurfaceView sv ) {
        super(context);

        mSurfaceView = sv;
//        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void setCamera(Camera camera) {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }

        mCamera = camera;
        Log.i("test", String.valueOf(mCamera));
        if (mCamera != null) {
            int m_resWidth;
            int m_resHeight;
            List<Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = localSizes;
            requestLayout();

            // get Camera parameters
            Camera.Parameters params = mCamera.getParameters();
            params.setColorEffect(EFFECT_POSTERIZE);
            params.setPreviewFrameRate(10);
            params.setExposureCompensation(20);
            params.setWhiteBalance(WHITE_BALANCE_DAYLIGHT);
            mCamera.setParameters(params);

            Log.i("exposuremin", String.valueOf(params.getMinExposureCompensation()));
            Log.i("exposuremax", String.valueOf(params.getMaxExposureCompensation()));
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                // set the focus mode
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                // set Camera parameters

            }
            mCamera.setParameters(params);

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {

        //Toast.makeText(getContext(), "surfaceCreated", Toast.LENGTH_LONG).show();
        Log.d( "@@@", "surfaceCreated" );

        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if ( mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Size> allSizes = parameters.getSupportedPreviewSizes();
            Camera.Size size = allSizes.get(0); // get top size
            for (int i = 0; i < allSizes.size(); i++) {
                if (allSizes.get(i).width > size.width)
                    size = allSizes.get(i);
            }
            //set max Preview Size
            parameters.setPreviewSize(size.width, size.height);

            // Important: Call startPreview() to start updating the preview surface.
            // Preview must be started before you can take a picture.
            mCamera.startPreview();
        }

    }

}
