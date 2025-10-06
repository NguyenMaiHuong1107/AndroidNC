package com.example.qldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Users
        db.execSQL("CREATE TABLE Users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "full_name TEXT NOT NULL," +
                "phone TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "role TEXT NOT NULL DEFAULT 'user'," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // Tạo bảng Tables
        db.execSQL("CREATE TABLE Tables (" +
                "table_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "table_name TEXT," +
                "capacity INTEGER NOT NULL CHECK (capacity > 0)," +
                "status TEXT NOT NULL DEFAULT 'available' CHECK (status IN ('available', 'occupied', 'reserved'))," +
                "last_updated_by INTEGER," +
                "last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (last_updated_by) REFERENCES Users(user_id))");

        // Tạo bảng Reservations
        db.execSQL("CREATE TABLE Reservations (" +
                "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "table_id INTEGER," +
                "reservation_date DATE NOT NULL," +
                "time_slot TIME NOT NULL," +
                "num_people INTEGER NOT NULL CHECK (num_people > 0)," +
                "status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'cancelled', 'completed'))," +
                "notes TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "confirmed_by INTEGER," +
                "confirmed_at TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id)," +
                "FOREIGN KEY (table_id) REFERENCES Tables(table_id)," +
                "FOREIGN KEY (confirmed_by) REFERENCES Users(user_id))");

        // Tạo bảng Reservation_History
        db.execSQL("CREATE TABLE Reservation_History (" +
                "history_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "reservation_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "status_change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "new_status TEXT NOT NULL CHECK (new_status IN ('pending', 'confirmed', 'cancelled', 'completed'))," +
                "changed_by INTEGER," +
                "FOREIGN KEY (reservation_id) REFERENCES Reservations(reservation_id)," +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id)," +
                "FOREIGN KEY (changed_by) REFERENCES Users(user_id))");

        // Thêm admin mặc định với mật khẩu băm
        String adminPassword = hashPassword("123456");
        String userPassword = hashPassword("123456");
        db.execSQL("INSERT INTO Users (full_name, phone, password_hash, role) " +
                "VALUES ('Admin', '0999999999', '" + adminPassword + "', 'admin')");
        db.execSQL("INSERT INTO Users (full_name, phone, password_hash, role) " +
                "VALUES ('Nguyễn Mai Hương', '0123456789', '" + userPassword + "', 'user')");

        // Thêm dữ liệu mẫu cho bảng Tables
        db.execSQL("INSERT INTO Tables (table_name, capacity, status) VALUES ('Table 1', 4, 'available')");
        db.execSQL("INSERT INTO Tables (table_name, capacity, status) VALUES ('Table 2', 6, 'available')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Reservation_History");
        db.execSQL("DROP TABLE IF EXISTS Reservations");
        db.execSQL("DROP TABLE IF EXISTS Tables");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    // Kiểm tra số điện thoại đã tồn tại chưa
    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"user_id"};
        String selection = "phone = ?";
        String[] selectionArgs = {phone};
        Cursor cursor = db.query("Users", columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Thêm user mới
    public long insertUser(String fullName, String phone, String passwordHash, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("phone", phone);
        values.put("password_hash", passwordHash);
        values.put("role", role);
        long result = db.insert("Users", null, values);
        return result;
    }

    // Kiểm tra đăng nhập
    public boolean checkUser(String phone, String passwordHash) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"user_id"};
        String selection = "phone = ? AND password_hash = ?";
        String[] selectionArgs = {phone, passwordHash};
        Cursor cursor = db.query("Users", columns, selection, selectionArgs, null, null, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // Phương thức băm mật khẩu SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback nếu lỗi (không nên dùng trong production)
        }
    }
    // Trả về user_id nếu đúng thông tin, ngược lại trả -1
    public int getUserId(String phone, String hashedPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM Users WHERE phone=? AND password_hash=?",
                new String[]{phone, hashedPassword});

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return userId;
    }




}