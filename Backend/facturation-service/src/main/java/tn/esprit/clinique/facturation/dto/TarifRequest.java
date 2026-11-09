package tn.esprit.clinique.facturation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tn.esprit.clinique.facturation.domain.TypeActe;

import java.math.BigDecimal;

public record TarifRequest(
        @NotNull TypeActe typeActe,
        @NotBlank String code,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal montant) {
}
