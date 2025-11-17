package com.soundcampus.ocr;

import android.media.Image;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

public class TextRecognizer {
    private static final String TAG = "TextRecognizer";
    private com.google.mlkit.vision.text.TextRecognizer recognizer;

    public interface RecognitionCallback {
        void onSuccess(String text);
        void onFailure(Exception e);
    }

    public TextRecognizer() {
        recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
    }

    public void recognizeText(@NonNull ImageProxy imageProxy, RecognitionCallback callback) {
        @androidx.camera.core.ExperimentalGetImage
        Image mediaImage = imageProxy.getImage();
        
        if (mediaImage == null) {
            callback.onFailure(new Exception("图像为空"));
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String recognizedText = extractText(visionText);
                    callback.onSuccess(recognizedText);
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Text recognition failed", e);
                    callback.onFailure(e);
                    imageProxy.close();
                });
    }

    private String extractText(Text visionText) {
        StringBuilder result = new StringBuilder();
        
        for (Text.TextBlock block : visionText.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                result.append(line.getText()).append("\n");
            }
        }

        String text = result.toString().trim();
        Log.d(TAG, "Recognized text: " + text);
        return text;
    }

    public void close() {
        if (recognizer != null) {
            recognizer.close();
        }
    }
}
