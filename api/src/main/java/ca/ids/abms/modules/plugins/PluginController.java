package ca.ids.abms.modules.plugins;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import ca.ids.abms.plugins.PluginKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plugins")
@SuppressWarnings({"unused", "squid:S1452"})
public class PluginController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PluginController.class);

    private final PluginService pluginService;
    private final PluginMapper pluginMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public PluginController(final PluginService pluginService,
                            final PluginMapper pluginMapper,
                            final ReportDocumentCreator reportDocumentCreator) {
        this.pluginService = pluginService;
        this.pluginMapper = pluginMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('manage_plugin_view')")
    @GetMapping
    public ResponseEntity<?> get(@RequestParam(name = "search", required = false) final String search,
                                 @SortDefault.SortDefaults({@SortDefault(sort = {"name"}, direction= Sort.Direction.ASC)}) Pageable pageable,
                                 @RequestParam(name = "visible", required = false, defaultValue = "true") boolean visible,
                                 @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all plugins");
        Page<Plugin> page = pluginService.findAll(search, pageable, visible);

        if (csvExport != null && csvExport) {
            final List<Plugin> list = page.getContent();
            final List<PluginCsvExportModel> csvExportModel = pluginMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Plugins", csvExportModel, PluginCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<Plugin> resultPage = new PageImplCustom<>(page.getContent(),
                pageable, page.getTotalElements(), pluginService.countAllByOrganization(visible));
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('manage_plugin_modify')")
    @PutMapping("/{id}")
    public ResponseEntity<PluginViewModel> put(@RequestBody PluginViewModel plugin, @PathVariable Integer id) {
        LOG.debug("REST request to update Plugin : {}", plugin);
        Plugin model = pluginMapper.toModel(plugin);
        return ResponseEntity.ok().body(pluginMapper.toViewModel(pluginService.update(id, model)));
    }

    @GetMapping("/isEnabled/{organizationName}/{pluginName}")
    public ResponseEntity<Boolean> get(@PathVariable String organizationName, @PathVariable String pluginName) {
        boolean isEnabled = false;

        if (organizationName != null && !organizationName.isEmpty() && pluginName != null && !pluginName.isEmpty()) {
            PluginKey pluginKey = PluginKey.forValue(organizationName.toLowerCase() + "." + pluginName.toLowerCase());
            isEnabled = pluginService.isEnabled(pluginKey);
        }

        return ResponseEntity.ok().body(isEnabled);
    }
}
