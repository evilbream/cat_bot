package com.baranova.cat_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_reactions")
public class Reaction {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "link_user")
    private Long user;

    private Integer reaction;

    @ManyToOne
    @JoinColumn(name = "link_photo")
    private Photo photo;

}
