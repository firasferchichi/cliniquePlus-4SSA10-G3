package tn.esprit.clinique.pharmacie.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ligne_delivrance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneDelivrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long medicamentId;

    private Integer quantitePrescrite;

    private Integer quantiteDelivree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivrance_id")
    private Delivrance delivrance;
}
