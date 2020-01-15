package logic;

import logic.FileCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@SuppressWarnings({"unused", "LawOfDemeter"})
public final class ClassCreator {
    private Class klass = null;
//    @Deprecated
//    private int methodi = 0, fieldi = 0;

    public ClassCreator(String name, String filetype, AccessModifier accessModifier, String... modifiers) {
        System.err.println("ClassCreator::ClassCreator ->");
        System.err.println("classname = " + name);

        klass = new Class(name, filetype, accessModifier);
    }


    public static String handleArrayElements(List<?> list, String delimiter, String optional, int minimumForOptional){
        StringBuilder sb = new StringBuilder(10);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (i == minimumForOptional) {
                    sb.append(optional).append(" ");
                }
                sb.append(list.get(i));
                if (i + 1 < list.size()) {
                    sb.append(delimiter).append(" ");
                }
            }
            return sb.toString();
        }
        return "";
    }

    public static <T> String handleArrayElements(List<T> arr, String delimiter) {
        return handleArrayElements(arr, delimiter, "", 0);
    }

    public final void setSuperClass(String superClass) {
        klass.superclass = superClass;
    }

    public final void setInterfaces(String... interfaces) {
        klass.interfaces.addAll(Arrays.asList(interfaces));
    }


    public final DummyMethod addMethod(String name, String returnType, AccessModifier access, String... modifiers) {
        Method temp = new Method(name,returnType, access);
        temp.modifiers.addAll(Arrays.asList(modifiers));
        klass.methods.add(temp);
        return new DummyMethod(temp);
    }

    public final void addField(String name, String type, AccessModifier accessModifier, String... modifiers) {
        Field temp = new Field(name, type, accessModifier);
        if (modifiers != null) {
            temp.modifiers.addAll(Arrays.asList(modifiers));
        }
        klass.fields.add(temp);
//        fieldi++;
    }

    public void addProperty(String name, String type, AccessModifier accessModifier, String[] modifiers,
                            boolean getter, boolean setter){
        addField(name,type,accessModifier,modifiers);
        if(getter)
            addMethod("get"+name,type,AccessModifier.PUBLIC);
        if(setter)
            addMethod("set"+name.replace(name.substring(0,1),name.substring(0,1).toUpperCase()),
                    "void", AccessModifier.PUBLIC)
                    .addBody(String.format("\nthis.%s = %s;\n",name,name))
                    .addParameter(name, type);
    }

    public final String build() {
        return create(klass);
    }

    // @NotNull
    private static String create( Class klass) {
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
            return str == null ? null :switch (str) {
                case "PUBLIC" -> PUBLIC;
                case "PROTECTED" -> PROTECTED;
                case "PACKAGE" -> PACKAGE;
                case "PRIVATE" -> PRIVATE;
                default -> null;
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
                case EXCEPTION -> "class //Exception";
            };
        }

        public static ClassType parse(String str){
            return switch(str.toUpperCase()){
                case "CLASS" -> CLASS;
                case "INTERFACE" -> INTERFACE;
                case "ENUM" -> ENUM;
                case "EXCEPTION" -> EXCEPTION;
                case "ANNOTATION","@INTERFACE"->ANNOTATION;
                default -> throw new IllegalStateException("Unexpected value: " + str.toUpperCase());
            };
        }
    }

    private static final class Class {
        final AccessModifier accessModifier;
        final String type, name;
        String superclass;
        final List<String> interfaces;
        final List<Method> methods;
        final List<Field> fields;

        private Class(String name, String type, AccessModifier accessModifier, String superclass, String... interfaces){
            System.err.println("Class::Class ->");
            System.err.println("classname = " + name);
            this.name = name;
            this.type = type;
            this.accessModifier = accessModifier;
            this.superclass = superclass;
            this.interfaces = new ArrayList<>();
            this.interfaces.addAll(Arrays.asList(interfaces));
            methods = new ArrayList<>();
            fields = new ArrayList<>();
        }

        private Class(String name, String type, AccessModifier accessModifier, String... interfaces) {
            this(name, type, accessModifier, "Object", interfaces);
        }

        // @NotNull
        private String header() {
            System.err.println("Class::header ->");
            System.err.println("classname = " + name);
            return accessModifier
                   + type + " " + name + ("Object".equals(superclass)? " ":" extends " + superclass )+
                   handleInterfaces() + "{\n";
        }

        // @NotNull
        private String body() {
            return handleFields() + handleMethods() + "\n}";
        }

        // @NotNull
        private String handleFields() {
//            StringBuilder sb = new StringBuilder();
//            sb.append(handleArrayElements(fields, ";\n"));
//            return sb.append("\n").toString();
            return handleArrayElements(fields, ";\n") + "\n";
        }

        // @NotNull
        private String handleInterfaces() {
            return handleArrayElements(interfaces, " ", "implements", 0);
        }

        // @NotNull
        private String handleMethods() {
            return handleArrayElements(methods, "\n");
        }

        @Override
        public String toString() {
            return header() + body();
        }

    }

    private static final class Method {
        //        DummyMethod dm;
        private String returnType;
        private String name;
        private final List<String> modifiers = new ArrayList<>();
        private final List<Field> parameters = new ArrayList<>();
        private AccessModifier accessModifier;
        private String body = "";

        private Method(String name, String returnType, AccessModifier accessModifier) {
            this.name = name;
            this.returnType = returnType;
            this.accessModifier = accessModifier;
        }


        private String body() {
            if (returnType == null || body == null) {
                System.err.println("Method::body");
                System.err.println("returnType or body is null so deserted");
                return "WWWWWWWWWWW";
            }
            return body.isEmpty() ? switch (returnType) {
                case "void" -> "";
                case "double", "int", "float", "long" , "short" , "byte" -> "return 0;";
                default -> "return null;";
            } : body;
        }
        // @NotNull
        @Override
        public String toString() {
            return accessModifier + handleArrayElements(modifiers, " ") + " " +
                   returnType + " " + name + "(" +
                   handleArrayElements(parameters, ", ") + "){\n\t" +
                   body() + "\n}\n";
        }
    }

    private static final class Field {
        final String type;
        final String name;
        final List<String> modifiers = new ArrayList<>();
        final AccessModifier accessType;

        private Field(String name, String type, AccessModifier accessModifier) {
            this.type = type;
            this.name = name;
            this.accessType = accessModifier;
        }

        @Override
        public String toString() {
            return ((accessType != null) ?
                    accessType.toString() : "")
                   + handleArrayElements(modifiers, " ") + " " + type + " " + name;
        }
    }

    public static final class DummyMethod {
        private final Method m;
        private boolean isEmpty = true;

        private DummyMethod(Method temp) {
            m = temp;
            List<Field> params = new ArrayList<>();
        }
        public DummyMethod addParameter(String name, String type){
            m.parameters.add(new Field(name,type,AccessModifier.PACKAGE));
            return this;
        }
//        @SafeVarargs
//        public final DummyMethod addParameters( Pair<String, String>... parameters) {
//            isEmpty = false;
//            m.parameters.addAll(Arrays.stream(parameters)
//                    .map((pair) -> new Field(pair.getLeft(),pair.getRight()))
//                    .collect(Collectors.toList()));
//            return this;
//        }

        // @Contract("_ -> this")
        public final DummyMethod addBody(String body) {
            isEmpty = false;
            m.body = body;
            return this;
        }

        // @Contract(pure = true)
        private boolean isEmpty() {
            return isEmpty;
        }
    }

    @Override
    public String toString() {
        return build();
    }


}