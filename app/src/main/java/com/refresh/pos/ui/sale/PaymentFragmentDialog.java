package com.refresh.pos.ui.sale;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.refresh.pos.R;
import com.refresh.pos.domain.DateTimeStrategy;
import com.refresh.pos.domain.inventory.KEY_VALUE;
import com.refresh.pos.domain.sale.Register;
import com.refresh.pos.networkmanger.Sale_flags;
import com.refresh.pos.networkmanger.Submit_order_Manger;
import com.refresh.pos.techicalservices.Globalclass;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.ui.MainActivity;
import com.refresh.pos.ui.component.UpdatableFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.refresh.pos.techicalservices.Globalclass.isNetworkAvailable;
import static com.refresh.pos.techicalservices.Globalclass.sync;

/**
 * A dialog for input a money for sale.
 * @author Refresh Team
 *
 */
@SuppressLint("ValidFragment")
public class PaymentFragmentDialog extends DialogFragment {

	Spinner spinner1, spinner2, spinner3;
	ArrayList<KEY_VALUE> list1, list2, list3;
	private TextView totalPrice;
	private EditText input;
	private Button clearButton;
	private Button confirmButton;
	private String strtext;
	private UpdatableFragment saleFragment;
	private UpdatableFragment reportFragment;

	private Register regis;
	
	/**
	 * Construct a new PaymentFragmentDialog.
	 * @param saleFragment
	 * @param reportFragment
	 */
	public PaymentFragmentDialog(UpdatableFragment saleFragment, UpdatableFragment reportFragment) {
		super();
		this.saleFragment = saleFragment;
		this.reportFragment = reportFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_payment, container,false);
		try {
			regis = Register.getInstance();
		} catch (NoDaoSetException e) {
			e.printStackTrace();
		}

		spinner1 = v.findViewById(R.id.spinner1);
		spinner2 = v.findViewById(R.id.spinner2);
		spinner3 = v.findViewById(R.id.spinner3);
		try {
			if (isNetworkAvailable(getActivity()))
				fillspaners();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("onCreateView: ", e.getMessage());
		}


		strtext=getArguments().getString("edttext");
		input = v.findViewById(R.id.dialog_saleInput);
		totalPrice = v.findViewById(R.id.payment_total);
		totalPrice.setText(strtext);
		clearButton = v.findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				end();
			}
		});

		confirmButton = v.findViewById(R.id.confirmButton);
		confirmButton.setEnabled(false);
		confirmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String inputString = input.getText().toString();
				
				if (inputString.equals("")) {
					Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.please_input_all), Toast.LENGTH_SHORT).show();
					return;
				}
				final double a = Double.parseDouble(strtext);
				final double b = Double.parseDouble(inputString);
				if (b < a) {
					Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.need_money) + " " + (b - a), Toast.LENGTH_SHORT).show();
				} else {


					/////////////////
					if (Globalclass.isNetworkAvailable(getActivity())) {
						//get time
						Date c = Calendar.getInstance().getTime();


						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
						String formattedDate = df.format(c);


						final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
						pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
						pDialog.setTitleText("Loading");
						pDialog.setCancelable(false);
						pDialog.show();

						Submit_order_Manger submit_order_manger = new Submit_order_Manger(getActivity());
						submit_order_manger.setListener(new Submit_order_Manger.mycustomer_click_lisner() {
							@Override
							public void onObjectReady(String response) {
								pDialog.setTitleText(getResources().getString(R.string.change) + "   " + (b - a))
										.setConfirmText("Done")
										.setConfirmClickListener(null)
										.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
								if (response != "") {
									regis.endSale(DateTimeStrategy.getCurrentTime(), Globalclass.ENDED);
									saleFragment.update();
									reportFragment.update();
//					Toast.makeText(getActivity(),getResources().getString(R.string.orderSubmited),Toast.LENGTH_LONG).show();
									if (sync == false)
										MainActivity.refresh(getActivity());
////									dismiss();
//									Bundle bundle = new Bundle();
//									bundle.putString("edttext", b - a + "");
//									bundle.putString("totalprice", strtext);
//									EndPaymentFragmentDialog newFragment = new EndPaymentFragmentDialog(
//											saleFragment, reportFragment);
//									newFragment.setArguments(bundle);
//									newFragment.show(getFragmentManager(), "");
									end();
								}


							}


							@Override
							public void onFailed(String s) {
								pDialog.setTitleText(getResources().getString(R.string.submitordererror))
										.setContentText(s)
										.setConfirmText("Done")
										.setConfirmClickListener(null)
										.changeAlertType(SweetAlertDialog.ERROR_TYPE);

								Log.e("onFailed:  ", s);


//
//								Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
//
//								Bundle bundle = new Bundle();
//								bundle.putString("massaget", s);
//								bundle.putString("totalprice", "");
//								SubmitOrderErrorDialog newFragment = new SubmitOrderErrorDialog(
//										saleFragment, reportFragment);
//								newFragment.setArguments(bundle);
//								newFragment.show(getFragmentManager(), "");


							}
						});
						double Tender = regis.getTotal() + Double.parseDouble((b - a) + "");
						submit_order_manger.Submit_order(regis.getCurrentSale(), (b - a) + "", Globalclass.counterid,
								"DEFAULT", "" + Tender, formattedDate);

					} else {

						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error_contant), Toast.LENGTH_LONG).show();
