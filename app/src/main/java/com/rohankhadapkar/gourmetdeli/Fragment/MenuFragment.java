package com.rohankhadapkar.gourmetdeli.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohankhadapkar.gourmetdeli.CategoryHolder;
import com.rohankhadapkar.gourmetdeli.CategoryList;
import com.rohankhadapkar.gourmetdeli.ItemClickListener;
import com.rohankhadapkar.gourmetdeli.R;

public class MenuFragment extends Fragment{

    RecyclerView recyclerMenu;
    public FirestoreRecyclerAdapter<CategoryList, CategoryHolder> adapter;
    private FirebaseFirestore database;
    View itemView;

    public MenuFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerMenu.setAdapter(adapter);
       adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerMenu.setAdapter(adapter);
        adapter.stopListening();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog dialog = new ProgressDialog(getActivity()); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        recyclerMenu = this.getActivity().findViewById(R.id.recyclerMenu);
        recyclerMenu.setHasFixedSize(true);

        database = FirebaseFirestore.getInstance();
        CollectionReference reference =  database.collection("Menu");

        FirestoreRecyclerOptions<CategoryList> recyclerOptions = new FirestoreRecyclerOptions.Builder<CategoryList>()
                .setQuery(reference,CategoryList.class).build();
        recyclerMenu.setAdapter(adapter);
        adapter = new FirestoreRecyclerAdapter<CategoryList, CategoryHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoryHolder holder, int position, @NonNull final CategoryList model) {
                Glide.with(getActivity()).load(model.getImage()).into(holder.categoryImage);
                holder.categoryName.setText(model.getName());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        Bundle bundle = new Bundle();
                        DocumentSnapshot documentSnapshot = adapter.getSnapshots().getSnapshot(holder.getAdapterPosition());
                        bundle.putString("CategoryId", documentSnapshot.getId());

                        if (documentSnapshot.getId().contentEquals("1"))
                        {
                            ItemListFragment itemListFragment = new ItemListFragment();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            itemListFragment.setArguments(bundle);
                            fragmentTransaction.replace(R.id.menu_fragment,itemListFragment);
                            fragmentTransaction.commit();
                        }
                        else {
                            Toast.makeText(getActivity(),model.getName(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_category,parent,false);
                return new CategoryHolder(itemView);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerMenu.setLayoutManager(linearLayoutManager);
        recyclerMenu.setAdapter(adapter);
        dialog.dismiss();
    }
}
