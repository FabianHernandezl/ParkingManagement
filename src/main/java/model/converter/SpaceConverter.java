package model.converter;

import model.dto.SpaceDTO;
import model.entities.Space;

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
        // El parkingLot se asigna en ParkingLotConverter

        return entity;
    }
}
