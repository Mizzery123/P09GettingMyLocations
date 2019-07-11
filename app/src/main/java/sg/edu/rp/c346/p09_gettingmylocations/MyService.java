package sg.edu.rp.c346.p09_gettingmylocations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {

    String folderLocation;
    boolean started;
    FusedLocationProviderClient client;
    LocationCallback mLocationCallBack;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("Service", "Service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false){
            started = true;
            Log.d("Service", "Service started");

            client = LocationServices.getFusedLocationProviderClient(this);

            folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";

            File folder = new File(folderLocation);
            if (folder.exists() == false){
                boolean result = folder.mkdir();
                if (result == true){
                    Log.d("File Read/Write", "Folder created");
                }else{
                    Log.d("File Read/Write", "Folder creation failed");
                }
            }

            final LocationRequest mLocationRequest = LocationRequest.create();

            mLocationCallBack = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null){
                        Location data = locationResult.getLastLocation();
                        double lat = data.getLatitude();
                        double lng = data.getLongitude();
                        String msg = lat + ", " + lng + "\n";
                        try{
                            File targetFile = new File(folderLocation, "cor.txt");
                            FileWriter writer = new FileWriter(targetFile, true);
                            writer.write(msg);
                            writer.flush();
                            writer.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            };

            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setSmallestDisplacement(100);

            if (checkPermission()) {
                client.requestLocationUpdates(mLocationRequest, mLocationCallBack, null);

            }
        }else{
            Log.d("Service", "Service is still running");
        }
        Toast.makeText(getApplicationContext(), "Service is running", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "Service exited");


        client.removeLocationUpdates(mLocationCallBack);

        super.onDestroy();
    }






    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_Read = ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck_Write = ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Read == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Write == PermissionChecker.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }
}