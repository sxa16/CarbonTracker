package com.sfu.aqua.carbontracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class Fragment extends AppCompatDialogFragment {
    static final int DIALOG_ID = 0;
    int year_x,month_x,day_x;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.utilitybox,null);
        //showDialog();
        createRadio();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("APP", "dialog box");
            }
        };
        return new AlertDialog.Builder(getActivity())
                .setTitle("change message")
                .setView(v)
                .setPositiveButton(android.R.string.ok, listener)
                .create();
    }

    private void createRadio() {
        RadioGroup group = (RadioGroup) getActivity().findViewById(R.id.radio_group);
        String[] num = getResources().getStringArray(R.array.choose);
        for (int i = 0; i<num.length;i++){
            String text = num[i];
            RadioButton button = new RadioButton(getActivity());
            button.setText(text);
            group.addView(button);
        }
    }
    /*
    public void showDialog(){
        EditText start  = (EditText) getActivity().findViewById(R.id.editText5);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(START_DIALOG);
            }
        });
    }

    protected Dialog onCreateDialog(int id){
        if (id == START_DIALOG)
            return new DatePickerDialog(getActivity(), dpickingListener, year_x,month_x,day_x);
        return null;
    }
    Private DatePickerDialog.OnDateSetListener dpickingListener
            = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker View, int year, int monthOfYear, int dayOfMonth){
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            Toast.makeText(getActivity(),year_x+"|"+month_x+"|"+day_x);
        }
    }*/
}
