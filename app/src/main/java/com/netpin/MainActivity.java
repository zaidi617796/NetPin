package com.netpin;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellSignalStrength;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv = new TextView(this);
        tv.setTextSize(18);
        tv.setPadding(40,100,40,40);
        setContentView(tv);
        checkSignal();
    }
    void checkSignal(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 1);
            return;
        }
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String result = "NetPin - File 1 Ready\n\n";
        try {
            for (CellInfo info : tm.getAllCellInfo()) {
                if (info instanceof android.telephony.CellInfoLte) {
                    int dbm = ((android.telephony.CellInfoLte) info).getCellSignalStrength().getDbm();
                    result += "Signal: " + dbm + " dBm\n";
                    result += "Lat: Getting...\n";
                }
            }
        } catch (Exception e) { result += e.toString(); }
        tv.setText(result + "\nAgar -80 dBm aye to signal tez hai!");
    }
}