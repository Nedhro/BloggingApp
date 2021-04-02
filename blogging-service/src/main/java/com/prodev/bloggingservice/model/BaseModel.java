package com.prodev.bloggingservice.model;

import com.prodev.bloggingservice.auth.AuthUser;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;

    @CreationTimestamp
    @Column(name = "last_modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastModifiedDate;

    @Column(name = "modified_by")
    private Long modified_by;


    @PrePersist
    public void prePersist() {
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String)) {
                AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                this.modified_by = user.getId();
            }
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = new Date();
    }
}
