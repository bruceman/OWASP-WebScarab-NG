package org.owasp.webscarab.domain;

import java.net.URI;
import java.util.Date;


import com.twmacinta.util.MD5;

public class ConversationSummary extends BaseEntity implements Comparable {

    private Date date = null;

    private String requestMethod = null;

    private URI requestUri = null;
    
    private String requestVersion = null;
    
    private String requestContentChecksum = null;

    private String requestContentType = null;
    
    private int requestContentSize = 0;
    
    private String responseVersion = null;
    
    private String responseStatus = null;

    private String responseMessage = null;

    private String responseContentChecksum = null;

    private String responseContentType = null;
    
    private int responseContentSize = 0;
    
    private String plugin = null;

    public ConversationSummary() {
    }

    public ConversationSummary(Conversation conversation) {
        setDate(conversation.getDate());
        setRequestMethod(conversation.getRequestMethod());
        setRequestUri(conversation.getRequestUri());
        setRequestVersion(conversation.getRequestVersion());
        NamedValue[] contentType = NamedValue.find("Content-Type", conversation.getRequestHeaders());
        if (contentType != null && contentType.length > 0)
        	setRequestContentType(contentType[0].getValue());
        byte[] content = conversation.getRequestContent();
        if (content != null && content.length > 0) {
            MD5 md5 = new MD5();
            md5.Update(content);
            setRequestContentChecksum(md5.asHex());
            setRequestContentSize(content.length);
        }
        setResponseStatus(conversation.getResponseStatus());
        setResponseMessage(conversation.getResponseMessage());
        contentType = NamedValue.find("Content-Type", conversation.getResponseHeaders());
        if (contentType != null && contentType.length > 0)
        	setResponseContentType(contentType[0].getValue());
        content = conversation.getResponseContent();
        if (content != null && content.length > 0) {
            MD5 md5 = new MD5();
            md5.Update(content);
            setResponseContentChecksum(md5.asHex());
            setResponseContentSize(content.length);
        }
        setResponseVersion(conversation.getResponseVersion());
        setId(conversation.getId());
    }

    /**
     * @return Returns the date.
     */
    public Date getDate() {
        return (date == null ? null : new Date(date.getTime()));
    }

    /**
     * @param date
     *            The date to set.
     */
    public void setDate(Date date) {
        this.date = (date == null ? null : new Date(date.getTime()));
    }

    /**
     * @return Returns the requestContentChecksum.
     */
    public String getRequestContentChecksum() {
        return requestContentChecksum;
    }

    /**
     * @param requestContentChecksum
     *            The requestContentChecksum to set.
     */
    public void setRequestContentChecksum(String requestContentChecksum) {
        this.requestContentChecksum = requestContentChecksum;
    }

    /**
     * @return Returns the requestMethod.
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * @param requestMethod
     *            The requestMethod to set.
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * @return Returns the requestUri.
     */
    public URI getRequestUri() {
        return requestUri;
    }

    /**
     * @param requestUri
     *            The requestUri to set.
     */
    public void setRequestUri(URI requestUri) {
        this.requestUri = requestUri;
    }

    /**
     * @return Returns the responseContentChecksum.
     */
    public String getResponseContentChecksum() {
        return responseContentChecksum;
    }

    /**
     * @param responseContentChecksum
     *            The responseContentChecksum to set.
     */
    public void setResponseContentChecksum(String responseContentChecksum) {
        this.responseContentChecksum = responseContentChecksum;
    }

    /**
     * @return Returns the responseMessage.
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @param responseMessage
     *            The responseMessage to set.
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * @return Returns the responseStatus.
     */
    public String getResponseStatus() {
        return responseStatus;
    }

    /**
     * @param responseStatus
     *            The responseStatus to set.
     */
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * @return Returns the plugin.
     */
    public String getPlugin() {
        return plugin;
    }

    /**
     * @param plugin
     *            The plugin to set.
     */
    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    /**
     * @return Returns the requestVersion.
     */
    public String getRequestVersion() {
        return requestVersion;
    }

    /**
     * @param requestVersion The requestVersion to set.
     */
    public void setRequestVersion(String requestVersion) {
        this.requestVersion = requestVersion;
    }

    /**
     * @return Returns the responseVersion.
     */
    public String getResponseVersion() {
        return responseVersion;
    }

    /**
     * @param responseVersion The responseVersion to set.
     */
    public void setResponseVersion(String responseVersion) {
        this.responseVersion = responseVersion;
    }

	/* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(Object o) {
        ConversationSummary that = (ConversationSummary) o;
        if (this.getId() == null || that.getId() == null)
            throw new NullPointerException("Id is null");
        return this.getDate().compareTo(that.getDate());
    }

	public int getRequestContentSize() {
		return this.requestContentSize;
	}

	public void setRequestContentSize(int requestContentSize) {
		this.requestContentSize = requestContentSize;
	}

	public int getResponseContentSize() {
		return this.responseContentSize;
	}

	public void setResponseContentSize(int responseContentSize) {
		this.responseContentSize = responseContentSize;
	}

	/**
	 * @return the requestContentType
	 */
	public String getRequestContentType() {
		return this.requestContentType;
	}

	/**
	 * @param requestContentType the requestContentType to set
	 */
	public void setRequestContentType(String requestContentType) {
		this.requestContentType = requestContentType;
	}

	/**
	 * @return the responseContentType
	 */
	public String getResponseContentType() {
		return this.responseContentType;
	}

	/**
	 * @param responseContentType the responseContentType to set
	 */
	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}
}