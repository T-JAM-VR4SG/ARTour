package bmtreuherz.artour.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.estimote.coresdk.cloud.model.Telemetry;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import android.support.design.widget.Snackbar;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.Session;
import bmtreuherz.artour.Activities.DescriptionActivity;
import java.io.File;
import android.media.MediaPlayer;
import com.github.kittinunf.fuel.Fuel;
import java.lang.System;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import bmtreuherz.artour.Utilities.DemoUtils;
import bmtreuherz.artour.R;

//import static com.google.android.filament.Texture.Format.R;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
    private static final int RC_PERMISSIONS = 0x123;

    private boolean hasfinishedloading = false;
    private boolean hasplacedbox = false;
    private boolean installRequested;
    private ArSceneView arsceneview;
    private GestureDetector gestureDetector;
    private Snackbar loadingMessageSnackbar = null;
    //private ModelRenderable andyRenderable;
   // private ViewRenderable testViewRenderable;
    private ViewRenderable textboxRenderable;
    private DescriptionActivity test = new DescriptionActivity();

//    public void Test() {
//        File f = new File(getFilesDir(), "test.mp3");
//        Fuel.download("http://sites.clas.ufl.edu/vrsocialgood/files/2018/11/en_marston.mp3")
//                .destination({
//            response, url -> f
//        });
//    }

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        test.setAudioURL("http://sites.clas.ufl.edu/vrsocialgood/files/2018/11/en_marston.mp3");
//        test.getAudio();

        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        //setContentView(R.layout.activity_ux);
        setContentView(R.layout.activity_ar_ux);
        arsceneview = findViewById(R.id.ar_scene_view);

        CompletableFuture<ViewRenderable> textboxstage =
                ViewRenderable.builder().setView(this,R.layout.sceneform).build();
        CompletableFuture.allOf(textboxstage)
                .handle(
                        (notUsed, throwable) -> {
                            if(throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderable", throwable);
                                return null;
                            }
                            try {
                                textboxRenderable = textboxstage.get();
                                hasfinishedloading = true;
                            }
                            catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderable", ex);
                            }
                            return null;
                        });
        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                onSingleTap(e);
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });

        arsceneview.getScene().setOnTouchListener(
                (HitTestResult hittestresult, MotionEvent event) -> {
                    if (!hasplacedbox) {
                        return gestureDetector.onTouchEvent(event);
                    }
                    return false;
                }
        );
        arsceneview
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (loadingMessageSnackbar == null) {
                                return;
                            }

                            Frame frame = arsceneview.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    hideLoadingMessage();
                                }
                            }
                        });
        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        HelloSceneformActivity.this.findViewById(android.R.id.content),
                        "finding plane",
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }
    private void onSingleTap(MotionEvent tap) {
        if (!hasfinishedloading) {
            // We can't do anything yet.
            return;
        }
        Frame frame = arsceneview.getArFrame();
        if (frame != null) {
            if(!hasplacedbox && tryPlacebox(tap, frame)) {
                hasplacedbox = true;
            }
        }
    }
    private boolean tryPlacebox(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    // Create the Anchor.
                    Anchor anchor = hit.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arsceneview.getScene());
                    Node box = new Node();
                    box.setRenderable(textboxRenderable);
                    box.setLocalPosition(new Vector3(0.0f, 0.25f, 0.0f));

                    anchorNode.addChild(box);
                    return true;
                }
            }
        }
        return false;
    }

        @Override
        protected void onResume () {
            super.onResume();
            if (arsceneview == null) {
                return;
            }

            if (arsceneview.getSession() == null) {
                // If the session wasn't created yet, don't resume rendering.
                // This can happen if ARCore needs to be updated or permissions are not granted yet.
                try {
                    Session session = DemoUtils.createArSession(this, installRequested);
                    if (session == null) {
                        installRequested = DemoUtils.hasCameraPermission(this);
                        return;
                    } else {
                        arsceneview.setupSession(session);
                    }
                } catch (UnavailableException e) {
                    DemoUtils.handleSessionException(this, e);
                }
            }

            try {
                arsceneview.resume();
            } catch (CameraNotAvailableException ex) {
                DemoUtils.displayError(this, "Unable to get camera", ex);
                finish();
                return;
            }

            if (arsceneview.getSession() != null) {
                showLoadingMessage();
            }
        }

    @Override
    public void onPause() {
        super.onPause();
        if (arsceneview != null) {
            arsceneview.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (arsceneview != null) {
            arsceneview.destroy();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!DemoUtils.hasCameraPermission(this)) {
            if (!DemoUtils.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                DemoUtils.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

}

