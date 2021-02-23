package ca.ids.abms.modules.util.models;

import ca.ids.abms.modules.util.models.geometry.CoordinatesConversionUtility;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by c.talpa on 11/12/2016.
 */
public class CoordinatesConversionUtilityTest {
    /**
     * Sets the up.
     *
     * @throws Exception
     *           the exception
     */
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testConvertLatitudeLongitudeFromDecimalToDMS() {

        // Test Case 1
        Double latitude=42.248874999999998;
        Double longitude=12.599000000000000;
        String trueValue = "421456N0123556E";
        String coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(trueValue, coordinateDMS);

        // Test Case 2
        latitude=45.287000;
        longitude=-75.873367;
        trueValue = "451713N0755224W";
        coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(trueValue,coordinateDMS);

        // Test Case 3
        latitude=-19.010569;
        longitude=29.201406;
        trueValue = "190038S0291205E";
        coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(trueValue,coordinateDMS);

        // Test Case 4 - Roma-Fiumicino
        latitude=41.800278;
        longitude=12.238889;
        trueValue = "414801N0121420E";
        coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(trueValue,coordinateDMS);

        // Test Case 5 - Gaborone airport
        latitude=-24.555278;
        longitude=25.918333;
        trueValue = "243319S0255506E";
        coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(trueValue,coordinateDMS);

        // Test Case 6 - São Paulo–Guarulhos
        latitude=-23.435556;
        longitude=-46.473056;
        trueValue = "232608S0462823W";
        coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(coordinateDMS, trueValue);

        // Test Case 7 - Caracas Airport
        latitude=10.603056;
        longitude=-66.990556;
        trueValue = "103611N0665926W";
        coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(coordinateDMS, trueValue);
    }

    @Test
    public void testConvertLatitudeLongitudeFromDecimalToDMSNull() {

        // Test Case 1
        Double latitude=null;
        Double longitude=12.599000000000000;
        String trueValue = null;
        String coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(coordinateDMS, trueValue);

        // Test Case 2
        latitude=null;
        longitude=null;
        trueValue = null;
        coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(coordinateDMS, trueValue);

        // Test Case 3
        latitude=null;
        longitude=42.248874999999998;
        trueValue = null;
        coordinateDMS = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude);
        assertEquals(coordinateDMS, trueValue);
    }


    @Test
    public void testConvertLatitudeLongitudeFromDMSToDecimal(){

        // Test Case 1 - Roma-Fiumicino
        String coordinates = "414801N0121420E";
        Double trueLatitude=41.800278;
        Double trueLongitude=12.238889;
        Double calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        Double calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);

        // Test Case 2 - Tokyo-Narita International Airport
        coordinates = "354555N1402308E";
        trueLatitude=35.765278;
        trueLongitude=140.385556;
        calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);

        // Test Case 3 - Gaborone airport
        coordinates = "243319S0255506E";
        trueLatitude=-24.555278;
        trueLongitude=25.918333;
        calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);

        // Test Case 4 - Santiago del Cile
        coordinates = "332334S0704708W";
        trueLatitude=-33.392778;
        trueLongitude=-70.785556;
        calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);

        // Test Case 5 - Ottawa-YOW
        coordinates = "451921N0754002W";
        trueLatitude=45.3225;
        trueLongitude=-75.667222;
        calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);
    }


    @Test
    public void testConvertLatitudeLongitudeFromDMSToDecimalNull(){

        // Test Case 1 - Roma-Fiumicino
        String coordinates = "0121420E";
        Double trueLatitude=null;
        Double trueLongitude=null;
        Double calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        Double calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);

        // Test Case 2 - Tokyo-Narita International Airport
        coordinates = "354555N";
        trueLatitude=null;
        trueLongitude=null;
        calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);

        // Test Case 3 - Gaborone airport
        coordinates = "   ";
        trueLatitude=null;
        trueLongitude=null;
        calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);

        // Test Case 4 - Santiago del Cile
        coordinates =null;
        trueLatitude=null;
        trueLongitude=null;
        calculateLatitude = CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(coordinates);
        calculateLongitude = CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(coordinates);
        assertEquals(trueLatitude, calculateLatitude);
        assertEquals(trueLongitude, calculateLongitude);

    }

}
