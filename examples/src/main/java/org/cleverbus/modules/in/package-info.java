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

/**
 * Modules which process input requests from external systems
 * - public interfaces are divided by entity type + there is special interface for communication with Vodafone (VF),
 * to be able to send messages to our system.
 * <p/>
 * Input modules publicize interfaces (mostly WSDL interface).
 * "In" modules are "servers" of the communication.
 */
package org.cleverbus.modules.in;