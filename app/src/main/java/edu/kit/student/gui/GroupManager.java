package edu.kit.student.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.util.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

class GroupManager {

	final private GraphViewGraphFactory factory;

    private List<Integer> groupIdsBacking;
    private ObservableList<Integer> groupIds;
    final private Map<Integer, VertexGroup> groupMap;
    final private List<Integer> removedGroups;

	GroupManager(GraphViewGraphFactory factory) {
		this.factory = factory;
		groupIdsBacking = new LinkedList<>();
		groupIds = FXCollections.observableList(groupIdsBacking);
		groupMap = new HashMap<>();
		removedGroups = new LinkedList<>();
	}
	
	boolean openAddGroupDialog(Set<ViewableVertex> vertices) {
		TextInputDialog dialog = new TextInputDialog(LanguageManager.getInstance().get("wind_group_new_default"));
    	dialog.setTitle(LanguageManager.getInstance().get("wind_group_new_title"));
    	dialog.setHeaderText(null);
    	dialog.setGraphic(null);
    	dialog.setContentText(LanguageManager.getInstance().get("wind_group_new_text"));
    	Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    	stage.getIcons().add(new Image("gans_icon.png"));
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    		VertexGroup group = new VertexGroup(factory, result.get(), vertices);
    		groupIds.add(group.getId());
    		groupMap.put(group.getId(), group);
    		return true;
    	}
    	return false;
    }

	void openGroupDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		ListView<Integer> groupList = new ListView<>();
		groupList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		groupList.setItems(groupIds);
		groupList.setCellFactory(list -> new GroupListCell());
		
		Button upButton = new Button(LanguageManager.getInstance().get("wind_group_up"));
		upButton.setDisable(true);
		upButton.setOnAction(event -> {
            Integer groupId = groupList.getSelectionModel().getSelectedItem();
            int currentPos = groupIds.indexOf(groupId);
            groupIds.remove(groupId);
            groupIds.add(currentPos - 1, groupId);
            groupList.getSelectionModel().select(groupId);
        });
		
		Button downButton = new Button(LanguageManager.getInstance().get("wind_group_down"));
		downButton.setDisable(true);
		downButton.setOnAction(event -> {
            Integer groupId = groupList.getSelectionModel().getSelectedItem();
            int currentPos = groupIds.indexOf(groupId);
            groupIds.remove(groupId);
            groupIds.add(currentPos + 1, groupId);
            groupList.getSelectionModel().select(groupId);
        });
		
		Button removeButton = new Button(LanguageManager.getInstance().get("wind_group_remove"));
		removeButton.setDisable(true);
		removeButton.setOnAction(event -> {
            Integer groupId = groupList.getSelectionModel().getSelectedItem();
            groupList.getSelectionModel().clearSelection();
            groupIds.remove(groupId);
            removedGroups.add(groupId);
            groupList.refresh();
        });
		
		groupList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) {
                upButton.setDisable(true);
                downButton.setDisable(true);
                removeButton.setDisable(true);
            } else {
                int index = groupIds.indexOf(newValue);
                upButton.setDisable(index == 0);
                downButton.setDisable(index == groupIds.size() - 1);
                removeButton.setDisable(false);
            }
        });
		
		HBox buttonBox = new HBox(upButton, downButton, removeButton);
		buttonBox.setSpacing(3);
		VBox root = new VBox(groupList, buttonBox);
		
		dialog.getDialogPane().setContent(root);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.APPLY);
		dialog.setTitle(LanguageManager.getInstance().get("wind_group_title"));
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    	stage.getIcons().add(new Image("gans_icon.png"));

		LinkedList<Integer> groupIdsAbortBackup = new LinkedList<>(this.groupIds);
		Map<Integer,Color> groupColorAbortBackup = new HashMap<>();
		for(Integer id : groupMap.keySet()) {
			groupColorAbortBackup.put(id,groupMap.get(id).getColor());
		}
		final Button btApply = (Button) dialog.getDialogPane().lookupButton(ButtonType.APPLY);
        btApply.addEventFilter(ActionEvent.ACTION, event -> {
		 	removedGroups.forEach(groupId -> groupMap.remove(groupId).uncolorVertices());
			removedGroups.clear();
			//TODO: maybe check for made changes and only apply them.
			applyGroups();
            event.consume();
         });
		Optional<ButtonType> result = dialog.showAndWait();
		if(result.isPresent()) {
			if(result.get() == ButtonType.OK) {
				removedGroups.forEach(groupId -> groupMap.remove(groupId).uncolorVertices());
				removedGroups.clear();
				//TODO: maybe check for made changes and only apply them.
				applyGroups();
			}
		} else {
			removedGroups.clear();
			this.groupIdsBacking = new LinkedList<>(groupIdsAbortBackup);
			this.groupIds = FXCollections.observableList(groupIdsBacking);
			for(Integer id : groupColorAbortBackup.keySet()) {
				groupMap.get(id).setColor(groupColorAbortBackup.get(id));
			}
		}
	}
	
	void applyGroups() {
		//TODO: inefficient, could map over all groups and vertices before coloring
		for(int i = groupIds.size() - 1; i > -1; i--) {
			groupMap.get(groupIds.get(i)).colorVertices();
		}
	}
	
	private class GroupListCell extends ListCell<Integer> {
		@Override
		public void updateItem(Integer item, boolean empty) {
			super.updateItem(item, empty);
			VertexGroup group = groupMap.get(item);
			if(!empty && group != null) {
				Region spacer = new Region();
				HBox box = new HBox(group.getLabel(), spacer, group.getPicker());
				HBox.setHgrow(spacer, Priority.ALWAYS);
				box.setSpacing(10);
				setGraphic(box);
			} else {
				setGraphic(null);
			}
		}
	}
}
