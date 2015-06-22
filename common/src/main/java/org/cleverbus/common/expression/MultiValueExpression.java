package org.cleverbus.common.expression;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * In this {@link Expression} we can add one or more {@link Expression} and in {@link #evaluate(Exchange, Class)}
 * is returned {@link List} of values from all {@link Expression}s.
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @since 2.0.4
 */
public class MultiValueExpression implements Expression {

    /**
     * {@code true} - in returned {@link List} is include {@code NULL} values, {@code false} - otherwise.
     */
    private final boolean includeNullValues;

    /**
     * All expressions to evaluate values.
     */
    private final List<Expression> expressions;

    /**
     * New instance.
     *
     * @param expressions       all expressions to evaluate values
     * @param includeNullValues {@code true} - in returned {@link List} is include {@code NULL} values,
     *                          {@code false} - otherwise
     */
    public MultiValueExpression(Collection<Expression> expressions, boolean includeNullValues) {
        Assert.notNull(expressions, "expressions must not be null");

        this.includeNullValues = includeNullValues;
        this.expressions = new ArrayList<>(expressions);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        Assert.notNull(exchange, "exchange must not be null");

        List<Object> result = new ArrayList<>(getExpressions().size());
        for (Expression expression : getExpressions()){
            Object expValue = expression.evaluate(exchange, Object.class);
            if (includeNullValues || expValue != null){
                result.add(expValue);
            }
        }
        return (T) result;
    }

    //---------------------------------------------- SET / GET ---------------------------------------------------------

    /**
     * Return information if in {@link List} will be {@code NULL} values.
     *
     * @return {@code true} - in returned {@link List} is include {@code NULL} values, {@code false} - otherwise.
     */
    public boolean isIncludeNullValues() {
        return includeNullValues;
    }

    /**
     * Return all expressions to evaluate values.
     *
     * @return all expressions to evaluate values
     */
    public List<Expression> getExpressions() {
        return Collections.unmodifiableList(expressions);
    }
}
