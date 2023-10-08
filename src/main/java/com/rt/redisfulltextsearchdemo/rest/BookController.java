package com.rt.redisfulltextsearchdemo.rest;

import com.redislabs.lettusearch.*;
import com.rt.redisfulltextsearchdemo.entity.Book;
import com.rt.redisfulltextsearchdemo.service.BookService;
import io.lettuce.core.RedisCommandExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/v1")
public class BookController {
    @Autowired
    BookService bookService;

    @Autowired
    StatefulRediSearchConnection<String, String> searchConnection;

    @Value("${app.booksSearchIndexName}")
    String searchIndexName;
    @PostMapping("/book")
    public ResponseEntity<Book> saveBook(@RequestBody Book book) {
        bookService.save(book);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBook(@PathVariable String isbn) {
        Book book = bookService.getBook(isbn);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public SearchResults<String,String> search(@RequestParam(name="q")String query) {
        RediSearchCommands<String, String> commands = searchConnection.sync();
        SearchResults<String, String> results = commands.search(searchIndexName, query);
        return results;
    }

    @PostMapping("/createIndex")
    public void createIndex() {
        RediSearchCommands<String, String> commands = searchConnection.sync();
        try {
            commands.ftInfo(searchIndexName);
        } catch (RedisCommandExecutionException rcee) {
            if (rcee.getMessage().equals("Unknown Index name")) {

                CreateOptions<String, String> options = CreateOptions.<String, String>builder()//
                        .prefix(String.format("%s:", Book.class.getName())).build();
                Field<String> name = Field.text("name").sortable(true).build();
                Field<String> isbn = Field.text("isbn").build();


                commands.create(
                        searchIndexName, //
                        options, //
                        name, isbn
                );

                System.out.println(">>>> Created Books Search Index...");
            }
        }
    }
}
