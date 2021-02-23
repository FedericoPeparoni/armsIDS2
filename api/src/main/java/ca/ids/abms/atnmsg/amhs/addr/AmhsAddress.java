package ca.ids.abms.atnmsg.amhs.addr;

/**
 * Minimal AMHS address information.
 *
 * <p>
 * Objects of this typed are returned by methods of {@link AmhsAddressCreator}.
 *
 */
public interface AmhsAddress {

    /**
     * AFTN address extracted part of this AMHS address. This is the value of key
     * CN= or OU1= .
     */
    public String toAftnString();

    /**
     * Normalized AMHS address. This string is meant to be 100% compatible with
     * UBIATN configuration and message files, at least for addresses produced by
     * {@link AmhsAddressCreator#parseUserString(String)}. It will contain the keys
     * in the right order, no unknown keys, no invalid characters and everything
     * upper-cased.
     * <p>See the
     * <a href="http://ubidev01.idscorporation.ca/viewvc/svn/projects/aim/8.3/aim-docs/published/cronos-amhs-addressformat/">spec</a>
     * for more info.
     */
    public String toUbimexString();

}