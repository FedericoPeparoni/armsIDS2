package ca.ids.abms.modules.flightmovementsbuilder.vo;

import com.ubitech.aim.routeparser.RPAirspaceType;



/**
 * Created by c.talpa on 14/03/2017.
 */

public class RPAirspaceVO {

    private String type;

    private String airspceWkt;

    public RPAirspaceVO(String type, String airspceWkt) {
        this.type = type;
        this.airspceWkt = airspceWkt;
    }

    public RPAirspaceType getRPAirspaceType(){
        RPAirspaceType rpAirspaceType=RPAirspaceType.FIR;

        if(type.equalsIgnoreCase("TMA")){
            rpAirspaceType=RPAirspaceType.TMA;
        }
        else if(type.equalsIgnoreCase("FIR_P")) {
            rpAirspaceType=RPAirspaceType.FIR_P;
        }

        return rpAirspaceType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAirspceWkt() {
        return airspceWkt;
    }

    public void setAirspceWkt(String airspceWkt) {
        this.airspceWkt = airspceWkt;
    }

    @Override
    public String toString() {
        return "RPAirspaceVO{" +
            "type='" + type + '\'' +
            ", airspceWkt='" + airspceWkt + '\'' +
            '}';
    }

}
