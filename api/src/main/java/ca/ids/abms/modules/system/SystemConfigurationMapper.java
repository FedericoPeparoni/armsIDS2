package ca.ids.abms.modules.system;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface SystemConfigurationMapper {

    List<SystemConfigurationViewModel> toViewModel(Iterable<SystemConfiguration> systemConfigurations);

    SystemConfigurationViewModel toViewModel(SystemConfiguration systemConfigurations);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "defaultValueList", ignore = true)
    @Mapping(target = "currentValueList", ignore = true)
    public abstract SystemConfiguration toModel(SystemConfigurationViewModel dto);

    default SystemValidationType mapSystemValidationType(final String systemValidationType) {
        return SystemValidationType.forValue(systemValidationType);
    }

    default String mapSystemValidationType(final SystemValidationType systemValidationType) {
        return systemValidationType != null ? systemValidationType.toValue() : null;
    }
}
