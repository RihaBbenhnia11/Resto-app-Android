package com.rbh.resto;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

//SQLiteOpenHelper(classe) s’occupe de créer, ouvrir, mettre à jour la base automatiquement
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MenuOrder.db";
    private static final int DATABASE_VERSION = 2; // Version incrémentée

    // Table Categories
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "name";

    // Table Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_ROLE = "role";

    // Table Dishes
    private static final String TABLE_DISHES = "dishes";
    private static final String COLUMN_DISH_ID = "dish_id";
    private static final String COLUMN_DISH_NAME = "name";
    private static final String COLUMN_DISH_DESCRIPTION = "description";
    private static final String COLUMN_DISH_PRICE = "price";
    private static final String COLUMN_DISH_CATEGORY_ID = "category_id"; // on ajoute category_id
    private static final String COLUMN_DISH_IMAGE = "image";

    // Table Orders
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_ORDER_USER_ID = "user_id";
    private static final String COLUMN_ORDER_DISH_ID = "dish_id";
    private static final String COLUMN_ORDER_QUANTITY = "quantity";
    private static final String COLUMN_ORDER_TOTAL_PRICE = "total_price";
    private static final String COLUMN_ORDER_DATE = "date";
    private static final String COLUMN_ORDER_STATUS = "status"; // peut être 'En attente', 'Acceptée', 'Refusée'

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table Categories
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_NAME + " TEXT UNIQUE"
                + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // Table Users
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_ROLE + " TEXT DEFAULT 'user'"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Table Dishes
        String CREATE_DISHES_TABLE = "CREATE TABLE " + TABLE_DISHES + "("
                + COLUMN_DISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DISH_NAME + " TEXT,"
                + COLUMN_DISH_DESCRIPTION + " TEXT,"
                + COLUMN_DISH_PRICE + " REAL,"
                + COLUMN_DISH_CATEGORY_ID + " INTEGER,"
                + COLUMN_DISH_IMAGE + " BLOB,"
                + "FOREIGN KEY(" + COLUMN_DISH_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ")"
                + ")";
        db.execSQL(CREATE_DISHES_TABLE);

        // Table Orders
        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ORDER_USER_ID + " INTEGER,"
                + COLUMN_ORDER_DISH_ID + " INTEGER,"
                + COLUMN_ORDER_QUANTITY + " INTEGER,"
                + COLUMN_ORDER_TOTAL_PRICE + " REAL,"
                + COLUMN_ORDER_DATE + " TEXT,"
                + COLUMN_ORDER_STATUS + " TEXT DEFAULT 'En attente',"
                + "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_ORDER_DISH_ID + ") REFERENCES " + TABLE_DISHES + "(" + COLUMN_DISH_ID + ")"
                + ")";
        db.execSQL(CREATE_ORDERS_TABLE);

        // Données initiales
        createDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Migration pour la nouvelle version
        if (oldVersion < 2) {
            // Supprimer les anciennes tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISHES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    private void createDefaultData(SQLiteDatabase db) {
        // Créer l'admin par défaut
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USER_NAME, "Admin");
        adminValues.put(COLUMN_USER_EMAIL, "admin@menuorder.com");
        adminValues.put(COLUMN_USER_PASSWORD, "admin123");
        adminValues.put(COLUMN_USER_ROLE, "admin");
        db.insert(TABLE_USERS, null, adminValues);

        // Créer des catégories par défaut
        String[] defaultCategories = {"Entrées", "Plats Principaux", "Desserts", "Boissons", "Pizzas", "Burgers"};
        for (String category : defaultCategories) {
            ContentValues categoryValues = new ContentValues();
            categoryValues.put(COLUMN_CATEGORY_NAME, category);
            db.insert(TABLE_CATEGORIES, null, categoryValues);
        }
    }

    // méthodes pour categories

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); //mode lecture
        String query = "SELECT * FROM " + TABLE_CATEGORIES + " ORDER BY " + COLUMN_CATEGORY_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public Category getCategoryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            Category category = new Category();
            category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
            cursor.close();
            return category;
        }
        cursor.close();
        return null;
    }

    public boolean addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);

        long result = db.insert(TABLE_CATEGORIES, null, values);
        return result != -1;
    }

    // méthodes pour Dishes

    public boolean addDish(String name, String description, double price, int categoryId, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISH_NAME, name);
        values.put(COLUMN_DISH_DESCRIPTION, description);
        values.put(COLUMN_DISH_PRICE, price);
        values.put(COLUMN_DISH_CATEGORY_ID, categoryId);
        values.put(COLUMN_DISH_IMAGE, image);

        long result = db.insert(TABLE_DISHES, null, values);
        return result != -1;
    }

    public List<Dish> getAllDishes() {
        List<Dish> dishList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT d.*, c." + COLUMN_CATEGORY_NAME + " as category_name FROM " + TABLE_DISHES + " d " +
                "LEFT JOIN " + TABLE_CATEGORIES + " c ON d." + COLUMN_DISH_CATEGORY_ID + " = c." + COLUMN_CATEGORY_ID;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Dish dish = new Dish();
                dish.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_DISH_ID)));
                dish.setName(cursor.getString(cursor.getColumnIndex(COLUMN_DISH_NAME)));
                dish.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DISH_DESCRIPTION)));
                dish.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_DISH_PRICE)));
                dish.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_DISH_CATEGORY_ID)));
                dish.setCategoryName(cursor.getString(cursor.getColumnIndex("category_name")));
                dish.setImage(cursor.getBlob(cursor.getColumnIndex(COLUMN_DISH_IMAGE)));
                dishList.add(dish);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dishList;
    }

    public boolean updateDish(int id, String name, String description, double price, int categoryId, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISH_NAME, name);
        values.put(COLUMN_DISH_DESCRIPTION, description);
        values.put(COLUMN_DISH_PRICE, price);
        values.put(COLUMN_DISH_CATEGORY_ID, categoryId);
        if (image != null) {
            values.put(COLUMN_DISH_IMAGE, image);
        }

        int result = db.update(TABLE_DISHES, values, COLUMN_DISH_ID + " = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public Dish getDishById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT d.*, c." + COLUMN_CATEGORY_NAME + " as category_name FROM " + TABLE_DISHES + " d " +
                "LEFT JOIN " + TABLE_CATEGORIES + " c ON d." + COLUMN_DISH_CATEGORY_ID + " = c." + COLUMN_CATEGORY_ID +
                " WHERE d." + COLUMN_DISH_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            Dish dish = new Dish();
            dish.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_DISH_ID)));
            dish.setName(cursor.getString(cursor.getColumnIndex(COLUMN_DISH_NAME)));
            dish.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DISH_DESCRIPTION)));
            dish.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_DISH_PRICE)));
            dish.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_DISH_CATEGORY_ID)));
            dish.setCategoryName(cursor.getString(cursor.getColumnIndex("category_name")));
            dish.setImage(cursor.getBlob(cursor.getColumnIndex(COLUMN_DISH_IMAGE)));
            cursor.close();
            return dish;
        }
        cursor.close();
        return null;
    }

    // méthodes pour Orders

    public boolean updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_STATUS, status);

        int result = db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        return result > 0;
    }

    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "SELECT o.*, " +
                "d." + COLUMN_DISH_NAME + " as dish_name, " +
                "u." + COLUMN_USER_NAME + " as user_name " +
                "FROM " + TABLE_ORDERS + " o " +
                "INNER JOIN " + TABLE_DISHES + " d ON o." + COLUMN_ORDER_DISH_ID + " = d." + COLUMN_DISH_ID + " " +
                "INNER JOIN " + TABLE_USERS + " u ON o." + COLUMN_ORDER_USER_ID + " = u." + COLUMN_USER_ID + " " +
                "ORDER BY o." + COLUMN_ORDER_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID)));
                order.setUserId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_USER_ID)));
                order.setDishId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISH_ID)));
                order.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_QUANTITY)));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_ORDER_TOTAL_PRICE)));
                order.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DATE)));
                order.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_STATUS)));


                order.setDishName(cursor.getString(cursor.getColumnIndex("dish_name")));
                order.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));

                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o.*, d." + COLUMN_DISH_NAME + " FROM " + TABLE_ORDERS + " o " +
                "INNER JOIN " + TABLE_DISHES + " d ON o." + COLUMN_ORDER_DISH_ID + " = d." + COLUMN_DISH_ID +
                " WHERE o." + COLUMN_ORDER_USER_ID + " = ? ORDER BY o." + COLUMN_ORDER_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID)));
                order.setUserId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_USER_ID)));
                order.setDishId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISH_ID)));
                order.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_QUANTITY)));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_ORDER_TOTAL_PRICE)));
                order.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DATE)));
                order.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_STATUS)));
                order.setDishName(cursor.getString(cursor.getColumnIndex(COLUMN_DISH_NAME)));
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }

    // Méthodes Users
    public boolean addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();//getWritableDatabase(): ouvre la base de données en mode écriture
        ContentValues values = new ContentValues();//ContentValues: pour l'écriture :(Insert,Update)
                                                    //elle associe des noms de colonnes (clés) à des valeurs (données)
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; //insertion réussie → true /si non si =-1 erreur (email déjà existant...)
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();//ouvre la base de données en mode lecture seule
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";//cherche un utilisateur
                                                                           // dont l'email est X et le mot de passe est Y
        String[] selectionArgs = {email, password}; // remplir les vraies valeurs

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;//true :user existe dans la base
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ROLE};
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        //SELECT role FROM users WHERE email = '.....';
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            String role = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ROLE));
            cursor.close();
            return role;
        }
        cursor.close();
        return "user";
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            cursor.close();
            return userId;
        }
        cursor.close();
        return -1;
    }

    public boolean deleteDish(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DISHES, COLUMN_DISH_ID + " = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean addOrder(int userId, int dishId, int quantity, double totalPrice, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_USER_ID, userId);
        values.put(COLUMN_ORDER_DISH_ID, dishId);
        values.put(COLUMN_ORDER_QUANTITY, quantity);
        values.put(COLUMN_ORDER_TOTAL_PRICE, totalPrice);
        values.put(COLUMN_ORDER_DATE, date);

        long result = db.insert(TABLE_ORDERS, null, values);
        return result != -1;
    }

    // Convert Bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Convert byte array to Bitmap
    public static Bitmap getBitmapFromBytes(byte[] image) {
        if (image == null) return null;
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}