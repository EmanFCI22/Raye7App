package application.raye7;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    ImageButton search;
    EditText sourceAdress;
    EditText destinationAdress;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    Spinner spinner,spinner1,spinner2,spinner3;
    ArrayAdapter<String> dataAdapter;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sourceAdress=(EditText) findViewById(R.id.FromAdress);
        destinationAdress=(EditText) findViewById(R.id.ToAdress);
        ImageButton location=(ImageButton) findViewById(R.id.location);
        search=(ImageButton)findViewById(R.id.BSearch);
        ImageButton illusrate=(ImageButton)findViewById(R.id.illustrate);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createDaySpinner();
        createMonthSpinner();
        createYearSpinner();
        createTimeSpinner();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSeach();
            }
        });
        illusrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message();
            }
        });
        location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myLocationAction();
            }

        });

    }

    public void message() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Date ( "+getDay()+" / "+getMonth()+" / "+getYear()+")  Time ( "+getTime()+" )");
        builder1.setCancelable(true);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void createDaySpinner() {
        spinner=(Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Day_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.list_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }
    public void createMonthSpinner()
    {
        spinner1=(Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Month_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.list_item);
        spinner1.setAdapter(adapter);
        spinner1.setSelection(0);
    }
    public void createYearSpinner() {
        spinner2=(Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Year_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.list_item);
        spinner2.setAdapter(adapter);
        spinner2.setSelection(0);
    }
    public void createTimeSpinner() {
        spinner3=(Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Time_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.list_item);
        spinner3.setAdapter(adapter);
        spinner3.setSelection(0);
    }
    private String getDay() {
        String text = spinner.getSelectedItem().toString();
        return text;

    }
    private String getMonth() {
        String text = spinner1.getSelectedItem().toString();
        return text;

    }
    private String getYear() {
        String text = spinner2.getSelectedItem().toString();
        return text;

    }
    private String getTime() {
        String text = spinner3.getSelectedItem().toString();
        return text;

    }
        @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        onLongPressMap();
        onSeach();

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onSeach()
    {

        sourceAdress=(EditText) findViewById(R.id.FromAdress);
        destinationAdress=(EditText) findViewById(R.id.ToAdress);
        String sourceLocation=sourceAdress.getText().toString();
        String destinationLocation=destinationAdress.getText().toString();
        List<Address> sourceAddressList=null;
        List<Address> destinationAddressList=null;
        if (sourceLocation!=null && !sourceLocation.equals(""))
        {
            Geocoder geocoder= new Geocoder(MapsActivity.this);
            try {
                sourceAddressList=geocoder.getFromLocationName(sourceLocation,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (sourceAddressList.size()>0) {
                Address address = sourceAddressList.get(0);
                LatLng lati = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.clear();
                markerPoints.clear();
                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.from_red))
                        .title(sourceLocation)
                        .position(lati)));
                markerPoints.add(lati);
            }
        }
        if (destinationLocation!=null && !destinationLocation.equals(""))
        {
            Geocoder geocoder= new Geocoder(MapsActivity.this);
            try {
                destinationAddressList=geocoder.getFromLocationName(destinationLocation,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (destinationAddressList.size()>0) {
                Address address = destinationAddressList.get(0);
                LatLng lati = new LatLng(address.getLatitude(), address.getLongitude());
                if (sourceLocation == null || sourceLocation.equals("")) {
                    mMap.clear();
                    markerPoints.clear();
                }
                markerPoints.add(lati);
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.to_green))
                        .title(destinationLocation)
                        .position(lati)));

            }
        }
        if (markerPoints.size()==2) {

            try {
                new DirectionFinder(this, sourceLocation, destinationLocation).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }



    }
    public void myLocationAction() {

        List<Address> myList=null;
        Location location=mMap.getMyLocation();
        Geocoder geocoder= new Geocoder(MapsActivity.this);
        try {
           myList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(myList.size()>0) {
            sourceAdress.setText(myList.get(0).getFeatureName()+","+myList.get(0).getCountryName());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 10));

    }

    public void onLongPressMap()
    {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                List<Address> myList=null;
                Geocoder geocoder= new Geocoder(MapsActivity.this);
                try {
                    myList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(myList.size()>0) {
                    destinationAdress.setText(myList.get(0).getFeatureName()+","+myList.get(0).getCountryName());
                }
            }
        });
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.from_red))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.to_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

    }
}
