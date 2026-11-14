package mx.hotel.backend_cav.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.hotel.backend_cav.entity.RegistroAcceso;

public interface DashboardAdminRepository extends JpaRepository<RegistroAcceso, Integer> {

    long countByFechaSalidaIsNull();

    @Query("""
        select r from RegistroAcceso r
        left join fetch r.empresa
        left join fetch r.transportadora
        left join fetch r.tipoVisitante
        where r.fechaEntrada >= :inicio and r.fechaEntrada < :fin
        order by r.fechaEntrada asc
        """)
    List<RegistroAcceso> findForDashboard(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
