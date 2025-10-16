package com.example.qldb.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qldb.R;
import com.example.qldb.ReservationModel;
import com.example.qldb.ReservationStatus;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.VH> {

    public interface OnItemAction {
        void onClick(ReservationModel item);
        void onCancel(ReservationModel item); // dùng cho nút Hủy (nếu cần)
    }

    private final Context ctx;
    private final List<ReservationModel> data;
    private final OnItemAction action;

    public ReservationAdapter(Context ctx, List<ReservationModel> data, OnItemAction action) {
        this.ctx = ctx; this.data = data; this.action = action;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_reservation, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int i) {
        ReservationModel r = data.get(i);

        // Title: có thể lấy từ DB, tạm dùng tên cố định
        h.tvTitle.setText("Vườn nướng BBQ - S101");

        // Subtitle: dd/MM • HH:mm • x NL, y TE
        h.tvSubtitle.setText(r.date + " • " + r.time + " • " + r.adult + " NL, " + r.child + " TE");

        // Status badge
        String st = r.status.getValue();
        h.tvStatus.setText(st.toUpperCase());

        int txt, bg;
        switch (r.status) {
            case PENDING:    txt = 0xFFFF9800; bg = 0x1FFF9800; break; // cam
            case CONFIRMED:  txt = 0xFF4CAF50; bg = 0x1F4CAF50; break; // xanh lá
            case CANCELLED:  txt = 0xFFFF3B30; bg = 0x1FFF3B30; break; // đỏ
            case COMPLETED:  txt = 0xFF2196F3; bg = 0x1F2196F3; break; // xanh dương
            default:         txt = 0xFF9E9E9E; bg = 0x1F9E9E9E;
        }
        h.tvStatus.setTextColor(txt);
        h.tvStatus.setBackgroundTintList(ColorStateList.valueOf(bg));

        h.itemView.setOnClickListener(v -> { if (action != null) action.onClick(r); });
        // nếu muốn có nút Hủy trong item, có thể thêm 1 Button và setOnClickListener → action.onCancel(r)
    }

    @Override public int getItemCount() { return data == null ? 0 : data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvStatus;
        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSubtitle = v.findViewById(R.id.tvSubtitle);
            tvStatus = v.findViewById(R.id.tvStatus);
        }
    }
}
