package com.prodev.bloggingservice.blog.post;

public class PostCategorySummaryDTO {

    private String name;
    private long count;

    public PostCategorySummaryDTO() {

    }
    public PostCategorySummaryDTO(PostCategory pc, long count) {
        this.name = pc.getName();
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }


}
