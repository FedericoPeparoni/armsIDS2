package ca.ids.abms.modules.spatiareader.mapper;

import ca.ids.abms.modules.common.mappers.DateTimeMapper;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import org.mapstruct.Mapper;

@Mapper
public abstract class CplFplMapper extends DateTimeMapper {

    public abstract FplObjectDto toFplDto (final FplObject entity);
}
