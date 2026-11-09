package tn.esprit.clinique.pharmacie.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivrance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ordonnanceId;

    private Long patientId;

    private String consultationId;

    @Enumerated(EnumType.STRING)
    private StatutDelivrance statut;

    private LocalDateTime date;

    @OneToMany(mappedBy = "delivrance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneDelivrance> lignes = new ArrayList<>();
}
