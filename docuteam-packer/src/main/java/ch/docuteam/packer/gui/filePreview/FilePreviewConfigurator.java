/**
 *  Copyright (C) since 2017 at Docuteam GmbH
 *  <p>
 *	This program is free software: you can redistribute it and/or modify <br>
 *	it under the terms of the GNU General Public License version 3 <br>
 *	as published by the Free Software Foundation. 
 *  <p>
 *	This program is distributed in the hope that it will be useful, <br>
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of <br>
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the <br>
 *	GNU General Public License for more details.
 *  <p>
 *	You should have received a copy of the GNU General Public License <br>
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 */
package ch.docuteam.packer.gui.filePreview;


import java.util.Iterator;
import java.util.Properties;
import ch.docuteam.tools.file.ResourceUtil;
import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.translations.I18N;

/**
 * Initial Date:  11.05.2018 <br>
 * @author l.dumitrescu
 */
public class FilePreviewConfigurator {
    
    public static final String NOT_CONFIGURED_EXCEPTION = "MessageFilePreviewConfiguratorPropertiesNotFound";

    private String configFileName = "/config/filePreviewConfigurator.properties"; //default
    private Properties properties;
    
    public FilePreviewConfigurator() {
        initialize();
    }
    
    FilePreviewConfigurator(String configFileName) {
        this.configFileName = configFileName;        
        initialize(); 
    }    
    
    private void initialize() {
        properties = ResourceUtil.getProperties(configFileName);
                
        // validate configuration
        if (getProperties().keySet().isEmpty()) {            
            Logger.warn(I18N.translate(NOT_CONFIGURED_EXCEPTION, configFileName));
        }

        Iterator<Object> iterator = getProperties().keySet().iterator();
        while (iterator.hasNext()) {
            String propertyKey = (String) iterator.next();
            String propertyValue = getPropertyValue(propertyKey);
            Logger.debug("filePreviewConfigurator properties: " + propertyKey + " -> " + propertyValue);            
        }
    }  
   
        
    public Properties getProperties() {
        return properties;
    }
    
    public String getPropertyValue(String key) {
        return properties.getProperty(key);
    }
}
