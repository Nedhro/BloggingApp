package  com.prodev.bloggingservice.blog.content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;

import com.prodev.bloggingservice.annotations.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiController
public class ContentController {

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private ContentComponent contentComponent;

	@GetMapping("/content/{id}")
	public void getContent(@PathVariable("id") Long id, HttpServletResponse response) {
		response.setContentType("image/jpg");

		try {

			Optional<Content> content = contentRepository.findById(id);
			if(content !=null) {
				File file = new File(content.get().getPath() + content.get().getName());
				byte[] fileContent = Files.readAllBytes(file.toPath());
				if (fileContent != null) {
					response.getOutputStream().write(fileContent, 0, fileContent.length);
					response.flushBuffer();
					response.setStatus(HttpStatus.OK.value());
				}
			}

		} catch (IOException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@PostMapping("/admin/content")
	public ResponseEntity<?> saveContent(@ModelAttribute ContentDTO contentDto) {

		try {
			Content content = contentComponent.saveContent(contentDto.getFiles());
			return ResponseEntity.ok(content);
		} catch (IOException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
		}
	}

}
