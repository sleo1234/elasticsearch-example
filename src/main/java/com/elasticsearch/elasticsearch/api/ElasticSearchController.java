package com.elasticsearch.elasticsearch.api;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.elasticsearch.elasticsearch.Product;
import com.elasticsearch.elasticsearch.ProductRepository;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;

@RestController
public class ElasticSearchController {

	
	 @Autowired
	    private ProductRepository elasticSearchQuery;
	 
	 
	 @PostMapping("/createOrUpdateDocument")
	 
	 public ResponseEntity<Object> createDocument(@RequestBody Product product) throws IOException{
		
		 String response = elasticSearchQuery.createOrUpdateDocument(product);
		 
		 return new ResponseEntity<>(response,HttpStatus.OK);
	 }
	 
	 
	 
	     @GetMapping("/findDocument/{id}")
	     
	     public ResponseEntity<Object> findProduct(@PathVariable("id") String id) throws ElasticsearchException, IOException{
	    	 Product product = elasticSearchQuery.getDocumentById(id);
	    	 return new ResponseEntity<>(product, HttpStatus.FOUND);
	     }
	     
	     

	     @GetMapping("/findAllDocuments")
	     
	     public ResponseEntity<Object> findAllProduct() throws ElasticsearchException, IOException{
	    	 List<Product> products = elasticSearchQuery.getAllDocuments();
	    	 return new ResponseEntity<>(products, HttpStatus.FOUND);
	     }
}
