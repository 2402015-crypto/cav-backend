package mx.hotel.backend_cav.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Integer id;

    @Column(name = "placa", nullable = false, unique = true, length = 20)
    private String placa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")
    private MarcaVehiculo marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_color")
    private ColorVehiculo color;

    @Column(name = "activo")
    private Boolean activo = true;

    public Integer getId() { return id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public MarcaVehiculo getMarca() { return marca; }
    public void setMarca(MarcaVehiculo marca) { this.marca = marca; }
    public ColorVehiculo getColor() { return color; }
    public void setColor(ColorVehiculo color) { this.color = color; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}