package com.example.projectprm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.example.projectprm.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex]);
        }
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("About");
        binding.aboutText.setText(aboutText());

    }

    private String aboutText() {
        return "App Nghe nhạc này được phát triển bởi 5 thành viên : Lê Hoài Nam, Nguyễn Hải Nam, Mai Ngọc Giang, Nguyễn Tuấn Linh, Đoàn Trọng Nhân" +
                "\n\nApp này có một số tính năng sau :"
                + "\n Tải nhạc lên từ file"
                + "\n Nghe nhạc"
                + "\n Thêm vào danh sách phát"
                + "\n Thêm vào mục ưa thích"
                + "\n Cài đặt thời gian nghe nhạc"
                + "\n Sắp xếp nhạc theo tiêu đề và thời gian"
                + "\n Thay đổi màu nền app"
                ;
    }
}