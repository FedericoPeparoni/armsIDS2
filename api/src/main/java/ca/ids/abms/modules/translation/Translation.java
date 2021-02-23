package ca.ids.abms.modules.translation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.languages.LanguagesService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.system.summary.SystemSummaryName;

public class Translation {

    private static final Logger LOG = LoggerFactory.getLogger(Translation.class);

    private static final Map<String, JSONObject> languages = new HashMap<>();

    private final SystemConfigurationService systemConfigurationService;

    private final LanguagesService languagesService;

    public Translation(SystemConfigurationService systemConfigurationService, LanguagesService languagesService) {
        this.systemConfigurationService = systemConfigurationService;
        this.languagesService = languagesService;
    }

    /**
     * Get language translation for current locale context for
     * the supplied `RejectedReasons`.
     *
     * @param rejectedReason rejected reason to translate
     * @return translated value
     */
    public static String getLangByToken(RejectedReasons rejectedReason) {
        if (rejectedReason != null && StringUtils.isNotBlank(rejectedReason.name())) {
            return getLangByToken(rejectedReason.name());
        } else {
            return null;
        }
    }

    /**
     * Get language translation for current locale context for
     * the supplied `ErrorConstants`.
     *
     * @param errorConstants error constants to translate
     * @return translated value
     */
    public static String getLangByToken(ErrorConstants errorConstants) {
        if (errorConstants != null && StringUtils.isNotBlank(errorConstants.name())) {
            return getLangByToken(errorConstants.name());
        } else {
            return null;
        }
    }

    /**
     * Get language translation for current locale context for
     * the supplied `SystemSummaryName`.
     *
     * @param systemSummaryName error constants to translate
     * @return translated value
     */
    public static String getLangByToken(SystemSummaryName systemSummaryName) {
        if (systemSummaryName != null && StringUtils.isNotBlank(systemSummaryName.name())) {
            return getLangByToken(systemSummaryName.name());
        } else {
            return null;
        }
    }

    /**
     * Get language translation for current locale context for
     * the supplied key.
     *
     * @param key to translate
     * @return translated value
     */
    public static String getLangByToken(String key) {
        return StringUtils.isBlank(key) ? null
            : getTranslation(getLanguage(), key);
    }

    /**
     * Get language translation for provided locale for
     * the supplied key.
     *
     * @param key to translate
     * @param locale locale to translate into
     * @return translated value
     */
    public static String getLangByToken(String key, Locale locale) {
        if (locale == null) {
            return getLangByToken(key);
        } else {
            return StringUtils.isBlank(key) ? null
                : getTranslation(getLanguage(locale.getLanguage()), key);
        }
    }

    /**
     * Get the language based on the current thread's locale context.
     * This is set in `accept-language` header request from clients and
     * defaults to `en` for now.
     *
     * @return language based on locale context
     */
    private static JSONObject getLanguage() {
        // get language code (first two letters) from Locale Context, ignore culture code (second two letters)
        return getLanguage(LocaleContextHolder.getLocale().toString().substring(0, 2));
    }

    /**
     * Get the language based on the provided language code (ISO-2), defaults to `en`.
     *
     * @return language based on provided language code
     */
    private static JSONObject getLanguage(String languageCode) {

        // return default if no key found
        if(languages.containsKey(languageCode))
            return languages.get(languageCode);
        else
            return languages.get("en");
    }

    /**
     * Returns the translation for the key from the provided language.
     *
     * @param lang language to find translation
     * @param key translation key
     * @return translated value
     */
    private static String getTranslation(JSONObject lang, String key) {

        if (lang != null) {
            String translated = (String) lang.get(key);

            if (translated != null) {
                return translated;
            } else {
                return key;
            }
        } else {
            return key;
        }
    }

    /**
     * Load all supported languages from file system into memory.
     */
    public void loadLanguageFiles() {
        JSONParser parser = new JSONParser();

        try {
            JSONArray langArray = (JSONArray) parser.parse(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.LANGUAGE_SUPPORTED));
            for (Object lang : langArray) {

                // we always assume that the result is a JSON Object with `code` property
                JSONObject langObject = (JSONObject) lang;
                String code = (String) langObject.get("code");

                // parse JSONObject from database using i18n code
                // and assume the contents are always valid JSON Object
                String data = languagesService.getLanguageForBackend(code);
                JSONObject dataObj = (JSONObject) parser.parse(data);

                // add to hashmap of languages using the i18n code as the key
                languages.put(code, dataObj);
            }
        } catch (ParseException | NullPointerException e) {
            LOG.error("Could not load language data", e);
        }
    }
}
