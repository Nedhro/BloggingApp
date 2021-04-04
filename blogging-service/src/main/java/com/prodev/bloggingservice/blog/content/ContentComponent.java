package  com.prodev.bloggingservice.blog.content;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ContentComponent implements Serializable {

	private static final long serialVersionUID = -7845086454358671862L;

	@Value("${content.upload.dir}")
    private String uploadPath;

	@Autowired
	private ContentRepository contentRepository;

	public void writeToDisk(MultipartFile file) throws IOException {
	        File uploadDir = new File(uploadPath);
	        if(!uploadDir.exists()) uploadDir.mkdirs();
	        String uploadFilePath = uploadPath + "/" + file.getOriginalFilename();
	        if (!file.isEmpty()) {
	        	byte[] bytes = file.getBytes();
		        Path path = Paths.get(uploadFilePath);
		        Files.write(path, bytes);
	        }
	}

	public Content saveContent(MultipartFile file) throws Exception {

		if (null != file) {
			this.writeToDisk(file);
			Content content = new Content();
			content.setPath(uploadPath);
			content.setType(file.getContentType());
			contentRepository.save(content);
			content.setName(file.getOriginalFilename());
			contentRepository.save(content);
			return content;
		}
		return null;
	}


}
