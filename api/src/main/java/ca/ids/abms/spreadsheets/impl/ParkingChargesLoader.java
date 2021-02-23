package ca.ids.abms.spreadsheets.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ids.abms.spreadsheets.FlightChargeType;
import ca.ids.abms.spreadsheets.ParkingCharges;

class ParkingChargesLoader extends SSLoaderBase <ParkingCharges> {
    
    public ParkingChargesLoader (final String sheetName) {
        super ("ParkingChargesExample.xlsx", sheetName);
    }
    
    @Override
    protected ParkingChargesImpl createView (WorkbookHelper helper, final String sheetName) {
        final MtowChargesFinder.Result res1 = MtowChargesFinder.find (helper, sheetName);
        final ParkingChargesFinder.Result res2 = ParkingChargesFinder.find (helper, res1.sheet, res1.mtow, res1.mtowDataCells, res1.charges);
        final List <Double> mtows = res2.mtows;
        final Map <FlightChargeType, ParkingChargesImpl.TimeCharges> charges = new HashMap<>();
        for (final FlightChargeType type: res2.charges.keySet()) {
            final ParkingChargesFinder.TimeCharges src = res2.charges.get (type);
            final ParkingChargesImpl.TimeCharges timeCharges = new ParkingChargesImpl.TimeCharges (src.basis, src.freeHours, src.charges);
            charges.put (type, timeCharges);
        }
        return new ParkingChargesImpl (helper, res2.sheet.getSheetName(), mtows, charges, res2.title);
    }
    
}
