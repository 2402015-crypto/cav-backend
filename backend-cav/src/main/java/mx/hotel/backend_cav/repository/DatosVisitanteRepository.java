package mx.hotel.backend_cav.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.hotel.backend_cav.entity.DatosVisitante;

public interface DatosVisitanteRepository extends JpaRepository<DatosVisitante, Integer> {

	java.util.Optional<DatosVisitante> findByRegistro_Id(Integer registroId);
}