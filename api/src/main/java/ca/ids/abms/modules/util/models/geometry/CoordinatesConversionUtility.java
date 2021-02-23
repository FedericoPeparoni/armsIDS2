package ca.ids.abms.modules.util.models.geometry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.util.StringUtils;

/**
 * The Class <strong>CoordinatesConversionUtility</strong>
 * performs conversion of coordinates from decimal to degrees.<br/>
 *
 * Typical patterns are accepted by formatting output value are :<br/>
 * <ol>
 * <li>DDMMSSCPDDDMMSSCP: is a default pattern;</li>
 * <li>DDMMCPDDDMMCP;</li>
 * <li>DDCPDDDCP;</li>
 * </ol>
 * <br />
 * Pattern Legend:
 * <ul>
 * <li>CP: Cardinal Point;</li>
 * <li>DD: Degrees;</li>
 * <li>MM: Minutes;</li>
 * <li>SS: Seconds;</li>
 * </ul>
 * <br />
 *
 *
 * @author c.talpa
 */
public class CoordinatesConversionUtility {

    private static Logger LOG = Logger.getLogger(CoordinatesConversionUtility.class);

    private static DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);

    private static final String ERROR_MSG = Translation.getLangByToken("The parameter \"coordinate\" is not a valid parameter: ");

    private static final DecimalFormat FORMAT_TWO = new DecimalFormat("00", decimalFormatSymbols);

    private static final DecimalFormat FORMAT_LAT_DECIMAL = new DecimalFormat("00.000000", decimalFormatSymbols);

    private static final DecimalFormat FORMAT_THREE = new DecimalFormat("000", decimalFormatSymbols);

    private static final DecimalFormat FORMAT_LON_DECIMAL = new DecimalFormat("000.000000", decimalFormatSymbols);

    private static final String SPLIT_REGEX = " ";

    private static final String NORTH = Translation.getLangByToken("N");

    private static final String SOUTH =Translation.getLangByToken( "S");

    private static final String EAST = Translation.getLangByToken("E");

    private static final String WEST = Translation.getLangByToken("W");

    private static final int COORDINATE_DIMENSION = 2; // Latitude and Longitude

    private static final int INDEX_LATITUDE = 0;

    private static final int INDEX_LONGITUDE = 1;

    private static final int SCALE_FACTOR = 100;

    private static final int SIZE_COORDINATE_DMS = 3;

    private static final int SECOND_HOUR = 3600;

    private static final int SECOND_MINUTE = 60;

    private static final int INDEX_DEGREE = 0;

    private static final int INDEX_MINUTE = 1;

    private static final int INDEX_SECOND = 2;

    /**
     * Convert.
     *
     *A typical invocation sequence is thus:<br/>
     *
     * <pre>
     *  <code>
     * 		String coordinateDecimal = "42.248874999999998 12.599000000000000";
     * 		String convertDMS = CoordinatesConversionUtility.convert(coordinateDecimal);
     * </code>
     * </pre>
     * OR:
     * <pre>
     *  <code>
     * 		String coordinateDecimal = "42.248874999999998 12.599000000000000";
     * 		String convertDMS = CoordinatesConversionUtility.convert(coordinateDecimal, CoordinatesPatternFormatter.DEGREES_MINUTES_FORMATTER);
     * </code>
     * </pre>
     *
     * @param coordinates
     *          It's a string that contains latitude and longitude separated by a blank space character.
     * @param formatter
     *          represent the pattern for formatting output value, (option). If this parameter is not specify is used the default pattern.
     * @return a string that contains the latitude and longitude in converted degrees format.
     */
    public static String convertDecimalToDMS(final String coordinates, final String formatter) {


        String cmsCoordinates = Translation.getLangByToken("The coordinates parameter:") + " " + coordinates + " " + Translation.getLangByToken("is not valid");

        if (StringUtils.isStringIfNotNull(coordinates)) {

            String[] strCoordinates = coordinates.split(SPLIT_REGEX);

            if (strCoordinates.length == COORDINATE_DIMENSION) {

                String latitude = convertLatitudeFromDecimalToDMS(strCoordinates[INDEX_LATITUDE], formatter);
                String longitude = convertLongitudeFromDecimalToDMS(strCoordinates[INDEX_LONGITUDE], formatter);
                cmsCoordinates = latitude + " " + longitude;
            }

        } else {
            LOG.warn("The attribute coordinates: " + coordinates + " is not valid.");

        }

        return cmsCoordinates;

    }


    /**
     * This method performs the conversion of latitude from decimal to degree format.<br/>
     *
     * @param decimalLatitude
     *          It's a string that contains latitude in decimal format.
     * @param formatter
     *          represent the pattern for formatting output value, (option). If this parameter is not specify is used the default pattern.
     * @return a string that contains the latitude in converted degrees format.
     *
     */
    public static String convertLatitudeFromDecimalToDMS(final String decimalLatitude, final String formatter) {

        String latitude = ERROR_MSG + decimalLatitude;

        if (StringUtils.isStringIfNotNull(decimalLatitude)) {
            try {
                Double decimalValue = Double.parseDouble(decimalLatitude);

                latitude = convertLatitudeFromDecimalToDMS(decimalValue, formatter);

            } catch (NumberFormatException e) {
                LOG.warn(ERROR_MSG + decimalLatitude);
            }
        }

        return latitude;
    }

    public static String convertLatitudeFromDecimalToDMS(final Double latitude){

        return convertLatitudeFromDecimalToDMS(latitude,null);
    }


    /**
     * This method performs the conversion of longitude from decimal to degree format.<br/>
     *
     * @param decimalLongitude
     *          It's a string that contains longitude in decimal format.
     * @param formatter
     *          represent the pattern for formatting output value, (option). If this parameter is not specify is used the default pattern.
     * @return a string that contains the longitude in converted degrees format.
     *
     */
    public static String convertLongitudeFromDecimalToDMS(final String decimalLongitude, final String formatter) {

        String longitude = ERROR_MSG + decimalLongitude;


        if (StringUtils.isStringIfNotNull(decimalLongitude)) {
            try {
                Double decimalValue = Double.parseDouble(decimalLongitude);
                longitude = convertLongitudeFromDecimalToDMS(decimalValue, formatter);

            } catch (NumberFormatException e) {
                LOG.warn(ERROR_MSG + decimalLongitude);
            }
        }

        return longitude;

    }

    public static String convertLongitudeFromDecimalToDMS(final Double longitude){

        return convertLongitudeFromDecimalToDMS(longitude, null);
    }


    public static String convertLatitudeLongitudeFromDecimalToDMS(final Double latitude, final Double longitude){

        return convertLatitudeLongitudeFromDecimalToDMS(latitude,longitude,null);
    }

    public static String convertLatitudeLongitudeFromDecimalToDMS(final Double latitude, final Double longitude, final String formatter){

        String conv=null;
        if(latitude!=null && longitude!=null){
            conv= convertLatitudeFromDecimalToDMS(latitude,formatter)+ convertLongitudeFromDecimalToDMS(longitude,formatter);
        }
        return conv;
    }


    public static Double convertLatitudeFromDMSToDecimal(String coordinates){
        Double latitudeDecimal=null;
        if(StringUtils.isStringIfNotNull(coordinates)){
            Double[] coordinateDMS = new Double[SIZE_COORDINATE_DMS];
            String latitude=null;
            String cardinalPoint=null;
            Matcher matcher= CoordinatesPatternFormatter.DEGREES_MINUTES_SECONDS_PATTERN.matcher(coordinates);

            if(matcher.find()){
                latitude=matcher.group(1);
                cardinalPoint=matcher.group(2);
                coordinateDMS[INDEX_DEGREE]=Double.parseDouble(latitude.substring(0,2));
                coordinateDMS[INDEX_MINUTE]=Double.parseDouble(latitude.substring(2,4));
                coordinateDMS[INDEX_SECOND]=Double.parseDouble(latitude.substring(4,6));

            } else {
            	matcher= CoordinatesPatternFormatter.DEGREES_MINUTES_SECONDS_PATTERN_FRONT.matcher(coordinates);
            	if(matcher.find()){
            		latitude=matcher.group(2);
            		cardinalPoint=matcher.group(1);
            		coordinateDMS[INDEX_DEGREE]=Double.parseDouble(latitude.substring(0,2));
            		coordinateDMS[INDEX_MINUTE]=Double.parseDouble(latitude.substring(2,4));
            		coordinateDMS[INDEX_SECOND]=Double.parseDouble(latitude.substring(4,6));
            	} else{
            		matcher=CoordinatesPatternFormatter.DEGREES_MINUTES_PATTERN.matcher(coordinates);
                    if(matcher.find()){
                        latitude=matcher.group(1);
                        cardinalPoint=matcher.group(2);
                        coordinateDMS[INDEX_DEGREE]=Double.parseDouble(latitude.substring(0,2));
                        coordinateDMS[INDEX_MINUTE]=Double.parseDouble(latitude.substring(2,4));
                        coordinateDMS[INDEX_SECOND]=0.0;
                    }else {
                    	matcher=CoordinatesPatternFormatter.DEGREES_MINUTES_PATTERN_FRONT.matcher(coordinates);
                    	if(matcher.find()){
                    		latitude=matcher.group(2);
                    		cardinalPoint=matcher.group(1);
                    		coordinateDMS[INDEX_DEGREE]=Double.parseDouble(latitude.substring(0,2));
                    		coordinateDMS[INDEX_MINUTE]=Double.parseDouble(latitude.substring(2,4));
                    		coordinateDMS[INDEX_SECOND]=0.0;
                    	} else {
                    		matcher=CoordinatesPatternFormatter.DEGREES_ONLY_PATTERN.matcher(coordinates);
                            if(matcher.find()){
                                latitude=matcher.group(1);
                                cardinalPoint=matcher.group(2);
                                coordinateDMS[INDEX_DEGREE]=Double.parseDouble(latitude.substring(0,2));
                                coordinateDMS[INDEX_MINUTE]=0.0;
                                coordinateDMS[INDEX_SECOND]=0.0;
                            } else {
                            	matcher=CoordinatesPatternFormatter.DEGREES_ONLY_PATTERN_FRONT.matcher(coordinates);
                                if(matcher.find()){
                                    latitude=matcher.group(2);
                                    cardinalPoint=matcher.group(1);
                                    coordinateDMS[INDEX_DEGREE]=Double.parseDouble(latitude.substring(0,2));
                                    coordinateDMS[INDEX_MINUTE]=0.0;
                                    coordinateDMS[INDEX_SECOND]=0.0;
                                }
                            }
                    	}
                    }
                }
            }
            if(latitude!=null && cardinalPoint!=null) {
                latitudeDecimal = convertDegreesToDecimal(coordinateDMS);
                if (cardinalPoint.equalsIgnoreCase(SOUTH)) {
                    latitudeDecimal = -latitudeDecimal;
                }

                latitudeDecimal = Double.parseDouble(FORMAT_LAT_DECIMAL.format(latitudeDecimal));
            }
        }



        return  latitudeDecimal;
    }

    public static Double convertLongitudeFromDMSToDecimal(String coordinates){
        Double longitudeDecimal=null;

        if(StringUtils.isStringIfNotNull(coordinates)){
            Double[] coordinateDMS = new Double[SIZE_COORDINATE_DMS];
            String longitude=null;
            String cardinalPoint=null;
            Matcher matcher= CoordinatesPatternFormatter.DEGREES_MINUTES_SECONDS_PATTERN.matcher(coordinates);

            if(matcher.find()){
                longitude=matcher.group(3);
                if (longitude.length() != 7) {
                    if (longitude.length() == 6) {
                        longitude = '0' + longitude;
                    } else {
                        return null;
                    }
                }
                cardinalPoint=matcher.group(4);
                coordinateDMS[INDEX_DEGREE]=Double.parseDouble(longitude.substring(0,3));
                coordinateDMS[INDEX_MINUTE]=Double.parseDouble(longitude.substring(3,5));
                coordinateDMS[INDEX_SECOND]=Double.parseDouble(longitude.substring(5,7));
            }else {
            	 matcher= CoordinatesPatternFormatter.DEGREES_MINUTES_SECONDS_PATTERN_FRONT.matcher(coordinates);

                if(matcher.find()){
                    longitude=matcher.group(4);
                    if (longitude.length() != 7) {
                        if (longitude.length() == 6) {
                            longitude = '0' + longitude;
                        } else {
                            return null;
                        }
                    }
                    cardinalPoint=matcher.group(3);
                    coordinateDMS[INDEX_DEGREE]=Double.parseDouble(longitude.substring(0,3));
                    coordinateDMS[INDEX_MINUTE]=Double.parseDouble(longitude.substring(3,5));
                    coordinateDMS[INDEX_SECOND]=Double.parseDouble(longitude.substring(5,7));
                }else{
                	matcher=CoordinatesPatternFormatter.DEGREES_MINUTES_PATTERN.matcher(coordinates);
                	if(matcher.find()){
                		longitude=matcher.group(3);
                		if (longitude.length() != 5) {
                			if (longitude.length() == 4) {
                				longitude = '0' + longitude;
                			} else {
                				return null;
                			}
                		}
                		cardinalPoint=matcher.group(4);
                		coordinateDMS[INDEX_DEGREE]=Double.parseDouble(longitude.substring(0,3));
                		coordinateDMS[INDEX_MINUTE]=Double.parseDouble(longitude.substring(3,5));
                		coordinateDMS[INDEX_SECOND]=0.0;
                	} else {
                		matcher=CoordinatesPatternFormatter.DEGREES_MINUTES_PATTERN_FRONT.matcher(coordinates);
                    	if(matcher.find()){
                    		longitude=matcher.group(4);
                    		if (longitude.length() != 5) {
                    			if (longitude.length() == 4) {
                    				longitude = '0' + longitude;
                    			} else {
                    				return null;
                    			}
                    		}
                    		cardinalPoint=matcher.group(3);
                    		coordinateDMS[INDEX_DEGREE]=Double.parseDouble(longitude.substring(0,3));
                    		coordinateDMS[INDEX_MINUTE]=Double.parseDouble(longitude.substring(3,5));
                    		coordinateDMS[INDEX_SECOND]=0.0;
                    	}else{
                    		matcher=CoordinatesPatternFormatter.DEGREES_ONLY_PATTERN.matcher(coordinates);
                    		if(matcher.find()){
                    			longitude=matcher.group(3);
                    			if (longitude.length() != 3) {
                    				if (longitude.length() == 2) {
                    					longitude = '0' + longitude;
                    				} else {
                    					return null;
                    				}
                    			}
                    			cardinalPoint=matcher.group(4);
                    			coordinateDMS[INDEX_DEGREE]=Double.parseDouble(longitude.substring(0,3));
                    			coordinateDMS[INDEX_MINUTE]=0.0;
                    			coordinateDMS[INDEX_SECOND]=0.0;
                    		} else {
                    			matcher=CoordinatesPatternFormatter.DEGREES_ONLY_PATTERN_FRONT.matcher(coordinates);
                    			if(matcher.find()){
                    				longitude=matcher.group(4);
                    				if (longitude.length() != 3) {
                    					if (longitude.length() == 2) {
                    						longitude = '0' + longitude;
                    					} else {
                    						return null;
                    					}
                    				}
                    				cardinalPoint=matcher.group(3);
                    				coordinateDMS[INDEX_DEGREE]=Double.parseDouble(longitude.substring(0,3));
                    				coordinateDMS[INDEX_MINUTE]=0.0;
                    				coordinateDMS[INDEX_SECOND]=0.0;
                    			}
                    		}
                		}
                	}
                }
            }
            if(longitude!=null && cardinalPoint!=null) {
                longitudeDecimal = convertDegreesToDecimal(coordinateDMS);
                if (cardinalPoint.equalsIgnoreCase(WEST)) {
                    longitudeDecimal = -longitudeDecimal;
                }

                longitudeDecimal = Double.parseDouble(FORMAT_LON_DECIMAL.format(longitudeDecimal));
            }
        }

        return longitudeDecimal;
    }



    /* Utility Methods*/


    /**
     * Convert longitude.
     *
     * @param decimalLongitude
     *          the decimal longitude
     * @param formatter
     *          the formatter
     * @return the string
     */
    private static String convertLongitudeFromDecimalToDMS(final Double decimalLongitude, final String formatter) {

        String longitude = "";
        String pattern = StringUtils.checkAndTrimString(formatter);
        if(!StringUtils.isStringIfNotNull(pattern)){
            pattern=CoordinatesPatternFormatter.DEGREES_MINUTES_SECONDS_FORMATTER;
        }

        Double[] dmsLongitude = convertDecimalToDegrees(decimalLongitude);
        String cardinalPoint = (decimalLongitude > 0) ? EAST : WEST;


        switch (pattern) {

            case CoordinatesPatternFormatter.DEGREES_ONLY_FORMATTER:
                longitude=FORMAT_THREE.format(dmsLongitude[INDEX_DEGREE])+cardinalPoint;
                break;

            case CoordinatesPatternFormatter.DEGREES_MINUTES_FORMATTER:
                longitude=FORMAT_THREE.format(dmsLongitude[INDEX_DEGREE])+FORMAT_TWO.format(dmsLongitude[INDEX_MINUTE])+cardinalPoint;
                break;

            default:
                longitude=FORMAT_THREE.format(dmsLongitude[INDEX_DEGREE])+FORMAT_TWO.format(dmsLongitude[INDEX_MINUTE])+FORMAT_TWO.format(dmsLongitude[INDEX_SECOND])+cardinalPoint;

        }

        return longitude;

    }

    /**
     * Convert latitude.
     *
     * @param decimalLatitude
     *          the decimal latitude
     * @param formatter
     *          the formatter
     * @return the string
     */
    private static String convertLatitudeFromDecimalToDMS(final Double decimalLatitude, final String formatter) {

        String latitude = "";
        String pattern = StringUtils.checkAndTrimString(formatter);

        if(!StringUtils.isStringIfNotNull(pattern)){
            pattern=CoordinatesPatternFormatter.DEGREES_MINUTES_SECONDS_FORMATTER;
        }


        Double[] dmsLatitude = convertDecimalToDegrees(decimalLatitude);
        String cardinalPoint = (decimalLatitude > 0) ? NORTH : SOUTH;


        switch (pattern) {

            case CoordinatesPatternFormatter.DEGREES_ONLY_FORMATTER:
                latitude=FORMAT_TWO.format(dmsLatitude[INDEX_DEGREE])+cardinalPoint;
                break;

            case CoordinatesPatternFormatter.DEGREES_MINUTES_FORMATTER:
                latitude=FORMAT_TWO.format(dmsLatitude[INDEX_DEGREE])+FORMAT_TWO.format(dmsLatitude[INDEX_MINUTE])+cardinalPoint;
                break;

            default:
                latitude=FORMAT_TWO.format(dmsLatitude[INDEX_DEGREE])+FORMAT_TWO.format(dmsLatitude[INDEX_MINUTE])+FORMAT_TWO.format(dmsLatitude[INDEX_SECOND])+cardinalPoint;

        }


        return latitude;

    }

    /**
     * Convert decimal to degrees.
     *
     * @param decimalCoordinate
     *          the decimal coordinate
     * @return the double[]
     */
    private static Double[] convertDecimalToDegrees(Double decimalCoordinate) {

        Double[] coordinateDMS = new Double[SIZE_COORDINATE_DMS];
        Double coordinate = Math.abs(decimalCoordinate);

        coordinateDMS[INDEX_DEGREE] = (Double) Math.floor(coordinate);
        coordinateDMS[INDEX_MINUTE] = (Double) Math.floor((coordinate - coordinateDMS[INDEX_DEGREE]) * SECOND_MINUTE);
        coordinateDMS[INDEX_SECOND] = (Double) ((((coordinate - coordinateDMS[INDEX_DEGREE]) - (coordinateDMS[INDEX_MINUTE] / SECOND_MINUTE)) * SECOND_HOUR) * SCALE_FACTOR) / SCALE_FACTOR;


        return coordinateDMS;
    }


    /**
     *
     * @param degreeCoordinate
     * @return
     */
    private static Double convertDegreesToDecimal(Double[] degreeCoordinate) {

        Double coordinateDecimal=null;

        if(degreeCoordinate!=null && degreeCoordinate.length==SIZE_COORDINATE_DMS){
            Double secondsDecimal=(Double)degreeCoordinate[INDEX_SECOND]/SECOND_HOUR;
            Double minutesDecimal=(Double)degreeCoordinate[INDEX_MINUTE]/SECOND_MINUTE;

            coordinateDecimal=degreeCoordinate[INDEX_DEGREE]+minutesDecimal+secondsDecimal;
        }
        return coordinateDecimal;
    }

}
