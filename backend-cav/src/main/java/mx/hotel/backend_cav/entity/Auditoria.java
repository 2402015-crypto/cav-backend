package mx.hotel.backend_cav.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro")
    private RegistroAcceso registro;

    @Column(name = "placa", length = 30)
    private String placa;

    @Column(name = "tabla_afectada", length = 100)
    private String tablaAfectada;

    @Column(name = "accion", length = 100)
    private String accion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    void prepararFecha() {
        if (fecha == null) fecha = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public RegistroAcceso getRegistro() { return registro; }
    public void setRegistro(RegistroAcceso registro) { this.registro = registro; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFecha() { return fecha; }
}
