package com.example.qldb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {
    private TextView txtFullName, txtPhone;
    private Button btnLogout;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Ánh xạ view
        txtFullName = findViewById(R.id.txtFullName);
        txtPhone = findViewById(R.id.txtPhone);
        btnLogout = findViewById(R.id.btnLogout);

        dbHelper = new DatabaseHelper(this);

        // Lấy user_id từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        Log.d("AccountActivity", "User ID: " + userId);

        if (userId != -1) {
            loadUserInfo(userId);
        } else {
            Log.d("AccountActivity", "No user_id found, redirecting to SignInActivity");
            Intent intent = new Intent(AccountActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        // Xử lý nút Đăng xuất
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // Xóa session
            editor.apply();

            Toast.makeText(AccountActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AccountActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });

        LinearLayout layoutAdd = findViewById(R.id.layoutAdd);

        layoutAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }



    // Hàm lấy dữ liệu từ DB và set lên TextView
    private void loadUserInfo(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT full_name, phone FROM Users WHERE user_id = ?",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            String fullName = cursor.getString(0);
            String phone = cursor.getString(1);
            Log.d("AccountActivity", "User Info: fullName=" + fullName + ", phone=" + phone);

            txtFullName.setText(fullName);
            txtPhone.setText(phone);
        } else {
            Log.d("AccountActivity", "No user found for user_id: " + userId);
        }
        cursor.close();
        db.close();
    }

}
