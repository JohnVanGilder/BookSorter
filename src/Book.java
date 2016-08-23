/**
 * Created by johnvangilder on 7/16/16.
 */

//Used by main to create a book object with various fields
//Books have a name, an isbn, and a library of congress classification code.
import javafx.beans.property.SimpleStringProperty;

public class Book {

    private SimpleStringProperty title;
    private SimpleStringProperty isbn;
    private SimpleStringProperty lcc;
    private SimpleStringProperty dewey;
    private SimpleStringProperty author;

    //Constructor
    public Book(String title, String isbn, String lcc, String dewey, String author) {
        this.title = new SimpleStringProperty(title);
        this.isbn = new SimpleStringProperty(isbn);
        this.lcc = new SimpleStringProperty(lcc);
        this.dewey = new SimpleStringProperty(dewey);
        this.author = new SimpleStringProperty(author);
    }

    //Get methods for the Book class
    public String getTitle(){return title.get();}
    public String getIsbn(){return isbn.get();}
    public String getLcc(){
        return lcc.get();
    }
    public String getDewey(){return dewey.get();}
    public String getAuthor(){return author.get();}

}
