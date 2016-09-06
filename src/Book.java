/**
 * Created by johnvangilder on 7/16/16.
 */

//Used by main to create a book object with various fields
//Books have a name, an isbn, and a library of congress classification code.
import javafx.beans.property.SimpleStringProperty;


public class Book implements Comparable{

    public static String valueToCompare;

    private SimpleStringProperty title;
    private SimpleStringProperty isbn;
    private SimpleStringProperty lcc;
    private SimpleStringProperty dewey;
    private SimpleStringProperty author;

    //Constructor
    public Book(String title, String author, String isbn, String lcc, String dewey) {
        this.title = new SimpleStringProperty(title);
        this.isbn = new SimpleStringProperty(isbn);
        this.lcc = new SimpleStringProperty(lcc);
        this.dewey = new SimpleStringProperty(dewey);
        this.author = new SimpleStringProperty(author);
    }

    public boolean setValueToCompare(String comparer){
        if(comparer.compareTo("title") == 0 ||
                comparer.compareTo("isbn") == 0 ||
                comparer.compareTo("lcc") == 0 ||
                comparer.compareTo("dewey") == 0||
                comparer.compareTo("author") == 0) {
            valueToCompare = comparer;
            return true;
        }else{
            return false;
        }
    }


    //Get methods for the Book class
    public String getTitle(){return title.get();}
    public String getIsbn(){return isbn.get();}
    public String getLcc(){
        return lcc.get();
    }
    public String getDewey(){return dewey.get();}
    public String getAuthor(){return author.get();}

    @Override
    public int compareTo(Object o1){


        Book compBook = (Book) o1;

        if(valueToCompare.compareToIgnoreCase("title") == 0){
            return compBook.getTitle().compareToIgnoreCase(this.getTitle());
        }else if(valueToCompare.compareToIgnoreCase("isbn") == 0){
            return compBook.getIsbn().compareToIgnoreCase(this.getIsbn());
        }else if(valueToCompare.compareToIgnoreCase("Library of Congress Classifier") == 0){
            return compBook.getLcc().compareToIgnoreCase(this.getLcc());
        }else if(valueToCompare.compareToIgnoreCase("dewey") == 0){
            return compBook.getDewey().compareToIgnoreCase(this.getDewey());
        }else if(valueToCompare.compareToIgnoreCase("author") == 0){
            return compBook.getAuthor().compareToIgnoreCase(this.getAuthor());
        }

        return 0;
    }
}
