package  com.prodev.bloggingservice.blog.post;

import java.util.List;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import  com.prodev.bloggingservice.blog.comment.CommentDTO;

@Data
public class PostDTO {

	private Long id;

	private String title;

	private String description;

	private MultipartFile imagePath;

	private long contentId;

	private String createdAt;

	private Long selectedCategory;

	private List<String> popularTags;

	private List<String> postTags;

	private String selectedLang;

	private List<CommentDTO> comments;

	private List<PostCategorySummaryDTO> categories;

	private List<RecentPostDTO> recentsPosts;

	private int likes ;

	private boolean isLike;

	private String status;
}
