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
 * Requests/responses saving.
 * <p/>
 * Default implementation uses Camel events that has one possible disadvantage - it's necessary to join request
 * and response together (= two Camel events) and if exchange is changed from sending request until response receive
 * then it's not possible to join it.
 * <p/>
 * Requests/responses are saved into database, {@link RequestResponseService} defines contract
 * - {@link RequestResponseServiceDefaultImpl default implementation} save them directly to DB in synchronous manner.
 */
package org.cleverbus.core.reqres;