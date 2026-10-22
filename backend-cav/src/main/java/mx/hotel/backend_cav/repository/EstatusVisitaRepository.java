package mx.hotel.backend_cav.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.hotel.backend_cav.entity.EstatusVisita;

public interface EstatusVisitaRepository extends JpaRepository<EstatusVisita, Integer> {

    Optional<EstatusVisita> findByNombreIgnoreCase(String nombre);
}