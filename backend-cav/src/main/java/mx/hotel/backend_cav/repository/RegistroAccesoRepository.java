package mx.hotel.backend_cav.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.hotel.backend_cav.entity.RegistroAcceso;
import mx.hotel.backend_cav.entity.TipoMovimiento;

public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Integer> {

	java.util.Optional<RegistroAcceso> findTopByVehiculoPlacaIgnoreCaseOrderByFechaEntradaDesc(String placa);

	java.util.Optional<RegistroAcceso> findTopByPlacaRegistradaIgnoreCaseAndFechaSalidaIsNullOrderByFechaEntradaDesc(String placa);

	long countByFechaEntradaGreaterThanEqualAndFechaEntradaLessThan(java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	    long countByFechaEntradaGreaterThanEqualAndFechaEntradaLessThanAndFechaSalidaIsNull(
		    java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	    long countByTipoMovimientoAndFechaEntradaGreaterThanEqualAndFechaEntradaLessThan(
		    TipoMovimiento tipoMovimiento, java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	long countByFechaSalidaGreaterThanEqualAndFechaSalidaLessThan(java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	@Modifying
	@Query("update RegistroAcceso r set r.empresa = null where r.empresa.id = :empresaId")
	int desvincularEmpresa(@Param("empresaId") Integer empresaId);

	@Modifying
	@Query("update RegistroAcceso r set r.transportadora = null where r.transportadora.id = :transportadoraId")
	int desvincularTransportadora(@Param("transportadoraId") Integer transportadoraId);
}