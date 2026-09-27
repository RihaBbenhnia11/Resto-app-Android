package com.rbh.resto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DishFormActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivDishImage;
    private EditText etDishName, etDishDescription, etDishPrice;
    private Spinner spinnerCategory;
    private Button btnSelectImage, btnSave, btnDelete;
    private DatabaseHelper databaseHelper;
    private Bitmap selectedImage;
    private int dishId = -1;
    private boolean isEditMode = false;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_form);

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupClickListeners();
        loadCategories(); // NOUVEAU

        // Vérifier si on est en mode édition
        dishId = getIntent().getIntExtra("dish_id", -1);
        if (dishId != -1) {
            isEditMode = true;
            loadDishData();
        }
    }

    private void initializeViews() {
        ivDishImage = findViewById(R.id.ivDishImage);
        etDishName = findViewById(R.id.etDishName);
        etDishDescription = findViewById(R.id.etDishDescription);
        etDishPrice = findViewById(R.id.etDishPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory); // MODIFIÉ
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        if (!isEditMode) {
            btnDelete.setEnabled(false);
        }
    }

    private void loadCategories() {
        categories = databaseHelper.getAllCategories(); // récupérer toutes les catégories depuis la BD
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());// parcourir la liste des catégories et extraire leurs noms
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,//layout simple Android pour afficher chaque ligne
                categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);// associer l'adaptateur au Spinner pour afficher les catégories
    }

    private void setupClickListeners() {
        btnSelectImage.setOnClickListener(v -> selectImage());
        btnSave.setOnClickListener(v -> saveDish());
        btnDelete.setOnClickListener(v -> deleteDish());
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");//Filtre pour n'afficher que les images

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Picture")//ouvre une fenêtre permettant de choisir l’app (galerie)
                , PICK_IMAGE_REQUEST  //identifiant pour récupérer le résultat

        );
    }

    //Récupère et affiche l'image choisie
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivDishImage.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDishData() {
        Dish dish = databaseHelper.getDishById(dishId);
        if (dish != null) {
            etDishName.setText(dish.getName());
            etDishDescription.setText(dish.getDescription());
            etDishPrice.setText(String.valueOf(dish.getPrice()));

            // Sélectionner la catégorie dans le spinner
            if (dish.getCategoryId() > 0) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getId() == dish.getCategoryId()) {// // Si l'id de la catégorie correspond à l'id du plat
                        spinnerCategory.setSelection(i);// Sélectionner l’élément
                        break;
                    }
                }
            }

            if (dish.getImage() != null) {
                Bitmap image = DatabaseHelper.getBitmapFromBytes(dish.getImage());
                ivDishImage.setImageBitmap(image);
                selectedImage = image;
            }
        }
    }

    private void saveDish() {
        String name = etDishName.getText().toString().trim();
        String description = etDishDescription.getText().toString().trim();
        String priceStr = etDishPrice.getText().toString().trim();

        // Récupérer la catégorie sélectionnée
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        if (selectedPosition < 0) {
            Toast.makeText(this, "Veuillez sélectionner une catégorie", Toast.LENGTH_SHORT).show();
            return;
        }
        int categoryId = categories.get(selectedPosition).getId();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] imageBytes = null;
        if (selectedImage != null) {
            imageBytes = DatabaseHelper.getBytesFromBitmap(selectedImage);
        }

        boolean success;
        if (isEditMode) {
            success = databaseHelper.updateDish(dishId, name, description, price, categoryId, imageBytes);
        } else {
            success = databaseHelper.addDish(name, description, price, categoryId, imageBytes);
        }

        if (success) {
            Toast.makeText(this, isEditMode ? "Plat modifié avec succès" : "Plat ajouté avec succès", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDish() {
        if (databaseHelper.deleteDish(dishId)) {
            Toast.makeText(this, "Plat supprimé avec succès", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
    }
}