package com.probojnik.googlemapmarker;

import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class TitleFragment extends DialogFragment implements OnClickListener {
	int index;
	String title, snippet;
	LatLng latLng;
	View view;
	EditText editText1, editText2;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	index = getArguments().getInt(MarkerBean.MB.INDEX.v, -1);
    	title = getArguments().getString(MarkerBean.MB.TITLE.v);
    	snippet = getArguments().getString(MarkerBean.MB.SNIPPET.v);
    	latLng = getArguments().getParcelable(MarkerBean.MB.LATLNG.v);

        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_title, null);
        editText1 = (EditText) view.findViewById(R.id.editText1);
        editText2 = (EditText) view.findViewById(R.id.editText2);
        
        if(title != null) editText1.setText(title);
        if(snippet != null) editText2.setText(snippet);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle((index<0)?getResources().getString(R.string.add):getResources().getString(R.string.update))
        .setPositiveButton(getResources().getString(R.string.save), this).setNeutralButton(getResources().getString(R.string.cancel), null).setView(view);
        
        if(latLng != null) builder.setMessage(latLng.toString());
        if(index > 0) builder.setNegativeButton(getResources().getString(R.string.delete), this);
                
        return builder.create();

    }

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		switch (arg1) {
	    case Dialog.BUTTON_POSITIVE:
	    	((MainActivity) getActivity()).dialogMethod(latLng, index,(byte) (index<0 ?1:2), editText1.getText().toString(), editText2.getText().toString());
	      break;
	    case Dialog.BUTTON_NEGATIVE:
	    	((MainActivity) getActivity()).dialogMethod(latLng, index,(byte) 3);
	      break;
	    default:
	      break;
	    }
	}



}
