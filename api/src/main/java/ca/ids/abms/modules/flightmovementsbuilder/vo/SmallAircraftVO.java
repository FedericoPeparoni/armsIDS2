package ca.ids.abms.modules.flightmovementsbuilder.vo;

import java.time.LocalDateTime;

public interface SmallAircraftVO {

    /**
     * Return true if small aircraft certificate of airworthiness (CoA) is valid for the provided date.
     */
    boolean isSmallAircraftCoaValid(LocalDateTime date);
}
