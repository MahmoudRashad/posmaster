package com.refresh.pos.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.refresh.pos.R;
import com.refresh.pos.domain.LanguageController;
import com.refresh.pos.domain.inventory.Inventory;
import com.refresh.pos.domain.inventory.Product;
import com.refresh.pos.domain.inventory.ProductCatalog;
import com.refresh.pos.networkmanger.All_Items_Manger;
import com.refresh.pos.networkmanger.Transactoin_manger;
import com.refresh.pos.networkmanger.logout_Manger;
import com.refresh.pos.techicalservices.DatabaseExecutor;
import com.refresh.pos.techicalservices.Globalclass;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;
import com.refresh.pos.ui.component.UpdatableFragment;
import com.refresh.pos.ui.inventory.InventoryFragment;
import com.refresh.pos.ui.inventory.ProductDetailActivity;
import com.refresh.pos.ui.sale.ReportFragment;
import com.refresh.pos.ui.sale.SaleFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.refresh.pos.techicalservices.Globalclass.sync;
import static com.refresh.pos.ui.MainActivity.reportFragment;
import static com.refresh.pos.ui.MainActivity.saleFragment;

/**
 * This UI loads 3 main pages (Inventory, Sale, Report)
 * Makes the UI flow by slide through pages using ViewPager.
 * 
 * @author Refresh Team
 *
 */
