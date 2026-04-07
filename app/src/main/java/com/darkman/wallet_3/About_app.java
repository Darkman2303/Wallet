package com.darkman.wallet_3;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.darkman.wallet_3.databinding.ActivityAboutAppBinding;

public class About_app extends AppCompatActivity {

    private ActivityAboutAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding.backBt.setOnClickListener(v -> finish());

        String appName = getString(R.string.app_name);
        String packageName = getPackageName();
        String versionName;
        long longVersionCode;

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(packageName, 0);
            versionName = pInfo.versionName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                longVersionCode = pInfo.getLongVersionCode();
            } else {
                longVersionCode = pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "Unknown";
            longVersionCode = -1;
        }

        binding.appInfoItle.setText(getString(R.string.about_app_title, appName));

        binding.appInfoText.setText(getString(R.string.app_info_full_format,
                getString(R.string.app_info_name),
                appName,
                getString(R.string.app_info_package),
                packageName,
                getString(R.string.app_info_version),
                versionName,
                longVersionCode,
                getString(R.string.app_info_device),
                Build.MANUFACTURER,
                Build.MODEL,
                getString(R.string.app_info_android),
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                getString(R.string.app_info_developer),
                getString(R.string.developer_name),
                getString(R.string.app_info_support),
                getString(R.string.developer_email)
        ));
    }
}
