package logic;

import java.util.*;
import java.util.stream.Collectors;

import static gui.Controller.getCaller;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@SuppressWarnings("LawOfDemeter")
public final class ClassCreator {
    private Class klass;

    public ClassCreator(String name, String filetype, AccessModifier accessModifier, String modifiers) {
        klass = new Class(name, filetype, accessModifier);
        klass.modifiers = modifiers;
    }

    public final void setSuperClass(String superClass) {
        klass.superclass = superClass;
    }

    public final void setInterfaces(String... interfaces) {
        klass.interfaces.addAll(Arrays.asList(interfaces));
    }


    public final DummyMethod addMethod(String name, String returnType, AccessModifier access, String modifiers) {
        Method temp = new Method(name,returnType, access);
        temp.modifiers = modifiers;
        klass.methods.add(temp);
        return new DummyMethod(temp);
    }

    public final void addField(String name, String type, AccessModifier accessModifier, String modifiers) {
        Field temp = new Field(name, type, accessModifier);
        if (modifiers != null) {
            temp.modifiers = modifiers;
        }
        klass.fields.add(temp);
    }

    public void addProperty(String name, String type, AccessModifier accessModifier, String modifiers,
                            boolean hasGetter, boolean hasSetter){
        addField(name,type,accessModifier,modifiers);
        if(hasGetter)
            addMethod("get"+capitalizeFirstLetter(name),
                    type,AccessModifier.PUBLIC, "")
                    .addBody(format("return %s;",name));
        if(hasSetter)
            addMethod("set"+capitalizeFirstLetter(name),
                    "void", AccessModifier.PUBLIC,"")
                    .addBody(format("this.%s = %s;",name,name))
                    .addParameter(name, type);
    }

    public final String build() {
        return create(klass);
    }

    private String capitalizeFirstLetter(String str){
        return str.replace(str.substring(0,1),str.substring(0,1).toUpperCase());
    }
    
    private static String create(Class klass) {
        return klass.toString();
    }

    public void toFile(String filepath) {
        assert filepath != null;
        assert !filepath.isEmpty();
        FileCreator fc = new FileCreator(CREATE, TRUNCATE_EXISTING);
        String absPath = filepath + klass.name + ".java";
        fc.write(absPath, build());
        System.out.println("Deployed class to " + absPath);
    }

    public enum AccessModifier {
        PUBLIC, PRIVATE, PROTECTED, PACKAGE;

        @Override
        public String toString() {
            return switch (this) {
                case PUBLIC -> "public ";
                case PACKAGE -> "";
                case PRIVATE -> "private ";
                case PROTECTED -> "protected ";
            };
        }
        public static AccessModifier parse(String str){
            if(str == null)
                throw new IllegalArgumentException(format("%s called AccessModifier#parse with the argument of null",
                                                    getCaller()));
            else return switch (str.toUpperCase()) {
                case "PUBLIC" -> PUBLIC;
                case "PROTECTED" -> PROTECTED;
                case "PACKAGE" -> PACKAGE;
                case "PRIVATE" -> PRIVATE;
                default -> throw
                        new IllegalArgumentException("there is no valid ClassType value that can be represented by "
                                                     + str);
            };
        }
    }
    public enum ClassType{
        CLASS, INTERFACE, ENUM, ANNOTATION, @Deprecated(since = "not supported yet") EXCEPTION;

        @Override
        public String toString() {
            return switch(this){
                case ENUM -> "enum";
                case CLASS -> "class";
                case INTERFACE -> "interface";
                case ANNOTATION -> "@interface";
                case EXCEPTION -> "class /*Exception*/";
            };
        }

        public static ClassType parse(String str){
            if(str == null)
                throw new IllegalArgumentException(format("%s called ClassType#parse with the argument of null",
                        getCaller()));
            else return switch(str.toUpperCase()){
                case "CLASS" -> CLASS;
                case "INTERFACE" -> INTERFACE;
                case "ENUM" -> ENUM;
                case "EXCEPTION" -> EXCEPTION;
                case "ANNOTATION","@INTERFACE"->ANNOTATION;
                default -> throw
                           new IllegalArgumentException("there is no valid ClassType value that can be represented by "
                                                        + str);
            };
        }
    }

