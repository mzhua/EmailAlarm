package im.mz.EmailAlarm.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Hua on 2014/10/16.
 */
public  class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private int hour;
    private int minute;

    private ITimeChange iTimeChange;

    public void setiTimeChange(ITimeChange iTimeChange) {
        this.iTimeChange = iTimeChange;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
//        final Calendar c = Calendar.getInstance();
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        Dialog dialog = new TimePickerDialog(getActivity(), this, this.hour, this.minute,
                DateFormat.is24HourFormat(getActivity()));
//        dialog.setTitle("选择时间");

        return dialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        iTimeChange.timeChanged(hourOfDay,minute);

    }

    public void initialData(int hour,int minute){
        this.hour = hour;
        this.minute = minute;
    }

    public interface ITimeChange{
        public void timeChanged(int hourOfDay,int minute);
    }
}
