package com.example.do_an_thuc_tap_main.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.do_an_thuc_tap_main.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "ShopShoeDB.db";
    private static final int DB_VER = 1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts(String uid){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price", "Discount", "Uid"};
        String sqlTable = "DetailOrder";

        qb.setTables(sqlTable);

        // Thêm điều kiện WHERE để lọc theo Uid
        String selection = "Uid=?";
        String[] selectionArgs = {uid};

        Cursor c = qb.query(db, sqlSelect, selection, selectionArgs,null,null,null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){
            do {
                result.add(new Order(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Uid"))
                        ));
            }
            while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO DetailOrder(ProductId, ProductName, Quantity, Price, Discount, Uid) VALUES ('%s','%s','%s','%s','%s','%s')",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getUid());
        db.execSQL(query);
    }

    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM DetailOrder");
        db.execSQL(query);
    }

    public void removeFromCart(String productId) {
        SQLiteDatabase db = getWritableDatabase();

        // Xây dựng điều kiện WHERE để xác định sản phẩm và uid cần xóa
        String whereClause = "ProductId=?";
        String[] whereArgs = {productId};

        // Thực hiện xóa dữ liệu từ bảng DetailOrder
        db.delete("DetailOrder", whereClause, whereArgs);

        // Đóng kết nối với database
        db.close();
    }

}
