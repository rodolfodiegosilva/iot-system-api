package com.iot.system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "commands")
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String command;

    @OneToMany(mappedBy = "command", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Parameter> parameters;

    @OneToOne(mappedBy = "command")
    @JsonBackReference
    private CommandDescription commandDescription;
}
