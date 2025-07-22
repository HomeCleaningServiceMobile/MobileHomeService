package com.example.prm_project.ui.view.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_project.R;
import com.example.prm_project.data.model.AdminServiceListResponse;
import com.example.prm_project.ui.adapter.AdminServiceAdapter;
import com.example.prm_project.ui.viewmodel.AdminServiceViewModel;
import com.example.prm_project.utils.SessionManager;

/**
 * Fragment hiển thị danh sách services cho admin
 */
public class AdminServiceListFragment extends Fragment implements AdminServiceAdapter.OnServiceClickListener {
    
    private RecyclerView recyclerViewServices;
    private AdminServiceAdapter serviceAdapter;
    private AdminServiceViewModel viewModel;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(this).get(AdminServiceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_service_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        observeViewModel();
        loadServiceList();

        // Thêm sự kiện chuyển sang màn hình thêm dịch vụ
        view.findViewById(R.id.btn_add_service).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_adminServiceListFragment_to_adminAddServiceFragment);
        });
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews(View view) {
        recyclerViewServices = view.findViewById(R.id.recycler_view_services);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    /**
     * Thiết lập RecyclerView và adapter
     */
    private void setupRecyclerView() {
        serviceAdapter = new AdminServiceAdapter(requireContext());
        serviceAdapter.setOnServiceClickListener(this);
        
        recyclerViewServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewServices.setAdapter(serviceAdapter);
    }

    /**
     * Quan sát các thay đổi từ ViewModel
     */
    private void observeViewModel() {
        viewModel.getServiceListLiveData().observe(getViewLifecycleOwner(), response -> {
            progressBar.setVisibility(View.GONE);
            
            if (response != null && response.isSucceeded()) {
                if (response.getData() != null && response.getData().getItems() != null) {
                    serviceAdapter.updateServiceList(response.getData().getItems());
                    
                    // Hiển thị thông tin phân trang
                    showPaginationInfo(response.getData());
                } else {
                    Toast.makeText(requireContext(), "Không có dịch vụ nào", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Lỗi khi tải danh sách dịch vụ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Tải danh sách services
     */
    private void loadServiceList() {
        String token = sessionManager.getAccessToken();
        if (token != null) {
            progressBar.setVisibility(View.VISIBLE);
            viewModel.getServiceList(token);
        } else {
            Toast.makeText(requireContext(), "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hiển thị thông tin phân trang
     */
    private void showPaginationInfo(AdminServiceListResponse.ServiceData data) {
        String info = String.format("Trang %d/%d - Tổng: %d dịch vụ", 
                data.getPageNumber(), 
                data.getTotalPages(), 
                data.getTotalCount());
        Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceClick(AdminServiceListResponse.ServiceItem service) {
        Toast.makeText(requireContext(), "Xem chi tiết: " + service.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceEditClick(AdminServiceListResponse.ServiceItem service) {
        Bundle args = new Bundle();
        args.putInt("serviceId", service.getId());
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.adminEditServiceFragment, args);
    }

    @Override
    public void onServiceDeleteClick(AdminServiceListResponse.ServiceItem service) {
        String token = sessionManager.getAccessToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        viewModel.deleteService(token, service.getId()).observe(getViewLifecycleOwner(), response -> {
            progressBar.setVisibility(View.GONE);
            if (response != null && response.isSucceeded) {
                serviceAdapter.removeServiceById(service.getId());
                Toast.makeText(requireContext(), "Xóa dịch vụ thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Xóa dịch vụ thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
