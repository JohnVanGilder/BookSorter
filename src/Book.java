/**
 * Created by johnvangilder on 7/16/16.
 */

//Used by main to create a book object with various fields
//Books have a name, an isbn, and a library of congress classification code.

public class Book {

    private String title;
    private String isbn;
    private String lcc;
    private String dewey;
    private String author;

    //Constructor
    public Book(String startTitle, String startIsbn, String startLcc, String startDewey, String startAuthor){
        title = startTitle;
        isbn = startIsbn;
        lcc = startLcc;
        dewey = startDewey;
        author = startAuthor;
    }


    //Get methods for the Book class
    public String getTitle(){return title;}
    public String getIsbn(){
        return isbn;
    }
    public String getLcc(){
        return lcc;
    }
    public String getDewey(){return dewey;}
    public String getAuthor(){return author;}

}
