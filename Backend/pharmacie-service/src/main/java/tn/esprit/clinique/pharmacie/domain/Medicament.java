package tn.esprit.clinique.pharmacie.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicament")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String nom;

    private String forme;

    private String dosage;

    @Column(precision = 12, scale = 3)
    private BigDecimal prixUnitaire;

    private Integer seuilAlerte;

    @OneToMany(mappedBy = "medicament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Lot> lots = new ArrayList<>();
}
