package com.rohankhadapkar.gourmetdeli.Class;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohankhadapkar.gourmetdeli.Interface.ItemClickListener;
import com.rohankhadapkar.gourmetdeli.R;

public class CartHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cartItemName, cartItemPrice, cartItemQuantity, cartItemTotalPrice, cartItemGrandTotal;
    public ImageView cartItemImage, cartItemRemove;
    private ItemClickListener itemClickListener;

    public CartHolder(View itemView) {
        super(itemView);

        cartItemName = itemView.findViewById(R.id.cartItemName);
        cartItemPrice = itemView.findViewById(R.id.cartItemPrice);
        cartItemQuantity = itemView.findViewById(R.id.cartItemQuantity);
        cartItemTotalPrice = itemView.findViewById(R.id.cartItemTotalPrice);

        cartItemImage = itemView.findViewById(R.id.cartItemImage);
        cartItemRemove = itemView.findViewById(R.id.cartItemRemove);

        cartItemGrandTotal = itemView.findViewById(R.id.total);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }
}
