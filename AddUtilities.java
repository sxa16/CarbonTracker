package com.sfu.aqua.carbontracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.aqua.carbontracker.models.DayList;
import com.sfu.aqua.carbontracker.models.Electricity;
import com.sfu.aqua.carbontracker.models.Gas;
import com.sfu.aqua.carbontracker.models.Singleton;

public class AddUtilities extends AppCompatActivity {
    static final int START_DIALOG = 0;
    static final int END_DIALOG = 1;
    int changeUnitFlag;
    String oldUtil; // Set default to electricity
    int utilIndex;
    Gas oldGas;
    Electricity oldElect;

    boolean edit = false;

    Calendar currentDate = Calendar.getInstance();
    Calendar oldStartDate;
    Calendar oldEndDate;
    Calendar newStartDate;
    Calendar newEndDate;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    boolean date1 = false;
    boolean date2 = false;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_utilities);
        setupEdit();
        createRadio();
        showDialog();
        extractflag();
        changeFont((TextView) findViewById(R.id.textView13));
        changeFont((TextView) findViewById(R.id.textView14));
        changeFont((TextView) findViewById(R.id.textView15));
        changeFont((TextView) findViewById(R.id.textView16));
        changeFont((TextView) findViewById(R.id.textView21));
    }

    private void extractflag() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",0);
        Log.i("IN SELECT"," "+flag);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_car, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                super.onBackPressed();
                break;
            case R.id.home:
                Intent intent = new Intent(AddUtilities.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ok:
                RadioGroup group = (RadioGroup) findViewById(R.id.GroupRadio);
                int idOfSelected = group.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(idOfSelected);
                EditText txtAmount = (EditText) findViewById(R.id.AmountText);
                EditText people = (EditText) findViewById(R.id.PeopleText);

                String newAmount = txtAmount.getText().toString();
                String newPeople = people.getText().toString();
                boolean valid = true;

                if (newAmount.isEmpty() || newPeople.isEmpty() || newPeople.equals("0")) {
                    Toast.makeText(AddUtilities.this, "Please enter a positive number.", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if (idOfSelected == -1) {
                    Toast.makeText(AddUtilities.this, "Please select a utility.", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if (!date1) {
                    newStartDate = (Calendar) oldStartDate.clone();
                }
                if (!date2) {
                    newEndDate = (Calendar) oldEndDate.clone();
                }
                if (newStartDate.after(newEndDate)) {
                    valid = false;
                    Toast.makeText(AddUtilities.this, "Make sure your start date is before or the same as your end date", Toast.LENGTH_LONG).show();
                }

                if (valid) {
                    Intent intent5 = new Intent();
                    //intent.putExtra("flag",flag);
                    double amount = Double.parseDouble(txtAmount.getText().toString());
                    int num_people = Integer.parseInt(people.getText().toString());
                    String newUtil = radioButton.getText().toString();

                    if (newUtil.equals("Gas")) {
                        Gas gas = new Gas(amount, num_people, newStartDate, newEndDate);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        //Deal with electList
                        if (edit) {
                            if (oldUtil.equals("Electricity")) {
                                Singleton.electList.deleteElectricity(utilIndex);
                                Singleton.dayList.removeElectricity(oldElect);

                                Singleton.gasList.addGas(gas);
                                Singleton.dayList.addGas(gas);
                            } else {
                                Singleton.gasList.changeGas(utilIndex, gas);
                                Singleton.dayList.changeGas(oldGas, gas);
                            }
                        } else {
                            Singleton.gasList.addGas(gas);
                            Singleton.dayList.addGas(gas);
                        }


                    } else {
                        Electricity elect = new Electricity(amount, num_people, newStartDate, newEndDate);
                        if (edit) {
                            if (oldUtil.equals("Gas")) {
                                Singleton.gasList.deleteGas(utilIndex);
                                Singleton.dayList.removeGas(oldGas);

                                Singleton.electList.addElectricity(elect);
                                Singleton.dayList.addElectricity(elect);
                            } else {
                                Singleton.electList.changeElectricity(utilIndex, elect);
                                Singleton.dayList.changeElectricity(oldElect, elect);
                            }
                        } else {
                            Singleton.electList.addElectricity(elect);
                            Singleton.dayList.addElectricity(elect);
                        }
                    }

                    setResult(Activity.RESULT_OK, intent5);
                    Intent intent2 = MainActivity.makeIntent(AddUtilities.this);
                    intent2.putExtra("flag",flag);
                    startActivity(intent2);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEdit() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Intent data = getIntent();
        oldUtil = data.getStringExtra("theUtil");

        double amount;
        int people;

        oldStartDate = Calendar.getInstance();
        oldEndDate = Calendar.getInstance();
        newStartDate = Calendar.getInstance();
        newEndDate = Calendar.getInstance();
        TextView units = (TextView) findViewById(R.id.amountUsage);

        if (oldUtil.equals("Gas")) {
            edit = true;
            utilIndex = data.getIntExtra("gasIndex", -1);
            oldGas = Singleton.gasList.getGas(utilIndex);

            amount = oldGas.getGJ();
            people = oldGas.getPeople();
            units.setText("Amount in GJ");
            oldStartDate = (Calendar) oldGas.getStartDate().clone();
            oldEndDate = (Calendar) oldGas.getEndDate().clone();

        } else if (oldUtil.equals("Electricity")) {
            edit = true;
            utilIndex = data.getIntExtra("electIndex", -1);
            oldElect = Singleton.electList.getElectricity(utilIndex);

            amount = oldElect.getKWh();
            people = oldElect.getPeople();
            units.setText("Amount in KWh");
            oldStartDate.set(oldElect.getStartDate().get(Calendar.YEAR), oldElect.getStartDate().get(Calendar.MONTH), oldElect.getStartDate().get(Calendar.DAY_OF_MONTH));
            oldEndDate.set(oldElect.getEndDate().get(Calendar.YEAR), oldElect.getEndDate().get(Calendar.MONTH), oldElect.getEndDate().get(Calendar.DAY_OF_MONTH));
        } else {
            amount = 0;
            people = 0;

            oldStartDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            oldEndDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            newStartDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            newEndDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }

        EditText amount_t = (EditText) findViewById(R.id.AmountText);
        if (amount != 0) {
            amount_t.setText(String.valueOf(amount));
        }

        EditText people_t = (EditText) findViewById(R.id.PeopleText);
        if (amount != 0) {
            people_t.setText(String.valueOf(people));
        }

        TextView start = (TextView) findViewById(R.id.textView13);
        start.setText(dateFormat.format(oldStartDate.getTime()));

        TextView end = (TextView) findViewById(R.id.textView14);
        end.setText(dateFormat.format(oldEndDate.getTime()));
    }

    private void createRadio() {
        RadioGroup group = (RadioGroup) findViewById(R.id.GroupRadio);
        String[] num = getResources().getStringArray(R.array.choose);
        final TextView units = (TextView) findViewById(R.id.amountUsage);
        for (int i = 0; i < num.length; i++) {
            final String text = num[i];
            RadioButton button = new RadioButton(this);
            button.setText(text);
            group.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (text.equals("Gas")) {
                        units.setText("Amount in GJ");
                    }
                    if (text.equals("Electricity")) {
                        units.setText("Amount in KWh");
                    }
                }
            });
        }

        RadioButton btn;
        if (oldUtil.equals("Gas")) {
            btn = (RadioButton) group.getChildAt(0);
            btn.setChecked(true);
        } else if (oldUtil.equals("Electricity")) {
            btn = (RadioButton) group.getChildAt(1);
            btn.setChecked(true);
        }
    }
    public static Intent makeIntent(Context context) {

        return new Intent(context, AddUtilities.class);
    }

    public void showDialog() {
        final FloatingActionButton start = (FloatingActionButton) findViewById(R.id.startDate);
        final FloatingActionButton end = (FloatingActionButton) findViewById(R.id.endDate);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(START_DIALOG);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(END_DIALOG);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == START_DIALOG)
            return new DatePickerDialog(this, startDatePicker,
                    oldStartDate.get(Calendar.YEAR),
                    oldStartDate.get(Calendar.MONTH),
                    oldStartDate.get(Calendar.DAY_OF_MONTH));
        if (id == END_DIALOG)
            return new DatePickerDialog(this, endDatePicker,
                    oldEndDate.get(Calendar.YEAR),
                    oldEndDate.get(Calendar.MONTH),
                    oldEndDate.get(Calendar.DAY_OF_MONTH));
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDatePicker
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            newStartDate.set(year, month, dayOfMonth);
            TextView start = (TextView) findViewById(R.id.textView13);
            String dateString = dateFormat.format(newStartDate.getTime());
            start.setText(dateString);
            oldStartDate.set(newStartDate.get(Calendar.YEAR), newStartDate.get(Calendar.MONTH), newStartDate.get(Calendar.DAY_OF_MONTH));
            date1 = true;

        }
    };
    private DatePickerDialog.OnDateSetListener endDatePicker
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            newEndDate.set(year, month, dayOfMonth);
            TextView end = (TextView) findViewById(R.id.textView14);
            end.setText(dateFormat.format(newEndDate.getTime()));
            oldEndDate.set(newStartDate.get(Calendar.YEAR), newStartDate.get(Calendar.MONTH), newStartDate.get(Calendar.DAY_OF_MONTH));
            date2 = true;
        }
    };

    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }
}