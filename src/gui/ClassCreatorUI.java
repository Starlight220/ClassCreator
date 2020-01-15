package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.ClassCreator;

import java.io.IOException;
import java.util.Map;

import static gui.Constants.*;
import static java.lang.String.format;
import static logic.ClassCreator.AccessModifier.parse;
public final class ClassCreatorUI extends Application {

//    private List<String> publicityList = Arrays.stream(logic.ClassCreator.AccessModifier.values())
//            .map(logic.ClassCreator.AccessModifier::toString)
//            .collect(Collectors.toList());

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmlFiles/CCUI.fxml"));
        var root = (Scene) loader.load();
        controller = loader.getController();
        String title = "Class Creator";

        stage.setScene(root/* instanceof Scene ? (Scene) root : new Scene((Parent) root)*/);
        stage.setTitle(title);
        stage.show();
        controller.initClassNameField();
    }
    private static Controller controller;

    static void build(ActionEvent ae) {
        Map<String, String> map = Controller.map;
        int methods = controller.methods, fields = controller.fields;
        System.err.println("ClassCreatorUI::build ->");
        System.err.println(map.get(className));
        ClassCreator classCreator = new ClassCreator(map.get(className), map.get(fileType),
                parse(map.get(classAccess)),
                map.get(classModifiers));
//        for (int i = 0; i < methods; i++) {
        int i = 0;
            classCreator.addMethod(map.get(format(methodName, i+1)),
                    map.get(format(methodReturn, i+1)),
                    parse(map.get(format(methodReturn, i+1))));
//        }
//        for (int i = 1; i <= fields; i++) {
//            classCreator.addField(map.get(format(fieldName, i)),
//                    map.get(format(fieldType, i)),
//                    parse(map.get(format(fieldType, i))));
//        }
        classCreator.toFile("");
    }
}