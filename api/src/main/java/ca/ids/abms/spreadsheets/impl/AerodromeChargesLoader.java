package ca.ids.abms.spreadsheets.impl;

import ca.ids.abms.spreadsheets.AerodromeCharges;
import ca.ids.abms.spreadsheets.FlightChargeType;
import ca.ids.abms.spreadsheets.SSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class AerodromeChargesLoader extends SSLoaderBase <AerodromeCharges> {

    private final Logger log = LoggerFactory.getLogger(AerodromeChargesLoader.class);

    public AerodromeChargesLoader (final String sheetName) {
        super ("AerodromeChargesExample.xlsx", sheetName);
    }

    @Override
    protected AerodromeChargesImpl createView (WorkbookHelper helper, final String sheetName) throws SSException {
        final MtowChargesFinder.Result res1 = MtowChargesFinder.find (helper, sheetName);
        if (res1 != null) {
            final AerodromeChargesFinder.Result res2 = AerodromeChargesFinder.find(helper, res1.sheet, res1.mtow, res1.mtowDataCells, res1.charges);
            final List<Double> mtows = res2.mtows.stream().map(x -> x.value).collect(Collectors.toList());
            final Map<FlightChargeType, List<AerodromeChargesImpl.TimeCharges>> charges = new HashMap<>();
            for (final FlightChargeType type : res2.timeCharges.keySet()) {
                charges.put(type,
                    res2.timeCharges.get(type).stream()
                        .map(x -> new AerodromeChargesImpl.TimeCharges(
                            x.timeRange.value.endSecond,
                            x.charges.stream().map(y -> y.value).collect(Collectors.toList())))
                        .collect(Collectors.toList()));
            }
            return new AerodromeChargesImpl(helper, res2.sheet.getSheetName(), mtows, charges, res2.title);
        } else {
            log.warn("Cannot parse the workbook with sheetname {}, ", sheetName);
            throw new SSException("Workbook not valid");
        }
    }

}
