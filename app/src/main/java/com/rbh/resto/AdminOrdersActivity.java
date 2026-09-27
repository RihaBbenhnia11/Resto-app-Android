package com.rbh.resto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity {
    private ListView listViewOrders;
    private Button btnBack;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private OrderAdapterAdmin orderAdapter;
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        loadOrders();
    }

    private void initializeViews() {
        listViewOrders = findViewById(R.id.listViewOrders);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        orders = databaseHelper.getAllOrders();
        orderAdapter = new OrderAdapterAdmin(this, orders);
        listViewOrders.setAdapter(orderAdapter);

        // Mettre à jour le statut quand un bouton est cliqué
        orderAdapter.setOnStatusChangeListener((orderId, newStatus) -> {
            if (databaseHelper.updateOrderStatus(orderId, newStatus)) {
                Toast.makeText(this, "Statut mis à jour", Toast.LENGTH_SHORT).show();
                loadOrders(); // Recharger les commandes
            } else {
                Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}