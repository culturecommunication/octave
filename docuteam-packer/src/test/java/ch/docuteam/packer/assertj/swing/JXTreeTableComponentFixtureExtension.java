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

package ch.docuteam.packer.assertj.swing;

import java.awt.Container;

import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.ComponentFixtureExtension;
import org.jdesktop.swingx.JXTreeTable;

public class JXTreeTableComponentFixtureExtension extends
        ComponentFixtureExtension<JXTreeTable, JXTreeTableComponentFixture> {

    private final String name;

    private JXTreeTableComponentFixtureExtension(final String name) {
        this.name = name;
    }

    public static JXTreeTableComponentFixtureExtension treeWithName(final String name) {
        return new JXTreeTableComponentFixtureExtension(name);
    }

    @Override
    public JXTreeTableComponentFixture createFixture(final Robot robot, final Container root) {
        final JXTreeTable tree = robot.finder().findByName(root, name, JXTreeTable.class, true);
        return new JXTreeTableComponentFixture(JXTreeTableComponentFixture.class, robot, tree);
    }

}
