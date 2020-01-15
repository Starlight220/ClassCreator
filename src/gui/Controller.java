package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static gui.Constants.*;

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

    public Integer methods = 0, fields = 0;
    @FXML
    private TextField classNameField;
    @FXML
    private Accordion methodAccordion;
    @FXML
    private Accordion fieldAccordion;

    private static String getSelected(ToggleGroup group) {
        return ((Labeled) group.getSelectedToggle()).getText();
    }

    private static ChoiceBox<String> getSourceChoiceBox(Event ae) {
        return ((ChoiceBox<String>) ae.getSource());
    }
@FXML
    void initClassNameField(){
        if(classNameField != null){
        classNameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                // text field has lost focus...
                System.out.println(classNameField.getText());
                map.put(className,classNameField.getText());
            }
        });}else new Thread(()->{
            long startTime = System.currentTimeMillis();
            while(System.currentTimeMillis()-startTime < 1000){}
            initClassNameField();
        }).start();
    }
    @FXML
    private void build(ActionEvent ae) {
        ClassCreatorUI.build(ae);
    }

    @FXML
    private void addAnotherField(ActionEvent ae) throws IOException {
        fields++;
        TitledPane pane = (TitledPane) FXMLLoader.load(getClass().getResource("fxmlFiles/fieldPane.fxml"));
        setID(pane, valueOf(fields));
        fieldAccordion.getPanes().add(pane);
    }

    @FXML
    private void addAnotherMethod(ActionEvent ae) throws IOException {
        methods++;
        TitledPane pane = FXMLLoader.load(getClass().getResource("fxmlFiles/methodPane.fxml"));
        setID(pane, valueOf(methods));
        System.out.println(pane.getId());
        printID(pane);
        methodAccordion.getPanes().add(pane);
    }
    void printID(Node node){
        System.err.println(node + "'s id is " + node.getId());
        if(node instanceof Parent){
            ((Parent) node).getChildrenUnmodifiable().forEach(this::printID);
        }
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
        ChoiceBox<String> src = getSourceChoiceBox(ae);
        if (src.getId() == null) {
            System.err.println("src.id is null , so deserted");
            System.err.println("gui.Controller::handleMethodPublicity");
            return;
        }
        System.err.println(src.getId());
//        System.out.println(src.idProperty());
        map.put(format(methodAccess, parseInt(src.getId())), src.getValue().toUpperCase());
    }

    @FXML
    private void handleClassPublicity(ActionEvent ae) {
        map.put("classAccess", getSourceChoiceBox(ae).getValue().toUpperCase());
    }

    @FXML
    private void handleFieldPublicity(ActionEvent ae) {
        TextField textfield = ((TextField) ae.getSource());
        map.put(format(fieldAccess, parseInt(textfield.getId())),
                textfield.getCharacters().toString());
    }

    @FXML
    private void handleMethodName(ActionEvent ae) {
        TextField textfield = ((TextField) ae.getSource());
        map.put(format(methodName, getParentID(textfield)),
                textfield.getCharacters().toString());
    }

    /**
     * A recursive function that finds the pane's ID
     * @param node
     * @return
     */
    int getParentID(Node node){
        if(node instanceof TitledPane){
            return parseInt(node.getId());
        }
        else return getParentID(node.getParent());
    }

    @FXML
    private void handleFieldModifiers(ActionEvent ae) {
        CheckBox src = ((CheckBox) ae.getSource());
        String key = format(fieldModifier, getParentID(src));
        String insertion = map.getOrDefault(key, "").replace("null","");
        if(src.isSelected()) {
            insertion = insertion.replace("null","")
                    .contains(src.getText()) ? map.get(key) : map.get(key) + " " + src.getText();
        }else if(!src.isSelected()){
            insertion = insertion.replace(src.getText(), "");
        }
        map.put(key, insertion.strip());
    }

    @FXML
    private void handleFieldName(ActionEvent ae) {
        TextField textfield = ((TextField) ae.getSource());
        map.put(format(fieldName, getParentID(textfield)),
                textfield.getCharacters().toString());
    }

    @FXML
    private void handleFieldType(ActionEvent ae) {
        TextField textfield = ((TextField) ae.getSource());
        map.put(format(fieldType, getParentID(textfield)),
                textfield.getCharacters().toString());
    }

    @FXML
    private void handleMethodReturnType(ActionEvent ae) {
        TextField textfield = ((TextField) ae.getSource());
        map.put(format(methodReturn, getParentID(textfield)),
                textfield.getCharacters().toString());
    }

    @FXML
    private void handleClassType(ActionEvent ae) {
        ChoiceBox<String> src = getSourceChoiceBox(ae);
        map.put(fileType, src.getValue());
    }

    public void handleClassAbstracticity(ActionEvent actionEvent) {
    }//TODO

    private static class LogMap extends HashMap<String, String> {
        private static final long serialVersionUID = -1473367291655026433L;

        @Override
        public String put(String key, String value) {
                       System.out.printf("put in map : \t key = %s \t value = %s\n", key, value);
            return super.put(key.replace("null",""), value.replace("null",""));
        }

        @Override
        public String get(Object key) {
            String val = super.get(key);
            System.err.printf("for key %s , returned value %s to caller %s\n", key, val, getCaller());
            return val;
        }

        @Override
        public String getOrDefault(Object key, String defaultValue) {
            String val = super.getOrDefault(key, defaultValue);
            System.err.printf("for key %s , returned value %s to caller %s with default %s\n",
                                        key,                val,     getCaller(),   defaultValue);
            return val;
        }

        String getCaller(){
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement element = stack[4];
            String trace = element.getClassName() + "::" + element.getMethodName() + " :" + element.getLineNumber();
            System.err.println(trace + "called something that called #getCaller()");
            return trace;
        }
    }

}