package mx.hotel.backend_cav.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registros_acceso")
public class RegistroAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conductor")
    private Conductor conductor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportadora")
    private Transportadora transportadora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_visitante", nullable = false)
    private TipoVisitante tipoVisitante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estatus", nullable = false)
    private EstatusVisita estatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_registro", nullable = false, length = 20)
    private MetodoRegistro metodoRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", length = 3)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "placa_registrada", length = 30)
    private String placaRegistrada;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDateTime fechaEntrada;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "observaciones")
    private String observaciones;

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public void setConductor(Conductor conductor) { this.conductor = conductor; }
    public void setTipoVisitante(TipoVisitante tipoVisitante) { this.tipoVisitante = tipoVisitante; }
    public void setEstatus(EstatusVisita estatus) { this.estatus = estatus; }
    public void setMetodoRegistro(MetodoRegistro metodoRegistro) { this.metodoRegistro = metodoRegistro; }
    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public void setPlacaRegistrada(String placaRegistrada) { this.placaRegistrada = placaRegistrada; }
    public void setFechaEntrada(LocalDateTime fechaEntrada) { this.fechaEntrada = fechaEntrada; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Integer getId() { return id; }
    public LocalDateTime getFechaEntrada() { return fechaEntrada; }
    public String getPlacaRegistrada() { return placaRegistrada; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public Conductor getConductor() { return conductor; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }
}