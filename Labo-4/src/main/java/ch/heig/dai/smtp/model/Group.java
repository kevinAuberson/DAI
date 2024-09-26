package ch.heig.dai.smtp.model;

import java.util.List;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file Group.java
 * @brief Represents a group with a sender, recipients, and a message.
 * @date 2020-03-25
 * <p>
 * This class encapsulates information about a group, including its sender,
 * recipients, and associated message. It allows for managing and accessing
 * these details as a single entity.
 * </p>
 */
public class Group {
    private final String sender;
    private final List<String> recipients;
    private Message message;

    /**
     * Constructs a Group object with a sender and a list of recipients.
     *
     * @param sender     The sender of the group.
     * @param recipients The recipients of the group.
     */
    public Group(String sender, List<String> recipients) {
        this.sender = sender;
        this.recipients = recipients;
    }

    /**
     * Returns the sender of the group.
     *
     * @return The sender of the group.
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * Returns the recipients of the group.
     *
     * @return The recipients of the group.
     */
    public List<String> getRecipients() {
        return this.recipients;
    }

    /**
     * Returns the message of the group.
     *
     * @return The message of the group.
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     * Sets the message of the group.
     *
     * @param message The message of the group.
     */
    public void setMessage(Message message) {
        this.message = message;
    }
}