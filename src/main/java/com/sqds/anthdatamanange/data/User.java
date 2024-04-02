package com.sqds.anthdatamanange.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "[user]")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id")
    private int id;
    @Column(name ="username")
    private  String username;
    @Column(name ="password")
    private String password;
    @Column(name ="roleid")
    private String roleid;
    @Transient
    private List<zt_user> userunit;


}
