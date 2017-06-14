package application.raye7;


import java.util.List;

/**
 * Created by eman on 13/06/17.
 */


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}