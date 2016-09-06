/**
 * Created by johnvangilder on 7/16/16.
 */


import java.util.*;


//APIs from isbndb.com, the isbn database

import java.net.*;
import java.io.*;
import java.util.*;

//Using GSON for JSON parsing
import com.google.gson.*;

//JavaFX GUI Toolkit imports



import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

    private static Book fakebook = new Book("faketitle","fakeauthor","0","0","0");


    //GUI Elements
    Button loadbutton;
    Button savebutton;
    Button quitbutton;
    Button printbutton;
    Button undobutton;
    Button helpbutton;
    Button removebutton;
    Button sortbutton;

    Label label;
    Label removelabel;

    TextField textfield;
    TextField removefield;


    public static void main(String[] args) {
        System.out.println("b".compareTo("a"));
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


        loadbutton = new Button("Load");
        savebutton = new Button("Save");
        quitbutton = new Button("Save and Quit");
        printbutton = new Button("Display List");
        undobutton = new Button("Delete Last Entry");
        helpbutton = new Button("Help");
        removebutton = new Button("Delete A Book");
        sortbutton = new Button("Sort List");

        label = new Label();
        textfield = new TextField();


        loadbutton.setOnAction(this);
        savebutton.setOnAction(this);
        quitbutton.setOnAction(this);
        printbutton.setOnAction(this);
        undobutton.setOnAction(this);
        helpbutton.setOnAction(this);
        textfield.setOnAction(this);
        removebutton.setOnAction(this);
        sortbutton.setOnAction(this);


        //Setup the layout of our GUI
        GridPane layout = new GridPane();
        HBox hbButtons = new HBox(3);
        HBox hbButtons2 = new HBox(3);

        label.setWrapText(true);

        layout.setAlignment(Pos.CENTER);


        layout.setVgap(5);
        hbButtons.getChildren().addAll(loadbutton, savebutton, printbutton, undobutton);
        hbButtons2.getChildren().addAll(sortbutton, helpbutton, quitbutton, removebutton);

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

            displayTable();

            //Uncomment to re-enable printing to the console for debugging
            /*
            for(int i = 0; i < library.size(); i++){
                System.out.println(library.get(i).getAuthor());
            }
            */

        }else if(event.getSource() == helpbutton){
            String helpDialogue;
            helpDialogue = "Enter a book title or ISBN into the search bar and it is added to your book list.\n\n" +
                    "Load imports a library, and Save exports it as a CSV.\n\n" +
                    "Delete Last Entry deletes the last book entered.\n\n" +
                    "Display List brings up a table of all books currently in the library.\n\n" +
                    "Save and Quit exports the file and quits the program.\n\n" +
                    "Delete a Book brings up the Delete window. \n\n" +
                    "Help displays this dialogue.";

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
        }else if(event.getSource() == removebutton){

           removeBook();
        }else if(event.getSource() == removefield){
            //Gets the book, so it can be compared against the database
            Book book = getBook(removefield.getCharacters().toString());

            for(int i = 0; i < library.size(); i++){
                if(library.get(i).getIsbn().compareTo(book.getIsbn()) == 0){
                    removelabel.setText(library.get(i).getTitle() + " by " +
                            library.get(i).getAuthor() + " successfully removed.");
                    library.remove(i);
                    return;
                }
            }
            removelabel.setText("Book not found in library");
        }else if(event.getSource() == sortbutton){
           // Alert alert = new Alert(Alert.AlertType.)
            //sortList();


        }

    }

    //This method removes a book from the list, given the ISBN or title.
    private void removeBook(){



        Stage removeStage = new Stage();
        removeStage.setTitle("Remove a Book");

        removefield = new TextField();
        removelabel = new Label();

        removefield.setOnAction(this);

        GridPane layout = new GridPane();


        removelabel.setWrapText(true);

        layout.setAlignment(Pos.CENTER);


        layout.add(removefield, 0, 0);
        layout.add(removelabel, 0, 1);

        removelabel.setText("Remove a Book");
        Scene scene = new Scene(layout, 300, 250);

        removeStage.setScene(scene);
        removeStage.show();


    }



    private static void displayTable(){
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
        return;
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

            String[] temp = new String[99];
            if(title.contains(",")){
                temp = title.split(",");
                title = "";
                for(int i = 0; i < temp.length; i++){
                    title += temp[i];
                }
            }


            //Trim the beginning and ending quotes off the Json data we just got
            title = title.substring(1,title.length()-1);
            lcc = lcc.substring(1,lcc.length()-1);
            isbn = isbn.substring(1,isbn.length()-1);
            dewey = dewey.substring(1,dewey.length()-1);
            author = author.substring(1, author.length()-1);



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
            Book book = new Book(title, author, isbn, lcc, dewey);
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
    //TODO: Implement sorting

    private static void sortList(String valueToCompare){
        Book.valueToCompare = valueToCompare;

        Collections.sort(library);
    }

}
