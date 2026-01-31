package br.com.spring_react.blog.post.internal;

import br.com.spring_react.blog.user.internal.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Size(min = 5, max = 150)
    private String title;

    @Size(max = 200)
    @Column
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(min = 100, max = 20000)
    private String content;

    @Column
    private String banner = "https://placehold.co/600x400/png";

    @Column(unique = true, nullable = false)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "created_at", updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();

        // cria um slug único para o post (titulo-completo-e-uuid)
        if (this.slug == null || this.slug.isEmpty()) {
            String baseSlug = title.toLowerCase().replaceAll("[^a-z0-9]", "-");
            String shortId = UUID.randomUUID().toString().split("-")[0];
            this.slug = baseSlug + "-" + shortId;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
