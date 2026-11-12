package mx.hotel.backend_cav.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.hotel.backend_cav.entity.RegistroAcceso;
import mx.hotel.backend_cav.entity.TipoMovimiento;

public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Integer> {

	java.util.Optional<RegistroAcceso> findTopByVehiculoPlacaIgnoreCaseOrderByFechaEntradaDesc(String placa);

	java.util.Optional<RegistroAcceso> findTopByPlacaRegistradaIgnoreCaseOrderByFechaEntradaDesc(String placa);

	java.util.Optional<RegistroAcceso> findTopByPlacaRegistradaIgnoreCaseAndFechaSalidaIsNullOrderByFechaEntradaDesc(String placa);

	java.util.List<RegistroAcceso> findAllByFechaSalidaIsNullOrderByFechaEntradaAsc();

	@org.springframework.data.jpa.repository.Query("select r from RegistroAcceso r left join r.conductor c where r.fechaSalida is null and (lower(r.placaRegistrada) like lower(concat('%', :texto, '%')) or lower(c.nombre) like lower(concat('%', :texto, '%'))) order by r.fechaEntrada asc")
	java.util.List<RegistroAcceso> buscarActivos(@org.springframework.data.repository.query.Param("texto") String texto);

	java.util.List<RegistroAcceso> findAllByVehiculo_IdOrderByFechaEntradaDesc(Integer vehiculoId);

	@Query("""
		select r from RegistroAcceso r
		left join r.conductor c
		left join r.empresa e
		left join r.transportadora t
		left join r.tipoVisitante tv
		where (:texto is null or :texto = ''
			or lower(r.placaRegistrada) like lower(concat('%', :texto, '%'))
			or lower(c.nombre) like lower(concat('%', :texto, '%'))
			or lower(e.nombre) like lower(concat('%', :texto, '%'))
			or lower(t.nombre) like lower(concat('%', :texto, '%')))
		and (:empresa is null or :empresa = ''
			or lower(e.nombre) like lower(concat('%', :empresa, '%'))
			or lower(t.nombre) like lower(concat('%', :empresa, '%')))
		and (:tipo is null or :tipo = ''
			or (lower(:tipo) = 'colaborador'
				and (lower(tv.nombre) like '%colaborador%' or lower(tv.nombre) like '%general%'))
			or (lower(:tipo) = 'proveedor' and lower(tv.nombre) like '%proveedor%')
			or (lower(:tipo) = 'transportadora' and lower(tv.nombre) like '%transportadora%')
			or (lower(:tipo) = 'taxi' and lower(tv.nombre) like '%taxi%'))
		and (:desde is null or r.fechaEntrada >= :desde)
		and (:hasta is null or r.fechaEntrada < :hasta)
		order by r.fechaEntrada desc
		""")
	java.util.List<RegistroAcceso> buscarHistorial(
			@Param("texto") String texto,
			@Param("empresa") String empresa,
			@Param("tipo") String tipo,
			@Param("desde") java.time.LocalDateTime desde,
			@Param("hasta") java.time.LocalDateTime hasta);

	long countByFechaEntradaGreaterThanEqualAndFechaEntradaLessThan(java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	    long countByFechaEntradaGreaterThanEqualAndFechaEntradaLessThanAndFechaSalidaIsNull(
		    java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	    long countByTipoMovimientoAndFechaEntradaGreaterThanEqualAndFechaEntradaLessThan(
		    TipoMovimiento tipoMovimiento, java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	long countByFechaSalidaGreaterThanEqualAndFechaSalidaLessThan(java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

	long countByFechaSalidaIsNull();

	@Query("""
		select r from RegistroAcceso r
		left join fetch r.conductor
		left join fetch r.empresa
		left join fetch r.transportadora
		left join fetch r.tipoVisitante
		where (r.fechaEntrada >= :inicio and r.fechaEntrada < :fin)
			or (r.fechaSalida >= :inicio and r.fechaSalida < :fin)
		order by coalesce(r.fechaSalida, r.fechaEntrada) desc
		""")
	java.util.List<RegistroAcceso> buscarMovimientosDelDia(
			@Param("inicio") java.time.LocalDateTime inicio,
			@Param("fin") java.time.LocalDateTime fin);

	@Modifying
	@Query("update RegistroAcceso r set r.empresa = null where r.empresa.id = :empresaId")
	int desvincularEmpresa(@Param("empresaId") Integer empresaId);

	@Modifying
	@Query("update RegistroAcceso r set r.transportadora = null where r.transportadora.id = :transportadoraId")
	int desvincularTransportadora(@Param("transportadoraId") Integer transportadoraId);
}