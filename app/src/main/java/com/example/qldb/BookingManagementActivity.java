package com.example.qldb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BookingManagementActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private BookingViewPagerAdapter viewPagerAdapter;
    private int currentAdminId = 1; // ID admin giả định

    // Các nút trong bottom navigation tự thiết kế
    private LinearLayout layoutHome, layoutBooking, layoutManage, layoutAccount, layoutAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_management);

        // Gán view
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        // Gán các layout trong thanh điều hướng
        layoutHome = findViewById(R.id.layoutHome);
        layoutBooking = findViewById(R.id.layoutBooking);
        layoutManage = findViewById(R.id.layoutManage);
        layoutAccount = findViewById(R.id.layoutAccount);
        layoutAdd = findViewById(R.id.layoutAdd);

        // Cài đặt ViewPager Adapter
        viewPagerAdapter = new BookingViewPagerAdapter(this, currentAdminId);
        viewPager.setAdapter(viewPagerAdapter);

        // Kết nối TabLayout với ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Đơn chờ xác nhận");
            } else {
                tab.setText("Đơn đã xác nhận");
            }
        }).attach();

        // Xử lý sự kiện click cho các nút điều hướng
        setupBottomNavEvents();
    }

    private void setupBottomNavEvents() {
        // Nút Trang chủ
        layoutHome.setOnClickListener(v -> {

        });

        // Nút Quản lý Đặt chỗ
        layoutManage.setOnClickListener(v -> {
            Intent intent = new Intent(BookingManagementActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
        });

        // Nút đặt chỗ
        layoutBooking.setOnClickListener(v -> {
            // Trang hiện tại => không cần chuyển
        });

        // Nút Tài khoản
        layoutAccount.setOnClickListener(v -> {
            Intent intent = new Intent(BookingManagementActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        });

        // Nút Cài đặt / Thêm mới
        layoutAdd.setOnClickListener(v -> {
        });
    }
}
