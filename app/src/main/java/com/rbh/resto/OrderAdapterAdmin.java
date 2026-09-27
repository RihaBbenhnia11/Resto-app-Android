package com.rbh.resto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class OrderAdapterAdmin extends ArrayAdapter<Order> {
    private Context context;
    private List<Order> orders;
    private OnStatusChangeListener statusChangeListener;

    public interface OnStatusChangeListener {
        void onStatusChange(int orderId, String newStatus);
    }

    public void setOnStatusChangeListener(OnStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }

    public OrderAdapterAdmin(Context context, List<Order> orders) {
        super(context, R.layout.list_item_order_admin, orders);
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_order_admin, parent, false);
        }

        Order currentOrder = orders.get(position);

        TextView tvOrderInfo = listItem.findViewById(R.id.tvOrderInfo);
        TextView tvOrderDetails = listItem.findViewById(R.id.tvOrderDetails);
        TextView tvOrderStatus = listItem.findViewById(R.id.tvOrderStatus);
        Button btnAccept = listItem.findViewById(R.id.btnAccept);
        Button btnReject = listItem.findViewById(R.id.btnReject);

        //  les informations de commande
        String orderInfo = currentOrder.getUserName() + " - " + currentOrder.getDishName();
        String orderDetails = "Quantité: " + currentOrder.getQuantity() +
                " | Total: " + String.format("%.2f DT", currentOrder.getTotalPrice()) +
                "\nDate: " + currentOrder.getDate();

        tvOrderInfo.setText(orderInfo);
        tvOrderDetails.setText(orderDetails);
        tvOrderStatus.setText(currentOrder.getStatus());

        // Changer la couleur selon le statut
        switch (currentOrder.getStatus()) {
            case "En attente":
                tvOrderStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "Acceptée":
                tvOrderStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
            case "Refusée":
                tvOrderStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
        }


        // Configurer les boutons d'action
        final int orderId = currentOrder.getId();
        btnAccept.setOnClickListener(v -> {
            if (statusChangeListener != null) {
                statusChangeListener.onStatusChange(orderId, "Acceptée");
            }
        });

        btnReject.setOnClickListener(v -> {
            if (statusChangeListener != null) {
                statusChangeListener.onStatusChange(orderId, "Refusée");
            }
        });

        // Désactiver les boutons si la commande n'est plus en attente
        if (!currentOrder.getStatus().equals("En attente")) {
            btnAccept.setEnabled(false);
            btnReject.setEnabled(false);
        } else {
            btnAccept.setEnabled(true);
            btnReject.setEnabled(true);
        }

        return listItem;
    }
}