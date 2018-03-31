package com.rohankhadapkar.gourmetdeli.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohankhadapkar.gourmetdeli.Activity.ItemDetail;
import com.rohankhadapkar.gourmetdeli.Class.ItemHolder;
import com.rohankhadapkar.gourmetdeli.Class.ItemsList;
import com.rohankhadapkar.gourmetdeli.Interface.ItemClickListener;
import com.rohankhadapkar.gourmetdeli.R;

public class ItemListFragment extends Fragment {

    public FirestoreRecyclerAdapter<ItemsList, ItemHolder> adapter;
    RecyclerView recyclerItems;
    ProgressBar progressBar;
    View itemView;
    String categoryId;
    private FirebaseFirestore database;

    public ItemListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        categoryId = bundle.getString("CategoryId");
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        progressBar = view.findViewById(R.id.progressBarList);
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerItems = this.getActivity().findViewById(R.id.recyclerItems);
        recyclerItems.setHasFixedSize(true);

        if (!categoryId.isEmpty() && categoryId != null) {
            recyclerItems.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            loadItemList(categoryId);
            recyclerItems.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadItemList(final String categoryId) {

        database = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = database.collection("Menu").document(categoryId).collection("Items");
        FirestoreRecyclerOptions<ItemsList> recyclerOptions = new FirestoreRecyclerOptions.Builder<ItemsList>()
                .setQuery(collectionReference, ItemsList.class).build();

        adapter = new FirestoreRecyclerAdapter<ItemsList, ItemHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ItemHolder holder, int position, @NonNull final ItemsList model) {
                Glide.with(getActivity()).load(model.getImage()).into(holder.itemImage);
                holder.itemName.setText(model.getName());
                String cost = model.getCost();
                cost = "\u20B9".concat(cost);
                holder.itemCost.setText(cost);
                Glide.with(getActivity()).load(model.getType()).into(holder.itemType);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Integer a = position + 1;
                        Intent itemDetail = new Intent(getActivity(), ItemDetail.class);
                        itemDetail.putExtra("ItemId", String.valueOf(a));
                        itemDetail.putExtra("CategoryId", categoryId);
                        startActivity(itemDetail);
                    }
                });
            }

            @Override
            public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_items, parent, false);
                return new ItemHolder(itemView);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerItems.setLayoutManager(linearLayoutManager);
        recyclerItems.setAdapter(adapter);


    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerItems.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerItems.setAdapter(adapter);
        adapter.stopListening();
    }


}
