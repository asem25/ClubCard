package ru.semavin.ClubCard.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Table(name = "templates_privileges")
@Data
public class TemplatesPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String template;
    @ElementCollection
    @CollectionTable(name = "templates_privileges", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "privilege")
    private List<String> privileges = new ArrayList<>();
}
