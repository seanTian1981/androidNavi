package com.soundcampus;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.soundcampus.navigation.NavigationActivity;
import com.soundcampus.ocr.OcrActivity;
import com.soundcampus.utils.AccessibilityHelper;
import com.soundcampus.utils.PermissionManager;

public class MainActivity extends AppCompatActivity {
    private Button navigationButton;
    private Button ocrButton;
    private Button settingsButton;
    private AccessibilityHelper accessibilityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeAccessibility();
        checkPermissions();
        setupListeners();
    }

    private void initializeViews() {
        navigationButton = findViewById(R.id.navigationButton);
        ocrButton = findViewById(R.id.ocrButton);
        settingsButton = findViewById(R.id.settingsButton);
    }

    private void initializeAccessibility() {
        accessibilityHelper = new AccessibilityHelper(this);
        accessibilityHelper.speak(getString(R.string.welcome_message));
    }

    private void checkPermissions() {
        if (!PermissionManager.hasAllPermissions(this)) {
            PermissionManager.requestPermissions(this);
        }
    }

    private void setupListeners() {
        navigationButton.setOnClickListener(v -> {
            if (PermissionManager.hasLocationPermission(this)) {
                startNavigationActivity();
            } else {
                Toast.makeText(this, R.string.permission_location, Toast.LENGTH_LONG).show();
                accessibilityHelper.speak(getString(R.string.permission_location));
                PermissionManager.requestPermissions(this);
            }
        });

        ocrButton.setOnClickListener(v -> {
            if (PermissionManager.hasCameraPermission(this)) {
                startOcrActivity();
            } else {
                Toast.makeText(this, R.string.permission_camera, Toast.LENGTH_LONG).show();
                accessibilityHelper.speak(getString(R.string.permission_camera));
                PermissionManager.requestPermissions(this);
            }
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "设置功能将在后续版本中提供", Toast.LENGTH_SHORT).show();
            accessibilityHelper.speak("设置功能将在后续版本中提供");
        });
    }

    private void startNavigationActivity() {
        accessibilityHelper.speak(getString(R.string.navigation_button));
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    private void startOcrActivity() {
        accessibilityHelper.speak(getString(R.string.ocr_button));
        Intent intent = new Intent(this, OcrActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionManager.PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                accessibilityHelper.speak(getString(R.string.permission_denied));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accessibilityHelper != null && !accessibilityHelper.isSpeaking()) {
            accessibilityHelper.speak(getString(R.string.welcome_message));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessibilityHelper != null) {
            accessibilityHelper.shutdown();
        }
    }
}
