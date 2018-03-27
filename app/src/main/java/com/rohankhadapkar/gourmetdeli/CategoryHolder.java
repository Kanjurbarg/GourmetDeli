package com.rohankhadapkar.gourmetdeli;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView categoryName;
    public ImageView categoryImage;

    private ItemClickListener itemClickListener;

    public CategoryHolder(View itemView) {
        super(itemView);

        categoryName = itemView.findViewById(R.id.categoryName);
        categoryImage = itemView.findViewById(R.id.categoryImage);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view,getAdapterPosition());
    }


}
