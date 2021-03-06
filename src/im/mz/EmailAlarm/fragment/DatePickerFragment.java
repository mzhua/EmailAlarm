package im.mz.EmailAlarm.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Hua on 2014/10/16.
 */
public class DatePickerFragment  extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int year;
    private int month;
    private int day;

    private IDateChange iDateChange;

    public void setiDataChange(IDateChange iDateChange) {
        this.iDateChange = iDateChange;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, this.year, this.month, this.day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        month = month + 1;

        iDateChange.dateChanged(year,month,day);
    }

    public void initialData(int year,int month,int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public interface IDateChange{
        public void dateChanged( int year, int month, int day);
    }
}