package com.kiran.sqlitedb.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kiran.sqlitedb.R;
import com.kiran.sqlitedb.database.DatabaseHelper;
import com.kiran.sqlitedb.database.model.Fruit;
import com.kiran.sqlitedb.utils.MyDividerItemDecoration;
import com.kiran.sqlitedb.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FruitsAdapter mAdapter;
    private List<Fruit> fruitsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noFruitsView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noFruitsView = findViewById(R.id.empty_fruits_view);

        db = new DatabaseHelper(this);

        fruitsList.addAll(db.getAllFruits());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFruitDialog(false, null, -1);
            }
        });

        mAdapter = new FruitsAdapter(this, fruitsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyFruits();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new fruit in db
     * and refreshing the list
     */
    private void createFruit(String fruit) {
        // inserting fruit in db and getting
        // newly inserted fruit id
        long id = db.insertFruit(fruit);

        // get the newly inserted fruit from db
        Fruit f = db.getFruit(id);

        if (f != null) {
            // adding new fruit to array list at 0 position
            fruitsList.add(0, f);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyFruits();
        }
    }

    /**
     * Updating fruit in db and updating
     * item in the list by its position
     */
    private void updateFruit(String fruit, int position) {
        Fruit f = fruitsList.get(position);
        // updating fruit text
        f.setFruit(fruit);

        // updating fruit in db
        db.updateFruit(f);

        // refreshing the list
        fruitsList.set(position, f);
        mAdapter.notifyItemChanged(position);

        toggleEmptyFruits();
    }

    /**
     * Deleting fruit from SQLite and removing the
     * item from the list by its position
     */
    private void deleteFruit(int position) {
        // deleting the fruit from db
        db.deleteFruit(fruitsList.get(position));

        // removing the fruit from the list
        fruitsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyFruits();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showFruitDialog(true, fruitsList.get(position), position);
                } else {
                    deleteFruit(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a fruit.
     * when shouldUpdate=true, it automatically displays old fruit and changes the
     * button text to UPDATE
     */
    private void showFruitDialog(final boolean shouldUpdate, final Fruit fruit, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.fruit_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputfruit = view.findViewById(R.id.fruit);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_fruit_title) : getString(R.string.lbl_edit_fruit_title));

        if (shouldUpdate && fruit != null) {
            inputfruit.setText(fruit.getFruit());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputfruit.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Fruit!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating fruit
                if (shouldUpdate && fruit != null) {
                    // update fruit by it's id
                    updateFruit(inputfruit.getText().toString(), position);
                } else {
                    // create new fruit
                    createFruit(inputfruit.getText().toString());
                }
            }
        });
    }

    /**
     * Toggling list and empty fruits view
     */
    private void toggleEmptyFruits() {
        // you can check fruitsList.size() > 0

        if (db.getFruitsCount() > 0) {
            noFruitsView.setVisibility(View.GONE);
        } else {
            noFruitsView.setVisibility(View.VISIBLE);
        }
    }
}