package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	
	private SellerService service;
	
    @FXML
	private TableView<Seller> tableViewSellerList;
    @FXML
	private TableColumn<Seller, Integer> tableColumnId;
    @FXML
	private TableColumn<Seller, String> tableColumnName;
    @FXML
	private TableColumn<Seller, String> tableColumnEmail;
    @FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
    @FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
    
    @FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
    @FXML
   	private TableColumn<Seller, Seller> tableColumnREMOVE;
    @FXML
	private Button btNew;
    
    private ObservableList<Seller> obsList;
    
    public void onBtNewAction(ActionEvent event) {
    	Stage parentStage = Utils.currentStage(event);
    	Seller obj = new Seller();
    	createSellerForm(obj, "/gui/SellerForm.fxml", parentStage);
    }

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		Stage stage  = (Stage) Main.getScene().getWindow();
		tableViewSellerList.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("service was null :/");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSellerList.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
     private void createSellerForm(Seller obj, String absoluteName, Stage parentStage) {
    	 FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
    	 try {
			Pane pane = loader.load();
			SellerFormController controller = loader.getController();
			controller.SetSeller(obj);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			controller.subcribeDataChangeListener(this);
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlerts("IO EXCEPTION", "Error loading view", e.getMessage(), AlertType.ERROR);
			
		}
     }

	@Override
	public void onDataChanged() {
		updateTableView();
		
	}
	private void initEditButtons() { 
		 tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		 tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() { 
		  private final Button button = new Button("editar"); 
		 
		  @Override 
		  protected void updateItem(Seller obj, boolean empty) { 
		   super.updateItem(obj, empty); 
		 
		   if (obj == null) { 
		    setGraphic(null); 
		    return; 
		   } 
		 
		   setGraphic(button); 
		   button.setOnAction( 
		   event -> createSellerForm( 
		    obj, "/gui/SellerForm.fxml",Utils.currentStage(event))); 
		  } 
		 }); 
		} 
	
	private void initRemoveButtons() { 
		 tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		 tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() { 
		        private final Button button = new Button("remover"); 
		 
		        @Override 
		        protected void updateItem(Seller obj, boolean empty) { 
		            super.updateItem(obj, empty); 
		 
		            if (obj == null) { 
		                setGraphic(null); 
		                return; 
		            } 
		 
		            setGraphic(button); 
		            button.setOnAction(event -> removeEntity(obj)); 
		        } 
		    }); 
		}

	private void removeEntity(Seller obj) {
	Optional<ButtonType> result =	Alerts.showConfirmation("Confirmação", "Tem certeza que deseja remover?");
		if(result.get()== ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("service was null :/");
			}
			try {
			service.remove(obj);
			updateTableView();
		}catch(DbIntegrityException e) {
			Alerts.showAlerts("erro removing seller", null, e.getMessage(), AlertType.ERROR);
		}
			}
	} 
}
