package com.example.callscreener;

import android.Manifest;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private final String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE
    };
    
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    
    SharedPreferences sharedPreferences;
    EditText etNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRequestPermissions = findViewById(R.id.btnRequestPermissions);
        Button btnSetCallScreener = findViewById(R.id.btnSetCallScreener);
        Button btnSetDefaultDialer = findViewById(R.id.btnSetDefaultDialer);
        etNumber = findViewById(R.id.etNumber);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnBlockNumber = findViewById(R.id.btnBlockNumber);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Permission request launcher
        requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = result.values().stream().allMatch(granted -> granted);
                    Toast.makeText(this, allGranted ? "Permissions granted!" : "Permissions denied!", Toast.LENGTH_SHORT).show();
                });

        // Button click listeners
        btnRequestPermissions.setOnClickListener(v -> requestPhonePermissions());
        btnSetCallScreener.setOnClickListener(v -> requestDefaultCallScreening());
        btnSetDefaultDialer.setOnClickListener(v -> requestDefaultDialer());
        btnSave.setOnClickListener(v -> saveLength());
        btnBlockNumber.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BlockList.class));
        });
        
        int savedLength = sharedPreferences.getInt("savedLength", 0);
        etNumber.setText(String.valueOf(savedLength));
    }

    private void requestPhonePermissions() {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(permissions);
        } else {
            Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestDefaultCallScreening() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
            startActivityForResult(intent, 101);
        } else {
            Toast.makeText(this, "Already set as Call Screener", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestDefaultDialer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                startActivityForResult(intent, 102);
            } else {
                Toast.makeText(this, "Already set as Default Phone App", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void saveLength() {
        String input = etNumber.getText().toString();
        if (!input.isEmpty()) {
            int number = Integer.parseInt(input);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("savedLength", number);
            editor.apply();
            Toast.makeText(this, "Length saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter an Int", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Toast.makeText(this, resultCode == RESULT_OK ? "Call Screener Set!" : "Call Screener Not Set", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 102) {
            Toast.makeText(this, resultCode == RESULT_OK ? "Dialer Set!" : "Dialer Not Set", Toast.LENGTH_SHORT).show();
        }
    }
}