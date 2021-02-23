package ca.ids.abms.spreadsheets.impl;

import org.springframework.stereotype.Component;

import ca.ids.abms.spreadsheets.SSService;

@Component
class SSServiceImpl implements SSService {

    @Override
    public ParkingChargesLoader parkingCharges() {
        return parkingChargesLoader;
    }
    
    @Override
    public AerodromeChargesLoader aerodromeCharges() {
        return aerodromeChargesLoader;
    }
    
    private final ParkingChargesLoader parkingChargesLoader = new ParkingChargesLoader (null);
    
    private final AerodromeChargesLoader aerodromeChargesLoader = new AerodromeChargesLoader (null);

}
