package org.cleverbus.api.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cleverbus.api.common.HumanReadable;
import org.springframework.util.Assert;

import javax.persistence.*;

/**
 * Entity in which is saved information about funnel value for message.
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @since 2.0.4
 */
@Entity
@Table(name = "funnel",
        uniqueConstraints = @UniqueConstraint(name = "uq_funnel_value_message",
                columnNames = {"msg_id", "funnel_value"}))
public class Funnel implements HumanReadable {

    @Id
    @Column(name = "funnel_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "msg_id", nullable = false)
    private Message message;

    @Column(name = "msg_id", nullable = false, insertable = false, updatable = false)
    private Long msgId;

    @Column(name = "funnel_value", length = 50, nullable = false)
    private String funnelValue;

    /**
     * New instance only for hibernate.
     */
    protected Funnel() {
    }

    /**
     * New instance.
     *
     * @param message     asynch. message for this funnel value
     * @param funnelValue alue for funnel filtering
     */
    public Funnel(Message message, String funnelValue) {
        Assert.notNull(message, "message must not be null");
        Assert.hasText(funnelValue, "funnelValue must not be empty");

        this.message = message;
        this.msgId = message.getMsgId();
        this.funnelValue = funnelValue;
    }
    //---------------------------------------------------- SET / GET ---------------------------------------------------

    /**
     * Gets unique funnel ID.
     *
     * @return funnel ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets unique funnel id.
     *
     * @param id funnel id
     */
    public void setId(Long id) {
        Assert.notNull(id, "id must not be null");

        this.id = id;
    }

    /**
     * Gets asynch. message for this funnel value.
     *
     * @return message
     * @see #getMsgId()
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets asynch. message for this funnel value.
     *
     * @param message message
     * @see #setMsgId(Long)
     */
    public void setMessage(Message message) {
        Assert.notNull(message, "message must not be null");

        this.message = message;
    }

    /**
     * Gets message ID (only for asynchronous message).
     *
     * @return message ID
     * @see #setMessage(Message)
     */
    public Long getMsgId() {
        return msgId;
    }

    /**
     * Sets message ID (only for asynchronous message).
     *
     * @param msgId message ID
     * @see #getMessage()
     */
    public void setMsgId(Long msgId) {
        Assert.notNull(msgId, "msgId must not be null");

        this.msgId = msgId;
    }

    /**
     * Gets value for funnel filtering - you can have funnel that will ensure that there is only one processing
     * message with same funnel value.
     *
     * @return funnel value
     * @see Message#getFunnelComponentId()
     */
    public String getFunnelValue() {
        return funnelValue;
    }

    /**
     * Sets value for funnel filtering - you can have funnel that will ensure that there is only one processing
     * message with same funnel value.
     *
     * @param funnelValue funnel value
     * @see Message#setFunnelComponentId(String)
     */
    public void setFunnelValue(String funnelValue) {
        Assert.hasText(funnelValue, "funnelValue must not be empty");

        this.funnelValue = funnelValue;
    }

    //-------------------------------------------- TO STRING / HASH / EQUALS -------------------------------------------

    @Override
    public String toHumanString() {
        return "(id = " + getId() + ", msgId = " + getMsgId() + ", funnelValue = " + getFunnelValue() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Funnel funnel = (Funnel) o;

        return new EqualsBuilder()
                .append(getId(), funnel.getId())
                .append(getMsgId(), funnel.getMsgId())
                .append(getFunnelValue(), funnel.getFunnelValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getMsgId())
                .append(getFunnelValue())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", getId())
                .append("msgId", getMsgId())
                .append("funnelValue", getFunnelValue())
                .toString();
    }
}
