package com.rohankhadapkar.gourmetdeli.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rohankhadapkar.gourmetdeli.Class.CartHolder;
import com.rohankhadapkar.gourmetdeli.Class.CartList;
import com.rohankhadapkar.gourmetdeli.Interface.ItemClickListener;
import com.rohankhadapkar.gourmetdeli.R;

import java.util.HashMap;
import java.util.Map;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirestoreRecyclerAdapter<CartList, CartHolder> adapter;
    FirebaseFirestore database;
    FirebaseAuth auth;
    FirebaseUser user;

    TextView cartGrandTotal;
    Button placeOrder;
    Integer grand = 0;
    String itemId, categoryId;
    TextView totalPrice;
    ItemDetail itemDetail;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cart");

        itemDetail = new ItemDetail();
        database = FirebaseFirestore.getInstance();

        cartGrandTotal = findViewById(R.id.total);
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        totalPrice = findViewById(R.id.total);
        placeOrder = findViewById(R.id.placeOrder);

        loadListFood();
    }

    private void loadListFood() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        final CollectionReference collectionReference = database.collection("Customer").document(user.getUid()).collection("Cart");

        FirestoreRecyclerOptions<CartList> recyclerOptions = new FirestoreRecyclerOptions.Builder<CartList>()
                .setQuery(collectionReference, CartList.class).build();

        adapter = new FirestoreRecyclerAdapter<CartList, CartHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CartHolder holder, final int position, @NonNull CartList model) {
                holder.cartItemName.setText(model.getName());
                String cost = model.getPrice();
                int c = Integer.parseInt(model.getPrice());
                cost = "Price: " + "\u20B9" + cost;
                holder.cartItemPrice.setText(cost);
                String quantity = model.getQuantity();
                final int q = Integer.parseInt(model.getQuantity());
                final int t = c * q; //Total Price
                grand = grand + t;
                Log.d("TestInt", grand.toString());
                String total = String.valueOf(t);
                total = "Total Price: " + "\u20B9" + total;
                quantity = "Quantity: " + quantity;
                holder.cartItemQuantity.setText(quantity);
                holder.cartItemTotalPrice.setText(total);
                Glide.with(getApplication()).load(model.getImage()).into(holder.cartItemImage);
                itemId = model.getItemId();
                categoryId = model.getCategoryId();
                String grandTotal = grand.toString();
                cartGrandTotal.setText(grandTotal);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(Cart.this, ItemDetail.class);
                        intent.putExtra("ItemId", itemId);
                        intent.putExtra("CategoryId", categoryId);
                        startActivity(intent);
                    }
                });

                holder.cartItemRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog = new ProgressDialog(Cart.this);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("Please wait...");
                        dialog.setIndeterminate(true);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        String price = adapter.getItem(position).getPrice();
                        String quantity = adapter.getItem(position).getQuantity();
                        final Integer total = Integer.parseInt(price) * Integer.parseInt(quantity);
                        database.collection("Customer").document(user.getUid()).collection("Cart").document(adapter.getItem(position).getItemId())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                                        grand = grand - total;
                                        String grandTotal = grand.toString();
                                        cartGrandTotal.setText(grandTotal);
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                    }
                });
            }

            @Override
            public CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_layout, parent, false);
                return new CartHolder(view);
            }
        };

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionReference from = database.collection("Customer").document(user.getUid()).collection("Cart");
                from.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (!documentSnapshots.isEmpty()) {
                            new AlertDialog.Builder(Cart.this)
                                    .setTitle("Place Order")
                                    .setMessage("Do you want to place the order?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialog = new ProgressDialog(Cart.this);
                                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            dialog.setMessage("Please wait...");
                                            dialog.setIndeterminate(true);
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.show();
                                            moveFirestoreCollection();

                                        }
                                    })
                                    .setNegativeButton("No", null).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Add items to cart to place order!!!", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }
                });

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Cart.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        String grandTotal = grand.toString();
        cartGrandTotal.setText(grandTotal);
    }

    private void moveFirestoreCollection() {
        final CollectionReference from = database.collection("Customer").document(user.getUid()).collection("Cart");
        from.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final DocumentSnapshot querySnapshot : task.getResult()) {
                        Map<String, Object> order = new HashMap<>();
                        order.put("Name", querySnapshot.getString("Name"));
                        order.put("Price", querySnapshot.getString("Price"));
                        order.put("Quantity", querySnapshot.getString("Quantity"));
                        DocumentReference to = database.collection("Orders").document(user.getUid()).collection("Items").document(querySnapshot.getId());
                        to.set(order)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        database.collection("Customer").document(user.getUid()).collection("Cart").document(querySnapshot.getId())
                                                .delete();
                                        cartGrandTotal.setText("0");
                                        Toast.makeText(getApplicationContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
