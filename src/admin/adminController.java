package admin;

import com.jfoenix.controls.JFXButton;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class adminController implements Initializable {
    private dbConnection db;
    private ObservableList<StudentData> data;

    @FXML
    private TableView<StudentData> studentTable;

    @FXML
    private TableColumn<StudentData, String> idcolum;

    @FXML
    private TableColumn<StudentData, String> firstnamecolum;

    @FXML
    private TableColumn<StudentData, String> lastnamecolum;

    @FXML
    private TableColumn<StudentData, String> emailcolum;

    @FXML
    private TableColumn<StudentData, String> dobcolum;

    @FXML
    private JFXButton searchText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.db = new dbConnection();
    }//initialize
    @FXML
    private void loadStudentData(ActionEvent event){
        try {
            Connection conn = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();
            //sql
            String sql = "select * from student";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                this.data.add(new StudentData(rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)));
            }//while
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //put data to tableview
        this.idcolum.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("id"));
        this.firstnamecolum.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("firstName"));
        this.lastnamecolum.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("lastName"));
        this.emailcolum.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("email"));
        this.dobcolum.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("DOB"));
        this.studentTable.setItems(null);
        this.studentTable.setItems(this.data);
//Filter Data in TableView
        FilteredList<StudentData> filteredData =
                new FilteredList<>(data, e -> true);
        searchText.setOnKeyReleased(e -> {
            searchText.textProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        filteredData.setPredicate(StudentData -> {
                            if (newValue == null || newValue.isEmpty()) {
                                return true;
                            }
                            String lowerCaseFilter = newValue.toLowerCase();
                            if (StudentData.getID().contains(newValue)) {
                                return true;
                            } else if
                                    (StudentData.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                                return true;
                            } else if
                                    (StudentData.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                                return true;
                            }
                            return false;
                        });
                    });
            SortedList<StudentData> sortedData =
                    new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(
                    studentTable.comparatorProperty());
            studentTable.setItems(sortedData);

        });

//add Data
        @FXML
        private void addStudent(ActionEvent event) {
            String sqlInsert = "insert into user(id,firstName,lastName,email,DOB) values (?,?,?,?,?)";

            try {
                Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlInsert);
                stmt.setString(1, this.ids.getText());
                stmt.setString(2, this.fname.getText());
                stmt.setString(3, this.lname.getText());
                stmt.setString(4, this.emails.getText());
                stmt.setString(5, this.dobs.getEditor().getText());

                stmt.execute();
                stmt.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadStudentData(new ActionEvent());
        }


//clear form

        @FXML
        private  void clearFields(ActionEvent event){
            this.ids.setText("");
            this.fname.setText("");
            this.lname.setText("");
            this.emails.setText("");
            this.dobs.setValue(null);
        }

//switch scene to login.fxml

        @FXML
        private void logOut(ActionEvent event) throws Exception {
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage primaryStage = new Stage();
            Main m = new Main();
            m.start(primaryStage);

        }

//Delete Data
        @FXML
        private void deleteRowFromTable(ActionEvent event)  {
            // studenttable.getItems().removeAll(studenttable.getSelectionModel().getSelectedItem());
            StudentData std = studenttable.getSelectionModel().getSelectedItem();
            JOptionPane.showConfirmDialog(null, "Do you want to delete student ID: " + std.getID() + "");
            if (std != null) {
                String sql = "delete from user where id = ?";
                try {
                    Connection conn = dbConnection.getConnection();
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1, std.getID());
                    statement.executeUpdate();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {
                System.exit(1);
            }
            loadStudentData(new ActionEvent());
        }

//Edit Data
        @FXML
        private void editStudent(ActionEvent event){
            StudentData std = studenttable.getSelectionModel().getSelectedItem();
            if (std != null) {
                ids.setText(std.getID());
                //set textfiled to read only
                ids.setDisable(true);
                fname.setText(std.getFirstName());
                lname.setText(std.getLastName());
                emails.setText(std.getEmail());
                dobs.getEditor().setText(std.getDOB());
            } else {
                System.exit(1);
            }


        }

//Save Data
        @FXML
        private void saveStudent(ActionEvent event){
            StudentData std = studenttable.getSelectionModel().getSelectedItem();
            JOptionPane.showConfirmDialog(null, "Do you want to update student ID: " + std.getID() + "");
            String sqlEdit = "update user set  firstName =?, lastName =?, email =?, DOB =? where id = ?";
            try {
                Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlEdit);

                stmt.setString(1, this.fname.getText());
                stmt.setString(2, this.lname.getText());
                stmt.setString(3, this.emails.getText());
                stmt.setString(4, this.dobs.getEditor().getText());
                stmt.setString(5, std.getID());


                stmt.executeUpdate();
                stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadStudentData(new ActionEvent());

        }







    }//loadStudentData

}//class