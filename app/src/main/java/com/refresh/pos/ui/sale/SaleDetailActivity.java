package com.refresh.pos.ui.sale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.refresh.pos.R;
import com.refresh.pos.domain.inventory.LineItem;
import com.refresh.pos.domain.inventory.Product;
import com.refresh.pos.domain.sale.Sale;
import com.refresh.pos.domain.sale.SaleLedger;
import com.refresh.pos.networkmanger.TransationitemsManger;
import com.refresh.pos.techicalservices.Globalclass;
import com.refresh.pos.techicalservices.NoDaoSetException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * UI for showing the detail of Sale in the record.
 * @author Refresh Team
 *
 */
public class SaleDetailActivity extends Activity{
	
	private TextView totalBox;
	private TextView dateBox;
	private ListView lineitemListView;
	private List<Map<String, String>> lineitemList;
	private Sale sale;
	private int saleId;
	private SaleLedger saleLedger;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try {
			saleLedger = SaleLedger.getInstance();
		} catch (NoDaoSetException e) {
			e.printStackTrace();
		}
		
		saleId = Integer.parseInt(getIntent().getStringExtra("id"));
		if (Globalclass.isNetworkAvailable(SaleDetailActivity.this))
			sale = get_sale(saleId);
		else
			Toast.makeText(SaleDetailActivity.this, getResources().getString(R.string.network_error_contant), Toast.LENGTH_SHORT).show();

		initUI(savedInstanceState);
	}

	private Sale get_sale(int saleId) {
		final Sale tmp = saleLedger.getSaleById(saleId);
		TransationitemsManger transationitemsManger = new TransationitemsManger(SaleDetailActivity.this);
		transationitemsManger.setListener(new TransationitemsManger.mycustomer_click_lisner() {
			@Override
			public void onObjectReady(JSONArray response) {

				for (int i = 0; i < response.length(); i++) {

					try {
						JSONObject j = response.getJSONObject(i);
						Product p = new Product(j);
						tmp.addLineItem(p, p.getQuantity());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				update();
			}

			@Override
			public void onFailed(String s) {
				Toast.makeText(SaleDetailActivity.this, s, Toast.LENGTH_SHORT).show();

			}
		});
		transationitemsManger.Transationitem("" + tmp.getId());


		return tmp;
	}


	/**
	 * Initiate actionbar.
	 */
	@SuppressLint("NewApi")
	private void initiateActionBar() {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(getResources().getString(R.string.sale));
			actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
		}
	}
	

	/**
	 * Initiate this UI.
	 * @param savedInstanceState
	 */
	private void initUI(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_saledetail);
		
		initiateActionBar();

		totalBox = findViewById(R.id.totalBox);
		dateBox = findViewById(R.id.dateBox);
		lineitemListView = findViewById(R.id.lineitemList);
	}

	/**
	 * Show list.
	 * @param list
	 */
	private void showList(List<LineItem> list) {
		lineitemList = new ArrayList<Map<String, String>>();
		for(LineItem line : list) {
			lineitemList.add(line.toMap());
		}

		SimpleAdapter sAdap = new SimpleAdapter(SaleDetailActivity.this, lineitemList,
				R.layout.listview_lineitem, new String[]{"name","quantity","price"}, new int[] {R.id.name,R.id.quantity,R.id.price});
		lineitemListView.setAdapter(sAdap);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Update UI.
	 */
	public void update() {
		totalBox.setText(sale.getTotal() + "");
		dateBox.setText(sale.getEndTime() + "");
		showList(sale.getAllLineItem());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		update();
	}
}
