/**
 * 
 */
package ca.ids.abms.modules.flightmovements.enumerate;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author heskina
 *
 */
public enum FlightmovementCategoryScope {
	DOMESTIC ("DO", "DOMESTIC"),
    REGIONAL ("RE", "REGIONAL"),
    INTERNATIONAL ("IN", "INTERNATIONAL");
    
    public String getCode() {
        return code;
    }
    public String getMtowFactorClass() {
        return mtowFactorClass;
    }
    
    @JsonValue
    public String toValue() {
        return getCode();
    }
    
    @JsonCreator
    public static FlightmovementCategoryScope forValue (String value) {
        if (value != null) {
            return Arrays.stream (FlightmovementCategoryScope.values()).filter (x->x.code.equals(value)).findFirst().orElse(null);
        }
        return null;
    }
    
    // ------------------- private -----------------
    
    private final String code;
    private final String mtowFactorClass;
    private FlightmovementCategoryScope (final String code, final String mtowFactorClass) {
        this.code = code;
        this.mtowFactorClass = mtowFactorClass;
    }
    
}
