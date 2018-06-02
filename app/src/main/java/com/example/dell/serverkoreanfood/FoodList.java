package com.example.dell.serverkoreanfood;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dell.serverkoreanfood.Common.Common;
import com.example.dell.serverkoreanfood.Model.Food;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {



    //Create database
    FirebaseDatabase database;
    DatabaseReference food_list;

    FirebaseStorage storage;
    StorageReference storageReference;

    Uri saveUri;

    String categoryId="";

    MaterialEditText editName, editDiscription, editPrice, editDiscount;
    FButton btnSelect,btnUpload;
    ListView list_food;
    FloatingActionButton fb;


    //Create a new food.
    Food newFood;

    FirebaseListAdapter<Food> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        database = FirebaseDatabase.getInstance();
        food_list = database.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");
        list_food = findViewById(R.id.list_food);

        fb = findViewById(R.id.fab);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        //Get Intent here

        if(getIntent()!=null)
        {
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        if(!categoryId.isEmpty() && categoryId!=null)
        {
            loadListFood(categoryId);
        }


        list_food.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = adapter.getItem(position);

//                //Get FoodId and send to new activity.
//                Intent fooddetail = new Intent(FoodList.this,FoodDetail.class);
//                fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());
//                startActivity(fooddetail);

            }
        });



    }



    private void showDialog() {


        AlertDialog.Builder altertDialog = new AlertDialog.Builder(FoodList.this);
        altertDialog.setTitle("Add New Food");
        altertDialog.setMessage("Please fill full information");



        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_food_layout,null);

        btnSelect = addMenuLayout.findViewById(R.id.btnSelect);
        btnUpload = addMenuLayout.findViewById(R.id.btnUpload);
        editName = addMenuLayout.findViewById(R.id.editName);
        editDiscription = addMenuLayout.findViewById(R.id.editDiscription);
        editPrice = addMenuLayout.findViewById(R.id.editPrice);
        editDiscount = addMenuLayout.findViewById(R.id.editDiscount);


        altertDialog.setView(addMenuLayout);
        altertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//let user select image from Gallery and save Uri of this image.
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });



        //set button
        altertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here just create new category
                if(newFood!=null)
                {
                    food_list.push().setValue(newFood);
                }
            }
        });
        altertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        altertDialog.show();




    }


    // Choose image to
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),71);
    }


    // Upload image to firebaseDB
    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading ......");
        progressDialog.show();

        String imageName = UUID.randomUUID().toString();

        final StorageReference imageFolder = storageReference.child("image/"+imageName);
        imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(FoodList.this,"Uploaded!!!", Toast.LENGTH_SHORT).show();
                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //set value for newFood if image upload and  we can get download link

                        newFood = new Food(
                                editName.getText().toString(),
                                uri.toString(),
                                editDiscription.getText().toString(),
                                editPrice.getText().toString(),
                                editDiscount.getText().toString(),
                                categoryId
                        );


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FoodList.this,e.getMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData()!=null)
        {
            // save uri on saveUri
            saveUri = data.getData();
            Toast.makeText(FoodList.this,""+saveUri, Toast.LENGTH_SHORT).show();
            btnSelect.setText("Image Selected");
        }



    }




    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)) {
            deleteFood(adapter.getRef(item.getItemId()).getKey());

            Toast.makeText(this,"Deleted",Toast.LENGTH_SHORT).show();
        }

        else if(item.getTitle().equals(Common.UPDATE)) {


            updateFood(adapter.getRef(item.getItemId()).getKey(),adapter.getItem(item.getItemId()));
            Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void deleteFood(String key) {
        food_list.child(key).removeValue();
    }

    private void updateFood(String key, final Food item) {

        AlertDialog.Builder altertDialog = new AlertDialog.Builder(FoodList.this);
        altertDialog.setTitle("Update Food");
        altertDialog.setMessage("Please fill full information");



        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_food_layout,null);

        btnSelect = addMenuLayout.findViewById(R.id.btnSelect);
        btnUpload = addMenuLayout.findViewById(R.id.btnUpload);
        editName = addMenuLayout.findViewById(R.id.editName);
        editDiscription = addMenuLayout.findViewById(R.id.editDiscription);
        editPrice = addMenuLayout.findViewById(R.id.editPrice);
        editDiscount = addMenuLayout.findViewById(R.id.editDiscount);


        //Show information about itemfood.
        editName.setText(item.getName());
        editDiscription.setText(item.getDescripition());
        editPrice.setText(item.getPrice());
        editDiscount.setText(item.getDiscount());


        altertDialog.setView(addMenuLayout);
        altertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//let user select image from Gallery and save Uri of this image.
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage(item);
            }
        });



        //set button
        altertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here just create new category
                if(newFood!=null)
                {
                    food_list.push().setValue(newFood);
                }
            }
        });
        altertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        altertDialog.show();


    }

    private void updateImage(final Food item) {


        if(saveUri!=null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading ......");
            progressDialog.show();


            String imageName = UUID.randomUUID().toString();

            final StorageReference imageFolder = storageReference.child("image/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //set value for item if image upload and  we can get download link

                            item.setImage(uri.toString());


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FoodList.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    private void loadListFood(String categoryId) {


        adapter = new FirebaseListAdapter<Food>(this,
                Food.class,
                R.layout.food_item,
                food_list.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateView(View v, Food model, final int position) {
                TextView textView = v.findViewById(R.id.food_name);
                ImageView imageView = v.findViewById(R.id.food_image);

                textView.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(imageView);


                v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle("Select The Action");
                        menu.add(0, position, 0, Common.DELETE);//groupId, itemId, order, title
                        menu.add(0, position, 0, Common.UPDATE);
                    }
                });



            }
        };
        list_food.setAdapter(adapter);
    }


}
