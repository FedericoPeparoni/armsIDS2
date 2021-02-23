package ca.ids.abms.atnmsg.amhs.addr;

import com.google.common.base.Preconditions;

class AmhsAddressImpl implements AmhsAddress {

    private final String aftnAddr;
    private final String ubimexString;

    public AmhsAddressImpl(final String aftnAddr, final String ubimexString) {
        Preconditions.checkNotNull(aftnAddr);
        Preconditions.checkNotNull(ubimexString);
        this.aftnAddr = aftnAddr;
        this.ubimexString = ubimexString;
    }

    @Override
    public String toAftnString() {
        return aftnAddr;
    }

    @Override
    public String toUbimexString() {
        return ubimexString;
    }

    @Override
    public String toString() {
        return ubimexString;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AmhsAddress) {
            return ubimexString.equals(((AmhsAddress)o).toUbimexString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.ubimexString.hashCode() * 19;
    }

}
