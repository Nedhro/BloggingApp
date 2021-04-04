package  com.prodev.bloggingservice.blog.post;

public class PostTagSummaryDTO {

	public PostTagSummaryDTO() {

	}

	public PostTagSummaryDTO(String name,long count) {
		this.tag = name;
		this.count = count;
	}

	private String tag;
	private long count;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}



}
