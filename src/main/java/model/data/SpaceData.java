/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.entities.Space;

/**
 *
 * @author jimen
 */
public class SpaceData {

    private ArrayList<Space> spaces;
    private static final String FILE_PATH = "data/spaces.json";

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public SpaceData() {

        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();
        }

        spaces = loadFromFile();
    }

    public String insertSpace(Space space) {
        String message = "Espacio inválido";

        if (findSpaceById(space.getId()) != null) {
            message = "Ya existe un espacio con ese ID";
        }

        spaces.add(space);
        saveToFile();
        message = "Espacio registrado correctamente";
        return message;
    }

    public ArrayList<Space> getAllSpaces() {
        return new ArrayList<>(spaces);
    }

    public Space findSpaceById(int id) {

        for (Space s : spaces) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public boolean updateSpace(Space updated) {

        Space existing = findSpaceById(updated.getId());
        boolean results = false;

        if (existing != null) {
            existing.setDisabilityAdaptation(updated.isDisabilityAdaptation());
            existing.setSpaceTaken(updated.isSpaceTaken());
            existing.setVehicleType(updated.getVehicleType());
            saveToFile();
            results = true;
        }
        return results;
    }

    public boolean deleteSpace(int id) {

        Space space = findSpaceById(id);
        boolean results = false;

        if (space != null) {
            spaces.remove(space);
            saveToFile();
            results = true;
        }
        return results;
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(spaces, writer);
        } catch (IOException e) {
            System.out.println("Error guardando espacios");
        }
    }

    private ArrayList<Space> loadFromFile() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Space>>() {
            }.getType();
            ArrayList<Space> data = gson.fromJson(reader, listType);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

}
