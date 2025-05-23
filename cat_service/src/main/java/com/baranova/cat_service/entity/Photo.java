package com.baranova.cat_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_photo")
public class Photo {

    @Id
    @GeneratedValue
    private Long id; // photo id on telegram servers

    private Long author;

    private String username;

    @Column(nullable = false)
    private String catName;

    private LocalDateTime uploadedAt;

    private byte[] photo;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions;

    public Photo(Long author, byte[] photo) {
        this.author = author;
        this.catName = null;
        this.photo = photo;
        this.uploadedAt = LocalDateTime.now();
    }

}
