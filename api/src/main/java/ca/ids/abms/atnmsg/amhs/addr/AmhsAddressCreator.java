package ca.ids.abms.atnmsg.amhs.addr;

import java.util.Map;

/**
 * Parse and construct AMHS addresses.
 *
 * <p>
 * This class implements parsing and validation of AMHS address strings as
 * specified in this document: <a href=
 * "http://ubidev01.idscorporation.ca/viewvc/svn/projects/aim/8.3/aim-docs/published/cronos-amhs-addressformat/">cronos-amhs-addressformat</a>
 *
 * <p>
 * It creates objects of type {@link #AmhsAddress} that contain pairs of
 * AMHS/AFTN addresses based on the input string.
 *
 * <p>
 * You need to use {@link AmhsAddressCreatorFactory} class to create instances
 * if this interface:
 *
 * <pre>
 * <code>
 * ...
 * // construct a creator
 * ValidatorFactory vf = ...;
 * Locale locale = ...;
 * AmhsAddressCreatorFactory f = new AmhsAddressCreatorFactory (vf);
 * AmhsAddressCreator c = f.newCreator (locale);
 *
 * // parse an address
 * AmhsAddress a = c.parseUserString ("C=CC,A=AA,P=PP,O=OO,OU1=UU,CN=AAAABBBB");
 *
 * // normalized AMHS address
 * String normalizedAmhsAddr = a.toUbimexString();
 * // AFTN address
 * String aftnAddr = a.getAftnAddr();
 *
 * @author dpanech
 *
 */
public interface AmhsAddressCreator {

    /**
     * Parse an AMHS address string provided by a user.
     *
     * <p>
     * This method should be used to validate and normalize any AMHS addresses
     * entered into the application by human users. It validates only the syntax and
     * does not use the database. The validation is very strict and follows the
     * specs in <code>cronos-amhs-addressformat</code> document in <a href=
     * "http://ubidev01.idscorporation.ca/viewvc/svn/projects/aim/8.3/aim-docs/published/cronos-amhs-addressformat/">aim-docs</a>
     * project.
     *
     * <p>
     * Don't use this function for addresses that were collected from UBIATN (XML)
     * message files, please use the {@link #parseUbimexString(String)} method
     * instead, which is less strict.
     *
     * @param amhsAddrString - the string to be parsed
     *
     * @return the address information, including its normalized string
     *         representation and the corresponding AFTN address.
     *
     * @throws ValidationException in case of syntax errors and such
     */
    
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    public AmhsAddress parseUserString(final String amhsAddrString) throws InvalidAmhsAddressException;

    /**
     * Parse an AMHS address string provided by UBIATN.
     *
     * <p>
     * This method should be used to validate and normalize any AMHS addresses that
     * have been read from UBIATN-produced XML message files. For AMHS addresses
     * entered by human users, please use the {@link #parseUserString(String)}
     * function, which validates more strictly.
     *
     * <p>
     * This function will attempt to normalize the input address string as described
     * in the <a href=
     * "http://ubidev01.idscorporation.ca/viewvc/svn/projects/aim/8.3/aim-docs/published/cronos-amhs-addressformat/">cronos-amhs-addressformats</a>
     * document, if possible. But if that fails (i.e., the input syntax deviates
     * from the spec in some way), it will simply extract the AFTN address from it,
     * and leave the original AMHS address string alone.
     *
     * @param amhsAddrString - the string to be parsed
     *
     * @return the address information, including its normalized string
     *         representation and the corresponding AFTN address.
     *
     * @throws ValidationException in case of syntax errors and such
     */
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    public AmhsAddress parseUbimexString(final String amhsAddrString) throws InvalidAmhsAddressException;

    /**
     * Construct an AMHS address given a map of key/value pairs.
     *
     * @param data - key/value map. These values must follow the requirements in the
     *             <a href=
     *             "http://ubidev01.idscorporation.ca/viewvc/svn/projects/aim/8.3/aim-docs/published/cronos-amhs-addressformat/">spec</a>
     *
     * @return the address information, including its normalized string
     *         representation and the corresponding AFTN address.
     *
     * @throws IllegalArgumentException - if the input violates the <a href=
     *                                  "http://ubidev01.idscorporation.ca/viewvc/svn/projects/aim/8.3/aim-docs/published/cronos-amhs-addressformat/">spec</a>
     *                                  in some way. It is your responsibility to
     *                                  ensure that you construct input data
     *                                  correctly (i.e., required keys are present,
     *                                  no invalid characters, etc.)
     */
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    public AmhsAddress create(final Map<String, String> data) throws IllegalArgumentException;

}
