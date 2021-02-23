package ca.ids.abms.atnmsg.amhs.addr;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
class AmhsAddressCreatorImpl implements AmhsAddressCreator {

    @Override
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    public AmhsAddress parseUserString(String s) throws InvalidAmhsAddressException {
        return (new UserAddrParser()).parse(s);
    }

    @Override
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    public AmhsAddress parseUbimexString(String s) throws InvalidAmhsAddressException {
        return (new UbimexAddrParser()).parse(s);
    }

    @Override
    public AmhsAddress create(Map<String, String> data) {
        return (new UserAddrParser()).create(data);
    }

}
