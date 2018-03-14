package com.refresh.pos.ui.sale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.refresh.pos.R;
import com.refresh.pos.domain.DateTimeStrategy;
import com.refresh.pos.domain.sale.Register;
import com.refresh.pos.networkmanger.Submit_order_Manger;
import com.refresh.pos.techicalservices.Globalclass;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.ui.MainActivity;
import com.refresh.pos.ui.component.UpdatableFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.refresh.pos.techicalservices.Globalclass.sync;

/**
 * A dialog shows the total change and confirmation for Sale.
 * @author Refresh Team
 *
 */
@SuppressLint("ValidFragment")
public class EndPaymentFragmentDialog extends DialogFragment  {

	String totalprice;
	private Button doneButton;
	private TextView chg;
	private Register regis;
	private UpdatableFragment saleFragment;
	private UpdatableFragment reportFragment;
	
	/**
	 * End this UI.
	 * @param saleFragment
	 * @param reportFragment
	 */
	public EndPaymentFragmentDialog(UpdatableFragment saleFragment, UpdatableFragment reportFragment) {
		super();
		this.saleFragment = saleFragment;
		this.reportFragment = reportFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			regis = Register.getInstance();
		} catch (NoDaoSetException e) {
			e.printStackTrace();
		}
		
		View v = inflater.inflate(R.layout.dialog_paymentsuccession, container,false);
		String strtext=getArguments().getString("edttext");
		totalprice = getArguments().getString("totalprice");
		chg = v.findViewById(R.id.changeTxt);
        chg.setText(strtext);
        doneButton = v.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				end();
			}
		});
		
		return v;
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	/**
	 * End
	 */
	private void end(){

		if(Globalclass.isNetworkAvailable(getActivity())){
			//get time
			Date c = Calendar.getInstance().getTime();


			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			String formattedDate = df.format(c);



			Submit_order_Manger submit_order_manger = new Submit_order_Manger(getActivity());
			submit_order_manger.setListener(new Submit_order_Manger.mycustomer_click_lisner() {
				@Override
				public void onObjectReady(String response) {
					if (response!=""){
						regis.endSale(DateTimeStrategy.getCurrentTime(),Globalclass.ENDED);
						saleFragment.update();
						reportFragment.update();
//					Toast.makeText(getActivity(),getResources().getString(R.string.orderSubmited),Toast.LENGTH_LONG).show();
						if (sync==false)
						MainActivity.refresh(getActivity());
					dismiss();
					}


				}


				@Override
				public void onFailed(String s) {
					Log.e("onFailed:  ", s);
					Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();

					Bundle bundle = new Bundle();
					bundle.putString("massaget", s);
					bundle.putString("totalprice", totalprice);
					SubmitOrderErrorDialog newFragment = new SubmitOrderErrorDialog(
							saleFragment, reportFragment);
					newFragment.setArguments(bundle);
					newFragment.show(getFragmentManager(), "");


					dismiss();

				}
			});
			double Tender = regis.getTotal() + Double.parseDouble (chg.getText().toString());
			submit_order_manger.Submit_order(regis.getCurrentSale(),chg.getText().toString(),Globalclass.counterid,
					"DEFAULT", "" + Tender, formattedDate);

		}else{

            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error_contant), Toast.LENGTH_LONG).show();
//			regis.endSale(DateTimeStrategy.getCurrentTime(),Globalclass.wiat_syncserver);
//			saleFragment.update();
//			reportFragment.update();
//			this.dismiss();

		}






	}

}
