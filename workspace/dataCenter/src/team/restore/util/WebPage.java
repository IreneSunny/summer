package team.restore.util;

import java.nio.ByteBuffer;

public class WebPage {
	private ByteBuffer uri;
	private ByteBuffer content;
	private String contentType;
	private ByteBuffer signature;
	private ByteBuffer title;
	private ByteBuffer picture;
	private long version;
	private long timestamp;
	
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public ByteBuffer getUri() {
		return uri;
	}
	public void setUri(ByteBuffer uri) {
		this.uri = uri;
	}
	public ByteBuffer getContent() {
		return content;
	}
	public void setContent(ByteBuffer content) {
		this.content = content;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public ByteBuffer getSignature() {
		return signature;
	}
	public void setSignature(ByteBuffer signature) {
		this.signature = signature;
	}
	public ByteBuffer getTitle() {
		return title;
	}
	public void setTitle(ByteBuffer title) {
		this.title = title;
	}
	public ByteBuffer getPicture() {
		return picture;
	}
	public void setPicture(ByteBuffer picture) {
		this.picture = picture;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "WebPage [uri=" + uri + ",version=" + version + ", timestamp="
				+ timestamp + "]";
	}
	
	
	
          
}
