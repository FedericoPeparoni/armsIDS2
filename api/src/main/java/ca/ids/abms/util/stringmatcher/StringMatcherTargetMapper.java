package ca.ids.abms.util.stringmatcher;

import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class StringMatcherTargetMapper {

    public abstract StringMatcherTarget toStringMatcherTarget(UnspecifiedDepartureDestinationLocation uDDL);

    /*************************************************
     * UnspecifiedDepartureDestinationLocation mapping
     ************************************************* /

    /**
     * Get the name of the UDDL
     * Name is used to match against the incoming text
     */
    @AfterMapping
    void toStringMatcherTargetOne(
        final UnspecifiedDepartureDestinationLocation uDDL, @MappingTarget StringMatcherTarget stringMatcherTarget
    ) {
        stringMatcherTarget.setStringTargetOne(uDDL.getTextIdentifier());
    }

    /**
     * Get the name of the UDDL
     * Name is used to match against the incoming text
     */
    @AfterMapping
    void toStringMatcherTargetTwo(
        final UnspecifiedDepartureDestinationLocation uDDL, @MappingTarget StringMatcherTarget stringMatcherTarget
    ) {
        if (uDDL.getAerodromeIdentifier() != null) {
            stringMatcherTarget.setStringTargetTwo(uDDL.getAerodromeIdentifier().toString());
        }
    }

    /**
     * Associate the UDDL object with the matcher target
     * The associated object is return as a result
     */
    @AfterMapping
    void toStringMatcherTargetObject(
        final UnspecifiedDepartureDestinationLocation uDDL, @MappingTarget StringMatcherTarget stringMatcherTarget
    ) {
        stringMatcherTarget.setObject(uDDL);
    }
}
