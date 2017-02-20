package com.thanggun99.btfilemanager.util;

import android.content.Context;
import android.widget.Toast;

import com.thanggun99.btfilemanager.interfaces.Refresh;
import com.thanggun99.btfilemanager.model.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Thanggun99 on 14/10/2016.
 */

public class CopyHelper {
    private Context c;
    private ArrayList<Item> items;
    private Operation operation;

    public enum Operation {
        COPY, CUT
    }

    public int getItemsCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void paste(File copyTo) {
        if (!copyTo.isDirectory())
            return;
        switch (operation) {
            case COPY:
                Toast.makeText(c, performCopy(copyTo) ? "Copy thành công" : "Copy không thành công", Toast.LENGTH_SHORT).show();
                clear();
                ((Refresh) c).onRefresh();
                break;
            case CUT:
                Toast.makeText(c, performCut(copyTo) ? "Move thành công" : "Move không thành công", Toast.LENGTH_SHORT).show();
                clear();
                ((Refresh) c).onRefresh();
                break;
            default:
                return;
        }
    }


    public CopyHelper(Context context) {
        this.c = context;
    }

    public void cut(Item item) {
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        cut(items);
    }

    public void cut(ArrayList<Item> items) {
        this.items = items;
        operation = Operation.CUT;
    }

    public void copy(Item item) {
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        copy(items);
    }

    public void copy(ArrayList<Item> items) {
        this.items = items;
        operation = Operation.COPY;
    }

    public boolean canPaste() {
        return items != null && !items.isEmpty();
    }

    public boolean performCopy(File fto) {
        boolean res = true;

        for (Item i : items) {
            File f = new File(i.getPath());
            if (f.isFile()) {
                res = copyFile(f, new File(fto.getAbsoluteFile(), f.getName()));
            } else {
                res = copyFolder(f, new File(fto.getAbsoluteFile(), f.getName()));
            }
        }
        return res;
    }

    public boolean performCut(File copyTo) {
        boolean res = true;

        for (Item i : items) {
            File f = new File(i.getPath());

            res = f.renameTo(new File(copyTo, i.getName()));

        }
        return res;
    }

    private boolean copyFolder(File f, File fto) {
        boolean res = true;
        if (f.isDirectory()) {
            if (!fto.exists()) {
                fto.mkdir();
            }

            String[] files = f.list();

            for (String file : files) {
                File srcFile = new File(f, file);
                File destFile = new File(fto, file);

                res = copyFolder(srcFile, destFile);
            }
        } else {
            res = copyFile(f, fto);
        }
        return res;
    }

    private boolean copyFile(File f, File fto) {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(f);
            out = new FileOutputStream(fto);

            byte[] buffer = new byte[1024];

            while (true) {
                int bytes = in.read(buffer);

                if (bytes <= 0)
                    break;
                out.write(buffer, 0, bytes);
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }
}
