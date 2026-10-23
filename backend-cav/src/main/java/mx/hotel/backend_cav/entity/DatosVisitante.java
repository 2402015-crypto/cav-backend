package mx.hotel.backend_cav.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "datos_visitante")
public class DatosVisitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dato")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_registro", nullable = false, unique = true)
    private RegistroAcceso registro;

    @Column(name = "area_destino", length = 150)
    private String areaDestino;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Column(name = "destino", length = 150)
    private String destino;

    @Column(name = "villa_huesped", length = 50)
    private String villaHuesped;

    @Column(name = "numero_pax")
    private Integer numeroPax;

    public void setRegistro(RegistroAcceso registro) { this.registro = registro; }
    public void setAreaDestino(String areaDestino) { this.areaDestino = areaDestino; }
    public String getAreaDestino() { return areaDestino; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getDepartamento() { return departamento; }
    public void setDestino(String destino) { this.destino = destino; }
    public String getDestino() { return destino; }
    public void setVillaHuesped(String villaHuesped) { this.villaHuesped = villaHuesped; }
    public String getVillaHuesped() { return villaHuesped; }
    public void setNumeroPax(Integer numeroPax) { this.numeroPax = numeroPax; }
    public Integer getNumeroPax() { return numeroPax; }
}