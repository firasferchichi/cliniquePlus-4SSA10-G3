package tn.esprit.clinique.personnel.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specialite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String libelle;
}
