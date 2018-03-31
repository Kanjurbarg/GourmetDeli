package com.rohankhadapkar.gourmetdeli.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohankhadapkar.gourmetdeli.R;

import java.util.HashMap;
import java.util.Map;

public class ItemDetail extends AppCompatActivity {

    static Integer counter = 1;
    public String categoryId = "";
    TextView itemName, itemPrice;
    Button addToCart;
    ImageView itemImage;
    ElegantNumberButton elegantNumberButton;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    ProgressBar progressBar;
    String itemId = "";
    String url;
    String price;
    String cost;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressDetails);

        elegantNumberButton = findViewById(R.id.numberButton);
        addToCart = findViewById(R.id.addToCart);

        itemName = findViewById(R.id.itemDetailName);
        itemPrice = findViewById(R.id.itemDetailCost);

        itemImage = findViewById(R.id.itemDetailImage);

        if (getIntent() != null) {
            itemId = getIntent().getStringExtra("ItemId");
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        if (!itemId.isEmpty()) {
            getItemDetails(itemId);
        }

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

    }

    public String getCategoryId() {
        return categoryId;
    }

    private void addToCart() {
        dialog = new ProgressDialog(ItemDetail.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String quantity = elegantNumberButton.getNumber();

        Map<String, Object> cart = new HashMap<>();
        cart.put("Name", itemName.getText());
        cart.put("Price", price);
        cart.put("Quantity", quantity);
        cart.put("Image", url);
        cart.put("ItemId", itemId);
        cart.put("CategoryId", categoryId);

        firebaseFirestore.collection("Customer").document(user.getUid()).collection("Cart").document(itemId)
                .set(cart)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Added " + elegantNumberButton.getNumber() + " items to cart", Toast.LENGTH_SHORT).show();
                        elegantNumberButton.setNumber(String.valueOf(1));
                        dialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                });
    }

    private void getItemDetails(String itemId) {
        progressBar.setVisibility(View.VISIBLE);
        collectionReference = firebaseFirestore.collection("Menu").document(categoryId).collection("Items");
        final DocumentReference documentReference = collectionReference.document(itemId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        url = snapshot.get("Image").toString();
                        Glide.with(getApplication()).load(snapshot.get("Image")).into(itemImage);
                        String name = snapshot.getString("Name");
                        setTitle(name);
                        name = name.toUpperCase();
                        itemName.setText(name);
                        price = snapshot.getString("Cost");
                        cost = snapshot.getString("Cost");
                        cost = "\u20B9".concat(cost);
                        itemPrice.setText(cost);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplication(), "An error has occurred!!!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplication(), "An error has occurred!!!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
