package mx.hotel.backend_cav.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipos_visitante")
public class TipoVisitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_visitante")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
}