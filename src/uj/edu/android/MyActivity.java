package uj.edu.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class MyActivity extends Activity {
    protected Button notificationsButton;
    protected int notificationsCount = 0;

    protected ChargesDAO chargesDao;
    protected BroadcastReceiver batteryReceiver;

    protected ChargesListAdapter chargesListAdapter;
    protected List<ChargeEntry> charges;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        View count = menu.findItem(R.id.badge).getActionView();
        notificationsButton = (Button) count.findViewById(R.id.notif_count);
        notificationsButton.setText(String.valueOf(notificationsCount));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_charges) {
            chargesDao.deleteAll();
            updateChargesData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateChargesData() {
        charges = chargesDao.all();
        chargesListAdapter.clear();
        chargesListAdapter.addAll(charges);
        chargesListAdapter.notifyDataSetChanged();

        TextView averageChargingTime = (TextView) findViewById(R.id.average_charging);
        TextView totalChargingTime = (TextView) findViewById(R.id.total_charging);

        averageChargingTime.setText(TimeUtils.humanize(Math.round(chargesDao.averageDailyChargingTime())));
        totalChargingTime.setText(TimeUtils.humanize(Math.round(chargesDao.totalDailyChargingTime())));

        notificationsCount = charges.size();
        invalidateOptionsMenu();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        chargesDao = new ChargesDAO(getApplicationContext());

        charges = chargesDao.all();

        chargesListAdapter = new ChargesListAdapter(this, charges);

        ListView chargesList = (ListView) findViewById(R.id.charges_list);
        chargesList.setAdapter(chargesListAdapter);

        chargesListAdapter.addAll(charges);

        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                if (status == BatteryManager.BATTERY_STATUS_CHARGING)
                    Log.d("CHARGING STATUS", "Charging"); else
                if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
                    Log.d("CHARGING STATUS", "Not charging"); else
                if (status == BatteryManager.BATTERY_STATUS_DISCHARGING)
                    Log.d("CHARGING STATUS", "Disharging"); else
                if (status == BatteryManager.BATTERY_STATUS_FULL)
                    Log.d("CHARGING STATUS", "Full");

                // boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

                if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING || status == BatteryManager.BATTERY_STATUS_DISCHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
                    MyActivity.this.chargesDao.stopCharging();
                    MyActivity.this.updateChargesData();
                }

                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    MyActivity.this.chargesDao.startCharging();
                }
            }
        };

        this.registerReceiver(this.batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public class ChargesListAdapter extends ArrayAdapter<ChargeEntry> {
        public ChargesListAdapter(Context context, List<ChargeEntry> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChargeEntry entry = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.charge_list_item, parent, false);
            }

            TextView startedAt = (TextView) convertView.findViewById(R.id.started_at);
            TextView endedAt = (TextView) convertView.findViewById(R.id.ended_at);
            TextView duration = (TextView) convertView.findViewById(R.id.duration);

            Date startTime = new Date(entry.getStartTime());
            Date endTime = new Date(entry.getEndTime());

            startedAt.setText(startTime.toString());
            endedAt.setText(endTime.toString());
            duration.setText(TimeUtils.humanize(entry.getDuration()));

            return convertView;
        }
    }
}
