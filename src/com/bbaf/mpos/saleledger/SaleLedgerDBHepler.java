package com.bbaf.mpos.saleledger;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.bbaf.mpos.sale.Sale;
import com.bbaf.mpos.sale.SaleLineItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class SaleLedgerDBHepler extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "bbafmpos2.db";
	private static final String TABLE_SALE_LEDGER = "saleledger";
	private static final String TABLE_PRODUCT_LEDGER = "productledger";
	private static final int DATABASE_VERSION = 1;
	private static SaleLedgerDBHepler dao;

	private SaleLedgerDBHepler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static SaleLedgerDBHepler getInstance(Context context) {
		if (dao == null)
			dao = new SaleLedgerDBHepler(context);
		return dao;
	}

	public static SaleLedgerDBHepler getInstance() {
		return dao;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// date text yyyy-MM-dd HH:mm:ss
		String sql = String
				.format("CREATE TABLE %s (_key INTEGER PRIMARY KEY, Date TEXT, TotalPrice DOUBLE);",
						TABLE_SALE_LEDGER);
		db.execSQL(sql);
		sql = String
				.format("CREATE TABLE %s (_key INTEGER PRIMARY KEY, Date TEXT, ProductId TEXT, ProductName TEXT, UnitPrice DOUBLE, Price DOUBLE);",
						TABLE_PRODUCT_LEDGER);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "USE TABLE IF EXISTS " + TABLE_SALE_LEDGER;
		db.execSQL(sql);
		sql = "USE TABLE IF EXISTS " + TABLE_PRODUCT_LEDGER;
		db.execSQL(sql);
	}

	public long addSale(Sale sale) {
		
		ArrayList<SaleLineItem> lines = sale.getAllList();
		
		// date text yyyy-MM-dd HH:mm:ss
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String current = sdf.format(new Date());
		
		/** add Sale Ledger **/
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues value = new ContentValues();
			value.put("Date", current);
			value.put("TotalPrice", sale.getTotal());
			long row = db.insert(TABLE_SALE_LEDGER, null, value);

			db.close();
			Log.d("add sale", "add SL r:" + row + " " + value.toString());
			
		} catch (Exception e) {
			return -1;
		}
		
		/** looping add Product Ledger **/		
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			for (int i = 0; i < lines.size(); i++) {
				SaleLineItem item = lines.get(i);
				
				ContentValues value = new ContentValues();
				value.put("Date", current);
				value.put("ProductId",item.getProductDescription().getId());
				value.put("ProductName", item.getProductDescription().getName());
				value.put("UnitPrice", item.getProductDescription().getPrice());
				value.put("Price", item.getTotal());
				long row = db.insert(TABLE_PRODUCT_LEDGER, null, value);
				
				Log.d("add sale", "add PL r:" + row + " " + i + "/" + lines.size() + " " + value.toString());
			}
			
			db.close();

		} catch (Exception e) {
			return -2;
		}
		
		return 0;
	}
	
	//
	// public long addProduct(ProductDescription product) {
	// try {
	// SQLiteDatabase db = this.getWritableDatabase();
	//
	// ContentValues value = new ContentValues();
	// value.put("ProductId", product.getId());
	// value.put("ProductName", product.getName());
	// value.put("Price", product.getPrice());
	// value.put("Cost", product.getCost());
	//
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// String currentDateandTime = sdf.format(new Date());
	// value.put("DateModified", currentDateandTime);
	//
	// long rows = db.insert(TABLE_INVENTORY, null, value);
	//
	// db.close();
	// return rows; // return rows inserted.
	//
	// } catch (Exception e) {
	// return -1;
	// }
	// }
	//
	// public long addQuantity(ProductDescription product, int quantity) {
	// try {
	// SQLiteDatabase db = this.getWritableDatabase();
	//
	// ContentValues value = new ContentValues();
	// value.put("ProductId", product.getId());
	// value.put("ProductQuantity", quantity);
	//
	// long rows = db.insert(TABLE_QUANTITY, null, value);
	//
	// db.close();
	// return rows; // return rows inserted.
	//
	// } catch (Exception e) {
	// return -1;
	// }
	// }
	//
	// public long setQuantity(ProductDescription product, int quantity) {
	// try {
	// SQLiteDatabase db = this.getWritableDatabase();
	//
	// ContentValues value = new ContentValues();
	// value.put("ProductId", product.getId());
	// value.put("ProductQuantity", quantity);
	//
	// //long rows = db.insert(TABLE_QUANTITY, null, value);
	// long rows = db.update(TABLE_QUANTITY, value, " ProductId=?",
	// new String[] { String.valueOf(product.getId()) });
	//
	// db.close();
	// return rows; // return rows inserted.
	//
	// } catch (Exception e) {
	// return -1;
	// }
	// }
	//
	// public ProductDescription getProduct(String id) {
	// try {
	// SQLiteDatabase db = this.getReadableDatabase();
	// ProductDescription product = null;
	// Cursor cursor = db.query(TABLE_INVENTORY, new String[] { "*" },
	// "ProductId=?", new String[] { id }, null,
	// null, null, null);
	// if (cursor != null) {
	// if (cursor.moveToFirst()) {
	// /**
	// * 0 = _key 1 = ProductId 2 = ProductName 3 = Price 4 = Cost
	// * 5 = DateModified
	// */
	// product = new ProductDescription(cursor.getInt(0),
	// cursor.getString(1), cursor.getString(2),
	// cursor.getDouble(3), cursor.getDouble(4),
	// cursor.getString(5));
	// }
	// }
	// cursor.close();
	// db.close();
	// return product;
	// } catch (Exception e) {
	// Log.d("table", "ex");
	// return null;
	// }
	// }
	//
	// public ArrayList<ProductDescription> getAllProduct() {
	// try {
	// ArrayList<ProductDescription> productList = new
	// ArrayList<ProductDescription>();
	//
	// SQLiteDatabase db = this.getReadableDatabase();
	//
	// String strSQL = "SELECT  * FROM " + TABLE_INVENTORY;
	// Cursor cursor = db.rawQuery(strSQL, null);
	//
	// if (cursor != null) {
	// if (cursor.moveToFirst()) {
	// do {
	// ProductDescription product = new ProductDescription(
	// cursor.getInt(0), cursor.getString(1),
	// cursor.getString(2), cursor.getDouble(3),
	// cursor.getDouble(4), cursor.getString(5));
	// productList.add(product);
	// } while (cursor.moveToNext());
	// }
	// }
	// cursor.close();
	// db.close();
	// return productList;
	// } catch (Exception e) {
	// return null;
	// }
	// }
	//
	// public int getQuantity(String id) {
	// try {
	// SQLiteDatabase db = this.getReadableDatabase();
	// int quantity = -1;
	// Cursor cursor = db.query(TABLE_QUANTITY, new String[] { "*" },
	// "ProductId=?", new String[] { id }, null,
	// null, null, null);
	//
	// if (cursor != null) {
	// if (cursor.moveToFirst()) {
	// quantity = cursor.getInt(1);
	// }
	// }
	// cursor.close();
	// db.close();
	// return quantity;
	// } catch (Exception e) {
	// return -1;
	// }
	// }
	//
	// public long editProduct(ProductDescription oldProduct,
	// ProductDescription newProduct) {
	// try {
	//
	// SQLiteDatabase db = this.getWritableDatabase();
	//
	// ContentValues value = new ContentValues();
	// value.put("ProductId", newProduct.getId());
	// value.put("ProductName", newProduct.getName());
	// value.put("Price", newProduct.getPrice());
	// value.put("Cost", newProduct.getCost());
	// long rows = db.update(TABLE_INVENTORY, value, " ProductId=?",
	// new String[] { String.valueOf(oldProduct.getId()) });
	//
	// db.close();
	// return rows;
	//
	// } catch (Exception e) {
	// return -1;
	// }
	//
	// }
	//
	// public long editQuantity(ProductDescription oldProduct,
	// ProductDescription newProduct,
	// int newQuantity) {
	// try {
	//
	// SQLiteDatabase db = this.getWritableDatabase();
	//
	// ContentValues value = new ContentValues();
	// value.put("ProductId", newProduct.getId());
	// value.put("ProductQuantity", newQuantity);
	// long rows = db.update(TABLE_QUANTITY, value, " ProductId=?",
	// new String[] { String.valueOf(oldProduct.getId()) });
	//
	// db.close();
	// return rows;
	//
	// } catch (Exception e) {
	// return -1;
	// }
	// }
	//
	// public long removeProduct(ProductDescription product) {
	// try {
	//
	// SQLiteDatabase db = this.getWritableDatabase();
	//
	// long rows = db.delete(TABLE_INVENTORY, "ProductId=?",
	// new String[] { String.valueOf(product.getId()) });
	// db.delete(TABLE_QUANTITY, "ProductId=?",
	// new String[] { String.valueOf(product.getId()) });
	//
	// db.close();
	// return rows; // return rows delete.
	//
	// } catch (Exception e) {
	// return -1;
	// }
	// }
	//
	// // bat: for test, get a part of string and find the products that have
	// the string in its name
	// //
	// public ArrayList<ProductDescription> getProductBySomething(String
	// something) {
	// try {
	// SQLiteDatabase db = this.getReadableDatabase();
	// ArrayList<ProductDescription> productList = new
	// ArrayList<ProductDescription>();
	//
	// Cursor cursor = db.query(TABLE_INVENTORY, new String[] { "*" },
	// "ProductName LIKE '%" + something + "%'", null, null, null, null);
	//
	// if (cursor != null) {
	// if (cursor.moveToFirst()) {
	// do {
	// ProductDescription product = new ProductDescription(
	// cursor.getInt(0), cursor.getString(1),
	// cursor.getString(2), cursor.getDouble(3),
	// cursor.getDouble(4), cursor.getString(5));
	// productList.add(product);
	// } while (cursor.moveToNext());
	// }
	// }
	// cursor.close();
	// db.close();
	// return productList;
	// } catch (Exception e) {
	// Log.d("table", "ex");
	// return null;
	// }
	// }

}