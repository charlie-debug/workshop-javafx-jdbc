package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.image.ImageView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	private Menu menuRegister;
	@FXML
	private Menu menuAbout;
		
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml",(DepartmentListController controller)->{
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml",(SellerListController controller)->{
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml",(x)->{});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		menuImgs();
		
	}

	private <T>void loadView(String nomeAbsoluto, Consumer<T> initializeAction) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
		try {
			VBox newVbox = loader.load();
			Scene mainScene = Main.getScene();
			VBox mainVbox =(VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVbox.getChildren().get(0);
			mainVbox.getChildren().clear();
			mainVbox.getChildren().add(mainMenu);
			mainVbox.getChildren().addAll(newVbox.getChildren());
			T controller = loader.getController();
			initializeAction.accept(controller);
			
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlerts("IO exception", "Erro ao carregar a tela", e.getMessage(), AlertType.ERROR);
			
		}
	}
	
	public void menuImgs() {
		ImageView imageRegister = new ImageView("/img/register.png");
		ImageView imageAbout = new ImageView("/img/about.png");
		ImageView imageDepartment = new ImageView("/img/department.png");
		ImageView imageSeller = new ImageView("/img/seller.png");
		ImageView imageInfo = new ImageView("/img/info.png");
		menuRegister.setGraphic(imageRegister);
	    menuAbout.setGraphic(imageAbout);
	    menuItemDepartment.setGraphic(imageDepartment);
	    menuItemSeller.setGraphic(imageSeller);
	    menuItemAbout.setGraphic(imageInfo);
	    imageRegister.setFitHeight(30);
	    imageRegister.setFitWidth(30);
	    imageAbout.setFitHeight(30);
	    imageAbout.setFitWidth(30);
	    imageDepartment.setFitHeight(30);
	    imageDepartment.setFitWidth(30);
	    imageSeller.setFitHeight(30);
	    imageSeller.setFitWidth(30);
	    imageInfo.setFitHeight(30);
	    imageInfo.setFitWidth(30);
		
	}
	
}
