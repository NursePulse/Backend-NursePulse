package com.brainspark.nursepulse.platform.shared.infrastructure.i18n.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * Configuration class for internationalization (i18n) and localization (l10n) settings.
 *
 * Current supported locales:
 * - English (Locale.ENGLISH) - Default locale
 * - Spanish (es) - Additional supported language
 *
 * The configuration uses Spring's {@link AcceptHeaderLocaleResolver} which automatically
 * detects the client's preferred language from HTTP request headers and applies the
 * corresponding locale for message translation and formatting.
 *
 * @see AcceptHeaderLocaleResolver
 * @see LocaleResolver
 * @see Locale
 */
@Configuration
public class LocaleConfiguration {

    /**
     * Creates and configures a {@link LocaleResolver} bean for handling HTTP request locales.
     *
     * This method sets up an {@link AcceptHeaderLocaleResolver} that:
     * <ul>
     *   <li>Reads the Accept-Language header from HTTP requests</li>
     *   <li>Maps the client's language preference to a supported {@link Locale}</li>
     *   <li>Falls back to the default locale (English) when no matching preference is found</li>
     *   <li>Supports English and Spanish languages</li>
     * </ul>
     *
     * @return a configured {@link LocaleResolver} bean that handles locale resolution
     *         based on HTTP Accept-Language headers
     */
    @Bean
    public LocaleResolver localeResolver(){
        var resolver = new AcceptHeaderLocaleResolver();
        //Set English as the default locale when no language preference is specified
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setSupportedLocales(List.of(Locale.ENGLISH, Locale.forLanguageTag("es")));
        return resolver;
    }
}
