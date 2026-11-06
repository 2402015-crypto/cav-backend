package mx.hotel.backend_cav.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.hotel.backend_cav.entity.OcrHistorial;

public interface OcrHistorialRepository extends JpaRepository<OcrHistorial, Integer> {

    long countByFechaGreaterThanEqualAndFechaLessThan(
            java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

    @org.springframework.data.jpa.repository.Query("""
            select count(o) from OcrHistorial o
            where o.fecha >= :inicio and o.fecha < :fin
            and o.placaDetectada is not null and trim(o.placaDetectada) <> ''
            """)
    long countDetectadas(@org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin);

    long countByFechaGreaterThanEqualAndFechaLessThanAndCorregidaManualTrue(
            java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

    @org.springframework.data.jpa.repository.Query("""
            select avg(o.tiempoProcesamientoMs) from OcrHistorial o
            where o.fecha >= :inicio and o.fecha < :fin
            and o.tiempoProcesamientoMs is not null
            """)
    Double promedioTiempo(@org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin);

    @org.springframework.data.jpa.repository.Query("""
            select o.usuarioCorreccion.usuario, count(o) from OcrHistorial o
            where o.corregidaManual = true and o.fecha >= :inicio and o.fecha < :fin
            group by o.usuarioCorreccion.usuario order by count(o) desc
            """)
    java.util.List<Object[]> correccionesPorUsuario(
            @org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin);
}