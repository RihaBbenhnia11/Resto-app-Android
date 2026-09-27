package com.rbh.resto;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity {
    private ListView listViewDishes;
    private Button btnMyOrders, btnLogout;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private DishAdapter dishAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        initializeViews();
        setupClickListeners();
        loadDishes();
    }

    private void initializeViews() {
        listViewDishes = findViewById(R.id.listViewDishes);
        btnMyOrders = findViewById(R.id.btnMyOrders);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupClickListeners() {
        btnMyOrders.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, UserOrdersActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());

        listViewDishes.setOnItemClickListener((parent, view, position, id) -> {
            Dish dish = (Dish) parent.getItemAtPosition(position);
            Intent intent = new Intent(UserDashboardActivity.this, DishDetailActivity.class);
            intent.putExtra("dish_id", dish.getId());
            startActivity(intent);
        });
    }

    private void loadDishes() {
        List<Dish> dishes = databaseHelper.getAllDishes();
        dishAdapter = new DishAdapter(this, dishes, false); // false pour user
        listViewDishes.setAdapter(dishAdapter);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDishes();
    }
}