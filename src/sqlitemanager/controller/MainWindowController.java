
package sqlitemanager.controller;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlitemanager.localStorage;
import sqlitemanager.model.Database;
import sqlitemanager.model.WindowController;

public class MainWindowController implements Initializable, WindowController {
    
    @FXML
    private Accordion dbsListAccordion;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private TextArea commandTextArea;
    @FXML
    private TextArea databaseTextArea;
    
    
    public void updateUI() {
        dbsListAccordion.getPanes().clear();
        
        for (Database db : localStorage.getDatabasesList()) {
            dbsListAccordion.getPanes().add(new TitledPane(db.getName(), null));
        }
        
        for (TitledPane pane : dbsListAccordion.getPanes()) {
            pane.setOnMouseClicked(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    openTab(event);
                }
            });
        }
    }
    
    private Stage stage;
    private String currentDbName = "";

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        localStorage.importSettings();
        databaseTextArea = null;
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                String tabName = newValue.getText();
                currentDbName = tabName;
                databaseTextArea = (TextArea)newValue.getContent();
            }
        });
        
    }
    
    private void openDialogWindow(String url, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(sqlitemanager.SQLiteManager.class.getResource(url));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            
            WindowController controller = loader.getController();
            controller.setStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setFullScreenExitHint("");
            stage.setTitle(title);
            stage.setScene(scene);  
            stage.showAndWait();
            
            updateUI();
            
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    private File openFileChooser(String title, ExtensionFilter ...filters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(filters);
        fileChooser.getExtensionFilters().add(
            new ExtensionFilter("All Files", "*.*"));
        
        File selectedFile = fileChooser.showOpenDialog(this.stage);
        return selectedFile;
    }
    
    private void openTab(Event event) {
        String tabName = ((TitledPane)event.getSource()).getText();
        currentDbName = tabName;
        if(((MouseEvent)event).getButton() != MouseButton.SECONDARY) {
            databaseTextArea = new TextArea();
            Tab tab = new Tab(tabName, databaseTextArea);
            mainTabPane.getTabs().add(tab);
        }
    }


    ////////////////////////////// FXML METHODS //////////////////////////////
    
    
    
    /// FILE ///
    
    @FXML
    public void newFileOnBtnClick(ActionEvent actionEvent) {
        String url = "ui/NewFileDialog.fxml";
        String title = "New File";
        openDialogWindow(url, title);
    }
    
    @FXML
    public void openFileOnBtnClick(ActionEvent actionEvent) {
        String title = "Open Database";
        ExtensionFilter filter = new ExtensionFilter("Database files", "*.db");
        File file = null;
        if ((file = openFileChooser(title, filter)) != null) {
            Database database = new Database(file);
            localStorage.putDatabase(database);
            updateUI();
            // TODO
        }
    }
    
    @FXML
    public void importDbOnBtnClick(ActionEvent actionEvent) {
        String title = "Import Database";
        ExtensionFilter filter = new ExtensionFilter("SQL files", "*.sql");
        File file = null;
        if ((file = openFileChooser(title, filter)) != null) {
            // TODO
        }
    }
    
    @FXML
    public void exportDbOnBtnClick(ActionEvent actionEvent) {
        // TODO
    }
    
    @FXML
    public void saveDbOnBtnClick(ActionEvent actionEvent) {
        // TODO
    }
    
    @FXML
    public void closeOnBtnClick(ActionEvent actionEvent) {
        Database db = localStorage.getDatabaseByName(currentDbName);
        localStorage.removeDatabase(db);
        updateUI();
    }
    
    @FXML
    public void exitOnBtnClick(ActionEvent actionEvent) {
        System.exit(0);
    }
    
    /// //// ///
    
    
    
    /// Service ///
    
    @FXML
    public void openCommandLine(ActionEvent actionEvent) {
        try {
            Runtime.getRuntime().exec(new String[] {"cmd", "/K", "Start"});
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    @FXML
    public void importSettings(ActionEvent actionEvent) {
        String title = "Import Settings";
        ExtensionFilter filter = new ExtensionFilter("JSON files", "*.json");
        File file = null;
        if ((file = openFileChooser(title, filter)) != null) {
            localStorage.importSettings(file);
        }
    }
    
    @FXML
    public void exportSettings(ActionEvent actionEvent) {
        localStorage.exportSettings();
    }
    
    @FXML
    public void settingsShowPageAction(ActionEvent actionEvent) {
        String url = "ui/SettingsPage.fxml";
        String title = "Settings";
        openDialogWindow(url, title);
    }
    
    /// /////// ///
    
    
    
    /// Help ///
    
    @FXML
    public void aboutShowPageAction(ActionEvent actionEvent) {
        String url = "ui/AboutPage.fxml";
        String title = "About";
        openDialogWindow(url, title);
    }
    
    @FXML
    public void contactShowPageAction(ActionEvent actionEvent) {
        String url = "ui/ContactPage.fxml";
        String title = "Contact";
        openDialogWindow(url, title);
    }
    
    @FXML
    public void donateShowPageAction(ActionEvent actionEvent) {
        String url = "ui/DonatePage.fxml";
        String title = "Donate";
        openDialogWindow(url, title);
    }
    
    /// //// ///
    
    
    
    /// Table Action Menu ///
    
    @FXML
    public void createTableAction(ActionEvent actionEvent) {
        commandTextArea.setText("CREATE TABLE IF NOT EXISTS `...` (`id` INT AUTO_INCREMENT PRIMARY KEY, ...)" );
    }
    
    @FXML
    public void showTableDataAction(ActionEvent actionEvent) {
         Database db = localStorage.getDatabaseByName(currentDbName);
         String data = db.getTablesData();
         Tab tab = new Tab("table_data(" + currentDbName +")", new TextArea(data));
         mainTabPane.getTabs().add(tab);
    }
    
    @FXML
    public void closeTableAction(ActionEvent actionEvent) {
        closeOnBtnClick(actionEvent);
    }
    
    /// ///// ////// //// ///
    
    
    
    /// UI Buttons ///
    
    @FXML
    public void refreshBtnOnClick(ActionEvent actionEvent) {
        updateUI();
    }
    
    @FXML
    public void runBtnOnClick(ActionEvent actionEvent) {
        Database db = localStorage.getDatabaseByName(currentDbName);
        String command = commandTextArea.getText();
        db.executeCommand(command);
        databaseTextArea.setText(databaseTextArea.getText() + "Result");
    }
    
    /// // /////// ///
    
}
