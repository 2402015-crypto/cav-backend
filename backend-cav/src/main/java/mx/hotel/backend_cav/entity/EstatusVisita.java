package mx.hotel.backend_cav.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estatus_visita")
public class EstatusVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estatus")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
}