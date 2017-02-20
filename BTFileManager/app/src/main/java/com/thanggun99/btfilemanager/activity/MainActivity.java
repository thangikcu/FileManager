package com.thanggun99.btfilemanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thanggun99.btfilemanager.R;
import com.thanggun99.btfilemanager.adapter.FileAdapter;
import com.thanggun99.btfilemanager.dialog.MultiDeleteDialog;
import com.thanggun99.btfilemanager.dialog.NewFileDialog;
import com.thanggun99.btfilemanager.dialog.NewFolderDialog;
import com.thanggun99.btfilemanager.dialog.SingleDeleteDialog;
import com.thanggun99.btfilemanager.interfaces.Refresh;
import com.thanggun99.btfilemanager.model.Item;
import com.thanggun99.btfilemanager.util.CopyHelper;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements Refresh {
    private static final String LAYOUT_BACKGROUND = "bg";
    private FileAdapter fileAdapter;
    private File currentDir;
    private ListView listView;
    private CopyHelper copyHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyHelper = new CopyHelper(this);
        currentDir = new File("/sdcard/");
        initComponents();
        fill(currentDir);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Option");
        getMenuInflater().inflate(R.menu.create_option, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_new_folder:
                new NewFolderDialog(this, currentDir.getPath()).show();
                break;
            case R.id.btn_new_file:
                new NewFileDialog(this, currentDir.getPath()).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ((TextView) findViewById(R.id.tv_item_select)).setVisibility(View.GONE);

        getMenuInflater().inflate(R.menu.menu_paste, menu);

        if (copyHelper.canPaste()) {
            ((TextView) findViewById(R.id.tv_item_select)).setVisibility(View.VISIBLE);
            menu.findItem(R.id.btn_cancel).setVisible(true);
            menu.findItem(R.id.btn_paste).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_cancel:
                ((TextView) findViewById(R.id.tv_item_select)).setVisibility(View.GONE);
                copyHelper.clear();
                invalidateOptionsMenu();
                break;
            case R.id.btn_paste:
                copyHelper.paste(currentDir);
                invalidateOptionsMenu();
                break;
            case R.id.btn_setting:
                PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.btn_setting));
                popupMenu.getMenuInflater().inflate(R.menu.popup_setting, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.btn_change_black:
                                ((RelativeLayout) findViewById(R.id.activity_main)).setBackgroundColor(Color.parseColor("#424242"));
                                savePreferences("#424242");
                                break;
                            case R.id.btn_change_white:
                                ((RelativeLayout) findViewById(R.id.activity_main)).setBackgroundColor(Color.parseColor("#EEEEEE"));
                                savePreferences("#EEEEEE");
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void savePreferences(String bg) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAYOUT_BACKGROUND, bg);
        editor.apply();
    }

    private void initComponents() {
        String bg = PreferenceManager.getDefaultSharedPreferences(this).getString(LAYOUT_BACKGROUND, "#424242");
        ((RelativeLayout) findViewById(R.id.activity_main)).setBackgroundColor(Color.parseColor(bg));
        listView = (ListView) findViewById(R.id.list_view_file);
        ((Button) findViewById(R.id.btn_new)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) findViewById(R.id.btn_new)).showContextMenu();
            }
        });
        registerForContextMenu(((Button) findViewById(R.id.btn_new)));
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(listView.getCheckedItemCount() + " selected");
                mode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();

                mode.getMenuInflater().inflate(R.menu.multi_select, menu);

                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean res;
                switch (item.getItemId()) {
                    case R.id.btn_check_all:
                        for (int i = 0; i < listView.getCount(); i++) {
                            ((ListView) listView).setItemChecked(i, true);
                        }
                        res = true;
                        break;
                    default:
                        switch (listView.getCheckedItemCount()) {
                            case 1:
                                switch (item.getItemId()) {
                                    case R.id.btn_delete:
                                        new SingleDeleteDialog(MainActivity.this, fileAdapter.getItem((int) listView.getCheckItemIds()[0])).show();
                                        break;
                                    case R.id.btn_copy:
                                        copyHelper.copy(fileAdapter.getItem((int) listView.getCheckItemIds()[0]));
                                        ((TextView) findViewById(R.id.tv_item_select)).setText(copyHelper.getItemsCount() + " item to copy");
                                        invalidateOptionsMenu();
                                        break;
                                    case R.id.btn_move:
                                        copyHelper.cut(fileAdapter.getItem((int) listView.getCheckItemIds()[0]));
                                        ((TextView) findViewById(R.id.tv_item_select)).setText(copyHelper.getItemsCount() + " item to move");
                                        invalidateOptionsMenu();
                                        break;
                                }
                                res = true;
                                break;
                            default:
                                switch (item.getItemId()) {
                                    case R.id.btn_delete:
                                        new MultiDeleteDialog(MainActivity.this, getItemsSelect()).show();
                                        break;
                                    case R.id.btn_copy:
                                        copyHelper.copy(getItemsSelect());
                                        ((TextView) findViewById(R.id.tv_item_select)).setText(copyHelper.getItemsCount() + " items to copy");
                                        invalidateOptionsMenu();
                                        break;
                                    case R.id.btn_move:
                                        copyHelper.cut(getItemsSelect());
                                        ((TextView) findViewById(R.id.tv_item_select)).setText(copyHelper.getItemsCount() + " items to move");
                                        invalidateOptionsMenu();
                                }
                                res = true;
                                break;
                        }
                        mode.finish();
                }
                return res;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item o = fileAdapter.getItem(position);
                if (o.getImage() == R.drawable.directory_icon || o.getImage() == R.drawable.directory_up) {
                    currentDir = new File(o.getPath());
                    fill(currentDir);
                } else {
                    onFileClick(o);
                }
            }
        });
    }

    public ArrayList<Item> getItemsSelect() {
        ArrayList<Item> items = new ArrayList<Item>();
        for (long l : listView.getCheckItemIds()) {
            items.add(fileAdapter.getItem((int) l));
        }
        return items;
    }

    private void fill(File f) {
        File[] dirs = f.listFiles();
        this.setTitle(currentDir.getPath());
        List<Item> dir = new ArrayList<Item>();
        List<Item> fls = new ArrayList<Item>();
        try {
            for (File ff : dirs) {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String date_modify = formater.format(lastModDate);
                if (ff.isDirectory()) {
                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if (fbuf != null) {
                        buf = fbuf.length;
                    } else buf = 0;
                    String num_item = String.valueOf(buf);
                    if (buf <= 1) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    dir.add(new Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(), R.drawable.directory_icon));
                } else {
                    fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), R.drawable.file_icon));
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getName().equalsIgnoreCase("sdcard")) {
            dir.add(0, new Item("..", "Parent Directory", "", f.getParent(), R.drawable.directory_up));
        }
        fileAdapter = new FileAdapter(this, R.layout.file_view, dir);
        listView.setAdapter(fileAdapter);

    }

    private void onFileClick(Item o) {
        File f = new File(o.getPath());
        if (!f.canRead() || !f.canWrite()) {
            new AlertDialog.Builder(this)
                    .setTitle("Access Denied")
                    .setMessage("Tệp tin bi bảo vệ không có quyền truy cập !")
                    .show();
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(f);
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
            intent.setDataAndType(uri, type == null ? "*/*" : type);
            startActivity((Intent.createChooser(intent, "Open File")));
        }
    }

    @Override
    public void onRefresh() {
        fill(currentDir);
    }
}
