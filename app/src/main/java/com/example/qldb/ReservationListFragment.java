package com.example.qldb;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class ReservationListFragment extends Fragment implements ReservationsAdapter.OnItemStatusChangedListener {

    private static final String ARG_STATUS = "status";
    private static final String ARG_ADMIN_ID = "admin_id";

    private String status;
    private int adminId;
    private RecyclerView recyclerView;
    private ReservationsAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Reservation> reservationList;

    // Interface để báo cho ViewPager Adapter biết cần refresh
    public interface OnDataChangedListener {
        void onDataChanged();
    }
    private OnDataChangedListener dataChangedListener;

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.dataChangedListener = listener;
    }


    public static ReservationListFragment newInstance(String status, int adminId) {
        ReservationListFragment fragment = new ReservationListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        args.putInt(ARG_ADMIN_ID, adminId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
            adminId = getArguments().getInt(ARG_ADMIN_ID);
        }
        dbHelper = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadReservations();

        return view;
    }

    // Tải hoặc tải lại dữ liệu
    public void loadReservations() {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(getContext());
        }
        reservationList = dbHelper.getReservationsByStatus(status);

        // Truyền 'this' (Fragment) làm listener
        adapter = new ReservationsAdapter(getContext(), reservationList, adminId, this);

        recyclerView.setAdapter(adapter);
    }

    // Được gọi từ Adapter khi một item được xác nhận/hủy
    @Override
    public void onItemStatusChanged() {
        // Báo cho Activity/ViewPager biết để tải lại fragment kia
        if (dataChangedListener != null) {
            dataChangedListener.onDataChanged();
        }
    }
}