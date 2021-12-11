package com.example.imagesphereconversion;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imagesphereconversion.OpenCvTutorial.ContourGeneration;
import com.example.imagesphereconversion.OpenCvTutorial.OpeningCamera;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap grayScaleBitmap, imageBitmap;

    @Override
    protected void onStart() {
        super.onStart();

        // Opening OpenCvTutorial
        Intent intent = new Intent(getApplicationContext(), ContourGeneration.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewInit();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetButtonAction();
            }
        });

        openCvChecking();

        openCvCameraOpen();

    }

    private void openCvCameraOpen() {
        // Opening camera using openCv
    /*
        Mat -> Used to store value of an image (0/1)
            -> Contaings image data
            -> It is n directional array
    */

        // Initialization
        // May myMat = new Mat(row,col, array type);
        // Mat myMat = new Mat();
        Mat myMat = new Mat();
    }

    private void openCvChecking() {
        // Checking OpneCv working or not
        if(OpenCVLoader.initDebug()){
            Toast.makeText(this, "Working OpenCv", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "OpenCv Not working", Toast.LENGTH_SHORT).show();
        }
    }


    private void SetButtonAction() {
        if (button.getText().equals("Get Image")) {
            GetImage();
        } else if (button.getText().equals("Get GrayScale")) {
            GetGrayScale();
        }
    }

    private void GetGrayScale() {

/*
        int width, height;
        height = imageBitmap.getHeight();
        width = imageBitmap.getWidth();
        grayScaleBitmap = Bitmap.createBitmap(width, height, imageBitmap.getConfig());

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int p = imageBitmap.getPixel(i, j);
                int r = Color.red(p);
                int g = Color.green(p);
                int b = Color.blue(p);
                int alpha = Color.alpha(p);
                r = 0;
                g += 150;
                b = 0;
                alpha = 0;

                grayScaleBitmap.setPixel(i,j,Color.argb(Color.alpha(p), r, g, b));
            }
        }
        imageView.setImageBitmap(grayScaleBitmap);
 */

        int width, height;
        height = imageBitmap.getHeight();
        width = imageBitmap.getWidth();
        grayScaleBitmap = Bitmap.createBitmap(width, height, imageBitmap.getConfig());
        Canvas c = new Canvas(grayScaleBitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(grayScaleBitmap, 0, 0, paint);
        imageView.setImageBitmap(grayScaleBitmap);
    }

    private void GetImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                button.setText("Get GrayScale");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void viewInit() {
        button = findViewById(R.id.activity_main_get_image);
        imageView = findViewById(R.id.activity_main_image_view);
    }
}