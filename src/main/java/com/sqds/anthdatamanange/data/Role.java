package com.sqds.anthdatamanange.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Collection;

@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    private  int id;
    private String rolenname;

}
