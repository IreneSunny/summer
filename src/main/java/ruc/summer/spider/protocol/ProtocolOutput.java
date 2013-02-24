package ruc.summer.spider.protocol;

public class ProtocolOutput {
	private Content content;
	private ProtocolStatus status;

	public ProtocolOutput(Content content, ProtocolStatus status) {
		this.content = content;
		this.status = status;
	}

	public ProtocolOutput(Content content) {
		this.content = content;
		this.status = ProtocolStatus.STATUS_SUCCESS;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public ProtocolStatus getStatus() {
		return status;
	}

	public void setStatus(ProtocolStatus status) {
		this.status = status;
	}
}
