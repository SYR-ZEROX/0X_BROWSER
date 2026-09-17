package com.zerox.browser;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfilesActivity extends AppCompatActivity {

    private RecyclerView profilesList;
    private ProfileAdapter adapter;
    private Button addProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        profilesList = findViewById(R.id.profilesList);
        addProfileBtn = findViewById(R.id.addProfileBtn);

        // إعداد RecyclerView
        profilesList.setLayoutManager(new LinearLayoutManager(this));
        loadProfiles();

        // زر إضافة بروفايل
        addProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilesActivity.this, SettingsActivity.class);
            intent.putExtra("new_profile", true);
            startActivity(intent);
        });

        // زر الرجوع
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }

    private void loadProfiles() {
        List<String> profileNames = ProfileManager.getAllProfileNames(this);
        String activeName = ProfileManager.getActiveProfile(this).name;

        adapter = new ProfileAdapter(
                this,
                profileNames,
                activeName,
                // عند الضغط على "تشغيل"
                (profileName) -> {
                    ProfileManager.setActiveProfile(this, profileName);
                    Toast.makeText(this, "تم تفعيل: " + profileName, Toast.LENGTH_SHORT).show();
                    loadProfiles();
                },
                // عند الضغط على "تعديل"
                (profileName) -> {
                    Intent intent = new Intent(ProfilesActivity.this, SettingsActivity.class);
                    intent.putExtra("edit_profile", profileName);
                    startActivity(intent);
                },
                // عند الضغط على "حذف"
                (profileName) -> {
                    new AlertDialog.Builder(this)
                            .setTitle("حذف البروفايل")
                            .setMessage("هل أنت متأكد من حذف " + profileName + "؟")
                            .setPositiveButton("نعم", (dialog, which) -> {
                                ProfileManager.deleteProfile(this, profileName);
                                loadProfiles();
                                Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("لا", null)
                            .show();
                }
        );

        profilesList.setAdapter(adapter);
    }
}