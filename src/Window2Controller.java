import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Window2Controller {
    @FXML
    Circle dators1, dators2, dators3, dators4, dators5, dators6, dators7, dators8, dators9, dators10, dators11, dators12;
    @FXML
    Button reservation_button;

    public List<String> readTakenComputersFromFile() {
        String filename = "src/users.txt";
        List<String> takenComputers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 4) {
                    String dropdown = parts[3];
                    takenComputers.add(dropdown);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return takenComputers;
    }

    public void updateComputerStatus() {
        List<String> takenComputers = readTakenComputersFromFile();

        updateCircleColor(dators1, "1", takenComputers);
        updateCircleColor(dators2, "2", takenComputers);
        updateCircleColor(dators3, "3", takenComputers);
        updateCircleColor(dators4, "4", takenComputers);
        updateCircleColor(dators5, "5", takenComputers);
        updateCircleColor(dators6, "6", takenComputers);
        updateCircleColor(dators7, "7", takenComputers);
        updateCircleColor(dators8, "8", takenComputers);
        updateCircleColor(dators9, "9", takenComputers);
        updateCircleColor(dators10, "10", takenComputers);
        updateCircleColor(dators11, "11", takenComputers);
        updateCircleColor(dators12, "12", takenComputers);
    }

    private void updateCircleColor(Circle circle, String computerNumber, List<String> takenComputers) {
        if (takenComputers.contains(computerNumber)) {
            circle.setFill(javafx.scene.paint.Color.RED);
        } else {
            circle.setFill(javafx.scene.paint.Color.GREEN);
        }
    }

    private String getUserInfoForComputer(String computerNumber) {
        String filename = "src/users.txt";
        StringBuilder userInfo = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 4 && parts[3].equals(computerNumber)) {
                    // User found for the computer
                    userInfo.append("Vārds: ").append(parts[0]).append("\n");
                    userInfo.append("Uzvārds: ").append(parts[1]).append("\n");
                    userInfo.append("Dzimšanas datums: ").append(parts[2]).append("\n");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo.toString();
    }

    private void deleteUserForComputer(String computerNumber) {
        String filename = "src/users.txt";
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 4 && parts[3].equals(computerNumber)) {
                    continue;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dators1Clicked() {
        handleComputerClick("1");
    }

    public void dators2Clicked() {
        handleComputerClick("2");
    }

    public void dators3Clicked() {
        handleComputerClick("3");
    }

    public void dators4Clicked() {
        handleComputerClick("4");
    }

    public void dators5Clicked() {
        handleComputerClick("5");
    }

    public void dators6Clicked() {
        handleComputerClick("6");
    }

    public void dators7Clicked() {
        handleComputerClick("7");
    }

    public void dators8Clicked() {
        handleComputerClick("8");
    }

    public void dators9Clicked() {
        handleComputerClick("9");
    }

    public void dators10Clicked() {
        handleComputerClick("10");
    }

    public void dators11Clicked() {
        handleComputerClick("11");
    }

    public void dators12Clicked() {
        handleComputerClick("12");
    }

    private void handleComputerClick(String computerNumber) {
        List<String> takenComputers = readTakenComputersFromFile();

        if (takenComputers.contains(computerNumber)) {
            // Computer is taken, show confirmation alert
            Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Datora rezervēšana");
            confirmationAlert.setHeaderText("Dators " + computerNumber + " ir rezervēts.");
            confirmationAlert.setContentText("Lietotāja informācija:\n" + getUserInfoForComputer(computerNumber));

            ButtonType deleteButton = new ButtonType("Dzēst lietotāju", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Atcelt", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationAlert.getButtonTypes().setAll(deleteButton, cancelButton);

            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == deleteButton) {
                deleteUserForComputer(computerNumber);
                updateComputerStatus();
            }
        }
    }

    @FXML
    private void backToReservation(ActionEvent event) throws IOException {
        Parent window1 = FXMLLoader.load(getClass().getResource("window1.fxml"));
        Scene scene1 = new Scene(window1);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene1);
        window.show();
    }

    @FXML
    public void initialize() {
        updateComputerStatus();
    }
}