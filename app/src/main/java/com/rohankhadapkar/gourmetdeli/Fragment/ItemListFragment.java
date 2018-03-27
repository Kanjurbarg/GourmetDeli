package com.rohankhadapkar.gourmetdeli.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohankhadapkar.gourmetdeli.CategoryHolder;
import com.rohankhadapkar.gourmetdeli.CategoryList;
import com.rohankhadapkar.gourmetdeli.ItemClickListener;
import com.rohankhadapkar.gourmetdeli.ItemHolder;
import com.rohankhadapkar.gourmetdeli.ItemsList;
import com.rohankhadapkar.gourmetdeli.R;

public class ItemListFragment extends Fragment {

    RecyclerView recyclerItems;
    public FirestoreRecyclerAdapter<ItemsList,ItemHolder> adapter;
    private FirebaseFirestore database;
    View itemView;
    String categoryId;

    public ItemListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        categoryId = bundle.getString("CategoryId");
        Log.d("Category",categoryId);
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerItems = this.getActivity().findViewById(R.id.recyclerItems);
        recyclerItems.setHasFixedSize(true);

        if (!categoryId.isEmpty() && categoryId != null)
        {
            loadItemList(categoryId);
        }

    }

    private void loadItemList(final String categoryId) {

        database = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = database.collection("Menu").document(categoryId).collection("Items");
        FirestoreRecyclerOptions<ItemsList> recyclerOptions = new FirestoreRecyclerOptions.Builder<ItemsList>()
                .setQuery(collectionReference,ItemsList.class).build();

        adapter = new FirestoreRecyclerAdapter<ItemsList, ItemHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull final ItemsList model) {
                Glide.with(getActivity()).load(model.getImage()).into(holder.itemImage);
                holder.itemName.setText(model.getName());
                String cost = model.getCost();
                cost = "\u20B9".concat(cost);
                holder.itemCost.setText(cost);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(getActivity(),model.getName(),Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_items,parent,false);
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