    private static final class Class {
        private final AccessModifier accessModifier;
        private final String type;
        private final String name;
        private String modifiers;
        private String superclass;
        private final List<String> interfaces;
        private final List<Method> methods;
        private final List<Field> fields;

        private Class(String name, String type, AccessModifier accessModifier, String superclass, String... interfaces){
            this.name = name;
            this.type = type;
            this.accessModifier = accessModifier;
            this.superclass = superclass;
            this.interfaces = new ArrayList<>();
            this.interfaces.addAll(Arrays.asList(interfaces));
            methods = new ArrayList<>();
            fields = new ArrayList<>();
            modifiers = "";
        }

        private Class(String name, String type, AccessModifier accessModifier, String... interfaces) {
            this(name, type, accessModifier, "Object", interfaces);
        }



        private String header() {
            String mods = ((modifiers == null) || modifiers.isEmpty()) ? "" : (modifiers + " ");
            return accessModifier + mods + type +" " + name + (
                    "Object".equals(superclass) ? "" : " extends " + superclass) +
                                                       handleInterfaces() + "{\n\t";
        }

        
        private String body() {
            return handleFields() + handleMethods() + "\n}";
        }

        
        private String handleFields() {
            return fields.stream()
                           .map(Field::toString)
                           .collect(Collectors.joining("\n\t"))
                            + "\n\t";
        }

        
        private String handleInterfaces() {
            return join(", ", interfaces);
        }

        
        private String handleMethods() {
            return methods.stream().map(Method::toString).collect(Collectors.joining("\n\t"));
        }

        @Override
        public String toString() {
            return header() + body();
        }

    }

    private static final class Method {
        private String returnType;
        private String name;
        private String modifiers;
        private final List<Field> parameters = new ArrayList<>();
        private AccessModifier accessModifier;
        private String body = "";

        private Method(String name, String returnType, AccessModifier accessModifier) {
            this.name = name;
            this.returnType = returnType;
            this.accessModifier = accessModifier;
        }


        private String body() {
            return body.isEmpty() ? switch (returnType) {
                case "void" -> "";
                case "double", "int", "float", "long", "short", "byte" -> "return 0;";
                default -> "return null;";
            } : body;
        }
        
        @Override
        public String toString() {
            String mods = ((modifiers == null) || modifiers.isEmpty()) ? "" : (modifiers + " ");
            return accessModifier + mods +
                   returnType + " " + name + "(" +
                   parameters.stream().map(Field::asParameter)
                           .collect(Collectors.joining(", ")).replace(";","") +
                   "){\n\t\t" + body() + "\n\t}\n";
        }
    }

    private static final class Field {
        private final String type;
        private final String name;
        private String modifiers ;
        private final AccessModifier accessType;

        private Field(String name, String type, AccessModifier accessModifier) {
            this.type = type;
            this.name = name;
            this.accessType = accessModifier;
        }

        private String asParameter(){
            return join(" ", type, name);
        }
        @Override
        public String toString() {
            String mods = ((modifiers == null) || modifiers.isEmpty()) ? "" : (modifiers + " ");
            return accessType + mods + type + " " + name + ";";
        }
    }

    public static final class DummyMethod {
        private final Method method;
        private boolean empty = true;

        private DummyMethod(Method temp) {
            method = temp;
        }
        public DummyMethod addParameter(String name, String type){
            method.parameters.add(new Field(name,type,AccessModifier.PACKAGE));
            return this;
        }

        public final DummyMethod addBody(String body) {
            empty = false;
            method.body = body;
            return this;
        }

        private boolean isEmpty() {
            return empty;
        }
    }

    @Override
    public String toString() {
        return build();
    }
}