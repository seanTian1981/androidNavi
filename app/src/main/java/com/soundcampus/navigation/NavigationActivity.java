package com.soundcampus.navigation;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.soundcampus.R;
import com.soundcampus.data.CampusLocation;
import com.soundcampus.data.NavigationInstruction;
import com.soundcampus.data.Route;
import com.soundcampus.utils.AccessibilityHelper;
import com.soundcampus.utils.LocationHelper;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {
    private Spinner destinationSpinner;
    private TextView currentLocationText;
    private TextView navigationStatus;
    private Button startNavigationButton;
    private Button stopNavigationButton;

    private MapManager mapManager;
    private LocationTracker locationTracker;
    private RouteCalculator routeCalculator;
    private AccessibilityHelper accessibilityHelper;

    private List<CampusLocation> locations;
    private Route currentRoute;
    private int currentInstructionIndex = 0;
    private boolean isNavigating = false;
    private Handler navigationHandler;
    private Runnable navigationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        initializeViews();
        initializeManagers();
        loadLocations();
        setupListeners();
    }

    private void initializeViews() {
        destinationSpinner = findViewById(R.id.destinationSpinner);
        currentLocationText = findViewById(R.id.currentLocationText);
        navigationStatus = findViewById(R.id.navigationStatus);
        startNavigationButton = findViewById(R.id.startNavigationButton);
        stopNavigationButton = findViewById(R.id.stopNavigationButton);
    }

    private void initializeManagers() {
        mapManager = new MapManager(this);
        locationTracker = new LocationTracker(this);
        routeCalculator = new RouteCalculator();
        accessibilityHelper = new AccessibilityHelper(this);
        navigationHandler = new Handler();

        accessibilityHelper.speak(getString(R.string.navigation_title));
    }

    private void loadLocations() {
        locations = mapManager.getAllLocations();
        ArrayAdapter<CampusLocation> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                locations
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(adapter);

        locationTracker.startTracking(new LocationTracker.LocationUpdateCallback() {
            @Override
            public void onLocationUpdate(Location location) {
                updateCurrentLocation(location);
            }

            @Override
            public void onLocationError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(NavigationActivity.this, error, Toast.LENGTH_SHORT).show();
                    accessibilityHelper.speak(error);
                });
            }
        });
    }

    private void setupListeners() {
        startNavigationButton.setOnClickListener(v -> startNavigation());
        stopNavigationButton.setOnClickListener(v -> stopNavigation());
    }

    private void updateCurrentLocation(Location location) {
        runOnUiThread(() -> {
            CampusLocation nearest = mapManager.findNearestLocation(
                    location.getLatitude(),
                    location.getLongitude()
            );

            if (nearest != null) {
                double distance = LocationHelper.calculateDistance(
                        location.getLatitude(),
                        location.getLongitude(),
                        nearest.getLatitude(),
                        nearest.getLongitude()
                );

                String locationText = getString(R.string.current_location) + ": " +
                        nearest.getName() + " (" + String.format("%.0f米", distance) + ")";
                currentLocationText.setText(locationText);
            }

            if (isNavigating && currentRoute != null) {
                updateNavigationProgress(location);
            }
        });
    }

    private void startNavigation() {
        if (!locationTracker.hasLocation()) {
            Toast.makeText(this, R.string.error_no_location, Toast.LENGTH_SHORT).show();
            accessibilityHelper.speak(getString(R.string.error_no_location));
            return;
        }

        CampusLocation destination = (CampusLocation) destinationSpinner.getSelectedItem();
        if (destination == null) {
            return;
        }

        Location currentLoc = locationTracker.getCurrentLocation();
        CampusLocation start = mapManager.findNearestLocation(
                currentLoc.getLatitude(),
                currentLoc.getLongitude()
        );

        if (start == null) {
            Toast.makeText(this, R.string.error_no_route, Toast.LENGTH_SHORT).show();
            accessibilityHelper.speak(getString(R.string.error_no_route));
            return;
        }

        currentRoute = routeCalculator.calculateRoute(start, destination);
        currentInstructionIndex = 0;
        isNavigating = true;

        startNavigationButton.setVisibility(View.GONE);
        stopNavigationButton.setVisibility(View.VISIBLE);

        String startMessage = getString(R.string.navigation_started) + "，" +
                getString(R.string.destination) + ": " + destination.getName();
        accessibilityHelper.speak(startMessage);

        announceNextInstruction();
        startNavigationUpdates();
    }

    private void stopNavigation() {
        isNavigating = false;
        currentRoute = null;
        currentInstructionIndex = 0;

        if (navigationRunnable != null) {
            navigationHandler.removeCallbacks(navigationRunnable);
        }

        startNavigationButton.setVisibility(View.VISIBLE);
        stopNavigationButton.setVisibility(View.GONE);
        navigationStatus.setText("");

        accessibilityHelper.speak(getString(R.string.navigation_stopped));
    }

    private void startNavigationUpdates() {
        navigationRunnable = new Runnable() {
            @Override
            public void run() {
                if (isNavigating && locationTracker.hasLocation()) {
                    Location current = locationTracker.getCurrentLocation();
                    updateNavigationProgress(current);
                    navigationHandler.postDelayed(this, 5000);
                }
            }
        };
        navigationHandler.postDelayed(navigationRunnable, 5000);
    }

    private void updateNavigationProgress(Location currentLocation) {
        if (currentRoute == null || currentInstructionIndex >= currentRoute.getInstructions().size()) {
            return;
        }

        double distanceToDestination = LocationHelper.calculateDistance(
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                currentRoute.getDestination().getLatitude(),
                currentRoute.getDestination().getLongitude()
        );

        if (distanceToDestination < 10) {
            arriveAtDestination();
            return;
        }

        NavigationInstruction currentInstruction = currentRoute.getInstructions().get(currentInstructionIndex);
        
        if (distanceToDestination < currentInstruction.getDistanceMeters() * 0.5) {
            currentInstructionIndex++;
            if (currentInstructionIndex < currentRoute.getInstructions().size()) {
                announceNextInstruction();
            }
        }
    }

    private void announceNextInstruction() {
        if (currentRoute == null || currentInstructionIndex >= currentRoute.getInstructions().size()) {
            return;
        }

        NavigationInstruction instruction = currentRoute.getInstructions().get(currentInstructionIndex);
        
        String message;
        if (instruction.getDirection() == NavigationInstruction.Direction.ARRIVED) {
            message = instruction.getDescription();
        } else {
            message = String.format(getString(R.string.instruction_format),
                    instruction.getDistanceMeters(),
                    instruction.getDirectionText());
        }

        navigationStatus.setText(message);
        accessibilityHelper.speak(message);
    }

    private void arriveAtDestination() {
        isNavigating = false;
        navigationStatus.setText(R.string.arrived);
        accessibilityHelper.speak(getString(R.string.arrived));

        startNavigationButton.setVisibility(View.VISIBLE);
        stopNavigationButton.setVisibility(View.GONE);

        if (navigationRunnable != null) {
            navigationHandler.removeCallbacks(navigationRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTracker != null) {
            locationTracker.stopTracking();
        }
        if (mapManager != null) {
            mapManager.close();
        }
        if (accessibilityHelper != null) {
            accessibilityHelper.shutdown();
        }
        if (navigationRunnable != null) {
            navigationHandler.removeCallbacks(navigationRunnable);
        }
    }
}
