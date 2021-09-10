package com.bookstore.worker.init;

import com.bookstore.worker.domain.Book;
import com.bookstore.worker.repository.BookRepository;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Configuration
public class Task implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {

        List<Book> listOfBooks = new ArrayList<>();
        StringBuilder sb = new StringBuilder();


        URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=subject:fiction&key=AIzaSyB-kvbcelAwHB393WxM_EpE1U9zp7nwMFY");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        //Getting the response code
        int responsecode = conn.getResponseCode();

        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        } else {

            String inline = "";
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            JSONObject jsonObject = new JSONObject(inline);
            System.out.println(jsonObject);

            JSONArray arr = jsonObject.getJSONArray("items");
            System.out.println(arr);


            for (int i = 0; i < arr.length(); i++) {
                JSONObject itemsObject = arr.getJSONObject(i);
                JSONObject volumeObject = itemsObject.getJSONObject("volumeInfo");
                String title = volumeObject.optString("title");
                String subtitle = volumeObject.optString("subtitle");
                JSONArray authorsArray = volumeObject.getJSONArray("authors");
                String publisher = volumeObject.optString("publisher");
                String publishedDate = volumeObject.optString("publishedDate");
                String description = volumeObject.optString("description");
                int pageCount = volumeObject.optInt("pageCount");
                JSONObject imageLinks = volumeObject.optJSONObject("imageLinks");
                String thumbnail = imageLinks.optString("thumbnail");
                String previewLink = volumeObject.optString("previewLink");
                String infoLink = volumeObject.optString("infoLink");
                JSONObject saleInfoObject = itemsObject.optJSONObject("saleInfo");
                String buyLink = saleInfoObject.optString("buyLink");

                if (authorsArray.length() != 0) {
                    for (int j = 0; j < authorsArray.length(); j++) {
                        sb.append(authorsArray.optString(j));
                    }
                }

                Book book = new Book(title, subtitle, sb.toString(), publisher, publishedDate, description, pageCount, thumbnail, previewLink, infoLink, buyLink);
                bookRepository.save(book);
            }
        }

    }
}

