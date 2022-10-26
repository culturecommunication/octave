/*
 * Copyright (C) since 2011 by docuteam AG
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BuildInfoIT {

    @Test
    public void test() {
        // build.properties was found
        assertNotNull(BuildInfo.getProduct());
        assertNotNull(BuildInfo.getVersion());
        assertNotNull(BuildInfo.getLastChange());

        // build.properties was filtered
        assertTrue("build.properties was not filtered", !BuildInfo.getProduct().startsWith("$"));
        assertTrue(BuildInfo.getProduct().contains("packer"));
    }

}
