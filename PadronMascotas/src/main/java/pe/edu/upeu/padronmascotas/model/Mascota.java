package pe.edu.upeu.padronmascotas.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.padronmascotas.enums.Especie;
import pe.edu.upeu.padronmascotas.enums.EstadoVacunacion;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {
    private Long idMascota;
    @NotBlank(message = "El nombre de la mascota es obligatorio")
    private String nombre;
    @NotNull(message = "La especie es obligatoria")
    private Especie especie;
    @NotBlank(message = "La raza es obligatoria")
    private String raza;
    @NotBlank(message = "El color es obligatorio")
    private String color;
    @NotNull(message = "La edad aproximada es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 40, message = "La edad debe ser menor a 40 años")
    private Integer edadAprox;
    @NotBlank(message = "El nombre del propietario es obligatorio")
    private String nombrePropietario;
    @NotBlank(message = "El teléfono del propietario es obligatorio")
    @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String telefonoPropietario;
    @NotNull(message = "El estado de vacunación es obligatorio")
    private EstadoVacunacion estadoVacunacion;
}