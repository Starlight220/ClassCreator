package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import jdk.jfr.BooleanFlag;
import logic.LogMap;

import java.io.IOException;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static gui.Constants.*;
import static java.lang.String.*;

public final class Controller {

    static Map<String, String> map = new LogMap();
    @FXML
    public ChoiceBox<String> md_publicityChooser = new ChoiceBox<>();
    @FXML
    public ChoiceBox<String> cd_publicityChooser = new ChoiceBox<>();
    @FXML
    public ToggleGroup cd_abstractGroup = new ToggleGroup();
    @FXML
    public ChoiceBox<String> fd_publicityChooser = new ChoiceBox<>();

    public static int methods = 0, fields = 0;
    @FXML
    private TextField classNameField;
    @FXML
    private Accordion methodAccordion;
    @FXML
    private Accordion fieldAccordion;

    private boolean initClassName = false;
    private static ChoiceBox<String> getSourceChoiceBox(Event ae) {
        return (ChoiceBox<String>) ae.getSource();
    }
    @FXML
    private void initClassNameField(){
        if((classNameField != null) && !initClassName){
            classNameField.focusedProperty()
                .addListener((obs, wasFocused, isNowFocused) -> {
                    if (wasFocused){
                        // text field has lost focus...
                        System.err.println(classNameField.getText());
                        map.put(className, classNameField.getText());
                    }
                });
            initClassName = true;
        }
    }
    @FXML
    private void build(ActionEvent ae) {
        ClassCreatorUI.build(ae);
    }

    @FXML
    private void addAnotherField(ActionEvent ae) throws IOException {
        fields++;
        TitledPane pane = FXMLLoader.load(getClass().getResource("fxmlFiles/fieldPane.fxml"));
        setID(pane, valueOf(fields));
        System.out.println(pane.getId());
        fieldAccordion.getPanes().add(pane);
    }

    @FXML
    private void addAnotherMethod(ActionEvent ae) throws IOException {
        methods++;
        TitledPane pane = FXMLLoader.load(getClass().getResource("fxmlFiles/methodPane.fxml"));
        setID(pane, valueOf(methods));
        System.out.println(pane.getId());
        methodAccordion.getPanes().add(pane);
    }

    private static void setID(ObservableList<? extends Node> list, String id) {
        list.forEach(node -> {
            node.setId(id);
//            System.err.printf("set %s's id as %s\n",node, node.getId());
            if (node instanceof Parent) {
//                System.err.println(node + " is a parent");
                setID((Parent) node, id);
            }
        });
    }

    private static void setID(Parent parent, String id) {
        parent.setId(id);
        setID(parent.getChildrenUnmodifiable(), id);
    }

    @FXML
    private void handleMethodPublicity(ActionEvent ae) {
        ChoiceBox<String> src = (ChoiceBox<String>) ae.getSource();
        map.put(format(methodAccess, getParentID(src)), src.getValue().toUpperCase());
    }

    @FXML
    private void handleClassPublicity(ActionEvent ae) {
        map.put("classAccess", ((ChoiceBox<String>)ae.getSource()).getValue().toUpperCase());
    }

    @FXML
    private void handleFieldPublicity(ActionEvent ae) {
        ChoiceBox<String> src = (ChoiceBox<String>) ae.getSource();
        map.put(format(fieldAccess, getParentID(src)), src.getValue());
    }
    @BooleanFlag
    private boolean initMethodName = false;
    @FXML
    private void handleMethodName(KeyEvent ae) {
        TextField src = (TextField) ae.getSource();
        if(!initMethodName){
            src.focusedProperty()
                    .addListener((obs,wasFocused,isNowFocused)-> {
                        map.put(format(methodName, getParentID(src)),
                                src.getText());
                    });
            initMethodName = true;
        }
    }

    /**
     * A recursive function that finds the pane's ID
     * @param node
     * @return
     */
    int getParentID(Node node){
        return node instanceof TitledPane ? parseInt(node.getId()) : getParentID(node.getParent());
    }

    @FXML
    private void handleFieldModifiers(ActionEvent ae) {
        CheckBox src = (CheckBox) ae.getSource();
        String key = format(fieldModifiers, getParentID(src));
        String insertion = map.getOrDefault(key, "").replace("null","");
        if(src.isSelected()) {
            insertion = insertion.replace("null", "")
                    .contains(src.getText()) ? insertion : (insertion + " " + src.getText());
        }else if(!src.isSelected()){
            insertion = insertion.replace(src.getText(), "");
        }
        map.put(key, insertion.strip());
    }

    @BooleanFlag
    private boolean initFieldName = false;
    @FXML
    private void handleFieldName(KeyEvent ae) {
        TextField src = (TextField) ae.getSource();
        if(!initFieldName){
            src.focusedProperty()
                    .addListener((obs,wasFocused,isNowFocused)-> {
                        map.put(format(fieldName, getParentID(src)),
                                src.getText());
                    });
            initFieldName= true;
        }
    }

    @BooleanFlag
    private boolean initFieldType = false;
    @FXML
    private void handleFieldType(KeyEvent ae) {
        TextField src = (TextField) ae.getSource();
        if(!initFieldType){
            src.focusedProperty()
                    .addListener((obs,wasFocused,isNowFocused)-> {
                        map.put(format(fieldType, getParentID(src)),
                                src.getText());
                    });
            initFieldType = true;
        }
    }

    @BooleanFlag
    private boolean initMethodReturn = false;
    @FXML
    private void handleMethodReturnType(KeyEvent ae) {
        TextField src = (TextField) ae.getSource();
        if(!initMethodReturn){
            src.focusedProperty()
                    .addListener((obs,wasFocused,isNowFocused)-> {
                        map.put(format(methodReturn, getParentID(src)),
                                src.getText());
                    });
            initMethodReturn = true;
        }
    }

    @FXML
    private void handleClassType(ActionEvent ae) {
        ChoiceBox<String> src = getSourceChoiceBox(ae);
        map.put(fileType, src.getValue());
    }
    @FXML
    private void handleClassAbstraction(ActionEvent ae) {
        Labeled src = (Labeled) ae.getSource();
        map.put(classModifiers, src.getText().replace("neither", ""));
    }
    @FXML
    private void handleMethodModifiers(ActionEvent ae) {
        Labeled src = (Labeled) ae.getSource();
        String content = map.get(classModifiers);
        String buttonText = src.getText();
        String text = content.contains("static")? join(" ",content, buttonText) : buttonText;
        map.put(format(methodModifiers, getParentID(src)), text.replace("neither",""));
    }
    @FXML
    private void handleMethodStatic(ActionEvent ae){
        CheckBox src = (CheckBox) ae.getSource();
        String id = format(methodModifiers, getParentID(src));
        String content = map.get(id);
        if(src.isSelected()) {
            map.put(id, content + " " + src.getText());
        }else if(!src.isSelected())
            map.put(id, content.replace("static", ""));
    }
    @FXML
    private void handleFieldGenerated(ActionEvent ae) {
        CheckBox src = (CheckBox) ae.getSource();
        String id = format(fieldGenerated, getParentID(src));
        String current = map.get(id);
        if(src.isSelected()){
            map.put(id, (current + " " + src.getText()).strip());
        }else if(!src.isSelected()){
            map.put(id, current.replace(src.getText(), "").strip());
        }
    }

    public static String getCaller(){
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement element = stack[4];
        return element.getClassName() + "::" + element.getMethodName() + " :" + element.getLineNumber();
    }
}