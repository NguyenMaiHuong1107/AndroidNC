package com.example.qldb.ActiVity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qldb.Adapter.ReservationAdapter;
import com.example.qldb.DatabaseHelper;
import com.example.qldb.R;
import com.example.qldb.ReservationModel;
import com.example.qldb.ReservationStatus;
import java.util.List;

public class ReservationHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_history);

        RecyclerView rv = findViewById(R.id.rvReservations);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        rv.setLayoutManager(new LinearLayoutManager(this));

        // lấy user hiện tại
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // load danh sách từ DB
        DatabaseHelper db = new DatabaseHelper(this);
        List<ReservationModel> list = db.getReservationsByUser(userId);

        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);

        ReservationAdapter adapter = new ReservationAdapter(this, list, new ReservationAdapter.OnItemAction() {
            @Override public void onClick(ReservationModel item) {
                // TODO: mở màn chi tiết nếu cần
            }

            @Override public void onCancel(ReservationModel item) {
                // Ví dụ hủy đơn đang pending
                new DatabaseHelper(ReservationHistoryActivity.this)
                        .updateReservationStatus(item.id, ReservationStatus.CANCELLED, userId);

                recreate(); // reload activity để refresh danh sách
            }
        });
        rv.setAdapter(adapter);
    }
}
