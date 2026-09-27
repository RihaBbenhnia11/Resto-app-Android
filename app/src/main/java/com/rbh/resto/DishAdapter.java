package com.rbh.resto;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class DishAdapter extends ArrayAdapter<Dish> {
    private Context context;
    private List<Dish> dishes;
    private boolean isAdmin;

    public DishAdapter(Context context, List<Dish> dishes, boolean isAdmin) {
        super(context, R.layout.list_item_dish, dishes);
        this.context = context;
        this.dishes = dishes;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_dish, parent, false);
        }

        Dish currentDish = dishes.get(position);

        ImageView imageView = listItem.findViewById(R.id.ivDishImage);
        TextView tvName = listItem.findViewById(R.id.tvDishName);
        TextView tvDescription = listItem.findViewById(R.id.tvDishDescription);
        TextView tvPrice = listItem.findViewById(R.id.tvDishPrice);
        TextView tvCategory = listItem.findViewById(R.id.tvDishCategory);

        // Charger l'image
        if (currentDish.getImage() != null) {
            Bitmap image = DatabaseHelper.getBitmapFromBytes(currentDish.getImage());
            imageView.setImageBitmap(image);
        } else {
            imageView.setImageResource(R.drawable.ic_restaurant);
        }

        tvName.setText(currentDish.getName());
        tvDescription.setText(currentDish.getDescription());
        tvPrice.setText(String.format("%.2f DT", currentDish.getPrice()));

        // Afficher le nom de la catégorie
        tvCategory.setText(currentDish.getCategoryName());

        return listItem;
    }
}