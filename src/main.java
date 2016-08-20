/**
 * Created by johnvangilder on 7/16/16.
 */


import java.util.*;

//APIs from isbndb.com, the isbn database

import java.net.*;
import java.io.*;

//Using GSON for JSON parsing
import com.google.gson.*;


public class main {

    //First part of the URL used for the API call to the OCLC database

    //Put API Key here
    private static String apiKey = "";


    private static String urlStart = "http://isbndb.com/api/v2/json/" + apiKey + "/book/";

    private static Scanner scan = new Scanner(System.in);

    private static ArrayList<Book> library = new ArrayList<>();

    private static Book fakebook = new Book("faketitle","0","0","0","fakeauthor");



    public static void main(String[] args) {
        //Check to see if the API key is present; if not, exit
        if(apiKey.compareTo("") == 0){
            System.out.println("No API key detected-check README for proper usage");
            System.exit(-2);
        }

        //Here's our program loop.
        while(true) {



            System.out.println("Please enter an ISBN or the title of a book.");
            String identifier = null;
            identifier = scan.nextLine();

            identifier.trim();
            if(identifier.length() == 0){
                System.out.println("Please try again");
                continue;
            }
            //if the identifier has spaces in it (after trimming) it's a title rather than an isbn, and we need to
            //replaces the spaces with underscores for our API call.

            identifier = identifier.replaceAll(" ", "_");

            try {
                Book book = getBook(identifier);

                if(book == null) {
                    System.out.println("Please try again");
                }else if(book.getTitle().compareToIgnoreCase("faketitle") == 0){
                    continue;
                }else{
                    library.add(book);
                    System.out.println(library.get(library.size()-1).getTitle().trim() +" by "+
                            library.get(library.size()-1).getAuthor().trim() + " successfully added.");
                }

            }catch(Exception e){
                if(e instanceof UnsupportedOperationException){
                    System.out.println("This function is currently unsupported.");
                }

            }

        }
        //System.out.println(library.get(0).getTitle());
        //System.out.println(library.get(0).getLcc());
        //System.out.println(library.get(0).getIsbn());

    }
    private static Book getBook(String identifier){

        if(identifier.compareTo("quit") == 0) {
            System.out.println("Are you sure you want to quit? y/n");
            identifier = scan.nextLine();
            if (identifier.compareToIgnoreCase("y") == 0) {
                exportList();
                System.out.println("Saved and quit");
                System.exit(1);
            } else {
                return fakebook;
            }
        }else if(identifier.compareTo("export") == 0){
            exportList();
            return fakebook;

        }else if(identifier.compareTo("import") == 0){
            importList();
            return fakebook;

        }else if(identifier.compareTo("print") == 0) {
            if(printList() == 0){
                System.out.println("The list is empty.");
            }
            return fakebook;

        }else if(identifier.compareTo("help") == 0){
            System.out.println("List of Commands (case sensitive):");
            System.out.println("help: displays this list");
            System.out.println("quit: exits the program");
            System.out.println("export: exports the list of books to CSV format (unsupported)");
            System.out.println("import: imports the list of books from CSV format (unsupported)");
            System.out.println("print: prints the list of books, their ISBNs and LCCs to the console");
            System.out.println
                    ("note: if a book shares a title with one of these commands, enter it with a capital letter");

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
    private static int printList(){


        for(int i = 0; i < library.size(); i++){
            System.out.println(library.get(i).getTitle() + " , "
                    + library.get(i).getAuthor() + " , "
                    + library.get(i).getIsbn() + " , "
                    + library.get(i).getLcc() + " , "
                    + library.get(i).getDewey());
        }

        return library.size();
    }


}
//9780380709120