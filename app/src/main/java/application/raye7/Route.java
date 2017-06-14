package application.raye7;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by eman on 13/06/17.
 */


public class Route {
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}