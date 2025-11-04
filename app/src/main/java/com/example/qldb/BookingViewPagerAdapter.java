package com.example.qldb;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BookingViewPagerAdapter extends FragmentStateAdapter {

    private int adminId;
    // Lưu trữ tham chiếu đến các fragment
    private ReservationListFragment pendingFragment;
    private ReservationListFragment confirmedFragment;

    public BookingViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int adminId) {
        super(fragmentActivity);
        this.adminId = adminId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (pendingFragment == null) {
                    pendingFragment = ReservationListFragment.newInstance("pending", adminId);
                    // Đặt listener
                    pendingFragment.setOnDataChangedListener(() -> {
                        if (confirmedFragment != null) {
                            confirmedFragment.loadReservations(); // Tải lại tab đã xác nhận
                        }
                    });
                }
                return pendingFragment;
            case 1:
            default:
                if (confirmedFragment == null) {
                    confirmedFragment = ReservationListFragment.newInstance("confirmed", adminId);
                    // Tab "đã xác nhận" không cần listener để cập nhật tab "pending"
                }
                return confirmedFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Hai tab
    }

    // Phương thức để tải lại dữ liệu cho fragment
    public void reloadFragmentData(int position) {
        if (position == 0 && pendingFragment != null) {
            pendingFragment.loadReservations();
        } else if (position == 1 && confirmedFragment != null) {
            confirmedFragment.loadReservations();
        }
    }
}