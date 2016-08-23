/**
 * Created by johnvangilder on 7/16/16.
 */


import java.util.*;


//APIs from isbndb.com, the isbn database

import java.net.*;
import java.io.*;

//Using GSON for JSON parsing
import com.google.gson.*;

//JavaFX GUI Toolkit imports

import com.intellij.notification.impl.NotificationActionProvider;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.geometry.Pos;


public class main extends Application implements EventHandler<ActionEvent>{

    //First part of the URL used for the API call to the OCLC database

    //Put API Key here
    private static String apiKey = "90U6WUGD";

    //for API call
    private static String urlStart = "http://isbndb.com/api/v2/json/" + apiKey + "/book/";

    private static Scanner scan = new Scanner(System.in);

    private static ArrayList<Book> library = new ArrayList<>();

    private static Book fakebook = new Book("faketitle","0","0","0","fakeauthor");


    //GUI Elements
    Button loadbutton;
    Button savebutton;
    Button quitbutton;
    Button printbutton;
    Button undobutton;
    Button helpbutton;

    Label label;
    TextField textfield;



    public static void main(String[] args) {

        launch(args);



    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Check to see if the API key is present; if not, alert and exit
        if(apiKey.compareTo("") == 0){
            Alert apiAlert = new Alert(Alert.AlertType.ERROR, "No API Key detected; see README for proper usage");
            apiAlert.setHeaderText("No API Key");
            apiAlert.showAndWait();
            System.exit(-2);
        }

        primaryStage.setTitle("BookSort");

        ArrayList<Node> nodeList = new ArrayList<Node>();

        loadbutton = new Button("Load");
        savebutton = new Button("Save");
        quitbutton = new Button("Save and Quit");
        printbutton = new Button("Display List");
        undobutton = new Button("Undo Last Entry");
        helpbutton = new Button("Help");

        label = new Label();
        textfield = new TextField();

        loadbutton.setOnAction(this);
        savebutton.setOnAction(this);
        quitbutton.setOnAction(this);
        printbutton.setOnAction(this);
        undobutton.setOnAction(this);
        helpbutton.setOnAction(this);
        textfield.setOnAction(this);


        //Setup the layout of our GUI
        GridPane layout = new GridPane();
        HBox hbButtons = new HBox(3);
        HBox hbButtons2 = new HBox(3);

        label.setWrapText(true);

        layout.setAlignment(Pos.CENTER);


        layout.setVgap(5);
        hbButtons.getChildren().addAll(loadbutton, savebutton, printbutton, undobutton);
        hbButtons2.getChildren().addAll(helpbutton, quitbutton);

        layout.add(textfield, 0, 0);
        layout.add(label, 0, 1);
        layout.add(hbButtons, 0, 2);
        layout.add(hbButtons2, 0, 3);

        hbButtons.setAlignment(Pos.CENTER);

        hbButtons2.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event){

        if(event.getSource() == textfield){
            Book book = getBook(textfield.getCharacters().toString());

            if(book == null) {
                System.out.println("Please try again");
            }else{
                library.add(book);
                label.setText(library.get(library.size()-1).getTitle().trim() +" by "+
                        library.get(library.size()-1).getAuthor().trim() + " successfully added.");
            }
            textfield.setText("");
        }else if(event.getSource() == loadbutton){
            importList();
        }else if(event.getSource() == savebutton){
            exportList();
        }else if(event.getSource() == undobutton){
            library.remove(library.size() - 1);
            label.setText("Last Entry Removed");
        }else if(event.getSource() == printbutton){
            //TODO: Look at JavaFX tables, implement this here

            Stage tableStage = new Stage();
            tableStage.setTitle("Book List");
            TableColumn titleColumn = new TableColumn("Title");
            TableColumn authorColumn = new TableColumn("Author");
            TableColumn isbnColumn = new TableColumn("ISBN");
            TableColumn lccColumn = new TableColumn("LCC");
            TableColumn deweyColumn = new TableColumn("Dewey");
            TableView table = new TableView();
            table.getColumns().addAll(titleColumn, authorColumn, isbnColumn, lccColumn, deweyColumn);

            table.setEditable(true);

            titleColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
            authorColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
            isbnColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("isbn"));
            lccColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("lcc"));
            deweyColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("dewey"));

            table.setItems(FXCollections.observableList(library));
            Scene scene = new Scene(table);

            tableStage.setScene(scene);
            tableStage.show();



            //console printing, for now
            //for(int i = 0; i < library.size(); i++){
             //   System.out.println(library.get(i).getAuthor());
            //}
        }else if(event.getSource() == helpbutton){
            String helpDialogue;
            helpDialogue = "Enter a book title or ISBN into the search bar and it is " +
                    "added to your book list.\n\nLoad imports a library, and Save exports it as a CSV.\n\nUndo deletes " +
                    "the last book entered, Save and Quit exports the file and quits the program.\n\nHelp displays this dialogue.";

            Alert help = new Alert(Alert.AlertType.INFORMATION);
            help.setTitle("BookSorter Help");
            help.setHeaderText("BookSorter v 2.0 \nJohn Van Gilder");
            help.setContentText(helpDialogue);

            help.showAndWait();

        } else if(event.getSource() == quitbutton){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you'd like to save and quit?");
            Optional<ButtonType> response = alert.showAndWait();
            if(response.isPresent() && response.get() == ButtonType.OK) {
                exportList();
                System.exit(1);
            }
        }

    }





    private static Book getBook(String identifier){

        //if the identifier has spaces in it (after trimming) it's probably a title rather than an isbn, and we need to
        //replaces the spaces with underscores for our API call.
        identifier = identifier.replaceAll(" ", "_");
        if(identifier.length() == 0){
            System.out.println("Please try again");
            return fakebook;
        }


       if(identifier.compareTo("print") == 0) {
            printList();

            return fakebook;

        }

        String urlString = urlStart + identifier;
        try {
            URL url = new URL(urlString);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String json = "";
            String input = "";
            while((input = in.readLine()) != null) {
                json = json + input;
            }

            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            if(jsonObject.toString().contains("error\":\"Unable")){
                return null;
            }
            //set author to a space so we can do error checking later
            String author = " ";


            //Unwrap the JSON data to get the title of the book as a string
            String title = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().get("title").toString();
            String lcc = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().get("lcc_number").toString();
            String isbn = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().get("isbn13").toString();
            String dewey = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().get("dewey_decimal").toString();

            //Since author data is an array, we have to check its size before getting anything from it.
            if(jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().getAsJsonArray("author_data").size() != 0) {
                author = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().getAsJsonArray("author_data")
                        .get(0).getAsJsonObject().get("name").toString();
            }else{
                System.out.println("Input author:");
                author = " " + scan.nextLine() + " ";
            }

            //Trim the beginning and ending quotes off the Json data we just got
            title = title.substring(1,title.length()-1);
            lcc = lcc.substring(1,lcc.length()-1);
            isbn = isbn.substring(1,isbn.length()-1);
            dewey = dewey.substring(1,dewey.length()-1);
            author = author.substring(1, author.length()-1);

            String[] temp = new String[2];

            if(title.length() == 0)
                title = " ";
            if(lcc.length() == 0)
                lcc = " ";
            if(isbn.length() == 0)
                isbn = " ";
            if(dewey.length() == 0)
                dewey = " ";
            //if the author name is listed as lastname,firstname, strip the comma and flip it around
            if(author.contains(",")){
                //System.out.println(author.split(",").length);
                if(author.split(",").length == 2){
                    temp = author.split(",");
                    author = temp[1] + " " + temp[0];
                }else{
                    author = "\""+author+"\"";
                }
            }

            in.close();
            Book book = new Book(title, isbn, lcc, dewey, author);
            return book;



        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

        return null;
    }
    private static void exportList(){
        //TODO: Check to see if a file with this name already exists
        try {

            PrintWriter out = new PrintWriter("booklibrary.csv");
            out.print("Title,Author,ISBN,LCC,Dewey Decimal\n");
            for(int i = 0; i < library.size(); i++) {
                out.print(library.get(i).getTitle() + " , "
                        + library.get(i).getAuthor() + " , "
                        + library.get(i).getIsbn() + " , "
                        + library.get(i).getLcc() + " , "
                        + library.get(i).getDewey() + "\n");
            }
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static void importList(){

        //TODO: More robust error checking for well-formed files

        boolean done = false;

        try {
            //Add support for different kinds of file imports?
            //System.out.println("Import file name:");
            //String filename = scan.nextLine();
            //if(filename.length() == 0){
            String filename = "booklibrary.csv";
            //}

            File inFile = new File(filename);
            Scanner inScanner = new Scanner(inFile);
            String inParse = inScanner.nextLine();
            String[] bookThings = new String[5];

            while(inScanner.hasNextLine()){
                inParse = inScanner.nextLine();
                if(inParse != null){
                    bookThings = inParse.split(",");
                    Book book = new Book(bookThings[0],bookThings[1],bookThings[2],bookThings[3],bookThings[4]);
                    library.add(book);

                }else{
                    done = true;
                }



            }



        }catch(Exception e){
            System.out.println("No import file found");
            //e.printStackTrace();
            return;
        }
        if(library.size() != 0) {
            System.out.println("File imported successfully");
        }
    }

    //Prints the list to the console
    private static void printList(){

        if (library.size() == 0){
            System.out.println("Library is empty");
            return;
        }
        for(int i = 0; i < library.size(); i++){
            System.out.println(library.get(i).getTitle() + " , "
                    + library.get(i).getAuthor() + " , "
                    + library.get(i).getIsbn() + " , "
                    + library.get(i).getLcc() + " , "
                    + library.get(i).getDewey());
        }

        return;
    }


}
