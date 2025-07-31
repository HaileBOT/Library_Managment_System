package UI;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class EditBookController {
    @FXML
    private TextField isbnField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;

    private Book book;
    private Runnable onSaveCallback;

    public void setBook(Book book) {
        this.book = book;
        isbnField.setText(book.getIsbn());
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String newIsbn = isbnField.getText().trim();
        String newTitle = titleField.getText().trim();
        String newAuthor = authorField.getText().trim();
        if (newIsbn.isEmpty() || newTitle.isEmpty() || newAuthor.isEmpty()) {
            // Optionally show error dialog
            return;
        }
        // Update in DB
        try (java.sql.Connection conn = DB.DbConnection.connect();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                "UPDATE books SET isbn=?, title=?, author=? WHERE isbn=?")) {
            stmt.setString(1, newIsbn);
            stmt.setString(2, newTitle);
            stmt.setString(3, newAuthor);
            stmt.setString(4, book.getIsbn());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (onSaveCallback != null) onSaveCallback.run();
        ((Stage) isbnField.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        ((Stage) isbnField.getScene().getWindow()).close();
    }
}
