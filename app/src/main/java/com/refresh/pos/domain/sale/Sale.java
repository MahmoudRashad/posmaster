package com.refresh.pos.domain.sale;

import com.refresh.pos.domain.inventory.LineItem;
import com.refresh.pos.domain.inventory.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sale represents sale operation.
 * 
 * @author Refresh Team
 *
 */
public class Sale {
	
	private final int id;
	private String startTime;
	private String endTime;
	private String status;
	private List<LineItem> items;
	

	public Sale(int id, String startTime) {
		this(id, startTime, startTime, "", new ArrayList<LineItem>());
	}
	
	/**
	 * Constructs a new Sale.
	 * @param id ID of this Sale.
	 * @param startTime start time of this Sale.
	 * @param endTime end time of this Sale.
	 * @param status status of this Sale.
	 * @param items list of LineItem in this Sale.
	 */
	public Sale(int id, String startTime, String endTime, String status, List<LineItem> items) {
		this.id = id;
		this.startTime = startTime;
		this.status = status;
		this.endTime = endTime;
		this.items = items;
	}
/*
* add sale
* */
	public Sale(JSONObject opj) {
		int id1;

		try {
			id1 = opj.getInt("TranId");} catch (JSONException e) {id1 =-1;
			e.printStackTrace();
			}
		this.id = id1;
		try {this.startTime=opj.getString("BookDate");}catch (JSONException e){this.startTime ="";	}
		try {this.endTime=opj.getString("ValueDate");}catch (JSONException e){this.endTime ="";	}

		this.status ="";


		JSONArray tmp;
		try {tmp=opj.getJSONArray("Details");}catch (JSONException e){tmp =null;	}
		this.items = new ArrayList<LineItem>();
        if (tmp != null)
            for (int i = 0; i<tmp.length(); i++){
                try {
                    Product Ptmp= new Product(tmp.getJSONObject(i));
                    LineItem LItmp =new LineItem(Ptmp,Ptmp.getQuantity());
                    items.add(LItmp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }





	}

	/**
	 * Returns list of LineItem in this Sale.
	 * @return list of LineItem in this Sale.
	 */
	public List<LineItem> getAllLineItem(){
		return items;
	}
	
	/**
	 * Add Product to Sale.
	 * @param product product to be added.
	 * @param quantity quantity of product that added.
	 * @return LineItem of Sale that just added.
	 */
	public LineItem addLineItem(Product product, int quantity) {
		
		for (LineItem lineItem : items) {
			if (lineItem.getProduct().getId() == product.getId()) {
				lineItem.addQuantity(quantity);
				return lineItem;
			}
		}
		
		LineItem lineItem = new LineItem(product, quantity);
		items.add(lineItem);
		return lineItem;
	}
	
	public int size() {
		return items.size();
	}
	
	/**
	 * Returns a LineItem with specific index.
	 * @param index of specific LineItem.
	 * @return a LineItem with specific index.
	 */
	public LineItem getLineItemAt(int index) {
		if (index >= 0 && index < items.size())
			return items.get(index);
		return null;
	}

	/**
	 * Returns the total price of this Sale.
	 * @return the total price of this Sale.
	 */
	public double getTotal() {
		double amount = 0;
		for(LineItem lineItem : items) {
			amount += lineItem.getTotalPriceAtSale();
		}
		return amount;
	}

	public int getId() {
		return id;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getStatus() {
		return status;
	}
	
	/**
	 * Returns the total quantity of this Sale.
	 * @return the total quantity of this Sale.
	 */
	public int getOrders() {
		int orderCount = 0;
		for (LineItem lineItem : items) {
			orderCount += lineItem.getQuantity();
		}
		return orderCount;
	}

	/**
	 * Returns the description of this Sale in Map format. 
	 * @return the description of this Sale in Map format.
	 */
	public Map<String, String> toMap() {	
		Map<String, String> map = new HashMap<String, String>();
		map.put("id",id + "");
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("status", getStatus());
		map.put("total", getTotal() + "");
		map.put("orders", getOrders() + "");
		
		return map;
	}
	/**
	 * Returns the description of this Sale in Map format.
	 * @return the description of this Sale in Map format.
	 */
	public JSONArray  itemstoJSONArray() {
		JSONArray res= new JSONArray();
		for (int i=0;i<getAllLineItem().size();i++)
		{

			res.put(items.get(i).itemtoJsonopject());

		}
		return res;
	}

	/**
	 * Removes LineItem from Sale.
	 * @param lineItem lineItem to be removed.
	 */
	public void removeItem(LineItem lineItem) {
		items.remove(lineItem);
	}

}