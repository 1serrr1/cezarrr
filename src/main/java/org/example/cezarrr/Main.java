package org.example.cezarrr;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    private TextField keyField;
    private Label fileLabel;
    private File selectedFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Шифр Цезаря");

        fileLabel = new Label("Файл не выбран");

        keyField = new TextField();
        keyField.setPromptText("Введите ключ (число)");

        Button chooseFileButton = new Button("Выберите файл");
        chooseFileButton.setOnAction(e -> chooseFile(primaryStage));

        Button encryptButton = new Button("Зашифровать");
        encryptButton.setOnAction(e -> encryptFile());

        Button decryptButton = new Button("Расшифровать");
        decryptButton.setOnAction(e -> decryptFile());

        Button bruteForceButton = new Button("Brute Force");
        bruteForceButton.setOnAction(e -> bruteForceDecrypt());

        // Основная панель
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(
                fileLabel,
                chooseFileButton,
                new Label("Введите ключ:"),
                keyField,
                encryptButton,
                decryptButton,
                bruteForceButton
        );

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите текстовый файл");
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            fileLabel.setText("Выбранный файл: " + selectedFile.getName());
        }
    }

    private void encryptFile() {
        if (selectedFile == null || keyField.getText().isEmpty()) {
            showAlert("Ошибка!", "Файл или ключ не выбраны");
            return;
        }

        try {
            int key = Integer.parseInt(keyField.getText());
            String content = readFile(selectedFile.getAbsolutePath());
            String encryptedText = CaesarCipher.encrypt(content, key);
            saveFile(encryptedText, "encrypted");
        } catch (NumberFormatException e) {
            showAlert("Ошибка!", "Ключ должен быть целым числом");
        } catch (IOException e) {
            showAlert("Ошибка!", "Ошибка чтения файла: " + e.getMessage());
        }
    }

    private void decryptFile() {
        if (selectedFile == null || keyField.getText().isEmpty()) {
            showAlert("Ошибка!", "Файл или ключ не выбраны");
            return;
        }

        try {
            int key = Integer.parseInt(keyField.getText());
            String content = readFile(selectedFile.getAbsolutePath());
            String decryptedText = CaesarCipher.decrypt(content, key);
            saveFile(decryptedText, "decrypted");
        } catch (NumberFormatException e) {
            showAlert("Ошибка!", "Ключ должен быть целым числом");
        } catch (IOException e) {
            showAlert("Ошибка!", "Ошибка чтения файла: " + e.getMessage());
        }
    }

    private void bruteForceDecrypt() {
        if (selectedFile == null) {
            showAlert("Ошибка!", "Файл не выбран");
            return;
        }

        try {
            String content = readFile(selectedFile.getAbsolutePath());
            String bruteForceResults = CaesarCipher.bruteForceDecrypt(content);
            saveFile(bruteForceResults, "bruteforce_results");
        } catch (IOException e) {
            showAlert("Ошибка!", "Ошибка чтения файла: " + e.getMessage());
        }
    }

    private String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private void saveFile(String content, String suffix) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл");
        fileChooser.setInitialFileName(suffix + "_output.txt");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                Files.write(Paths.get(file.getAbsolutePath()), content.getBytes());
                showAlert("Успех!", "Файл сохранен: " + file.getName());
            } catch (IOException e) {
                showAlert("Ошибка!", "Ошибка сохранения файла: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    static class CaesarCipher {
        private static final String ALPHABET = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        private static final int ALPHABET_SIZE = ALPHABET.length();

        public static String encrypt(String text, int key) {
            key = key % ALPHABET_SIZE;
            StringBuilder encryptedText = new StringBuilder();

            for (char character : text.toUpperCase().toCharArray()) {
                int index = ALPHABET.indexOf(character);
                if (index != -1) {
                    int newIndex = (index + key) % ALPHABET_SIZE;
                    encryptedText.append(ALPHABET.charAt(newIndex));
                } else {
                    encryptedText.append(character);
                }
            }
            return encryptedText.toString();
        }

        public static String decrypt(String text, int key) {
            return encrypt(text, ALPHABET_SIZE - (key % ALPHABET_SIZE));
        }

        public static String bruteForceDecrypt(String text) {
            StringBuilder results = new StringBuilder();
            for (int key = 1; key < ALPHABET_SIZE; key++) {
                String decryptedText = decrypt(text, key);
                results.append("Key: ").append(key).append("\n");
                results.append(decryptedText).append("\n\n");
            }
            return results.toString();
        }
    }
}