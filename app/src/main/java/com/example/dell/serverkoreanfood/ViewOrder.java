package com.example.dell.serverkoreanfood;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.serverkoreanfood.Common.Common;
import com.example.dell.serverkoreanfood.Model.Request;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ViewOrder extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference requests;

    ListView lvOrderView;

    Spinner spinner;

    FirebaseListAdapter<Request> adapter;

    String nowStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);


        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        lvOrderView = findViewById(R.id.lvOrderView);


        adapter = new FirebaseListAdapter<Request>(ViewOrder.this, Request.class, R.layout.view_order_layout, requests) {
            @Override
            protected void populateView(View v, Request model, final int position) {
                TextView txtID = v.findViewById(R.id.txtOrderId);
                TextView txtStatus = v.findViewById(R.id.txtOrderStatus);
                TextView txtOrderphone = v.findViewById(R.id.txtOrderPhone);
                TextView txtOrderAddress = v.findViewById(R.id.txtOrderAddress);
                txtID.setText(adapter.getRef(position).getKey());
                txtStatus.setText(convertToStatus(model.getStatus()));
                txtOrderphone.setText(model.getPhone());
                txtOrderAddress.setText(model.getAddress());
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

        lvOrderView.setAdapter(adapter);


    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)) {
            deleteBill(adapter.getRef(item.getItemId()).getKey());

            Toast.makeText(this,"Deleted",Toast.LENGTH_SHORT).show();
        }

        else if(item.getTitle().equals(Common.UPDATE)) {


            showUpdateDialog(adapter.getRef(item.getItemId()).getKey(),adapter.getItem(item.getItemId()));
            Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void deleteBill(String key) {
        requests.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Request request) {




        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");


        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.update_order_layout,null);


        spinner = view.findViewById(R.id.statusSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
               R.array.planets_array , android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        alertDialog.setView(view);




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                nowStatus = parent.getItemAtPosition(position).toString();
                if(nowStatus.equals("Placed"))
                {
                    nowStatus = "0";
                }
                else if(nowStatus.equals("On My Way"))
                {
                    nowStatus = "1";
                }
                else if(nowStatus.equals("Shipped"))
                {
                    nowStatus = "2";
                }

                //Toast.makeText(ViewOrder.this,""+nowStatus,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    request.setStatus(nowStatus);
                    requests.child(key).setValue(request);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });



        alertDialog.show();


    }


    private String convertToStatus(String status) {

        if (status.equals("0"))
            return "Placed";
        if (status.equals("1"))
            return "On my way";
        if (status.equals("2"))
            return "Shipped";
        return null;
    }


}

