package com.rbh.resto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {
    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        super(context, R.layout.list_item_order, orders);
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_order, parent, false);
        }

        Order currentOrder = orders.get(position);

        TextView tvDishName = listItem.findViewById(R.id.tvOrderDishName);
        TextView tvQuantity = listItem.findViewById(R.id.tvOrderQuantity);
        TextView tvTotalPrice = listItem.findViewById(R.id.tvOrderTotalPrice);
        TextView tvDate = listItem.findViewById(R.id.tvOrderDate);
        TextView tvStatus = listItem.findViewById(R.id.tvOrderStatus);

        tvDishName.setText(currentOrder.getDishName());
        tvQuantity.setText(String.format("Quantité: %d", currentOrder.getQuantity()));
        tvTotalPrice.setText(String.format("Total: %.2f DT", currentOrder.getTotalPrice()));
        tvDate.setText(currentOrder.getDate());
        tvStatus.setText(currentOrder.getStatus());

        // Changer la couleur selon le statut
        switch (currentOrder.getStatus()) {
            case "En attente":
                tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "Acceptée":
                tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
            case "Refusée":
                tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            default:
                tvStatus.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        return listItem;
    }
}