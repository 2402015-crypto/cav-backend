package mx.hotel.backend_cav.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.hotel.backend_cav.entity.TipoVisitante;

public interface TipoVisitanteRepository extends JpaRepository<TipoVisitante, Integer> {

    Optional<TipoVisitante> findByNombreIgnoreCase(String nombre);
}