package mx.hotel.backend_cav.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "ocr_historial")
public class OcrHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ocr")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro")
    private RegistroAcceso registro;

    @Column(name = "placa_detectada", length = 30)
    private String placaDetectada;

    @Column(name = "confianza_detectada", precision = 5, scale = 2)
    private BigDecimal confianzaDetectada;

    @Column(name = "region_detectada", length = 100)
    private String regionDetectada;

    @Column(name = "tiempo_procesamiento_ms")
    private Integer tiempoProcesamientoMs;

    @Column(name = "resultado_final", length = 30)
    private String resultadoFinal;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    public Integer getId() { return id; }
        public void vincularRegistro(RegistroAcceso registro) { this.registro = registro; }
    public void setRegistro(RegistroAcceso registro) { this.registro = registro; }
    public void setPlacaDetectada(String placaDetectada) { this.placaDetectada = placaDetectada; }
    public void setConfianzaDetectada(BigDecimal confianzaDetectada) { this.confianzaDetectada = confianzaDetectada; }
    public void setRegionDetectada(String regionDetectada) { this.regionDetectada = regionDetectada; }
    public void setTiempoProcesamientoMs(Integer tiempoProcesamientoMs) { this.tiempoProcesamientoMs = tiempoProcesamientoMs; }
    public void setResultadoFinal(String resultadoFinal) { this.resultadoFinal = resultadoFinal; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}