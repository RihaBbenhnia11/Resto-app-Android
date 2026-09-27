package com.rbh.resto;

public class Order {
    private int id;
    private int userId;
    private int dishId;
    private int quantity;
    private double totalPrice;
    private String date;
    private String status; // MODIFIÉ: peut être 'En attente', 'Acceptée', 'Refusée'
    private String dishName;
    private String userName; // NOUVEAU

    public Order() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDishId() { return dishId; }
    public void setDishId(int dishId) { this.dishId = dishId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public String getUserName() { return userName; } // NOUVEAU
    public void setUserName(String userName) { this.userName = userName; }
}