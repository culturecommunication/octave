/*
 * Copyright (C) since 2011  Docuteam GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.docuteam.packer.gui.filePreview;

import java.util.Iterator;
import java.util.Properties;

import ch.docuteam.tools.file.ResourceUtil;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

/**
 * Initial Date: 11.05.2018 <br>
 * 
 * @author l.dumitrescu
 */
public class FilePreviewConfigurator {

    public static final String NOT_CONFIGURED_EXCEPTION = "MessageFilePreviewConfiguratorPropertiesNotFound";

    private String configFileName = "/config/filePreviewConfigurator.properties"; // default

    private Properties properties;

    public FilePreviewConfigurator() {
        initialize();
    }

    FilePreviewConfigurator(final String configFileName) {
        this.configFileName = configFileName;
        initialize();
    }

    private void initialize() {
        properties = ResourceUtil.getProperties(configFileName);

        // validate configuration
        if (getProperties().keySet().isEmpty()) {
            Logger.warn(I18N.translate(NOT_CONFIGURED_EXCEPTION, configFileName));
        }

        final Iterator<Object> iterator = getProperties().keySet().iterator();
        while (iterator.hasNext()) {
            final String propertyKey = (String) iterator.next();
            final String propertyValue = getPropertyValue(propertyKey);
            Logger.debug("filePreviewConfigurator properties: " + propertyKey + " -> " + propertyValue);
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public String getPropertyValue(final String key) {
        return properties.getProperty(key);
    }
}
