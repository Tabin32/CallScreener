package com.example.callscreener;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telecom.Call.Details;
import android.util.Log;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Set;

public class MyCallScreeningService extends CallScreeningService {

    @Override
    public void onScreenCall(Call.Details callDetails) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String phoneNumber = callDetails.getHandle() != null ? callDetails.getHandle().getSchemeSpecificPart() : "Unknown";
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> 
        Toast.makeText(getApplicationContext(), "Screening Call: " + phoneNumber, Toast.LENGTH_SHORT).show()
        );
        
        CallResponse.Builder responseBuilder = new CallResponse.Builder();
        int savedLength = sharedPreferences.getInt("savedLength", 0);
       
        if (phoneNumber.length()<savedLength || isBlockedNumber(phoneNumber)) {
            responseBuilder.setDisallowCall(true)
                           .setRejectCall(true)
                           .setSkipCallLog(false)
                           .setSkipNotification(false);
        } else {
            responseBuilder.setDisallowCall(false);
        }

        respondToCall(callDetails, responseBuilder.build());
    }

    private boolean isBlockedNumber(String phoneNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences("BlockedNumbers", MODE_PRIVATE);
        Set<String> blockedList = sharedPreferences.getStringSet("blockedList", new HashSet<>());
        return blockedList.contains(phoneNumber);
    }
}