package ca.ids.abms.modules.plugins;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PluginMapper {

    List<PluginViewModel> toViewModel(Iterable<Plugin> items);

    PluginViewModel toViewModel(Plugin item);

    Plugin toModel(PluginViewModel dto);

    PluginCsvExportModel toCsvModel(Plugin item);

    List<PluginCsvExportModel> toCsvModel(Iterable<Plugin> items);
}