@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {

	public static UpdatableFragment reportFragment, saleFragment;
	static SweetAlertDialog reloadD;
	private static boolean SDK_SUPPORTED;
	private ViewPager viewPager;
	private ProductCatalog productCatalog;
	private String productId;
	private Product product;
	private PagerAdapter pagerAdapter;
	private Resources res;

	public static void refresh(final Activity cont) {
		if (Globalclass.isNetworkAvailable(cont)) {

			reloadD = new SweetAlertDialog(cont, SweetAlertDialog.PROGRESS_TYPE);
			reloadD.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
			reloadD.setTitleText(cont.getResources().getString(R.string.LOADING));
			reloadD.setCancelable(false);
			reloadD.show();

			if (Looper.myLooper() == Looper.getMainLooper()) {
				Thread task = new Thread() {
					@Override
					public void run() {
						sync = true;

						//submit local orders

						if (Globalclass.fristlogin)
							DatabaseExecutor.getInstance().dropAllData();
						get_items_from_api(cont);
						getreports(cont);
						sync = false;
					}
				};

				task.start();
				return;
			}


		} else {
			Toast.makeText(cont, cont.getResources().getString(R.string.network_error_contant), Toast.LENGTH_SHORT).show();
		}
		Globalclass.fristlogin = false;
	}

	public static void getreports(Activity cont) {

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date c = Calendar.getInstance().getTime();



		Date f;
		try {
			f = formatter.parse(getfromdate("" + Globalclass.curr_year));
			c = formatter.parse(gettodate("" + Globalclass.curr_year));
		} catch (ParseException e) {
			e.printStackTrace();
			f = new Date();
		}




		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String to = df.format(c);
		String from = df.format(f);


		Transactoin_manger transactoin_manger = new Transactoin_manger(cont);
		transactoin_manger.setListener(new Transactoin_manger.mycustomer_click_lisner() {
			@Override
			public void onObjectReady(String response) {

				reportFragment.update();
				if (reloadD.isShowing())
					reloadD.dismiss();
			}

			@Override
			public void onFailed(String s) {
				if (reloadD.isShowing())
					reloadD.dismiss();
			}
		});
		transactoin_manger.get_Transactions(from, to);
	}

	private static String getfromdate(String s) {
		return "01/01/" + s;
	}

	private static String gettodate(String s) {
		return "31/12/" + s;
	}

	private static void get_items_from_api(Activity cont) {

		All_Items_Manger all_items_manger = new All_Items_Manger(cont);
		all_items_manger.setCustomObjectListener(new All_Items_Manger.MyCustomObjectListener() {

			@Override
			public void onObjectReady(JSONArray Products) {
				try {
					ProductCatalog catalog = Inventory.getInstance().getProductCatalog();
					catalog.add_Products(Products);
					InventoryFragment.searchBox.setText("");
				} catch (NoDaoSetException e) {
					Log.e("on ObjectRead:get items", e.getMessage());
					e.printStackTrace();
				} catch (JSONException e) {
					Log.e("on ObjectRead:get items", e.getMessage());
					e.printStackTrace();
				}
				if (reloadD.isShowing())
					reloadD.dismiss();
			}

			@Override
			public void onFailed(String title) {
				Log.e("onFailed:get items", title);
				if (reloadD.isShowing())
					reloadD.dismiss();
			}
		});
		all_items_manger.get_items_list();


	}

	public static void go_logout(Activity activity) {
		//free data
		DatabaseExecutor.getInstance().dropAllData();
		//logout
		LoginSharedPreferences temp = new LoginSharedPreferences(activity);
		temp.removeLogin(activity);
		logout_Manger logout_manger = new logout_Manger(activity);
		logout_manger.logout();
		go(activity);
	}

	private static void go(Activity activity) {

		Intent newActivity = new Intent(activity,
				LoginActivity.class);
		activity.startActivity(newActivity);
		activity.finish();
	}

	@SuppressLint("NewApi")
	/**
	 * Initiate this UI.
	 */
	private void initiateActionBar() {
		if (SDK_SUPPORTED) {
			ActionBar actionBar = getActionBar();

			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
				}

				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					viewPager.setCurrentItem(tab.getPosition());
				}

				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				}
			};
			actionBar.addTab(actionBar.newTab().setText(res.getString(R.string.inventory))
					.setTabListener(tabListener), 0, false);
			actionBar.addTab(actionBar.newTab().setText(res.getString(R.string.sale))
					.setTabListener(tabListener), 1, true);
			actionBar.addTab(actionBar.newTab().setText(res.getString(R.string.report))
					.setTabListener(tabListener), 2, false);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
						.parseColor("#73bde5")));
			}

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		String lang = LanguageController.getInstance().getLanguage();
		if (lang.equals("ar"))
			forceRTLIfSupported();
		else
			forceLTRIfSupported();

		res = getResources();

		setContentView(R.layout.layout_main);
		Globalclass.activity=MainActivity.this;
		viewPager = findViewById(R.id.pager);
		super.onCreate(savedInstanceState);
		SDK_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
		initiateActionBar();
		FragmentManager fragmentManager = getSupportFragmentManager();
		pagerAdapter = new PagerAdapter(fragmentManager, res);
		viewPager.setAdapter(pagerAdapter);
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						if (SDK_SUPPORTED)
							getActionBar().setSelectedNavigationItem(position);
					}
				});
		viewPager.setCurrentItem(1);
		Globalclass.curr_year = Calendar.getInstance().get(Calendar.YEAR);
		if (Globalclass.fristlogin) {
			refresh(MainActivity.this);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openQuitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Open quit dialog.
	 */
	private void openQuitDialog() {
		AlertDialog.Builder quitDialog = new AlertDialog.Builder(
				MainActivity.this);
		quitDialog.setTitle(res.getString(R.string.dialog_quit));
		quitDialog.setPositiveButton(res.getString(R.string.quit), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		quitDialog.setNegativeButton(res.getString(R.string.no), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		quitDialog.show();
	}

	/**
	 * Option on-click handler.
	 * @param view
	 */
	public void optionOnClickHandler(View view) {
		viewPager.setCurrentItem(0);
		String id = view.getTag().toString();
		productId = id;
		try {
			productCatalog = Inventory.getInstance().getProductCatalog();
		} catch (NoDaoSetException e) {
			e.printStackTrace();
		}
		product = productCatalog.getProductById(Integer.parseInt(productId));

//		go to details
		Intent newActivity = new Intent(MainActivity.this,
				ProductDetailActivity.class);
		newActivity.putExtra("id", productId);
		startActivity(newActivity);
//		goto details or remove  dialog
//		openDetailDialog();

	}

	/**
	 * Open detail dialog.
	 */
	private void openDetailDialog() {
		AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
		quitDialog.setTitle(product.getName());
		quitDialog.setPositiveButton(res.getString(R.string.remove), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				openRemoveDialog();
			}
		});

		quitDialog.setNegativeButton(res.getString(R.string.product_detail), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent newActivity = new Intent(MainActivity.this,
						ProductDetailActivity.class);
				newActivity.putExtra("id", productId);
				startActivity(newActivity);
			}
		});

		quitDialog.show();
	}

	/**
	 * Open remove dialog.
	 */
	private void openRemoveDialog() {
		AlertDialog.Builder quitDialog = new AlertDialog.Builder(
				MainActivity.this);
		quitDialog.setTitle(res.getString(R.string.dialog_remove_product));
		quitDialog.setPositiveButton(res.getString(R.string.no), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		quitDialog.setNegativeButton(res.getString(R.string.remove), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				productCatalog.suspendProduct(product);
				pagerAdapter.update(0);
			}
		});

		quitDialog.show();
	}

	/**
	 * Get view-pager
	 * @return
	 */
	public ViewPager getViewPager() {
		return viewPager;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.main, menu);

		String lang = LanguageController.getInstance().getLanguage();
		if (lang.equals("en")) {
			menu.findItem(R.id.lang_en).setVisible(false);
		}
		if (lang.equals("ar")) {
			menu.findItem(R.id.lang_ar).setVisible(false);
		}
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lang_en:
            	setLanguage("en");
                return true;
            case R.id.lang_th:
            	setLanguage("th");
                return true;
            case R.id.lang_jp:
            	setLanguage("jp");
                return true;
            case R.id.lang_ar:
            	setLanguage("ar");
                return true;
            case R.id.refresh:
				if(Globalclass.isNetworkAvailable(MainActivity.this))
            	refresh(MainActivity.this);
				else
					Toast.makeText(MainActivity.this,getResources().getString(R.string.network_error_contant),Toast.LENGTH_SHORT).show();
                return true;
			case R.id.logout:
				go_logout(MainActivity.this);
				return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	/**
	 * Set language
	 * @param localeString
	 */
	public void setLanguage(String localeString) {

		Locale locale = new Locale(localeString);
		Locale.setDefault(locale);

		Configuration config = new Configuration();
		config.locale = locale;

		LanguageController.getInstance().setLanguage(localeString);
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		Intent intent = getIntent();
		finish();
		startActivity(intent);


	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void forceRTLIfSupported() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void forceLTRIfSupported() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
		}
	}

	public boolean isRTL(Context ctx) {
		Configuration config = ctx.getResources().getConfiguration();
		return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
	}

}

/**
 * 
 * @author Refresh team
 *
 */
class PagerAdapter extends FragmentStatePagerAdapter {

	private UpdatableFragment[] fragments;
	private String[] fragmentNames;

	/**
	 * Construct a new PagerAdapter.
	 * @param fragmentManager
	 * @param res
	 */
	public PagerAdapter(FragmentManager fragmentManager, Resources res) {
		
		super(fragmentManager);

		reportFragment = new ReportFragment();
		saleFragment = new SaleFragment(reportFragment);
		UpdatableFragment inventoryFragment = new InventoryFragment(
				saleFragment);

		fragments = new UpdatableFragment[] { inventoryFragment, saleFragment,
				reportFragment };
		fragmentNames = new String[] { res.getString(R.string.inventory),
				res.getString(R.string.sale),
				res.getString(R.string.report) };

	}

	@Override
	public Fragment getItem(int i) {
		return fragments[i];
	}

	@Override
	public int getCount() {
		return fragments.length;
	}

	@Override
	public CharSequence getPageTitle(int i) {
		return fragmentNames[i];
	}

	/**
	 * Update
	 * @param index
	 */
	public void update(int index) {
		fragments[index].update();
	}

}