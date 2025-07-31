package UI;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class loginController {

    @FXML
    private Button Login;

    @FXML
    private PasswordField Password;

    @FXML
    private TextField Username;
    @FXML
    private Label WrongLogin;
    @FXML
    void Login(ActionEvent event) throws IOException {
        String username = Username.getText();
        String password = Password.getText();
        if (username.isEmpty() || password.isEmpty()) {
            WrongLogin.setText("Enter your data!");
            return;
        }
        if (checkCredentials(username, password)) {
            // Change scene after successful login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyLibrary.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            WrongLogin.setText("Wrong username or password!");
        }
    }

    private boolean checkCredentials(String username, String password) {
        try (Connection conn = DB.DbConnection.connect()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
