package com.soundcampus.ocr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.soundcampus.R;
import com.soundcampus.utils.AccessibilityHelper;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OcrActivity extends AppCompatActivity {
    private static final String TAG = "OcrActivity";
    private static final int CAMERA_PERMISSION_CODE = 100;

    private PreviewView cameraPreview;
    private TextView statusText;
    private TextView recognizedText;
    private Button captureButton;
    private Button repeatButton;

    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private ExecutorService cameraExecutor;
    private TextRecognizer textRecognizer;
    private AccessibilityHelper accessibilityHelper;

    private String lastRecognizedText = "";
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        initializeViews();
        initializeComponents();

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void initializeViews() {
        cameraPreview = findViewById(R.id.cameraPreview);
        statusText = findViewById(R.id.statusText);
        recognizedText = findViewById(R.id.recognizedText);
        captureButton = findViewById(R.id.captureButton);
        repeatButton = findViewById(R.id.repeatButton);
    }

    private void initializeComponents() {
        cameraExecutor = Executors.newSingleThreadExecutor();
        textRecognizer = new TextRecognizer();
        accessibilityHelper = new AccessibilityHelper(this);

        accessibilityHelper.speak(getString(R.string.ocr_title));
        accessibilityHelper.speakQueued(getString(R.string.please_aim_at_text));

        captureButton.setOnClickListener(v -> captureAndRecognize());
        repeatButton.setOnClickListener(v -> repeatLastText());
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, R.string.permission_camera, Toast.LENGTH_LONG).show();
                accessibilityHelper.speak(getString(R.string.permission_camera));
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(this, R.string.error_camera, Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        try {
            cameraProvider.unbindAll();
            camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
            );
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    private void captureAndRecognize() {
        if (isProcessing) {
            return;
        }

        isProcessing = true;
        statusText.setText(R.string.processing);
        accessibilityHelper.speak(getString(R.string.processing));

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                textRecognizer.recognizeText(image, new TextRecognizer.RecognitionCallback() {
                    @Override
                    public void onSuccess(String text) {
                        runOnUiThread(() -> handleRecognitionSuccess(text));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> handleRecognitionFailure(e));
                    }
                });
                
                imageAnalysis.clearAnalyzer();
            }
        });

        try {
            cameraProvider.unbindAll();
            
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());
            
            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();

            camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
            );
        } catch (Exception e) {
            Log.e(TAG, "Error binding camera for capture", e);
            handleRecognitionFailure(e);
        }
    }

    private void handleRecognitionSuccess(String text) {
        isProcessing = false;
        
        if (text == null || text.isEmpty()) {
            statusText.setText(R.string.no_text_found);
            recognizedText.setText("");
            accessibilityHelper.speak(getString(R.string.no_text_found));
        } else {
            lastRecognizedText = text;
            statusText.setText(R.string.text_recognized);
            recognizedText.setText(text);
            recognizedText.setVisibility(View.VISIBLE);
            repeatButton.setVisibility(View.VISIBLE);
            
            accessibilityHelper.speak(getString(R.string.text_recognized) + ": " + text);
        }
    }

    private void handleRecognitionFailure(Exception e) {
        isProcessing = false;
        statusText.setText(R.string.error_ocr);
        Toast.makeText(this, R.string.error_ocr, Toast.LENGTH_SHORT).show();
        accessibilityHelper.speak(getString(R.string.error_ocr));
        Log.e(TAG, "Recognition failed", e);
    }

    private void repeatLastText() {
        if (!lastRecognizedText.isEmpty()) {
            accessibilityHelper.speak(lastRecognizedText);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (textRecognizer != null) {
            textRecognizer.close();
        }
        if (accessibilityHelper != null) {
            accessibilityHelper.shutdown();
        }
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}
