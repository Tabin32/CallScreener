package com.example.callscreener;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BlockList extends AppCompatActivity {

    private EditText etNumber;
    private Button btnBlock;
    private ListView lvBlockedNumbers;
    private ArrayList<String> blockedNumbersList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_block_list);

        etNumber = findViewById(R.id.etNumber);
        btnBlock = findViewById(R.id.btnBlock);
        lvBlockedNumbers = findViewById(R.id.lvBlockedNumbers);

        sharedPreferences = getSharedPreferences("BlockedNumbers", MODE_PRIVATE);
        blockedNumbersList = new ArrayList<>(loadBlockedNumbers());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, blockedNumbersList);
        lvBlockedNumbers.setAdapter(adapter);

        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockNumber();
            }
        });
        
        lvBlockedNumbers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteNumber(position);
                return true;
            }
        });
    }

    private void blockNumber() {
        String number = etNumber.getText().toString().trim();
        if (!number.isEmpty() && !blockedNumbersList.contains(number)) {
            blockedNumbersList.add(number);
            saveBlockedNumbers();
            adapter.notifyDataSetChanged();
            etNumber.setText("");
            Toast.makeText(this, "Number Blocked!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void deleteNumber(int position) {
        String removedNumber = blockedNumbersList.get(position);
        blockedNumbersList.remove(position);
        saveBlockedNumbers();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Unblocked: " + removedNumber, Toast.LENGTH_SHORT).show();
    }

    private void saveBlockedNumbers() {
        Set<String> set = new HashSet<>(blockedNumbersList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("blockedList", set);
        editor.apply();
    }

    private Set<String> loadBlockedNumbers() {
        return sharedPreferences.getStringSet("blockedList", new HashSet<>());
    }
}