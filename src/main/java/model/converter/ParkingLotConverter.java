package model.converter;

import model.dto.ParkingLotDTO;
import model.dto.SpaceDTO;
import model.entities.ParkingLot;
import model.entities.Space;
import java.util.ArrayList;
import java.util.List;

public class ParkingLotConverter {

    public static ParkingLotDTO toDTO(ParkingLot entity) {
        if (entity == null) {
            return null;
        }

        ParkingLotDTO dto = new ParkingLotDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setNumberOfSpaces(entity.getNumberOfSpaces());

        List<SpaceDTO> spaceDTOs = new ArrayList<>();
        if (entity.getSpaces() != null) {
            for (Space space : entity.getSpaces()) {
                spaceDTOs.add(SpaceConverter.toDTO(space));
            }
        }
        dto.setSpaces(spaceDTOs);

        return dto;
    }

    public static ParkingLot fromDTO(ParkingLotDTO dto) {
        if (dto == null) {
            return null;
        }

        ParkingLot entity = new ParkingLot();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setNumberOfSpaces(dto.getNumberOfSpaces());

        if (dto.getSpaces() != null) {
            Space[] spaces = new Space[dto.getSpaces().size()];
            for (int i = 0; i < dto.getSpaces().size(); i++) {
                Space space = SpaceConverter.fromDTO(dto.getSpaces().get(i));
                space.setParkingLot(entity);
                spaces[i] = space;
            }
            entity.setSpaces(spaces);
        }

        return entity;
    }

    public static List<ParkingLotDTO> toDTOList(List<ParkingLot> entities) {
        List<ParkingLotDTO> dtos = new ArrayList<>();
        for (ParkingLot entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    public static List<ParkingLot> fromDTOList(List<ParkingLotDTO> dtos) {
        List<ParkingLot> entities = new ArrayList<>();
        for (ParkingLotDTO dto : dtos) {
            entities.add(fromDTO(dto));
        }
        return entities;
    }
}
