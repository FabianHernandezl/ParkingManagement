package model.converter;

import model.dto.SpaceDTO;
import model.entities.Space;
import model.entities.Vehicle;
import model.entities.VehicleType;
import java.util.Date;

public class SpaceConverter {

    public static SpaceDTO toDTO(Space entity) {
        if (entity == null) {
            return null;
        }

        SpaceDTO dto = new SpaceDTO();
        dto.setId(entity.getId());
        dto.setDisabilityAdaptation(entity.isDisabilityAdaptation());
        dto.setSpaceTaken(entity.isSpaceTaken());
        dto.setAvailable(entity.isAvailable());

        if (entity.getParkingLot() != null) {
            dto.setParkingLotId(entity.getParkingLot().getId());
        }

        // 🔥 GUARDAR INFORMACIÓN DEL VEHÍCULO SI EXISTE
        if (entity.getVehicle() != null) {
            dto.setVehiclePlate(entity.getVehicle().getPlate());

            if (entity.getVehicle().getVehicleType() != null) {
                dto.setVehicleTypeId(entity.getVehicle().getVehicleType().getId());
                dto.setVehicleTypeDescription(entity.getVehicle().getVehicleType().getDescription());
            }
        } else if (entity.getVehicleType() != null) {
            // Si no hay vehículo pero hay tipo (caso de espacios recién creados)
            dto.setVehicleTypeId(entity.getVehicleType().getId());
            dto.setVehicleTypeDescription(entity.getVehicleType().getDescription());
        }

        if (entity.getEntryTime() != null) {
            dto.setEntryTime(entity.getEntryTime().getTime());
        }

        return dto;
    }

    public static Space fromDTO(SpaceDTO dto) {
        if (dto == null) {
            return null;
        }

        Space entity = new Space();
        entity.setId(dto.getId());
        entity.setDisabilityAdaptation(dto.isDisabilityAdaptation());
        entity.setSpaceTaken(dto.isSpaceTaken());
        entity.setAvailable(dto.isAvailable());

        // 🔥 RESTAURAR INFORMACIÓN DEL VEHÍCULO
        if (dto.getVehiclePlate() != null || dto.getVehicleTypeId() > 0) {
            Vehicle vehicle = new Vehicle();
            vehicle.setPlate(dto.getVehiclePlate() != null ? dto.getVehiclePlate() : "DESCONOCIDO");

            if (dto.getVehicleTypeId() > 0) {
                VehicleType vt = new VehicleType();
                vt.setId(dto.getVehicleTypeId());
                vt.setDescription(dto.getVehicleTypeDescription() != null
                        ? dto.getVehicleTypeDescription() : "Desconocido");
                vehicle.setVehicleType(vt);
                entity.setVehicleType(vt);
            }

            entity.setVehicle(vehicle);
        } else if (dto.getVehicleTypeId() > 0) {
            // Solo tipo, sin vehículo
            VehicleType vt = new VehicleType();
            vt.setId(dto.getVehicleTypeId());
            vt.setDescription(dto.getVehicleTypeDescription() != null
                    ? dto.getVehicleTypeDescription() : "Desconocido");
            entity.setVehicleType(vt);
        }

        if (dto.getEntryTime() != null && dto.getEntryTime() > 0) {
            entity.setEntryTime(new Date(dto.getEntryTime()));
        }

        return entity;
    }
}
