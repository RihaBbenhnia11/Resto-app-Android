package com.rbh.resto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private ListView listViewDishes;
    private Button btnAddDish, btnViewDishes, btnManageOrders, btnLogout;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private DishAdapter dishAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        loadDishes();
    }

    private void initializeViews() {
        listViewDishes = findViewById(R.id.listViewDishes);
        btnAddDish = findViewById(R.id.btnAddDish);
        btnViewDishes = findViewById(R.id.btnViewDishes);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupClickListeners() {
        btnAddDish.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, DishFormActivity.class);
            startActivity(intent);
        });

        btnViewDishes.setOnClickListener(v -> loadDishes());

        btnManageOrders.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadDishes() {
        List<Dish> dishes = databaseHelper.getAllDishes();
        dishAdapter = new DishAdapter(this, dishes, true);
        listViewDishes.setAdapter(dishAdapter);

        listViewDishes.setOnItemClickListener((parent, view, position, id) -> {
            Dish dish = dishes.get(position);
            Intent intent = new Intent(AdminDashboardActivity.this, DishFormActivity.class);
            intent.putExtra("dish_id", dish.getId());
            startActivity(intent);
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDishes();
    }
}