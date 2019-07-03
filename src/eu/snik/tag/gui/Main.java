package eu.snik.tag.gui;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import eu.snik.tag.Clazz;
import eu.snik.tag.Extractor;
import eu.snik.tag.Relation;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class Main extends Application
{
	final TextArea rdfText = new TextArea("Ihr extrahierter Text");
	final TextArea docxPane = new TextArea("Hier kommt das DOCX hin");
	final Pane textPane = new HBox(); 

	final ObservableList<Clazz> classes = FXCollections.observableArrayList();

	final ClassTableView tableView = new ClassTableView(classes, this::update);

	Stage stage;

	public void update()
	{
		rdfText.setText(classes.toString());
		tableView.refresh();
//		relationPane.setClasses(classes);
	}

	@SneakyThrows
	void openDocx(File file)
	{
		classes.clear();
		classes.addAll(Extractor.extract(file));
		update();
		tableView.getItems().clear();
		tableView.getItems().addAll(classes);
	}


	class UnclosableTab extends Tab
	{
		UnclosableTab(String text, Node content)
		{
			super(text,content);
			setClosable(false);			
		}
	}
	

	@Override
	public void start(Stage stage)
	{		
		this.stage=stage;	
		stage.setTitle("SNIK Tag");

		var pane = new VBox();
		{
			pane.setAlignment(Pos.TOP_CENTER);
			Scene scene = new Scene(pane, 1600, 1000);
			scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
			stage.setScene(scene);
			stage.setMaximized(true);
			stage.show();
		}		
		pane.getChildren().add(MainMenuBar.create(this));

		rdfText.setMinSize(300, 500);			
		textPane.getChildren().addAll(docxPane,new RelationPane(classes,this::update));
		
		{
			TabPane tabPane = new TabPane();
			tabPane.setTabDragPolicy(TabDragPolicy.REORDER);			

			tabPane.getTabs().addAll(
					new UnclosableTab("Text", textPane),
					new UnclosableTab("Klassen", tableView),
					new UnclosableTab("RDF", rdfText),
					new UnclosableTab("Verbindungen", new RelationPane(classes,this::update)));
			
			pane.getChildren().add(tabPane);
		}
		openDocx(new File("benchmark/input.docx"));
	}

	public static void main(String[] args) {
		launch();		
	}

}