package com.example.qldb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {

    private List<Reservation> reservationList;
    private Context context;
    private DatabaseHelper dbHelper;
    private int adminId; // ID của admin đang đăng nhập

    // Interface để Fragment có thể lắng nghe sự kiện
    public interface OnItemStatusChangedListener {
        void onItemStatusChanged();
    }
    private OnItemStatusChangedListener statusChangedListener;

    public ReservationsAdapter(Context context, List<Reservation> reservationList, int adminId, OnItemStatusChangedListener listener) {
        this.context = context;
        this.reservationList = reservationList;
        this.dbHelper = new DatabaseHelper(context);
        this.adminId = adminId;
        this.statusChangedListener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);

        holder.tvUserName.setText(reservation.getUserName());
        holder.tvDateTime.setText(reservation.getDateTime());
        holder.tvNumPeople.setText("Số lượng: " + reservation.getNumPeople());

        // Ẩn/hiện các nút tùy theo trạng thái
        if (reservation.getStatus().equals("pending")) {
            holder.layoutButtons.setVisibility(View.VISIBLE);
        } else {
            holder.layoutButtons.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click
        holder.btnConfirm.setOnClickListener(v -> {
            boolean success = dbHelper.updateReservationStatus(reservation.getReservationId(), "confirmed", adminId);
            if (success) {
                Toast.makeText(context, "Đã xác nhận", Toast.LENGTH_SHORT).show();
                removeItem(position); // Xóa khỏi danh sách 'pending'
                statusChangedListener.onItemStatusChanged(); // Báo cho Fragment biết
            } else {
                Toast.makeText(context, "Lỗi xác nhận", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            // Tương tự, bạn có thể cập nhật status thành 'cancelled'
            boolean success = dbHelper.updateReservationStatus(reservation.getReservationId(), "cancelled", adminId);
            if (success) {
                Toast.makeText(context, "Đã hủy", Toast.LENGTH_SHORT).show();
                removeItem(position);
                statusChangedListener.onItemStatusChanged();
            } else {
                Toast.makeText(context, "Lỗi hủy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    // Xóa item khỏi list và thông báo cho adapter
    private void removeItem(int position) {
        reservationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reservationList.size());
    }

    // ViewHolder
    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDateTime, tvNumPeople;
        LinearLayout layoutButtons;
        ImageView btnConfirm, btnCancel;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvNumPeople = itemView.findViewById(R.id.tv_num_people);
            layoutButtons = itemView.findViewById(R.id.layout_buttons);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}