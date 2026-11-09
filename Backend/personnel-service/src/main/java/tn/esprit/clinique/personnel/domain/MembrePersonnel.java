package tn.esprit.clinique.personnel.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membre_personnel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembrePersonnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricule;

    private String nom;

    private String prenom;

    @Enumerated(EnumType.STRING)
    private RolePersonnel role;

    private String email;

    private String telephone;

    private String keycloakId;

    private boolean actif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HoraireTravail> horaires = new ArrayList<>();

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Absence> absences = new ArrayList<>();
}
