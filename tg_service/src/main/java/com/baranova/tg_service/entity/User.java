package com.baranova.tg_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.baranova.shared.enums.Commands;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @Column
    private Long id;

    @Column
    private String username;

    @Column
    private String state;

    @Column(name="view_cat_page")
    private Integer viewCatPage;

    public User(Long id) {
        this.id = id;
        this.state = Commands.START.getCommandName();
    }

}
