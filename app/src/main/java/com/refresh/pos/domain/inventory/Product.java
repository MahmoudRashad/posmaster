package com.refresh.pos.domain.inventory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Product or item represents the real product in store.
 * 
 * @author Refresh Team
 *
 */
public class Product {

	/**
	 * Static value for UNDEFINED ID.
	 */
	public static int UNDEFINED_ID = -1;
	private int id;
	private int quantity;
	private String name;
	private String barcode;
	private double unitPrice;

	/**
	 * Constructs a new Product.
	 * @param id ID of the product, This value should be assigned from database.
	 * @param name name of this product.
	 * @param barcode barcode (any standard format) of this product.
	 * @param salePrice price for using when doing sale.
	 */
	public Product(int id, String name, String barcode, double salePrice) {
		this.id = id;
		this.name = name;
		this.barcode = barcode;
		this.unitPrice = salePrice;
		this.quantity=0;
	}	/**
	 * Constructs a new Product.
	 * @param opj opj JSONObject for product data
	 */
	public Product(JSONObject opj) {
		try {
			this.id = opj.getInt("ItemId");
		} catch (JSONException e) {
			e.printStackTrace();
			id = UNDEFINED_ID;
			UNDEFINED_ID++;
		}
		try {this.name = opj.getString("ItemName");} catch (JSONException e) {e.printStackTrace();name="";}
        try {
            this.barcode = opj.getString("ItemCode");
        } catch (JSONException e) {
            e.printStackTrace();
            barcode = "";
        }

        if (opj.has("SellingPrice"))
            try {
                this.unitPrice = opj.getDouble("SellingPrice");
            } catch (JSONException e) {
                e.printStackTrace();
                unitPrice = 0.0;
            }
        else
			try {
				this.unitPrice = opj.getDouble("Price");

			} catch (JSONException e) {
				e.printStackTrace();
				unitPrice = 0.0;
			}

		if (opj.has("Quantity"))
			try {
				this.quantity = opj.getInt("Quantity");
			} catch (JSONException e) {
				e.printStackTrace();
				quantity = 0;
			}
		else
			try {this.quantity = opj.getInt("ReorderQuantity");} catch (JSONException e) {e.printStackTrace();quantity=0;}
	}
	
	/**
	 * Constructs a new Product.
	 * @param name name of this product.
	 * @param barcode barcode (any standard format) of this product.
	 * @param salePrice price for using when doing sale.
	 */
	public Product(String name, String barcode, double salePrice) {
		this(UNDEFINED_ID, name, barcode, salePrice);
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Returns name of this product.
	 * @return name of this product.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name of this product.
	 * @param name name of this product.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns id of this product.
	 * @return id of this product.
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns barcode of this product.
	 * @return barcode of this product.
	 */
	public String getBarcode() {
		return barcode;
	}

	/**
	 * Sets barcode of this product.
	 *
	 * @param barcode barcode of this product.
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	/**
	 * Returns price of this product.
	 * @return price of this product.
	 */
	public double getUnitPrice() {
		return unitPrice;
	}

	/**
	 * Sets price of this product.
	 *
	 * @param unitPrice price of this product.
	 */
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * Returns the description of this Product in Map format. 
	 * @return the description of this Product in Map format.
	 */
	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id + "");
		map.put("name", name);
		map.put("barcode", barcode);
		map.put("unitPrice", unitPrice + "");
		return map;
		
	}
	
}
