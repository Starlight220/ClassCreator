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


    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmlFiles/CCUI.fxml"));
        Scene root = loader.load();
        controller = loader.getController();
        String title = "Class Creator v_1";
        stage.setScene(root);
        stage.setTitle(title);
        stage.show();
    }
    private static Controller controller;

    static void build(ActionEvent ae) {
        Map<String, String> map = Controller.map;
//
        ClassCreator classCreator = new ClassCreator(map.get(className), map.get(fileType),
                parse(map.get(classAccess)),
                map.get(classModifiers));
        for (int i = 1; i <= Controller.methods; i++) {
            System.err.printf("for method loop , iteration %d out of %d methods\n", i, Controller.methods);
            classCreator.addMethod(map.get(format(methodName, i)),
                    map.get(format(methodReturn, i)),
                    parse(map.get(format(methodAccess, i))),
                    map.get(format(methodModifiers,i)));
        }
        for (int i = 1; i <= Controller.fields; i++) {
            System.err.printf("for field loop , iteration %d out of %d field\n", i, Controller.fields);
            classCreator.addProperty(map.get(format(fieldName, i)),
                    map.get(format(fieldType, i)),
                    parse(map.get(format(fieldAccess, i))),
                    map.get(format(fieldModifiers, i)),
                    map.get(format(fieldGenerated,i)).contains("getter"),
                    map.get(format(fieldGenerated,i)).contains("setter"));
        }
        classCreator.toFile("");
    }
}