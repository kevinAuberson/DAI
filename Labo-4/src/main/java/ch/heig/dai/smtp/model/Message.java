package ch.heig.dai.smtp.model;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file Message.java
 * @brief Represents an email message.
 * @date 2020-03-25
 * <p>
 * The Message class contains information about an email message, including
 * its sender, recipients, subject, and body.
 * </p>
 */
public class Message {
    private final String subject;
    private final String body;

    /**
     * Constructor for creating a Message object.
     *
     * @param subject The subject of the message.
     * @param body    The body of the message.
     */
    public Message(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    /**
     * Returns the subject of the message.
     *
     * @return The subject of the message.
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Returns the body of the message.
     *
     * @return The body of the message.
     */
    public String getBody() {
        return this.body;
    }
}
