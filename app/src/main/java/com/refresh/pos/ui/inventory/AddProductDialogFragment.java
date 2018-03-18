package com.refresh.pos.ui.inventory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.refresh.pos.R;
import com.refresh.pos.domain.inventory.Inventory;
import com.refresh.pos.domain.inventory.ProductCatalog;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.ui.component.UpdatableFragment;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A dialog of adding a Product.
 * 
 * @author Refresh Team
 *
 */
@SuppressLint("ValidFragment")
public class AddProductDialogFragment extends DialogFragment {

	private EditText barcodeBox;
	private ProductCatalog productCatalog;
	private Button scanButton;
	private EditText priceBox;
	private EditText nameBox;
	private Button confirmButton;
	private Button clearButton;
	private UpdatableFragment fragment;
	private Resources res;

	/**
	 * Construct a new AddProductDialogFragment
	 * @param fragment
	 */
	public AddProductDialogFragment(UpdatableFragment fragment) {
		
		super();
		this.fragment = fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		try {
			productCatalog = Inventory.getInstance().getProductCatalog();
		} catch (NoDaoSetException e) {
			e.printStackTrace();
		}
		
		View v = inflater.inflate(R.layout.layout_addproduct, container,
				false);
		
		res = getResources();

        barcodeBox = v.findViewById(R.id.barcodeBox);
        scanButton = v.findViewById(R.id.scanButton);
        priceBox = v.findViewById(R.id.priceBox);
        nameBox = v.findViewById(R.id.nameBox);
        confirmButton = v.findViewById(R.id.confirmButton);
        clearButton = v.findViewById(R.id.clearButton);

		initUI();
		return v;
	}

	/**
	 * Construct a new 
	 */
	private void initUI() {
		scanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				IntentIntegratorSupportV4 scanIntegrator = new IntentIntegratorSupportV4(AddProductDialogFragment.this);
//				scanIntegrator.initiateScan();
//				new IntentIntegrator(getActivity()).initiateScan();
				IntentIntegrator.forSupportFragment(AddProductDialogFragment.this).initiateScan();

			}
		});

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (nameBox.getText().toString().equals("")
					|| barcodeBox.getText().toString().equals("")
					|| priceBox.getText().toString().equals("")) {


                    SweetAlertDialog pDialog = new SweetAlertDialog(getActivity().getBaseContext(), SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText(res.getString(R.string.please_input_all));
                    pDialog.setConfirmClickListener(null);
                    pDialog.show();

                } else {
					boolean success = productCatalog.addProduct(nameBox
							.getText().toString(), barcodeBox.getText()
							.toString(), Double.parseDouble(priceBox.getText()
							.toString()));

					if (success) {


                        SweetAlertDialog pDialog = new SweetAlertDialog(getActivity().getBaseContext(), SweetAlertDialog.WARNING_TYPE);
                        pDialog.setTitleText(res.getString(R.string.success) + ", " + nameBox.getText().toString());
                        pDialog.setConfirmClickListener(null);
                        pDialog.show();

                        fragment.update();
						clearAllBox();
						AddProductDialogFragment.this.dismiss();
						
					} else {
                        SweetAlertDialog pDialog = new SweetAlertDialog(getActivity().getBaseContext(), SweetAlertDialog.WARNING_TYPE);
                        pDialog.setTitleText(res.getString(R.string.fail));
                        pDialog.setConfirmClickListener(null);
                        pDialog.show();
                    }
				}
			}
		});
		
		clearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(barcodeBox.getText().toString().equals("") && nameBox.getText().toString().equals("") && priceBox.getText().toString().equals("")){
					AddProductDialogFragment.this.dismiss();
				} else {
					clearAllBox();
				}
			}
		});
	}

	/**
	 * Clear all box
	 */
	private void clearAllBox() {
		barcodeBox.setText("");
		nameBox.setText("");
		priceBox.setText("");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			barcodeBox.setText(scanContent);
		} else {

            SweetAlertDialog pDialog = new SweetAlertDialog(getActivity().getBaseContext(), SweetAlertDialog.WARNING_TYPE);
            pDialog.setTitleText(res.getString(R.string.fail));
            pDialog.setConfirmClickListener(null);
            pDialog.show();
        }
	}
}
