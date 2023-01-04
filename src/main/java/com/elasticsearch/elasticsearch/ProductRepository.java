package com.elasticsearch.elasticsearch;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;

@Repository
public class ProductRepository {

	
	  @Autowired
	    private ElasticsearchClient elasticsearchClient;
	    
	  private final String indexName = "products";
	  
	  public String createOrUpdateDocument(Product product) throws IOException {
		  
		  IndexResponse response = elasticsearchClient.index(i-> i.index(indexName)
				                                                .id(product.getId())
				                                                .document(product));
				                                               
		  
		  
		  
		  
	        if(response.result().name().equals("Created")){
	            return new StringBuilder("Document has been successfully created.").toString();
	        }else if(response.result().name().equals("Updated")){
	            return new StringBuilder("Document has been successfully updated.").toString();
	        }
	        return new StringBuilder("Error while performing the operation.").toString();
	    

	 
		  
	  }
	  
	  
	  public Product getDocumentById (String id) throws ElasticsearchException, IOException {
		  Product product = null;
	        GetResponse<Product> response = elasticsearchClient.get(g -> g
	                        .index(indexName)
	                        .id(id),
	                Product.class
	        );

	        if (response.found()) {
	             product = response.source();
	            System.out.println("Product name " + product.getName());
	        } else {
	            System.out.println ("Product not found");
	        }

	       return product;
	  }
	 
	  
	  
}
