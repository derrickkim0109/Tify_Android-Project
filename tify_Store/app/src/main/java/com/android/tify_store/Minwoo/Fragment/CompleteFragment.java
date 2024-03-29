package com.android.tify_store.Minwoo.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.tify_store.Minwoo.Adapter.CompleteAdapter;
import com.android.tify_store.Minwoo.Adapter.OrderRequestAdapter;
import com.android.tify_store.Minwoo.Adapter.ProgressingAdapter;
import com.android.tify_store.Minwoo.Bean.Complete;
import com.android.tify_store.Minwoo.Bean.OrderRequest;
import com.android.tify_store.Minwoo.NetworkTask.LMW_OrderListNetworkTask;
import com.android.tify_store.R;

import java.util.ArrayList;


public class CompleteFragment extends Fragment {

    String TAG = "ProgressingFragment";

    SwipeRefreshLayout mSwipeRefreshLayout = null;

    private ArrayList<OrderRequest> completes = new ArrayList<>();
    private ArrayList<OrderRequest> list;
    private RecyclerView recyclerView;
    private CompleteAdapter mAdapter;

    String macIP;
    String urlAddr = null;
    String where = null;
    int skSeqNo = 0;
    String makeDone = null;
    String pickUpDone = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.lmw_fragment_complete, container, false);

        // StoreInfoActivity로 부터 값을 받는다.
        Bundle bundle = getArguments();
        macIP = bundle.getString("macIP");
        skSeqNo = bundle.getInt("skSeqNo");

        list = new ArrayList<OrderRequest>();
        list = connectGetData(); // db를 통해 받은 데이터를 담는다.

        if(list.size() == 0){ // 완료된 주문이 없는 경우
            Toast.makeText(getActivity(), "아직 완료된 주문이 없습니다.", Toast.LENGTH_SHORT).show();
        }

        //recyclerview
        recyclerView = v.findViewById(R.id.complete_recycler_view);
        recyclerView.setHasFixedSize(true);
        mAdapter = new CompleteAdapter(CompleteFragment.this, R.layout.lmw_fragment_complete, completes, macIP, skSeqNo);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.complete_swiper); // 당겨서 리프레쉬 하기 위한 Layout
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private ArrayList<OrderRequest> connectGetData(){
        ArrayList<OrderRequest> beanList = new ArrayList<OrderRequest>();

        where = "select";
        urlAddr = "http://" + macIP + ":8080/tify/lmw_orderlist_select_complete.jsp?skSeqNo=" + skSeqNo;

        try {
            ///////////////////////////////////////////////////////////////////////////////////////
            // Date : 2020.12.25
            //
            // Description:
            //  - NetworkTask의 생성자 추가 : where <- "select"
            //
            ///////////////////////////////////////////////////////////////////////////////////////
            LMW_OrderListNetworkTask networkTask = new LMW_OrderListNetworkTask(getActivity(), urlAddr, where);
            ///////////////////////////////////////////////////////////////////////////////////////

            Object obj = networkTask.execute().get();
            completes = (ArrayList<OrderRequest>) obj;
            Log.v(TAG, "completes.size() : " + completes.size());

            beanList = completes;

        }catch (Exception e){
            e.printStackTrace();
        }
        return beanList;
    }

    private void notifyDataSetChanged(){

        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

}