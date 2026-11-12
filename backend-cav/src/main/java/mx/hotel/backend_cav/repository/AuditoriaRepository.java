package mx.hotel.backend_cav.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.hotel.backend_cav.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    @Query("""
        select a from Auditoria a
        join fetch a.usuario u
        left join fetch a.registro r
        where (:placa is null or lower(a.placa) like lower(concat('%', :placa, '%')))
          and (:usuario is null or u.id = :usuario)
          and (:accion is null
            or (:accion = 'OBSERVACIONES' and a.accion in ('ANADIO_OBSERVACIONES', 'MODIFICO_OBSERVACIONES', 'ELIMINO_OBSERVACIONES'))
            or (:accion = 'GESTION_USUARIOS' and a.accion in ('CREO_USUARIO', 'ACTIVO_USUARIO', 'DESACTIVO_USUARIO', 'ELIMINO_USUARIO', 'MODIFICO_USUARIO'))
            or a.accion = :accion)
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha < :hasta)
        order by a.fecha desc
        """)
    List<Auditoria> buscar(@Param("placa") String placa, @Param("usuario") Integer usuario,
            @Param("accion") String accion, @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
