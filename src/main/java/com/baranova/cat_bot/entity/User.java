package com.baranova.cat_bot.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.baranova.cat_bot.enums.Commands;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @Column
    private Long id;

    @Column
    private String username;

    @Column()
    private String state;

    @Column()
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Photo> photos;

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
        this.state = Commands.START.getCommandName();
    }

    public User(Long id) {
        this.id = id;
        this.state = Commands.START.getCommandName();
    }

    public User(Long id, String username, String state) {
        this.id = id;
        this.username = username;
        this.state = state;
    }

}
