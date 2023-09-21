import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Window1Controller extends Window2Controller {

    @FXML
    TextField name, surname;
    @FXML
    Button reserve, apskatit_datorus;
    @FXML
    Circle name_circle, surname_circle;
    @FXML
    Label label_reservation;
    @FXML
    ComboBox<String> datoru_dropdown;
    @FXML
    DatePicker dateOfBirth;

    private static final String NAMES_REGEX = "^[a-zA-Z]+$";

    public void initialize() {
        addTextChangeListener(name, name_circle, NAMES_REGEX, true);
        addTextChangeListener(surname, surname_circle, NAMES_REGEX, true);

        // Initialize the ComboBox
        refreshComputerList();
    }

    private List<String> readTakenComputerNumbersFromFile() {
        List<String> takenComputerNumbers = new ArrayList<>();
        String filename = "src/users.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String computerNumber = parts[parts.length - 1];
                    takenComputerNumbers.add(computerNumber);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return takenComputerNumbers;
    }

    private void refreshComboBox() {
        datoru_dropdown.getItems().clear();

        refreshComputerList();
    }

    private void refreshComputerList() {
        List<String> takenComputerNumbers = readTakenComputerNumbersFromFile();
        List<String> availableComputers = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            String computerNumber = String.valueOf(i);
            if (!takenComputerNumbers.contains(computerNumber)) {
                availableComputers.add(computerNumber);
            }
        }

        datoru_dropdown.getItems().setAll(availableComputers);
    }

    @FXML
    private void onComputerSelectionChanged() {
        String selectedComputer = datoru_dropdown.getValue();
        reserve.setDisable(selectedComputer == null);
    }

    private void addTextChangeListener(TextField textField, Circle circle, String regex, boolean checkUppercase) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                boolean isValidFormat = isValidFormat(newValue, regex);
                boolean startsWithUppercase = !checkUppercase || (newValue.length() > 0 && Character.isUpperCase(newValue.charAt(0)));

                Platform.runLater(() -> {
                    if (!startsWithUppercase && checkUppercase) {
                        circle.setFill(Color.RED);
                    } else {
                        circle.setFill(isValidFormat ? Color.GREEN : Color.RED);
                    }
                });
            }
        });
    }

    @FXML
    private void save() {
        String vards = name.getText();
        String uzvards = surname.getText();

        if (!isValidFormat(vards, NAMES_REGEX) || !Character.isUpperCase(vards.charAt(0))) {
            System.out.println("Nepareiz varda formats, jasakas ar lielo burtu!");
            return;
        }

        if (!isValidFormat(uzvards, NAMES_REGEX) || !Character.isUpperCase(uzvards.charAt(0))) {
            System.out.println("Nepareiz uzvarda formats, jasakas ar lielo burtu!");
            return;
        }

        LocalDate dob = dateOfBirth.getValue();
        boolean isAgeValid = isAgeValid(dob, 8, 100);

        String dropdown = datoru_dropdown.getValue();

        name_circle.setFill(Color.GREEN);
        surname_circle.setFill(Color.GREEN);
        dateOfBirth.setStyle(isAgeValid ? "" : "-fx-border-color: red;");

        if (isAgeValid) {
            String filename = "src/users.txt";

            try {
                if (isComputerAvailable(filename, dropdown)) {
                    FileWriter myFileWriter = new FileWriter(filename, true);
                    BufferedWriter myWriter = new BufferedWriter(myFileWriter);
                    myWriter.write(vards + " " + uzvards + " " + dob + " " + dropdown);
                    myWriter.newLine();
                    myWriter.close();
                    label_reservation.setVisible(true);
                    label_reservation.setTextFill(Color.GREEN);
                    label_reservation.setText("Dators rezervēts!");

                    refreshComputerList();
                } else {
                    label_reservation.setVisible(true);
                    label_reservation.setTextFill(Color.RED);
                    label_reservation.setText("Dators jau rezervēts!");
                }
            } catch (IOException e) {
                label_reservation.setVisible(true);
                label_reservation.setTextFill(Color.RED);
                label_reservation.setText("Dators netika rezervēts!");
                e.printStackTrace();
            }
        }
    }

    private boolean isComputerAvailable(String filename, String computerNumber) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String reservedComputerNumber = parts[parts.length - 1];
                    if (reservedComputerNumber.equals(computerNumber)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @FXML
    private void apskatit_pc(ActionEvent event) throws IOException {
        Parent window2 = FXMLLoader.load(getClass().getResource("window2.fxml"));
        Scene scene2 = new Scene(window2);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene2);
        window.show();

        refreshComboBox();
    }

    private boolean isValidFormat(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    private boolean isAgeValid(LocalDate dob, int minimumAge, int maximumAge) {
        if (dob == null) {
            return false;
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate minimumValidDate = currentDate.minusYears(maximumAge);
        LocalDate maximumValidDate = currentDate.minusYears(minimumAge);
        return !dob.isBefore(minimumValidDate) && !dob.isAfter(maximumValidDate);
    }
}