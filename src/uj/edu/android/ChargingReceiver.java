package uj.edu.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by shybovycha on 23.01.15.
 */
public class ChargingReceiver extends BroadcastReceiver {
    protected ChargesDAO chargesDao;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (chargesDao == null) {
            chargesDao = new ChargesDAO(context);
        }

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        // boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        chargesDao.stopCharging();

        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            chargesDao.startCharging();
        }
    }
}
