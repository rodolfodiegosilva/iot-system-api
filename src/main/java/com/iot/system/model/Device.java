// src/main/java/com/iot/system/model/Device.java
package com.iot.system.model;

import com.iot.system.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
