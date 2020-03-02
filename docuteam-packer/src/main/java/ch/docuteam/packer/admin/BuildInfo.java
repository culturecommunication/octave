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

package ch.docuteam.packer.admin;

import ch.docuteam.tools.out.Logger;
import ch.docuteam.tools.util.BuildInfoUtil;


/**
 * Uses build.properties in packer module.
 * 
 * @author lavinia
 *
 */
public class BuildInfo {
		
private static BuildInfoUtil buildInfo;
	
	static {
		buildInfo = new BuildInfoUtil();			
	}

	public static String getVersion() {
		return buildInfo.getVersion();	
	}
	
	public static String getLastChange() {		
		return buildInfo.getLastChange();
	}
	
	public static String getProduct() {
		return buildInfo.getProduct();
	}
		
	public static void main(String[] args) throws Exception {
		Logger.info("product: " + getProduct());
		Logger.info("version: " + getVersion());
		Logger.info("last changed: " + getLastChange());
	}	
}