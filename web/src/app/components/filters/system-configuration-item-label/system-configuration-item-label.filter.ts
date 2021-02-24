/**
 * System configuration item label format filter.
 *
 * This filter is used to trim plugin specific prefixes
 * on display.
 *
 */

/** @ngInject */
export function systemConfigurationItemLabel(): Function {

  return function(name: string, pluginKey: string): string {

    // if no plugin key set, return existing name
    if (!pluginKey || pluginKey.length === 0) {
      return name;
    }

    // regular expression that finds plugin key prefix
    const pluginPrefix = new RegExp('^' + pluginKey.replace('_', ' '), 'i');

    // return name without plugin key prefix and capitalizes first character
    return name
      .replace(pluginPrefix, '').trim()
      .replace(/^\w/, (c: string) => c.toUpperCase());
  };
}
