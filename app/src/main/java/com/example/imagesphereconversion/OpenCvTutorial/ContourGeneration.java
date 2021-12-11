package com.example.imagesphereconversion.OpenCvTutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.imagesphereconversion.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ContourGeneration extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    int activeBackCamera = CameraBridgeViewBase.CAMERA_ID_BACK;

    int activeFrontCamera = CameraBridgeViewBase.CAMERA_ID_FRONT;
    Mat frame = null;
    Mat mat2 = new Mat(), mat3 = new Mat();
    Button getContour;
    int cameraId = 0;
    ImageButton flipCamera;
    boolean checkContour = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contour_generation);

        flipCamera = findViewById(R.id.contour_flip_camera);
        getContour = findViewById(R.id.contout_get_contour);
        // Initializing Cameraview
        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.contour_camera_view);

        if (ContextCompat.checkSelfPermission((ContourGeneration.this), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContourGeneration.this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            initializeCamera((JavaCameraView) cameraBridgeViewBase, activeBackCamera);
        }

        // Initializing BaseLoaderCallBack
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
//                        Toast.makeText(mAppContext, "Load Successful", Toast.LENGTH_SHORT).show();
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

        getContour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkContour = !checkContour;
            }
        });

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapCamera();
            }
        });

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mat2 = new Mat(width, height, CvType.CV_8UC4);
        mat3 = new Mat(width, height, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame = inputFrame.rgba();

        // Rotating camera to portrait mode
        Core.transpose(frame, mat2);
        Imgproc.resize(mat2, mat3, mat3.size(), 0, 0, 0);
        Core.flip(mat2, frame, 1);
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);

        if(checkContour){
            Imgproc.Canny(frame, frame, 125, 125);
        }

        return frame;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "OpenCv load failed", Toast.LENGTH_SHORT).show();
        } else if (baseLoaderCallback != null) {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera((JavaCameraView) cameraBridgeViewBase, activeBackCamera);
            } else {
                Toast.makeText(this, "Permission not granted!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeCamera(JavaCameraView javaCameraView, int activeCamera) {
        javaCameraView.setCameraPermissionGranted();
        javaCameraView.setCameraIndex(activeCamera);
        javaCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
    }

    private void swapCamera(){
        cameraId = cameraId ^ 1;
        cameraBridgeViewBase.disableView();
        cameraBridgeViewBase.setCameraIndex(cameraId);
        cameraBridgeViewBase.enableView();
    }
}