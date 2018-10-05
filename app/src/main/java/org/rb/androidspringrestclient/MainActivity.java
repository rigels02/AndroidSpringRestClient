package org.rb.androidspringrestclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import org.rb.androidspringrestclient.model.Good;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etUrl;
    private TextView tvResult;
    private String url;
    private ListView listView;
    private List<Good> goods;
    private ArrayAdapter<Good> adapter;
    private Good selectedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItem = null;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etUrl = (EditText) findViewById(R.id.etUrl);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvResult.setMovementMethod(new ScrollingMovementMethod());

        listView = (ListView) findViewById(R.id.listView);
        goods= new ArrayList<>();
//        for(int i=1; i<= 10;i++) {
//            goods.add(new Good(new Date(), "Name_"+i, "Shop_"+i, 12.00, 0.0));
//
//        }
        adapter = new ArrayAdapter<Good>(this,
                R.layout.myitem, goods);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Consume Spring Rest", Snackbar.LENGTH_LONG)
                        .setAction("Get Goods", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getGoodsTask();
                            }
                        }).show();
            }
        });
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add item", Snackbar.LENGTH_LONG)
                        .setAction("Add Good", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addGoodTask();
                            }
                        }).show();
            }
        });
        FloatingActionButton fabDel = (FloatingActionButton) findViewById(R.id.fabDel);

        fabDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Delete Good", Snackbar.LENGTH_LONG)
                        .setAction("Delete Goods", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteGoodTask();
                            }
                        }).show();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemview, int pos, long l) {
                itemview.setSelected(true);
                selectedItem = (Good) adapterView.getAdapter().getItem(pos);

                Toast.makeText(MainActivity.this,"Selected: "+adapterView.getAdapter().getItem(pos)
                        ,Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void deleteGoodTask() {
        if(selectedItem != null){
            Toast.makeText(this,"To be deleted: "+ selectedItem,Toast.LENGTH_SHORT).show();
            new ConfirmDialog(this, "Confirm", "Are you sure to delete?") {
                @Override
                protected void confirmedResult() {
                    new HttpRequestTaskDeleteGood().execute();
                }
            }.show();
        }
    }

    private void addGoodTask() {
        Toast.makeText(this,"To be added.. ",Toast.LENGTH_SHORT).show();
    }

    private void getGoodsTask() {
        url = etUrl.getText().toString();
        new HttpRequestTaskGetAllGoods().execute();

    }
    private class HttpRequestTaskDeleteGood extends AsyncTask<Void,Void,Void> {

        private String errMsg;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //final String url = "http://rest-service.guides.spring.io/greeting";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Void> response=
                        restTemplate.exchange(url+"/"+ selectedItem.getId(), HttpMethod.DELETE,null,Void.class);
                if( !(response.getStatusCode()== HttpStatus.OK)){

                    errMsg="Http Error code: "+response.getStatusCode();
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
                errMsg= e.getMessage();
                //showAlertDialog(MainActivity.this,"Error",e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(errMsg != null) {
                showAlertDialog(MainActivity.this,"Error",(errMsg==null)?"Unknown error":errMsg);

            }else{
                String report = tvResult.getText().toString() + "\nDeleted item id: "+selectedItem.getId();

                tvResult.setText(report);
                getGoodsTask();
                Toast.makeText(MainActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class HttpRequestTaskGetAllGoods extends AsyncTask<Void,Void,List<Good>>{
        String errMsg;

        @Override
        protected List<Good> doInBackground(Void... voids) {
            List<Good> goods=null;
            try {
                //final String url = "http://rest-service.guides.spring.io/greeting";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Good[]> response=
                        restTemplate.getForEntity(url,Good[].class);
                if(response.getStatusCode()== HttpStatus.OK){
                    goods= Arrays.asList(response.getBody());
                }else {
                    errMsg="Http Error code: "+response.getStatusCode();
                }
                return goods;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
                errMsg= e.getMessage();
                //showAlertDialog(MainActivity.this,"Error",e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Good> goods) {
            if(goods != null) {
                //tvResult.setText(goods.toString());
                String report = tvResult.getText().toString() +
                        "\nReceived "+goods.size()+" items.";
                tvResult.setText(report);
                adapter.clear();
                adapter.addAll(goods);
            }else{

                showAlertDialog(MainActivity.this,"Error",(errMsg==null)?"Unknown error":errMsg);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    static void showAlertDialog(Activity activity, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    abstract class  ConfirmDialog{

        private final AlertDialog.Builder  builder;

        ConfirmDialog(Activity activity, String title, String message){
            builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                    confirmedResult();
                }
            });
        }
        void show(){
            builder.create().show();
        }

        protected abstract void confirmedResult();

        //return builder.create();

    }
}
