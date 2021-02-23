package ca.ids.abms.modules.plugins;

import ca.ids.abms.modules.system.SystemItemType;
import ca.ids.abms.plugins.PluginKey;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SuppressWarnings("FieldCanBeLocal")
public class PluginTest {

    private static int ID = 1;
    private static String NAME = "Mock Plugin";
    private static String DESCRIPTION = "Mock Description";
    private static Boolean ENABLED = true;
    private static Boolean VISIBLE = true;
    private static PluginKey KEY = PluginKey.PROTOTYPE;
    private static Set<SystemItemType> SYSTEM_ITEM_TYPES = new HashSet<>();

    private Plugin plugin;

    @Before
    public void setup() {
        plugin = getPlugin();
    }

    @Test
    public void getterTest() {
        assertThat(plugin.getId()).isEqualTo(ID);
        assertThat(plugin.getName()).isEqualTo(NAME);
        assertThat(plugin.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(plugin.getEnabled()).isEqualTo(ENABLED);
        assertThat(plugin.getVisible()).isEqualTo(VISIBLE);
        assertThat(plugin.getKey()).isEqualTo(KEY);
        assertThat(plugin.getSystemItemTypes()).isEqualTo(SYSTEM_ITEM_TYPES);
    }

    @Test
    public void toStringTest() {
        assertThat(plugin.toString()).isEqualTo(getPlugin().toString());
    }

    @Test
    public void equalTest() {
        assertThat(plugin).isEqualTo(getPlugin());
        assertThat(plugin).isEqualTo(plugin);
        assertThat(plugin).isNotEqualTo("mock string");

        Plugin plugin1 = getPlugin();
        Plugin plugin2 = getPlugin();
        plugin2.setId(0);

        assertThat(plugin1).isNotEqualTo(plugin2);

        plugin1.setId(null);
        plugin2.setId(null);

        assertThat(plugin1).isEqualTo(plugin2);
    }

    public static Plugin getPlugin() {
        Plugin plugin = new Plugin();
        plugin.setId(ID);
        plugin.setName(NAME);
        plugin.setDescription(DESCRIPTION);
        plugin.setEnabled(ENABLED);
        plugin.setVisible(VISIBLE);
        plugin.setKey(KEY);
        plugin.setSystemItemTypes(SYSTEM_ITEM_TYPES);
        return plugin;
    }
}
