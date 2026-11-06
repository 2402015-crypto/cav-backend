package mx.hotel.backend_cav.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "metricas_ocr")
public class MetricasOcr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metrica")
    private Integer id;

    @Column(name = "fecha", nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "imagenes_procesadas")
    private int imagenesProcesadas;

    @Column(name = "detectadas")
    private int detectadas;

    @Column(name = "fallidas")
    private int fallidas;

    @Column(name = "correcciones_manuales")
    private int correccionesManuales;

    @Column(name = "precision_ocr", precision = 5, scale = 2)
    private BigDecimal precisionOcr;

    @Column(name = "tiempo_promedio_ms")
    private Integer tiempoPromedioMs;

    public Integer getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public int getImagenesProcesadas() { return imagenesProcesadas; }
    public int getDetectadas() { return detectadas; }
    public int getFallidas() { return fallidas; }
    public int getCorreccionesManuales() { return correccionesManuales; }
    public BigDecimal getPrecisionOcr() { return precisionOcr; }
    public Integer getTiempoPromedioMs() { return tiempoPromedioMs; }
    public void setFecha(LocalDate value) { fecha = value; }
    public void setImagenesProcesadas(int value) { imagenesProcesadas = value; }
    public void setDetectadas(int value) { detectadas = value; }
    public void setFallidas(int value) { fallidas = value; }
    public void setCorreccionesManuales(int value) { correccionesManuales = value; }
    public void setPrecisionOcr(BigDecimal value) { precisionOcr = value; }
    public void setTiempoPromedioMs(Integer value) { tiempoPromedioMs = value; }
}
