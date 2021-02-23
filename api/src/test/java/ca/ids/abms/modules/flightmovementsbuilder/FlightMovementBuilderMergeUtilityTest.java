package ca.ids.abms.modules.flightmovementsbuilder;


import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderMergeUtility;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by c.talpa on 03/03/2017.
 */
public class FlightMovementBuilderMergeUtilityTest {

    @Test
    public void testCheckFlightMovementField(){
        Map<String, Field> flightMovementFields= FlightMovementBuilderMergeUtility.getAllFlightMovementFields();
        Assert.assertTrue(flightMovementFields!=null && flightMovementFields.size()>0);
    }


    @Test
    public void testGetFlightMovementFieldsByName(){
        // Test Case 1
        String nameFieldFlightMovement="movementType";
        Field field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByName(nameFieldFlightMovement);
        Assert.assertTrue(field!=null && field.getName().equals(nameFieldFlightMovement));

        //Test Case
        nameFieldFlightMovement="passengersChargeableDomestic";
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByName(nameFieldFlightMovement);
        Assert.assertTrue(field!=null && field.getName().equals(nameFieldFlightMovement));

        //Test Case 3
        nameFieldFlightMovement="status";
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByName(nameFieldFlightMovement);
        Assert.assertTrue(field!=null && field.getName().equals(nameFieldFlightMovement));

        //Test Case 4
        nameFieldFlightMovement="   ";
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByName(nameFieldFlightMovement);
        Assert.assertTrue(field==null);

        //Test Case 5
        nameFieldFlightMovement=null;
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByName(nameFieldFlightMovement);
        Assert.assertTrue(field==null);
    }


    @Test
    public void  testGetFlightMovementFieldsBySnakeCaseName(){
        // Test Case 1
        String nameFieldFlightMovementSnakeCase="movement_type";
        String nameFieldFlightMovement="movementType";
        Field field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsBySnakeCaseName(nameFieldFlightMovementSnakeCase);
        Assert.assertTrue(field!=null && field.getName().equals(nameFieldFlightMovement));

        //Test Case 2
        nameFieldFlightMovementSnakeCase="passengers_chargeable_domestic";
        nameFieldFlightMovement="passengersChargeableDomestic";
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsBySnakeCaseName(nameFieldFlightMovementSnakeCase);
        Assert.assertTrue(field!=null && field.getName().equals(nameFieldFlightMovement));

        //Test Case 3
        nameFieldFlightMovementSnakeCase="passengersChargeableDomestic";
        nameFieldFlightMovement="passengersChargeableDomestic";
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsBySnakeCaseName(nameFieldFlightMovementSnakeCase);
        Assert.assertTrue(field!=null && field.getName().equals(nameFieldFlightMovement));

        //Test Case 4
        nameFieldFlightMovementSnakeCase="status";
        nameFieldFlightMovement="status";
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsBySnakeCaseName(nameFieldFlightMovementSnakeCase);
        Assert.assertTrue(field!=null && field.getName().equals(nameFieldFlightMovement));

        //Test Case 5
        nameFieldFlightMovementSnakeCase="   ";
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsBySnakeCaseName(nameFieldFlightMovementSnakeCase);
        Assert.assertTrue(field==null);

        //Test Case 6
        nameFieldFlightMovementSnakeCase=null;
        field= FlightMovementBuilderMergeUtility.getFlightMovementFieldsBySnakeCaseName(nameFieldFlightMovementSnakeCase);
        Assert.assertTrue(field==null);
    }


    @Test
    public void  testGetFlightMovementFieldsByManuallyChanged(){
        // Test Case 1
        String manuallyChangedFields="arrival_ad,arrival_time,date_of_flight";
        List<Field> fields= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByManuallyChanged(manuallyChangedFields);
        Assert.assertTrue(fields!=null && fields.size()==3);
        Assert.assertTrue(fields!=null && fields.get(0).getName().equals("arrivalAd"));

        // Test Case 2
        manuallyChangedFields="arrival_ad,arrival_time,date_of_flight,status";
        fields= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByManuallyChanged(manuallyChangedFields);
        Assert.assertTrue(fields!=null && fields.size()==4);
        Assert.assertTrue(fields!=null && fields.get(3).getName().equals("status"));

        // Test Case 3
        manuallyChangedFields="arrival_ad";
        fields= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByManuallyChanged(manuallyChangedFields);
        Assert.assertTrue(fields!=null && fields.size()==1);
        Assert.assertTrue(fields!=null && fields.get(0).getName().equals("arrivalAd"));

        //Test Case 4
        manuallyChangedFields="   ";
        fields= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByManuallyChanged(manuallyChangedFields);
        Assert.assertTrue(fields==null);

        //Test Case 5
        manuallyChangedFields=null;
        fields= FlightMovementBuilderMergeUtility.getFlightMovementFieldsByManuallyChanged(manuallyChangedFields);
        Assert.assertTrue(fields==null);
    }


    @Test
    public void  testCheckFieldManuallyChanged(){
        // Test Case 1
        String fieldToCheck="arrivalAd";
        String manuallyChangedFields="arrival_ad,arrival_time,date_of_flight";
        Boolean returnValue= FlightMovementBuilderMergeUtility.checkManuallyFieldChanged(fieldToCheck,manuallyChangedFields);
        Assert.assertTrue(returnValue==Boolean.TRUE);

        // Test Case 2
        fieldToCheck="status";
        manuallyChangedFields="arrival_ad,arrival_time,date_of_flight";
        returnValue= FlightMovementBuilderMergeUtility.checkManuallyFieldChanged(fieldToCheck,manuallyChangedFields);
        Assert.assertTrue(returnValue==Boolean.FALSE);


        //Test Case 4
        fieldToCheck="  ";
        manuallyChangedFields="arrival_ad,arrival_time,date_of_flight";
        returnValue= FlightMovementBuilderMergeUtility.checkManuallyFieldChanged(fieldToCheck,manuallyChangedFields);
        Assert.assertTrue(returnValue==Boolean.FALSE);

        //Test Case 4
        fieldToCheck="status";
        manuallyChangedFields="   ";
        returnValue= FlightMovementBuilderMergeUtility.checkManuallyFieldChanged(fieldToCheck,manuallyChangedFields);
        Assert.assertTrue(returnValue==Boolean.FALSE);

        //Test Case 5
        fieldToCheck="status";
        manuallyChangedFields=null;
        returnValue= FlightMovementBuilderMergeUtility.checkManuallyFieldChanged(fieldToCheck,manuallyChangedFields);
        Assert.assertTrue(returnValue==Boolean.FALSE);
    }

}
