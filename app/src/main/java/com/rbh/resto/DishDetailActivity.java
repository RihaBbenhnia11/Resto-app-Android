package com.rbh.resto;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DishDetailActivity extends AppCompatActivity {
    private ImageView ivDishImage;
    private TextView tvDishName, tvDishDescription, tvDishPrice, tvDishCategory;
    private TextView tvQuantity, tvTotalPrice;
    private Button btnDecrease, btnIncrease, btnOrder;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private Dish currentDish;
    private int quantity = 1;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        initializeViews();
        loadDishData();
        setupClickListeners();
        updateTotalPrice();
    }

    private void initializeViews() {
        ivDishImage = findViewById(R.id.ivDishImage);
        tvDishName = findViewById(R.id.tvDishName);
        tvDishDescription = findViewById(R.id.tvDishDescription);
        tvDishPrice = findViewById(R.id.tvDishPrice);
        tvDishCategory = findViewById(R.id.tvDishCategory);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnOrder = findViewById(R.id.btnOrder);
    }

    private void loadDishData() {
        int dishId = getIntent().getIntExtra("dish_id", -1);
        if (dishId != -1) {
            currentDish = databaseHelper.getDishById(dishId);
            if (currentDish != null) {
                // Charger l'image
                if (currentDish.getImage() != null) {
                    Bitmap image = DatabaseHelper.getBitmapFromBytes(currentDish.getImage());
                    ivDishImage.setImageBitmap(image);
                }

                tvDishName.setText(currentDish.getName());
                tvDishDescription.setText(currentDish.getDescription());
                tvDishPrice.setText(String.format("%.2f DT", currentDish.getPrice()));
                tvDishCategory.setText(currentDish.getCategoryName());
            }
        }
    }

    private void setupClickListeners() {
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;// Diminue la quantité
                tvQuantity.setText(String.valueOf(quantity));// Affiche la nouvelle quantité
                updateTotalPrice();// Met à jour le prix total
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateTotalPrice();
        });

        btnOrder.setOnClickListener(v -> placeOrder());
    }

    private void updateTotalPrice() {
        if (currentDish != null) {
            double total = currentDish.getPrice() * quantity;
            tvTotalPrice.setText(String.format("Total: %.2f DT", total));
        }
    }

    private void placeOrder() {
        if (currentDish != null) {
            double totalPrice = currentDish.getPrice() * quantity;
            String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            if (databaseHelper.addOrder(userId, currentDish.getId(), quantity, totalPrice, currentDate)) {
                Toast.makeText(this, "Commande passée avec succès !", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la commande", Toast.LENGTH_SHORT).show();
            }
        }
    }
}