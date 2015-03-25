/*
 * Copyright (C) 2015
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cleverbus.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * 
 * Tests for Tools class
 * 
 * @author <a href="mailto:pavel.hora@cleverlance.com">Pavel Hora</a>
 */
@RunWith(JUnit4.class)
public class ToolsTest{

    /**
     * Test toString method.
     * 
     * When input is null, it should return null, otherwise the input.toString method
     */
    @Test
    public void testToString() {
        
        Integer i = null;
        assertNull(Tools.toString(i));
        
        i = 2;
        
        assertEquals("2", Tools.toString(i));
    }
    
    /**
     * Test joinNonEmpty method.
     * 
     * Method merges the input strings and each string should be separated by enrich character. If string before, after
     * next one is null or empty, the enrich character is not inserted.
     */
    @Test
    public void testJoinNonEmpty() {
        assertEquals("Separate this character",
                Tools.joinNonEmpty(new String[]{"", "Separate", "this", null, "character", "", null}, ' '));

        assertEquals("",
                Tools.joinNonEmpty(new String[]{}, 'x'));

        assertEquals("//",
                Tools.joinNonEmpty(new String[]{"//"}, 'x'));

        assertEquals("//",
                Tools.joinNonEmpty(new String[]{null, "//", null}, 'x'));

        assertEquals("///",
                Tools.joinNonEmpty(new String[]{"/", "", "/", null}, '/'));

        assertEquals("/,2",
                Tools.joinNonEmpty(new String[]{"/", "   ", "2", null}, ','));
    }
}
