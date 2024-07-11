package com.iot.system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "command_descriptions")
public class CommandDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operation;
    private String description;
    private String result;
    private String format;

    @ManyToOne
    @JoinColumn(name = "device_id")
    @JsonBackReference
    private Device device;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "command_id", referencedColumnName = "id")
    @JsonManagedReference
    private Command command;
}
