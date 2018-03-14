package com.refresh.pos.ui.sale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.refresh.pos.R;
import com.refresh.pos.domain.sale.Register;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.ui.component.UpdatableFragment;

/**
 * Created by mahmoudrashad on 3/14/2018.
 */

@SuppressLint("ValidFragment")
public class SubmitOrderErrorDialog extends DialogFragment {

    String totalprice;
    private Button doneButton;
    private TextView massage;
    private Register regis;
    private UpdatableFragment saleFragment;
    private UpdatableFragment reportFragment;

    /**
     * End this UI.
     *
     * @param saleFragment
     * @param reportFragment
     */
    public SubmitOrderErrorDialog(UpdatableFragment saleFragment, UpdatableFragment reportFragment) {
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

        View v = inflater.inflate(R.layout.submitordererrordialoglayout, container, false);
        String strtext = getArguments().getString("massaget");
        totalprice = getArguments().getString("totalprice");
        massage = v.findViewById(R.id.massage);
        massage.setText(strtext);
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
    private void end() {
        dismiss();
    }

    public void showPopup(View anchorView) {
        Bundle bundle = new Bundle();
        bundle.putString("edttext", totalprice);
        PaymentFragmentDialog newFragment = new PaymentFragmentDialog(saleFragment, reportFragment);
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "");
    }

}
