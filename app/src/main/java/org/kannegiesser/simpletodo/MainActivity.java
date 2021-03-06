package org.kannegiesser.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int EDIT_ITEM_REQUEST = 1;

    List<Item> items;
    ArrayAdapter<Item> itemsAdapter;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView)findViewById(R.id.lvItems);
        items = new Select().from(Item.class).execute();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListeners();
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

    public void onAddItem(View view) {
        EditText etNewItem = (EditText)findViewById(R.id.etNewItem);
        Item item = new Item(etNewItem.getText().toString());
        item.save();
        itemsAdapter.add(item);
        etNewItem.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_ITEM_REQUEST) {
            int listPosition = data.getIntExtra("listPosition", -1);
            String itemText = data.getStringExtra("itemText");
            Item item = items.get(listPosition);
            item.text = itemText;
            item.save();
            itemsAdapter.notifyDataSetChanged();
        }
    }

    private void setupListViewListeners() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                Item itemToDelete = (Item)adapter.getItemAtPosition(pos);
                itemToDelete.delete();
                items.remove(pos);
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("itemText", ((TextView)item).getText());
                i.putExtra("listPosition", pos);
                startActivityForResult(i, EDIT_ITEM_REQUEST);
            }
        });
    }
}
