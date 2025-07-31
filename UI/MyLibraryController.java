package UI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;


public class MyLibraryController {

    @FXML
    private Button AddBooks;

    @FXML
    private TextField Author;

    @FXML
    private Button Delete;

    @FXML
    private Button Edit;

    @FXML
    private TextField ISBN;

    @FXML
    private Button Logout;

    @FXML
    private TextField Search;

    @FXML
    private TableColumn<Book, String> T_Author;

    @FXML
    private TableColumn<Book, String> T_ISBN;

    @FXML
    private TableColumn<Book, String> T_Title;

    @FXML
    private TableView<Book> tableView;

    @FXML
    private TextField Title;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

@FXML
public void initialize() {
    T_ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
    T_Title.setCellValueFactory(new PropertyValueFactory<>("title"));
    T_Author.setCellValueFactory(new PropertyValueFactory<>("author"));


    // Make Title, Author, and ISBN columns wrap text and expand row height
    javafx.util.Callback<TableColumn<Book, String>, javafx.scene.control.TableCell<Book, String>> wrapCellFactory = col -> new javafx.scene.control.TableCell<Book, String>() {
        private final javafx.scene.text.Text text = new javafx.scene.text.Text();
        {
            text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));
            setGraphic(text);
        }
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                text.setText("");
                setPrefHeight(20);
            } else {
                text.setText(item);
                setPrefHeight(text.getLayoutBounds().getHeight() + 20);
            }
        }
    };
    T_Title.setCellFactory(wrapCellFactory);
    T_Author.setCellFactory(wrapCellFactory);
    T_ISBN.setCellFactory(wrapCellFactory);

    loadBooksFromDatabase();
}

@FXML
void ISBN(ActionEvent event) {
    // Handler for ISBN TextField action
}

@FXML
void Author(ActionEvent event) {
    // Handler for Author TextField action
}


private void loadBooksFromDatabase() {
    try (Connection conn = DB.DbConnection.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT isbn, title, author FROM books")) {
        bookList.clear();
        while (rs.next()) {
            bookList.add(new Book(
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author")
            ));
        }
        tableView.setItems(bookList);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    @FXML
    void Title(ActionEvent event) {
        // Handler for Title TextField action
    }

    @FXML
    void Logout(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("login.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) Logout.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Haile's Library Management System");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Edit(ActionEvent event) {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            // Optionally show an error dialog: "No book selected"
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("EditBook.fxml"));
            javafx.scene.Parent root = loader.load();
            UI.EditBookController controller = loader.getController();
            controller.setBook(selectedBook);
            controller.setOnSaveCallback(this::loadBooksFromDatabase);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Edit Book");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Delete(ActionEvent event) {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            // Optionally show an error dialog: "No book selected"
            return;
        }
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (Connection conn = DB.DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, selectedBook.getIsbn());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        loadBooksFromDatabase();
    }

    @FXML
    private void Search(ActionEvent event) {
        String searchText = Search.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            tableView.setItems(bookList);
            return;
        }
        ObservableList<Book> filteredList = FXCollections.observableArrayList();
        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(searchText) ||
                book.getAuthor().toLowerCase().contains(searchText) ||
                book.getIsbn().toLowerCase().contains(searchText)) {
                filteredList.add(book);
            }
        }
        tableView.setItems(filteredList);
    }

    @FXML
    void AddBooks(ActionEvent event) {
        String isbn = ISBN.getText().trim();
        String title = Title.getText().trim();
        String author = Author.getText().trim();

        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty()) {
            // Optionally, show an error dialog here
            return;
        }

        String sql = "INSERT INTO books (isbn, title, author) VALUES (?, ?, ?)";
        try (Connection conn = DB.DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Clear input fields
        ISBN.clear();
        Title.clear();
        Author.clear();

        // Refresh the table
        loadBooksFromDatabase();
    }
}
