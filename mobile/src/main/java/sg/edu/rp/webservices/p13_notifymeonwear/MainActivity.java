package sg.edu.rp.webservices.p13_notifymeonwear;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<Task> tasks;
    ArrayAdapter<Task> adapter;
    Button btnAdd;
    Task task;
    int actReqCode = 1;
    String r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        DBHelper dbh = new DBHelper(this);
        tasks = dbh.getAllTasks();
        adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, tasks);
        lv.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(i, actReqCode);
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                task = (Task)lv.getItemAtPosition(position);

                registerForContextMenu(lv);
                return false;



//                Task t = tasks.get(position);
//
//                DBHelper dbh = new DBHelper(MainActivity.this);
//                dbh.deleteTask(t.getId());
//                dbh.close();
//                tasks.clear();
//                tasks.addAll(dbh.getAllTasks());
//                adapter.notifyDataSetChanged();

            }
        });

        CharSequence reply = null;
        Intent intent = getIntent();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            reply = remoteInput.getCharSequence("status");
        }

        if (reply != null) {
            r = String.valueOf(reply);
            if (r.equalsIgnoreCase("Completed")) {
                int id = intent.getIntExtra("taskid", 0);
                dbh.deleteTask(id);
                dbh.close();
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"done",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == actReqCode) {
            if (resultCode == RESULT_OK) {
                DBHelper dbh = new DBHelper(MainActivity.this);
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0,0,0,"Update");//menu
        menu.add(0,1,1,"Delete");//menu

    }
    //Step 2
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId()==0) { //check whether the selected menu item ID is 0
            //code for action
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout passPhrase =
                    (LinearLayout) inflater.inflate(R.layout.activity_dialog, null);
            final EditText etName = (EditText) passPhrase
                    .findViewById(R.id.editName);
            final EditText etDesc = (EditText) passPhrase
                    .findViewById(R.id.editDesc);

            etName.setText(task.getName());
            etDesc.setText(task.getDescription());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Enter")
                    .setView(passPhrase)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String nName = etName.getText().toString().trim();
                            String nDesc = etDesc.getText().toString().trim();
                            task.setName(nName);
                            task.setDescription(nDesc);
                            DBHelper dbh = new DBHelper(MainActivity.this);
                            dbh.updateNote(task);
                            dbh.close();
                            tasks.clear();
                            tasks.addAll(dbh.getAllTasks());
                            adapter.notifyDataSetChanged();

                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }else{

            DBHelper dbh = new DBHelper(MainActivity.this);
                dbh.deleteTask(task.getId());
                dbh.close();
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                adapter.notifyDataSetChanged();
                return true;



        }
        return super.onContextItemSelected(item); //pass menu item to the superclass implementation.
    }


}
