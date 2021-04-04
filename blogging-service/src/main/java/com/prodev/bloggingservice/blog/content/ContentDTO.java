package  com.prodev.bloggingservice.blog.content;

import org.springframework.web.multipart.MultipartFile;

public class ContentDTO {

	private long id;

	private MultipartFile files;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MultipartFile getFiles() {
		return files;
	}

	public void setFiles(MultipartFile files) {
		this.files = files;
	}



}
