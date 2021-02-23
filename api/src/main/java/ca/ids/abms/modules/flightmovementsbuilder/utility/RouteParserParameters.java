package ca.ids.abms.modules.flightmovementsbuilder.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RouteParserParameters {

    @Value("${route-finder.maxRouteLengthRatio:1.5}")
    private  double maxRouteLengthRatio;
    @Value("${route-finder.maxNotFoundTokenRatio:0.5}")
    private  double maxNotFoundTokenRatio;
    @Value("${route-finder.maxCourseChangeInDegrees:90}")
    private  double maxCourseChangeInDegrees;
    @Value("${route-finder.ingestIncorrectFormat:true}")
    private  Boolean ingestIncorrectFormat;
    @Value("${route-finder.routeLengthMaxToleranceCoeff:1.5}")
    private double routeLengthMaxToleranceCoeff;
    @Value("${route-finder.maxRouteSegmentLength :100}")
    private double maxRouteSegmentLength ;

    public double getMaxRouteLengthRatio() {
        return maxRouteLengthRatio;
    }

    public double getMaxNotFoundTokenRatio() {
        return maxNotFoundTokenRatio;
    }

    public double getMaxCourseChangeInDegrees() {
        return maxCourseChangeInDegrees;
    }

    public Boolean getIngestIncorrectFormat() {
        return ingestIncorrectFormat;
    }

    public double getRouteLengthMaxToleranceCoeff() {
        return routeLengthMaxToleranceCoeff;
    }

    public double getMaxRouteSegmentLength() {
        return maxRouteSegmentLength;
    }
}
