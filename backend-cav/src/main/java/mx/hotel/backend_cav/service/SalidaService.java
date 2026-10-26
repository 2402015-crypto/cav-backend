package mx.hotel.backend_cav.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.SalidaLookupResponse;
import mx.hotel.backend_cav.dto.SalidaRequest;
import mx.hotel.backend_cav.dto.SalidaActivaResponse;
import mx.hotel.backend_cav.dto.SalidaDetalleResponse;
import mx.hotel.backend_cav.entity.RegistroAcceso;
import mx.hotel.backend_cav.entity.DatosVisitante;
import mx.hotel.backend_cav.exception.ConflictException;
import mx.hotel.backend_cav.exception.ResourceNotFoundException;
import mx.hotel.backend_cav.repository.RegistroAccesoRepository;
import mx.hotel.backend_cav.repository.DatosVisitanteRepository;

@Service
public class SalidaService {

    private final RegistroAccesoRepository registroRepository;
    private final ApplicationTimeService timeService;
        private final DatosVisitanteRepository datosRepository;

        public SalidaService(RegistroAccesoRepository registroRepository, ApplicationTimeService timeService, DatosVisitanteRepository datosRepository) {
        this.registroRepository = registroRepository;
        this.timeService = timeService;
        this.datosRepository = datosRepository;
    }

        @Transactional(readOnly = true)
        public java.util.List<SalidaActivaResponse> listarActivos(String texto) {
        java.util.List<RegistroAcceso> registros = texto == null || texto.isBlank()
            ? registroRepository.findAllByFechaSalidaIsNullOrderByFechaEntradaAsc()
            : registroRepository.buscarActivos(texto.trim());
        return registros.stream().map(registro -> new SalidaActivaResponse(registro.getId(), registro.getPlacaRegistrada(),
            registro.getConductor() == null ? "N/A" : registro.getConductor().getNombre(), relacionado(registro),
            registro.getTipoVisitante() == null ? "N/A" : registro.getTipoVisitante().getNombre(), registro.getFechaEntrada())).toList();
        }

        @Transactional(readOnly = true)
        public SalidaDetalleResponse detalle(Integer id) {
        RegistroAcceso registro = registroRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registro de entrada no encontrado"));
        java.util.List<SalidaDetalleResponse.VisitaHistorialResponse> historial = registro.getVehiculo() == null
            ? java.util.List.of() : registroRepository.findAllByVehiculo_IdOrderByFechaEntradaDesc(registro.getVehiculo().getId()).stream()
            .map(item -> new SalidaDetalleResponse.VisitaHistorialResponse(item.getFechaEntrada(), item.getFechaSalida(),
                formatear(Duration.between(item.getFechaEntrada(), item.getFechaSalida() == null ? timeService.now() : item.getFechaSalida()).toMinutes())))
            .toList();
        return new SalidaDetalleResponse(mapear(registro), historial, historial.size());
        }

    @Transactional(readOnly = true)
    public SalidaLookupResponse buscar(String placa) {
        String valor = placa == null ? "" : placa.trim();
        return registroRepository.findTopByPlacaRegistradaIgnoreCaseAndFechaSalidaIsNullOrderByFechaEntradaDesc(valor)
                .map(this::mapear)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una entrada activa para esa placa"));
    }

    @Transactional
    public SalidaLookupResponse registrar(Integer id, SalidaRequest request) {
        RegistroAcceso registro = registroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de entrada no encontrado"));
        if (registro.getFechaSalida() != null) {
            throw new ConflictException("La salida de este vehículo ya fue registrada");
        }
        registro.setFechaSalida(timeService.now());
        registro.setObservaciones(request == null || request.observaciones() == null
                ? registro.getObservaciones() : request.observaciones().trim());
        return mapear(registroRepository.save(registro));
    }

    private SalidaLookupResponse mapear(RegistroAcceso registro) {
        long minutos = Math.max(0, Duration.between(registro.getFechaEntrada(),
                registro.getFechaSalida() == null ? timeService.now() : registro.getFechaSalida()).toMinutes());
        String tipo = registro.getTipoVisitante() == null ? null : registro.getTipoVisitante().getNombre();
        DatosVisitante datos = datosRepository.findByRegistro_Id(registro.getId()).orElse(null);
        String tipoLower = tipo == null ? "" : tipo.toLowerCase(java.util.Locale.ROOT);
        String area = tipoLower.contains("proveedor") ? datos == null ? null : datos.getAreaDestino()
            : tipoLower.contains("colaborador") || tipoLower.contains("general") ? datos == null ? null : datos.getDepartamento() : null;
        return new SalidaLookupResponse(registro.getId(), registro.getPlacaRegistrada(),
                registro.getConductor() == null ? null : registro.getConductor().getNombre(), tipo,
                registro.getEmpresa() == null ? null : registro.getEmpresa().getNombre(),
                registro.getTransportadora() == null ? null : registro.getTransportadora().getNombre(),
                registro.getVehiculo() == null || registro.getVehiculo().getMarca() == null ? null : registro.getVehiculo().getMarca().getNombre(),
                registro.getVehiculo() == null || registro.getVehiculo().getColor() == null ? null : registro.getVehiculo().getColor().getNombre(),
                registro.getFechaEntrada(), registro.getFechaSalida(), registro.getObservaciones(), minutos, formatear(minutos),
                area, datos == null ? null : datos.getVillaHuesped(), datos == null ? null : datos.getNumeroPax(),
                datos == null ? null : datos.getNumeroUnidad(), datos == null ? null : datos.getDestino());
    }

    private String formatear(long minutos) {
        return (minutos / 60) + "h " + (minutos % 60) + "m";
    }

    private String relacionado(RegistroAcceso registro) {
        String tipo = registro.getTipoVisitante() == null ? "" : registro.getTipoVisitante().getNombre().toLowerCase(Locale.ROOT);
        if (tipo.contains("proveedor")) return registro.getEmpresa() == null ? null : registro.getEmpresa().getNombre();
        if (tipo.contains("transportadora")) return registro.getTransportadora() == null ? null : registro.getTransportadora().getNombre();
        if (tipo.contains("taxi")) return datosRepository.findByRegistro_Id(registro.getId()).map(datos -> datos.getNumeroUnidad()).orElse(null);
        return null;
    }
}