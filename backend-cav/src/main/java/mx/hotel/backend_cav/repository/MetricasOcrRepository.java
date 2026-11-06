package mx.hotel.backend_cav.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.hotel.backend_cav.entity.MetricasOcr;

public interface MetricasOcrRepository extends JpaRepository<MetricasOcr, Integer> {
    Optional<MetricasOcr> findByFecha(LocalDate fecha);
    List<MetricasOcr> findAllByFechaBetweenOrderByFechaAsc(LocalDate inicio, LocalDate fin);
}
