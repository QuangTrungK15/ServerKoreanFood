package com.example.dell.serverkoreanfood;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.serverkoreanfood.Common.Common;
import com.example.dell.serverkoreanfood.Model.Category;
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

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    FirebaseDatabase database;
    DatabaseReference category;

    FirebaseStorage storage;
    StorageReference storageReference;


    TextView txtFullName;
    ListView list_menu;

    //Init adapter
    FirebaseListAdapter<Category> adapter;

    //Create a new category.
    Category newCategory;






    MaterialEditText editName;
    FButton btnSelect, btnUpload;

    Uri saveUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);



        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");






        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //set name for user
        View  headerView = navigationView.getHeaderView(0);
        txtFullName = headerView.findViewById(R.id.txtFullname);
        txtFullName.setText(Common.currentUser.getName());
        //load menu
        list_menu = findViewById(R.id.list_menu);
        loadMenu();
        list_menu.setAdapter(adapter);



        //Register
        registerForContextMenu(list_menu);








    }


    //
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Delete")) {
            deleteCatagory(adapter.getRef(item.getItemId()).getKey());
            Toast.makeText(this,"Deleted",Toast.LENGTH_SHORT).show();
        }

        else if(item.getTitle().equals("Update")) {


            updateCategory(adapter.getRef(item.getItemId()).getKey(),adapter.getItem(item.getItemId()));
            Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void updateCategory(final String key, final Category item) {
        AlertDialog.Builder altertDialog = new AlertDialog.Builder(Home.this);
        altertDialog.setTitle("Update Category");
        altertDialog.setMessage("Please fill full information");



        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_menu_layout,null);

        btnSelect = addMenuLayout.findViewById(R.id.btnSelect);
        btnUpload = addMenuLayout.findViewById(R.id.btnUpload);
        editName = addMenuLayout.findViewById(R.id.editName);

        editName.setText(item.getName());
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
                //here just update category

                item.setName(editName.getText().toString());
                category.child(key).setValue(item);

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

    private void updateImage(final Category item) {

        if(saveUri!=null) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading ......");
            progressDialog.show();


            String imageName = UUID.randomUUID().toString();

            final StorageReference imageFolder = storageReference.child("image/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Home.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Home.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void deleteCatagory(String key) {
        category.child(key).removeValue();
    }


    private void showDialog() {


        AlertDialog.Builder altertDialog = new AlertDialog.Builder(Home.this);
        altertDialog.setTitle("Add New Category");
        altertDialog.setMessage("Please fill full information");



        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_menu_layout,null);

        btnSelect = addMenuLayout.findViewById(R.id.btnSelect);
        btnUpload = addMenuLayout.findViewById(R.id.btnUpload);
        editName = addMenuLayout.findViewById(R.id.editName);


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
                if(newCategory!=null)
                {
                    category.push().setValue(newCategory);
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
                Toast.makeText(Home.this,"Uploaded!!!", Toast.LENGTH_SHORT).show();
                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //set value for newCategory if image upload and  we can get download link
                        newCategory = new Category(editName.getText().toString(),uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this,e.getMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Attach a event , method be actived when startActivityForResult begin.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData()!=null)
        {
            // save uri on saveUri
            saveUri = data.getData();
            Toast.makeText(Home.this,""+saveUri, Toast.LENGTH_SHORT).show();
            btnSelect.setText("Image Selected");
        }



    }


    //Method load menu on listview.
    private void loadMenu()
    {
        adapter = new FirebaseListAdapter<Category>(Home.this,Category.class,R.layout.menu_item,category) {
            @Override
            protected void populateView(View v, Category model, final int position) {
                ImageView imageView = v.findViewById(R.id.menu_image);
                TextView textView = v.findViewById(R.id.menu_name);
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

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Get categoryId and send to new activity.
                        Intent foodlist = new Intent(Home.this,FoodList.class);

                        //Because CategoryId is key , so we just get key of this item
                        foodlist.putExtra("CategoryId",adapter.getRef(position).getKey());


                        startActivity(foodlist);
                    }
                });

            }
        };
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_orders) {
            Intent order = new Intent(Home.this,ViewOrder.class);
            startActivity(order);

        } else if (id == R.id.nav_logout) {
            Intent main = new Intent(Home.this,MainActivity.class);
            startActivity(main);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
