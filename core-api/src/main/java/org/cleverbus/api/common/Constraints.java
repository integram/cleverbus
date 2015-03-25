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

package org.cleverbus.api.common;


import org.cleverbus.api.exception.ErrorExtEnum;
import org.cleverbus.api.exception.IllegalDataException;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.api.exception.ValidationIntegrationException;


/**
 * Assertion utility class that assists in validating arguments by throwing specific
 * {@link ValidationIntegrationException} exceptions.
 *
 * <p>Useful for identifying data integration errors early and clearly at runtime.
 *
 * <p>For example, if the contract of a public method states it does not
 * allow {@code null} arguments, Constraints can be used to validate that
 * contract. Doing this clearly indicates a contract violation when it
 * occurs and protects the class's invariants.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
public final class Constraints {

    /**
     * Assert that the given String has valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalDataException occurs if {@code text} is null or does not contain nor non-whitespace
     *                              character.
     */
    public static void hasText(String text, String message) throws IllegalDataException {
        notNull(text, message);
        if (!org.springframework.util.StringUtils.hasText(text)) {
            throw new IllegalDataException(InternalErrorEnum.E109, message);
        }
    }

    /**
     * Assert that the given String has valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     *
     * @param text      the String to check
     * @param message   the exception message to use if the assertion fails
     * @param errorCode the internal error code
     * @throws IllegalDataException occurs if {@code text} is null or does not contain nor non-whitespace
     *                              character.
     */
    public static void hasText(String text, String message, ErrorExtEnum errorCode) throws IllegalDataException {
        notNull(text, message);
        if (!org.springframework.util.StringUtils.hasText(text)) {
            throw new IllegalDataException(errorCode, message);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalDataException(InternalErrorEnum.E109, message);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @param errorCode the internal error code
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull(Object object, String message, ErrorExtEnum errorCode) {
        if (object == null) {
            throw new IllegalDataException(errorCode, message);
        }
    }

    /**
     * Assert that an object <strong>is</strong> {@code null}.
     * <pre class="code">Assert.isNull(clazz, "The class must be null");</pre>
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalDataException(InternalErrorEnum.E109, message);
        }
    }

    /**
     * Assert that an object <strong>is</strong> {@code null}.
     * <pre class="code">Assert.isNull(clazz, "The class must be null");</pre>
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @param errorCode the internal error code
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object, String message, ErrorExtEnum errorCode) {
        if (object != null) {
            throw new IllegalDataException(errorCode, message);
        }
    }

    /**
     * Assert a boolean expression, throwing {@code IllegalArgumentException}
     * if the test result is {@code false}.
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(Boolean expression, String message) {
        notNull(expression, message);
        if (expression.booleanValue() != Boolean.TRUE) {
            throw new IllegalDataException(InternalErrorEnum.E109, message);
        }
    }

    /**
     * Assert a boolean expression, throwing {@code IllegalArgumentException}
     * if the test result is {@code false}.
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @param errorCode the internal error code
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(Boolean expression, String message, ErrorExtEnum errorCode) {
        notNull(expression, message);
        if (expression.booleanValue() != Boolean.TRUE) {
            throw new IllegalDataException(errorCode, message);
        }
    }

    private Constraints() {
    }
}
