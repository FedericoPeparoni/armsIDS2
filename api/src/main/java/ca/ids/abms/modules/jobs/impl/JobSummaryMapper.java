package ca.ids.abms.modules.jobs.impl;

import org.mapstruct.Mapper;

@Mapper
public interface JobSummaryMapper {

    JobSummaryViewModel toViewModel(JobSummary jobSummary);
}