//			regis.endSale(DateTimeStrategy.getCurrentTime(),Globalclass.wiat_syncserver);
//			saleFragment.update();
//			reportFragment.update();
//			this.dismiss();

					}

					//////////////////////////

//					Bundle bundle = new Bundle();
//					bundle.putString("edttext", b - a + "");
//					bundle.putString("totalprice", strtext);
//					EndPaymentFragmentDialog newFragment = new EndPaymentFragmentDialog(
//							saleFragment, reportFragment);
//					newFragment.setArguments(bundle);
//					newFragment.show(getFragmentManager(), "");
//					end();
				}

			}
		});
		return v;
	}

	/**
	 * End.
	 */
	private void end() {


		this.dismiss();
		
	}
	private void fillspaners() {

		final Sale_flags sale_flags3= new Sale_flags(getActivity());
		sale_flags3.setCustomObjectListener(new Sale_flags.MyCustomObjectListener() {
			@Override
			public void onObjectReady(ArrayList<KEY_VALUE> list) {
				try {
					list3 = list;
					ArrayAdapter<KEY_VALUE> dataAdapter = new ArrayAdapter<KEY_VALUE>(getActivity(),
							android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner3.setAdapter(dataAdapter);
					confirmButton.setEnabled(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(String title) {
				try {
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_spinner_item, new String[]{"No data"});
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner3.setAdapter(dataAdapter);
					Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		Sale_flags sale_flags1= new Sale_flags(getActivity());
		sale_flags1.setCustomObjectListener(new Sale_flags.MyCustomObjectListener() {
			@Override
			public void onObjectReady(ArrayList<KEY_VALUE> list) {
				list1=list;
				try {
					ArrayAdapter<KEY_VALUE> dataAdapter = new ArrayAdapter<KEY_VALUE>(getActivity(),
							android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner1.setAdapter(dataAdapter);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailed(String title) {

				try {
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_spinner_item, new String[]{"No data"});
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner1.setAdapter(dataAdapter);

					Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		sale_flags1.get_spaner1_data();


		Sale_flags sale_flags2= new Sale_flags(getActivity());
		sale_flags2.setCustomObjectListener(new Sale_flags.MyCustomObjectListener() {
			@Override
			public void onObjectReady(ArrayList<KEY_VALUE> list) {
				try {
					list2 = list;
					ArrayAdapter<KEY_VALUE> dataAdapter = new ArrayAdapter<KEY_VALUE>(getActivity(),
							android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner2.setAdapter(dataAdapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(String title) {
				try {
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_spinner_item, new String[]{"No data"});
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner2.setAdapter(dataAdapter);
					Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		sale_flags2.get_spaner2_data();




		spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				Globalclass.price_type = list1.get(i).getKey();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				Globalclass.storeid = list2.get(i).getKey();
				sale_flags3.get_spaner3_data(Globalclass.storeid);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				Globalclass.storeid = list3.get(i).getKey();

			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});



	}


}
