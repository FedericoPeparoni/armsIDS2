package ca.ids.abms.modules.flightmovementsbuilder.utility;

import ca.ids.abms.modules.flightmovementsbuilder.vo.DeltaFlightVO;
import ca.ids.abms.modules.util.models.geometry.CoordinatesPatternFormatter;
import ca.ids.abms.util.GeometryUtils;
import ca.ids.abms.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.birt.report.model.api.util.StringUtil;

public class Item18Parser {

    private static final int GROUP_OF_INTEREST=1;
    private static final int GROUP_OF_INTEREST_2=2;
    private static final String END_OF_ITEM = "(?:(?:\\b(?:EET|RIF|REG|SEL|OPR|STS|TYP|PER|COM|DAT|NAV|DEST|DEP|ALTN|RALT|CODE|RMK|SUR|DOF|SAR)\\s*/)|$)";
    private static final Pattern STS_PATTERN= Pattern.compile("(?i)STS/((\\w* \\d*)*)");
    private static final Pattern REG_PATTERN= Pattern.compile("(?i)REG/((\\w*.\\d*))");
    private static final Pattern TYP_PATTERN= Pattern.compile("(?i)TYP/(\\d*)(([a-zA-Z0-9]*\\s)*)/?");
    private static final Pattern DEST_PATTERN= Pattern.compile("(?i)\\bDEST\\s*/\\s*(.+?)" + END_OF_ITEM, Pattern.DOTALL);
    private static final Pattern DEP_PATTERN= Pattern.compile("(?i)\\bDEP\\s*/\\s*(.+?)" + END_OF_ITEM, Pattern.DOTALL);
    private static final Pattern OPR_PATTERN= Pattern.compile("(?i)OPR/(([a-zA-Z]*\\s?)*/?)");
    private static final Pattern RMK_PATTERN = Pattern.compile ("(?i)\\bRMK\\s*/\\s*(.+?)" + END_OF_ITEM, Pattern.DOTALL);
    private static final Pattern DESIGNATOR_TIME_PATTERN_DELTA = Pattern.compile("([A-Za-z]/?)+(\\d{4})/?(\\d{4})?");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{4})/?(\\d{4})?");
    private static final Pattern DESIGNATOR_TIME_PATTERN = Pattern.compile("^([A-Za-z]*\\s*[A-Za-z]+)(\\d{4})/?(\\d{4})?");
    private static final Pattern COORD_PATTERN = Pattern.compile ("^\\d{2,6}[nNsS]\\d{2,7}[eEwW]$");

    private static final List<String> GARBAGE_LIST = Arrays.asList(
            " O1 ",
            " O1",
            " 0 ",
            " 0",
            " O ",
            " O",
            "RN/STOP",
            "N/STOP",
            "N/S",
            "NIGHTSTOP",
            "NIGHT STOP",
            "D/STOP",
            "D/S",
            "DAYSTOP",
            "DAY STOP",
            ";",
            "DOF([0-9]+)?"
    );

    /**
     * This method return the informations (string and coordinates) of the ITEM18FIELD parse.
     * The coordinates format is the same of that use route finder.
     *
     * @param otherInformation it's a filed of other information from FplObject
     * @param item18Field it's a type of field {Destinatio, Status, Departure, ...}
     * @return the only interest information by field type or null if there aren't
     */
    public static String parse(String otherInformation, Item18Field item18Field){

        String parseValue=null;
        Matcher matcher=null;
        if(otherInformation!=null && !otherInformation.isEmpty() && item18Field!=null) {
            switch (item18Field) {
                case STS:
                    matcher = STS_PATTERN.matcher(otherInformation);
                    if (matcher.find()) {
                        parseValue = matcher.group(GROUP_OF_INTEREST).trim();
                    }
                    break;
                    
                case DEP:
                    matcher = DEP_PATTERN.matcher(otherInformation);
                    if (matcher.find()) {
                        parseValue = matcher.group(GROUP_OF_INTEREST).trim();
                    }
                    break;
                    
                case DEST:
                    matcher=DEST_PATTERN.matcher(otherInformation);
                    if(matcher.find()){
                        parseValue=matcher.group(GROUP_OF_INTEREST);
                        parseValue = cleanItem18Destination(parseValue);
                    }

                    break;
                    
                case REG:
                    matcher = REG_PATTERN.matcher(otherInformation);
                    if (matcher.find()) {
                        parseValue = matcher.group(GROUP_OF_INTEREST).trim();
                    }
                    break;
                    
                case TYP:
                    // In case of formation flight where there are more than one aircraft type,
                    // then ZZZZ shall be inserted in Item 9 and Item 18 shall contain TYP/ followed by
                    // the number and type of aircraft, each entry separated by a space.
                    // LR: Actually only TYP without formation flight are handled (see email's Werner)
                    matcher = TYP_PATTERN.matcher(otherInformation);
                    if (matcher.find()) {
                        String formationNumber = matcher.group(GROUP_OF_INTEREST).trim();
                        if (formationNumber == null || formationNumber.isEmpty()) {
                            parseValue = matcher.group(GROUP_OF_INTEREST_2).trim();
                        }
                    }
                    break;

                case OPR:
                    matcher = OPR_PATTERN.matcher(otherInformation);
                    if (matcher.find()) {
                        parseValue = StringUtils.normalizeWhiteSpace(matcher.group(GROUP_OF_INTEREST).trim());
                        if(parseValue!=null && parseValue.contains("/") && parseValue.length() > 4){
                            parseValue=parseValue.substring(0, parseValue.length()-4).trim();
                        }
                    }
                    break;

                case RMK:
                    matcher = RMK_PATTERN.matcher(otherInformation);
                    if (matcher.find()) {
                        final String value = matcher.group(1).trim();
                        if (!value.isEmpty()) {
                            parseValue = StringUtils.normalizeWhiteSpace (value);
                        }
                    }
                    break;
            }

        }

        if(parseValue!=null && parseValue.isEmpty()){
            parseValue=null;
        }

        return parseValue;
    }

    /**
     * This method returns coordinates from Item18 field. It uses the same regular expressions of RouteFinder.
     *
     *<ul>
     *     <li>"^([0-9]{2})([NnSs])([0-9]{3})([EeWw])$" : Degrees only (7 characters);</li>
     *     <li>"^([0-9]{4})([NnSs])([0-9]{5})([EeWw])$" : Degrees and minutes (11 characters);</li>
     *     <li>"^([0-9]{6})([NnSs])([0-9]{7})([EeWw])$" : Degrees, minutes and seconds (15 characters);</li>
     *</ul>
     *
     * @param item18 item 18 field
     * @return coordinates
     */
    @SuppressWarnings("squid:S2259")
    private static String parseNotClearCoordinates(String item18){
        String coordinates=null;
        Matcher matcher=null;

        if(StringUtils.isNotBlank(item18)){

            matcher= CoordinatesPatternFormatter.DEGREES_MINUTES_SECONDS_PATTERN.matcher(item18);

            if(matcher.find()){
                coordinates=matcher.group();
            }else{
            	matcher= CoordinatesPatternFormatter.DEGREES_MINUTES_SECONDS_PATTERN_FRONT.matcher(item18);
            	if(matcher.find()){
                    coordinates=matcher.group();
            	} else {
            		matcher=CoordinatesPatternFormatter.DEGREES_MINUTES_PATTERN.matcher(item18);
            		if(matcher.find()){
            			coordinates=matcher.group();
            		}else{
            			matcher=CoordinatesPatternFormatter.DEGREES_MINUTES_PATTERN_FRONT.matcher(item18);
            			if(matcher.find()){
            				coordinates=matcher.group();
            			} else {		
            				matcher=CoordinatesPatternFormatter.DEGREES_ONLY_PATTERN.matcher(item18);
            				if(matcher.find()){
            					coordinates=matcher.group();
            				} else {
            					matcher=CoordinatesPatternFormatter.DEGREES_ONLY_PATTERN_FRONT.matcher(item18);
                				if(matcher.find()){
                					coordinates=matcher.group();
                				}	
            				}
            			}
            		}
                }
            }
        }
        return coordinates;
    }

    public static String parseCoordinates(String item18) {
        String coordinates = null;
        if(StringUtils.isNotBlank(item18)) {
            coordinates = parseNotClearCoordinates(item18);
        }
        
        // remove whitespaces if coordinates are in the format '020000N 0375959E'

        if(coordinates != null && StringUtils.isNotBlank(coordinates)) {
            coordinates = coordinates.replaceAll("\\s", "");
        }
        return coordinates;
    }
    
    /**
     * This method performs the following task:
     * Input  : Item= "TAUPAN0745/0800  POMPOM0900/0910 NTSWI0920/0930 FBMN0950"
     * Output : List <DeltaFlightVO @see DeltaFlightVO>
     * @param item item 18 DEP/ or DEST/ field
     * @return list of delta flight vo
     */
    public static List<DeltaFlightVO> destFieldToMap(String item){
        // prevent null pointer but still return empty list
        if (item == null)
            item = "";

        List<DeltaFlightVO> list=null;
        String cleanedItem = cleanItem18Destination(item);
        String[] fieldArray = destFieldToArray(cleanedItem);
        if(fieldArray!=null && fieldArray.length>0){
            list=new ArrayList<>();
            Matcher matcher=null;

            for(String str: fieldArray){
                matcher=DESIGNATOR_TIME_PATTERN.matcher(str);
                if(matcher.find() && !matcher.group().isEmpty() && !matcher.group(1).isEmpty()){
                    DeltaFlightVO deltaFlightVO=new DeltaFlightVO();
                    deltaFlightVO.setIdent(matcher.group(1).trim());
                    if(matcher.group(2)!=null) {
                        deltaFlightVO.setArrivaAt(matcher.group(2).trim());
                    }
                    if(matcher.group(3)!=null) {
                        deltaFlightVO.setDepartAt(matcher.group(3).trim());
                    }

                    list.add(deltaFlightVO);
                }
            }
        }

        return list;
    }

    /**
     * This method returns the last aerodrome map from item18 field.
     *
     * @param item18Field DEP/ or DEST/ item 18 field value
     * @return delta flight vo
     */
    public static DeltaFlightVO mapDeltaDest(String item18Field) {
        List<DeltaFlightVO> list = destFieldToMap(item18Field);

        DeltaFlightVO result = null;
        if (list != null && !list.isEmpty()) {
            result = list.get(list.size() - 1);
        }

        return result;
    }

    /**
     * Extract the first aerodrome designator from item 18 field.
     *
     * @param item18Field DEP/ or DEST/ item 18 field value
     * @return aerodrome designator
     */
    public static String getFirstAerodrome(final String item18Field) {
        // destination string is not empty
        if (item18Field != null && !item18Field.isEmpty()) {
            // split on white space
            String[] destFielArray = destFieldToArray(item18Field);
            if (destFielArray != null) {
                final List<String> tokens = Arrays.asList(destFielArray);
                if (!tokens.isEmpty()) {
                    // get first word
                    final String firstToken = tokens.get(0);
                    if (firstToken != null && !firstToken.isEmpty()) {

                        // Does it match designator time pattern (delta
                        // flights)?
                        Matcher m;
                        m = DESIGNATOR_TIME_PATTERN.matcher(firstToken);
                        if (m.matches()) {
                            final String ident = m.group(1).trim();
                            if (ident != null && !ident.isEmpty()) {
                                // match; return
                                return ident;
                            }
                        }

                        // Does it match coordinates pattern?
                        if (GeometryUtils.parseAviationCoordinate(firstToken) == null) {
                            // If not, it's a deisgnator, return
                            return firstToken;
                        }

                    }
                }
            }
        }
        return null;
    }

    /**
     * Extract the first aerodrome designator or coordinates from item 18 field.
     *
     * @param item18Field DEP/ or DEST/ item 18 field value
     * @return aerodrome designator or coordinates
     */
    public static String getFirstAerodromeOrDMS(String item18Field) {
        // item18 field string is not empty
        if (item18Field != null && !item18Field.isEmpty()) {
            // split on white space
            String[] destFielArray = destFieldToArray(item18Field);
            if (destFielArray != null) {
                final List<String> tokens = Arrays.asList(destFielArray);
                if (!tokens.isEmpty()) {
                    // get first word
                    final String firstToken = tokens.get(0);
                    if (firstToken != null && !firstToken.isEmpty()) {

                        // Does it match designator time pattern (delta
                        // flights)?
                        Matcher m;
                        m = DESIGNATOR_TIME_PATTERN.matcher(firstToken);
                        if (m.matches()) {
                            final String ident = m.group(1).trim();
                            if (ident != null && !ident.isEmpty()) {
                                // match; return
                                return ident;
                            }
                        }

                        // Otherwise it's either a coordinate or some
                        // identifier, return
                        return firstToken;

                    }
                }
            }
        }
        return null;
    }

    /**
     * This method performs the following task:
     * Input  : Item= "TAUPAN0745/0800  POMPOM0900/0910 NTSWI0920/0930 FBMN0950"
     * Output : Array[0]="TAUPAN0745/0800"
     *          Array[1]="POMPOM0900/0910"
     *          ...
     *          Array[N]="FBMN0950"
     * @param item item 18 DEP/ or DEST/ field
     * @return list of identifiers and/or coordinates
     */
    public static String[] destFieldToArray(String item) {
        String itemTrim = item.trim();
        Matcher matcher = null;
        String[] array = null;
        if (itemTrim != null && !itemTrim.isEmpty()) {
            itemTrim = cleanItem18Destination(itemTrim);
            final String[] split = itemTrim.split("\\s+");
            final String[] parts = itemTrim.split("\\s+");
            if (parts != null) {
                final List<String> list = new ArrayList<>(parts.length);
                if (parts != null) {
                    if (parts.length == 1) {
                        return split;
                    } else if (parts.length > 1) {
                        int i = 0;
                        while (i < parts.length) {
                            String first = parts[i];

                            matcher = COORD_PATTERN.matcher(first);
                            if (matcher.find()) {
                                return split;
                            }
                            matcher = DESIGNATOR_TIME_PATTERN_DELTA.matcher(first);
                            if (matcher.find()) {
                                list.add(first);
                                i = i + 1;
                            } else {
                                if (i + 1 < parts.length) {
                                    String second = parts[i + 1];
                                    matcher = COORD_PATTERN.matcher(second);
                                    if (matcher.find()) {
                                        return split;
                                    }
                                    matcher = DESIGNATOR_TIME_PATTERN_DELTA.matcher(second);
                                    if (matcher.find()) {
                                        String concat = first + " " + second;
                                        list.add(concat);
                                        i = i + 2;
                                    } else {
                                        matcher = TIME_PATTERN.matcher(second);
                                        if (matcher.find()) {
                                            String concat = first + second;
                                            list.add(concat);
                                            i = i + 2;
                                        } else {
                                            i = i + 1;
                                            parts[i] = first + " " + second;
                                        }
                                    }
                                } else {
                                    return split;
                                }
                            }
                        }
                    }
                    array = list.toArray(new String[0]);
                }

            }
        }

        return array;
    }

    /**
     * This method returns the string (designator) from Item18 field.
     *
     * @param item18 it's a type of field {Destinatio, Status, Departure, ...}
     * @return the only interest information by field type or null if there aren't
     */
    public static String parseDesignator(String item18){
        String designator=null;
        if(StringUtil.isBlank(item18)) {
        	return designator;
        }

        String aerodromeCoordinates = parseNotClearCoordinates(item18);
 
        if(StringUtils.isBlank(aerodromeCoordinates)) {
        	designator = item18.trim();
        } else {
        	// aerodrome text designator is a remainder
        	int index = item18.indexOf(aerodromeCoordinates);
        	if(index < 0) {
        		designator = aerodromeCoordinates;
        	} else {
        		designator = item18.substring(0, index);
        		designator =designator.trim();
        		if(StringUtils.isBlank(designator)) {
        			designator = aerodromeCoordinates;
        		}
        	}
        }
        return designator != null ? cleanItem18Destination(designator) : designator;
    }
    
    /**
     * This method removes garbage characters from the Item18 field.
     *
     * @param item18 destination
     * @return cleaned Item18 destination
     */
    private static String cleanItem18Destination(String item18) {
        for (String garbage : GARBAGE_LIST) {
            item18 = item18.replaceAll(garbage, "");
        }
        return item18.trim();
    }

    private Item18Parser() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
