package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.mysql.cj.util.Util;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	private Seller entity;
	private SellerService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;

	
	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	@FXML
	private Label lblErrorName;
	@FXML
	private Label lblErrorEmail;
	@FXML
	private Label lblErrorBirthDate;
	@FXML
	private Label lblErrorBaseSalary;

	@FXML
	public void onbtnSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("entity was null :/");
		}
		if (service == null) {
			throw new IllegalStateException("service was null :/");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifySubcribeDataChangeListener();
			Utils.currentStage(event).close();
		}catch(ValidationException e) {
			setMessageError(e.getError());
		}
		catch (DbException e) {
			Alerts.showAlerts("error saving seller", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifySubcribeDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "campo obrigatório!");
		}
		if (exception.getError().size() > 0) {
         throw exception;
		}
		obj.setName(txtName.getText());
		return obj;
	}

	@FXML
	public void onbtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void SetSeller(Seller entity) {
		this.entity = entity;
	}

	public void SetSellerService(SellerService service) {
		this.service = service;
	}

	public void subcribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();

	}

	private void initializeNodes() {

		Constraints.setTextFildInteger(txtId);
		Constraints.setTextFildMaxLength(txtName, 15);
		Constraints.setTextFildMaxLength(txtEmail, 70);
		Constraints.setTextFildDouble(txtBaseSalary);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException(" entity was null :/");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		if(entity.getBirthDate() != null) {
		dpBirthDate.setValue(LocalDate.ofInstant( entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary())  );
	}
	
	public void setMessageError(Map<String, String> errors) {
		Set<String> filds = errors.keySet();
		if(filds.contains("name")) {
			lblErrorName.setText(errors.get("name"));
		}
	}

}
